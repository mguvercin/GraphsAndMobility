package DBOp;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.sparsity.sparksee.gdb.Condition;
import com.sparsity.sparksee.gdb.Database;
import com.sparsity.sparksee.gdb.EdgeData;
import com.sparsity.sparksee.gdb.EdgesDirection;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Session;
import com.sparsity.sparksee.gdb.Sparksee;
import com.sparsity.sparksee.gdb.SparkseeConfig;
import com.sparsity.sparksee.gdb.SparkseeProperties;
import com.sparsity.sparksee.gdb.Value;

import DataStructures.Time;
import DataStructures.Edge;
import Exceptions.NoVIDFoundException;
import Exceptions.OutOfBoundaryException;
import DataStructures.Path;
import DataStructures.Tuple;
import DataStructures.Vertex;

/**
 * This class includes the major operations of the graph facilities. The graph
 * features will be used over the class.
 * 
 * @see GraphInterface
 * 
 */
public class Graph implements GraphInterface {
	/** The parameters needed for Sparksee connection and manipulation. */
	private static Database db;
	Session sess;
	/**
	 * The variable is public for providing users who want to employ Sparksee
	 * database directly.
	 */
	public com.sparsity.sparksee.gdb.Graph g;
	Sparksee sparksee;
	int nodeType;
	int edgeType;
	int vid;
	int lat;
	int lon;
	int fixed;
	private final long INVALID;

	Value val;

	/**
	 * @param config
	 *            is the configuration file for the usage of Sparksee, see
	 *            Sparksee or elif.eser.bilkent.edu.tr/roadnetwork for
	 *            information.
	 * @param graphDir
	 *            is the road network data file provided at
	 *            elif.eser.bilkent.edu.tr/roadnetwork
	 *
	 * @throws FileNotFoundException
	 */
	public Graph(String config, String graphDir) throws FileNotFoundException {

		/** configuration file of Sparksee */
		SparkseeProperties.load(config);
		SparkseeConfig cfg = new SparkseeConfig();
		sparksee = new Sparksee(cfg);
		/** database file written in Sparksee */
		db = sparksee.open(graphDir, true);// read only
		sess = db.newSession();
		g = sess.getGraph();
		val = new Value();
		nodeType = g.findType("GEO_LOC");
		edgeType = g.findType("EDGE");
		vid = g.findAttribute(nodeType, "vid");
		lat = g.findAttribute(nodeType, "lat");
		lon = g.findAttribute(nodeType, "lon");
		fixed = g.findAttribute(edgeType, "fixed");
		INVALID = Objects.InvalidOID;
	}

	@Override
	public Vertex getVertexOf(int vID) throws NoVIDFoundException {
		long fromV;

		fromV = g.findObject(vid, val.setInteger(vID));
		if (fromV == INVALID)
			throw new NoVIDFoundException();
		Vertex v;
		v = new Vertex(g.getAttribute(fromV, lat).getDouble(), g.getAttribute(fromV, lon).getDouble(),
				g.getAttribute(fromV, vid).getInteger());
		return v;
	}

	@Override
	public Vertex getVertexOf(double latitude, double longitude) throws OutOfBoundaryException {
		if (latitude < 45.33 || latitude > 45.58 || longitude < 8.94 || longitude > 9.38)
			throw new OutOfBoundaryException();
		Objects latEquality = g.select(lat, Condition.Equal, val.setDouble(latitude));
		Objects longEquality = g.select(lon, Condition.Equal, val.setDouble(longitude));
		Objects intersect = Objects.combineIntersection(latEquality, longEquality);
		latEquality.close();
		longEquality.close();

		if (intersect.size() == 0)
			return null;
		Vertex v;
		ObjectsIterator iterator = intersect.iterator();
		long fromV = iterator.nextObject();
		v = new Vertex(g.getAttribute(fromV, lat).getDouble(), g.getAttribute(fromV, lon).getDouble(),
				g.getAttribute(fromV, vid).getInteger());
		intersect.close();

		return v;
	}

	@Override
	public Vertex closestVertexTo(double latitude, double longtitude) throws OutOfBoundaryException {
		if (latitude < 43.33 || latitude > 45.58 || longtitude < 8.94 || longtitude > 9.38)
			throw new OutOfBoundaryException();
		Vertex v = null;
		double min = Double.POSITIVE_INFINITY, dist, olat, olong;
		Objects vertices = g.select(nodeType);
		for (long ver : vertices) {
			olat = g.getAttribute(ver, lat).getDouble();
			olong = g.getAttribute(ver, lon).getDouble();

			dist = distFrom(olat, olong, latitude, longtitude);
			if (dist < min) {
				v = new Vertex(olat, olong, g.getAttribute(ver, vid).getInteger());
			}
		}

		vertices.close();
		return v;
	}

	@Override
	public Edge edgeBetween(int startVertex, int endVertex) throws NoVIDFoundException {
		long fromV, toV;
		long rel = 0;

		fromV = g.findObject(vid, val.setInteger(startVertex));
		toV = g.findObject(vid, val.setInteger(endVertex));
		rel = g.findEdge(edgeType, fromV, toV);

		if (fromV == INVALID || toV == INVALID)
			throw new NoVIDFoundException();
		if (rel == INVALID)
			return null;

		Edge edge = new Edge(startVertex, endVertex);
		edge.setDaily(getDailyAll(rel));
		Value fixedVal = g.getAttribute(rel, fixed);
		if (!fixedVal.isNull()) {
			edge.setFixed(fixedVal.getDouble());
		}
		return edge;
	}

	@Override
	public Edge edgeBetween(Vertex startVertex, Vertex endVertex) throws NoVIDFoundException {
		return edgeBetween(startVertex.getvID(), endVertex.getvID());
	}

	@Override
	public List<Vertex> giveNeighbors(Vertex v) throws NoVIDFoundException {
		return giveNeighbors(v.getvID());
	}

	@Override
	public List<Vertex> giveNeighbors(int vID) throws NoVIDFoundException {
		return giveNeighbors(vID, DBOp.Direction.Outgoing);
	}

	private double[] getDailyAll(long rel) {
		double[] vals = new double[288];
		int dailyAtt;
		Value v;
		for (int i = 0; i < vals.length; i++) {
			dailyAtt = g.findAttribute(edgeType, "daily" + i);
			v = g.getAttribute(rel, dailyAtt);
			vals[i] = v.getDouble();
		}
		return vals;
	}

	@Override
	public List<Edge> incomingEdgesOf(Vertex v) throws NoVIDFoundException {

		return incomingEdgesOf(v.getvID());
	}

	@Override
	public List<Edge> outgoingEdgesOf(Vertex v) throws NoVIDFoundException {
		return outgoingEdgesOf(v.getvID());
	}

	@Override
	public List<Edge> incomingEdgesOf(int vID) throws NoVIDFoundException {
		return edgesOf(vID, Direction.Ingoing);

	}

	@Override
	public List<Edge> edgesOf(int vID) throws NoVIDFoundException {
		return edgesOf(vID, Direction.Any);
	}

	@Override
	public List<Edge> edgesOf(Vertex v) throws NoVIDFoundException {
		return edgesOf(v.getvID());
	}

	private List<Edge> edgesOf(int vID, Direction dir) throws NoVIDFoundException {
		long fromV;
		fromV = g.findObject(vid, val.setInteger(vID));
		List<Edge> list;
		if (fromV == INVALID) // check whether it is true or not
			throw new NoVIDFoundException();
		list = new ArrayList<Edge>();

		Objects rels = g.neighbors(fromV, edgeType, EdgesDirection.valueOf(dir.toString()));

		ObjectsIterator iterator = rels.iterator();
		Edge edge = null;

		long other, edge1, edge2;
		Value fixedVal;
		while (iterator.hasNext()) {
			other = iterator.nextObject();
			edge1 = g.findEdge(edgeType, fromV, other);
			edge2 = g.findEdge(edgeType, other, fromV);
			if (dir.equals(Direction.Any)) {

				if (edge1 != INVALID) {
					edge = new Edge(g.getAttribute(fromV, vid).getInteger(), g.getAttribute(other, vid).getInteger());
					edge.setDaily(getDailyAll(edge1));
					fixedVal = g.getAttribute(edge1, fixed);
					if (!fixedVal.isNull()) {
						edge.setFixed(fixedVal.getDouble());
					}

					list.add(edge);
				}
				if (edge2 != INVALID) {
					edge = new Edge(g.getAttribute(other, vid).getInteger(), g.getAttribute(fromV, vid).getInteger());
					edge.setDaily(getDailyAll(edge2));
					fixedVal = g.getAttribute(edge2, fixed);
					if (!fixedVal.isNull()) {
						edge.setFixed(fixedVal.getDouble());
					}

					list.add(edge);
				}

			} else if (dir.equals(Direction.Ingoing)) {
				if (edge2 != INVALID) {
					edge = new Edge(g.getAttribute(other, vid).getInteger(), g.getAttribute(fromV, vid).getInteger());
					edge.setDaily(getDailyAll(edge2));
					fixedVal = g.getAttribute(edge2, fixed);
					if (!fixedVal.isNull()) {
						edge.setFixed(fixedVal.getDouble());
					}
					list.add(edge);
				}
			} else {
				if (edge1 != INVALID) {

					edge = new Edge(g.getAttribute(fromV, vid).getInteger(), g.getAttribute(other, vid).getInteger());
					edge.setDaily(getDailyAll(edge1));
					// control
					fixedVal = g.getAttribute(edge1, fixed);
					if (!fixedVal.isNull()) {
						edge.setFixed(fixedVal.getDouble());
					}

					list.add(edge);
				}
			}
			// System.out.println(edgeLong);

		}

		rels.close();
		// unless no edge found
		return list;

	}

	@Override
	public List<Edge> outgoingEdgesOf(int vID) throws NoVIDFoundException {
		return edgesOf(vID, Direction.Outgoing);
	}

	@Override
	public Iterable<Vertex> allVertices() {
		Objects all = g.select(nodeType);

		ArrayList<Vertex> list = new ArrayList<Vertex>();
		Vertex v;
		ObjectsIterator iterator = all.iterator();
		long vertex;
		while (iterator.hasNext()) {

			vertex = iterator.nextObject();
			v = new Vertex(g.getAttribute(vertex, lat).getDouble(), g.getAttribute(vertex, lon).getDouble(),
					g.getAttribute(vertex, vid).getInteger());

			list.add(v);
		}
		all.close();
		return list;
	}

	@Override
	public Iterable<Edge> allEdges() {
		Objects all = g.select(edgeType);

		ArrayList<Edge> list = new ArrayList<Edge>();
		Edge edge;
		ObjectsIterator iterator = all.iterator();
		EdgeData edgeData;
		while (iterator.hasNext()) {
			edgeData = g.getEdgeData(iterator.nextObject());
			edge = new Edge(g.getAttribute(edgeData.getHead(), vid).getInteger(),
					g.getAttribute(edgeData.getTail(), vid).getInteger());
			edge.setDaily(getDailyAll(edgeData.getEdge()));
			Value fixedVal = g.getAttribute(edgeData.getEdge(), fixed);
			if (!fixedVal.isNull()) {
				edge.setFixed(fixedVal.getDouble());
			}
			list.add(edge);
		}
		all.close();
		return list;
	}

	private double distFrom(double lat1, double long1, double lat2, double long2) {
		double dist;
		double radLat1 = Math.toRadians(lat1);
		double radLat2 = Math.toRadians(lat2);

		double R = 6371000;
		dist = Math.acos(Math.sin(radLat1) * Math.sin(radLat2)
				+ Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(Math.toRadians(long1 - long2))) * R;
		return dist;
	}

	protected List<Vertex> completeTrajectory(List<Edge> edgeList) throws NoVIDFoundException {
		ArrayList<Vertex> vList = new ArrayList<Vertex>();
		vList.add(getVertexOf(edgeList.get(0).getInitial()));
		Path p;
		for (int i = 1; i < edgeList.size(); i++) {

			// previous one is added
			if (edgeList.get(i - 1).getLast() == edgeList.get(i).getInitial())
			// if it is properly followed
			{
				vList.add(getVertexOf(edgeList.get(i).getInitial()));
			} else {
				p = getShortestPath(edgeList.get(i - 1).getLast(), edgeList.get(i).getInitial());
				vList.addAll(p.getVertexList());
			}

		}
		return vList;
	}

	@Override
	public double calculateTravelTime(List<Vertex> list, Time dTime) throws NoVIDFoundException {
		double ttime = 0;
		Edge e;
		for (int i = 1; i < list.size(); i++) {
			e = edgeBetween(list.get(i - 1), list.get(i));
			ttime += e.getTravelTime(dTime);
			dTime = dTime.add(e.getTravelTime(dTime));
		}
		return ttime;
	}

	@Override
	public double calculateTravelTimeFixed(List<Vertex> list) throws NoVIDFoundException {
		double ttime = 0;
		Edge e;
		for (int i = 1; i < list.size(); i++) {
			e = edgeBetween(list.get(i - 1), list.get(i));
			ttime += e.getTravelTime();

		}
		return ttime;
	}

	/**
	 * @return A path having minimum distance between the given vertices
	 * @exception Invalid
	 *                vertex ID
	 */
	@Override
	public Path getShortestPath(Vertex start, Vertex end) throws NoVIDFoundException {

		return getShortestPath(start.getvID(), end.getvID());
	}

	@Override
	public Path getShortestPath(int start, int end) throws NoVIDFoundException {
		Path p = null;
		// new Path(); //bunu oath bulunursa yapcaz
		HashMap<Integer, Double> distanceMap = new HashMap<Integer, Double>();
		HashMap<Integer, Integer> parentMap = new HashMap<Integer, Integer>();

		distanceMap.put(start, 0.0);
		parentMap.put(start, null);
		ArrayList<Integer> discovered = new ArrayList<Integer>();

		Tuple<Integer, Double> min;
		List<Edge> neighborRels;// bunun int versiyonunu tut
		boolean found = false;
		// long time=System.currentTimeMillis();
		// &&System.currentTimeMillis()-time<120000
		while ((distanceMap.size() != discovered.size()))
		// if all are discovered
		{
			min = extractMin(distanceMap, discovered);
			// System.out.println(min+" min");
			if (min.getS() == end) {
				found = true;
				break;
			}

			discovered.add(min.getS());
			neighborRels = outgoingEdgesOf(min.getS());
			// System.out.println(neighborRels.size());
			for (Edge e : neighborRels) {
				if (!discovered.contains(e.getLast())
						&& getThePriorityIndex(distanceMap, e.getLast()) > min.getT() + e.getTravelTime()) {

					distanceMap.put(e.getLast(), min.getT() + e.getTravelTime());
					parentMap.put(e.getLast(), min.getS());

				}
			}

		}

		if (found) {

			Integer parent = end;

			LinkedList<Vertex> list = new LinkedList<Vertex>();
			while (parent != null) {
				list.addFirst(getVertexOf(parent));
				parent = parentMap.get(parent);

			}

			p = new Path(list, distanceMap.get(end));

		}
		return p;
	}

	@Override
	public Path getShortestPath(Vertex start, Vertex end, Time dtime) throws Exception {
		return getShortestPath(start.getvID(), end.getvID(), dtime);
	}

	@Override
	public Path getShortestPath(int start, int end, Time dtime) throws Exception {
		Path p = null;
		// new Path(); //bunu oath bulunursa yapcaz
		HashMap<Integer, Double> distanceMap = new HashMap<Integer, Double>();
		HashMap<Integer, Integer> parentMap = new HashMap<Integer, Integer>();

		distanceMap.put(start, 0.0);
		parentMap.put(start, null);
		ArrayList<Integer> discovered = new ArrayList<Integer>();

		Tuple<Integer, Double> min;
		List<Edge> neighborRels;// bunun int versiyonunu tut
		boolean found = false;
		long time = System.currentTimeMillis();

		while (distanceMap.size() != discovered.size() && System.currentTimeMillis() - time < 300000)
		// if all are discovered
		{
			min = extractMin(distanceMap, discovered);
			if (min.getS() == end) {
				found = true;
				break;
			}

			discovered.add(min.getS());
			neighborRels = outgoingEdgesOf(min.getS());
			for (Edge e : neighborRels) {
				if (!discovered.contains(e.getLast()) && getThePriorityIndex(distanceMap, e.getLast()) > min.getT()
						+ e.getTravelTime(dtime.add(min.getT()))) {
					distanceMap.put(e.getLast(), min.getT() + e.getTravelTime(dtime.add(min.getT())));
					parentMap.put(e.getLast(), min.getS());

				}
			}

		}

		if (found) {

			Integer parent = end;

			LinkedList<Vertex> list = new LinkedList<Vertex>();
			while (parent != null) {
				list.addFirst(getVertexOf(parent));
				parent = parentMap.get(parent);

			}

			p = new Path(list, distanceMap.get(end));

		}
		return p;
	}

	private double getThePriorityIndex(HashMap<Integer, Double> distanceMap, int vertex) {
		Double d = distanceMap.get(vertex);
		if (d == null)
			return Double.POSITIVE_INFINITY;
		else
			return d.doubleValue();

	}

	private Tuple<Integer, Double> extractMin(HashMap<Integer, Double> distanceMap, ArrayList<Integer> discovered) {
		int minVertex = -1;// if there is no vertex

		double min = Double.POSITIVE_INFINITY;
		Tuple<Integer, Double> val = new Tuple<Integer, Double>(minVertex, min);
		for (int v : distanceMap.keySet()) {
			if (min > distanceMap.get(v) && !discovered.contains(v)) {
				minVertex = v;
				min = distanceMap.get(v);
				val = new Tuple<Integer, Double>(minVertex, min);
			}
		}

		return val;
	}

	@Override
	public Path getShortestPath(double lat1, double long1, double lat2, double long2)
			throws NoVIDFoundException, OutOfBoundaryException {
		if (lat1 < 45.33 || lat1 > 45.58 || long1 < 8.94 || long1 > 9.38 || lat2 < 45.33 || lat2 > 45.58 || long2 < 8.94
				|| long2 > 9.38)
			throw new OutOfBoundaryException();
		return getShortestPath(getVertexOf(lat1, long1), getVertexOf(lat2, long2));
	}

	@Override
	public List<Vertex> giveNeighbors(Vertex v, DBOp.Direction direction) throws NoVIDFoundException {

		return giveNeighbors(v.getvID(), direction);
	}

	@Override
	public List<Vertex> giveNeighbors(int vID, Direction dir) throws NoVIDFoundException {
		long fromV;
		fromV = g.findObject(vid, val.setInteger(vID));
		List<Vertex> list;
		if (fromV == INVALID) // check whether it is true or not
			throw new NoVIDFoundException();
		list = new ArrayList<Vertex>();

		Objects rels = g.neighbors(fromV, edgeType, EdgesDirection.valueOf(dir.toString()));
		ObjectsIterator iterator = rels.iterator();

		long other;
		while (iterator.hasNext()) {
			other = iterator.nextObject();

			list.add(new Vertex(g.getAttribute(other, lat).getDouble(), g.getAttribute(other, lon).getDouble(),
					g.getAttribute(other, vid).getInteger()));

		}

		rels.close();
		// unless no edge found
		return list;

	}

	@Override
	public int countNodes() {
		return (int) g.countNodes();
	}

	@Override
	public int countEdges() {
		Objects edges = g.select(edgeType);
		int size = edges.size();
		edges.close();
		return size;
	}

	@Override
	public void close() {
		sess.close();
		db.close();
		sparksee.close();

	}

}

package Experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import ConstraintBased.ConstraintBasedDijkstra;
import ConstraintBased.DijkstraConstraintBased;
import DataStructures.EdgeTime;
import edu.asu.emit.qyan.alg.control.DijkstraShortestPathAlg;
import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.Pair;
import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.TimeBasedVariableGraph2;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

//the updated and fixed one
//Includes the two heuristics with top k replacement and dijkstra based 
public class TopkImprovementReplaceHeuristic {

	Map<Pair<Integer, Integer>, int[]> load;
	List<Path> infeasible;
	Graph graph;

	// this code assumes that every edge has at least 1 capacity, otherwise
	// cannot catch the congestion
	public TopkImprovementReplaceHeuristic(Graph graph) {
		this.graph = graph;
		load = new HashMap<Pair<Integer, Integer>, int[]>();
	}

	public TopkImprovementReplaceHeuristic(TimeBasedVariableGraph2 graph) {
		this.graph = graph;
		load = new HashMap<Pair<Integer, Integer>, int[]>();
	}

	public TopkImprovementReplaceHeuristic(Graph graph, Map<Pair<Integer, Integer>, int[]> load) {
		this.graph = graph;
		this.load = load;
	}

	public int getInfeasibleSize() {
		return (infeasible == null) ? 0 : infeasible.size();
	}

	// parameter keeping
	// graph and load matrix is given already
	public List<Path> pathReplacementHeuristic(List<Pair<Integer, Integer>> queryList, int tstart) {
		List<Path> pathList = new ArrayList<Path>();

		DijkstraShortestPathAlg dij = new DijkstraShortestPathAlg(graph);

		
		Map<Pair<Integer, Integer>, Map<Path, Map<Integer, Object>>> invertedList = new HashMap<Pair<Integer, Integer>, Map<Path, Map<Integer, Object>>>();

		for (Pair<Integer, Integer> query : queryList) {
			Path sp = dij.get_shortest_path(graph.get_vertex(query.o1), graph.get_vertex(query.o2));
			pathList.add(sp);
			// load is another parameter
			update(sp, invertedList, tstart);
		}

		HashMap<Pair<Integer, Integer>, Path> problematicPaths = new HashMap<Pair<Integer, Integer>, Path>();

		HashMap<EdgeTime, HashMap<Path, Object>> congestedEdges = new HashMap<EdgeTime, HashMap<Path, Object>>();
		HashMap<EdgeTime, HashMap<Path, Object>> fullEdges = new HashMap<EdgeTime, HashMap<Path, Object>>();

		Map<Path, Map<Integer, Object>> mapPathToTime;
		for (Pair<Integer, Integer> edge : load.keySet()) {
			// inverted list edge versus path: residing time
			mapPathToTime = invertedList.get(edge);
			
			for (int t = tstart; t < load.get(edge).length; t++) {
				if (load.get(edge)[t] > graph.getCapacityOf(edge)) {
					for (Path path : mapPathToTime.keySet()) {
						for (int incT : mapPathToTime.get(path).keySet()) {
							// a path lays on edge at i and not already added,
							// not
							// necessarily it s a set
							if (incT == t) {
								// scan all paths in the inverted list of that edge
								// find all the paths related to that problematic t								
								// add to congested edge paths
								problematicPaths.put(new Pair<Integer, Integer>(path.get_vertices().get(0).get_id(),
										path.get_vertices().get(path.getVertexSize() - 1).get_id()), path);
								EdgeTime congestion = new EdgeTime(edge.o1, edge.o2, t);
								HashMap<Path, Object> congestedPathOverT = congestedEdges.get(congestion);
								if (congestedPathOverT == null) {
									congestedPathOverT = new HashMap<Path, Object>();
									congestedEdges.put(congestion, congestedPathOverT);
								}
								
								congestedPathOverT.put(path, null);
							}
						}
					}

				} else if (load.get(edge)[t] == graph.getCapacityOf(edge)) {
					
					if (mapPathToTime == null) {
						EdgeTime full = new EdgeTime(edge.o1, edge.o2, t);
						fullEdges.put(full, new HashMap<Path, Object>());
					} else {
						for (Path path : mapPathToTime.keySet()) {
							// a path lays on edge at i and not already added,
							// not necessarily it s a set
							if (mapPathToTime.get(path).containsKey(t)) {
								EdgeTime full = new EdgeTime(edge.o1, edge.o2, t);
								HashMap<Path, Object> fullOverT = fullEdges.get(full);
								if (fullOverT == null) {
									fullOverT = new HashMap<Path, Object>();
									fullEdges.put(full, fullOverT);
								}
								
								fullOverT.put(path, null);
							}
						}
					}
				}
			}
		}
		
		if (congestedEdges.isEmpty())
			return pathList;
		// for selecting alternative paths
		PriorityQueue<Path> heap = new PriorityQueue<Path>();
		HashMap<EdgeTime, HashMap<Path, Object>> specializedFull;
		// Line 19 to 24
		for (Pair<Integer, Integer> query : problematicPaths.keySet()) {
			specializedFull = new HashMap<EdgeTime, HashMap<Path, Object>>();
			for (EdgeTime fullEdgeTime : fullEdges.keySet()) {
				// if the path is not in the edge time pairs problematic path list
				if (!fullEdges.get(fullEdgeTime).containsKey(problematicPaths.get(query))) {
					// check whether it needs to be newly added to the fullEdges after update. 
					specializedFull.put(fullEdgeTime, fullEdges.get(fullEdgeTime));
				}

			}
			// line 21 define constraints
			specializedFull.putAll(congestedEdges);
			
			Path newPath = constraintBased(graph.get_vertex(query.o1), graph.get_vertex(query.o2), specializedFull,
					tstart);
			if (newPath == null) {
				if (infeasible == null)
					infeasible = new ArrayList<Path>();
				// put shortest paths which are infeasible
				infeasible.add(problematicPaths.get(query));
				backtrace(problematicPaths.get(query), invertedList, fullEdges, congestedEdges, tstart);
			}

			else {

				// assign delta
				newPath.setDelta((newPath.get_weight() - problematicPaths.get(query).get_weight())
						/ problematicPaths.get(query).get_weight());
				heap.add(newPath);
				
			}
		}

		// TODO if no congestion, do not do this, or it will give
		// exception
		// now repeat until phase
		do {
			
			Path minPath = heap.poll();// takes min
			System.err.println(minPath);
			
			Path psd = problematicPaths.get(new Pair<Integer, Integer>(minPath.get_vertices().get(0).get_id(),
					minPath.get_vertices().get(minPath.getVertexSize() - 1).get_id()));

			// line 27
			pathList.remove(psd);
			pathList.add(minPath);
			// maybe useful for detecting which path will be affected
			HashMap<EdgeTime, HashMap<Path, Object>> oldConstraints = update(psd, minPath, invertedList, congestedEdges,
					fullEdges, tstart);

			if (congestedEdges.isEmpty()) {
				// now terminated
				break;
			}
			
			updatePaths(oldConstraints, invertedList, congestedEdges, fullEdges, heap, problematicPaths, tstart);

		} while (!heap.isEmpty());

		return pathList;
	}

	private void backtrace(Path psd, Map<Pair<Integer, Integer>, Map<Path, Map<Integer, Object>>> invertedList,
			HashMap<EdgeTime, HashMap<Path, Object>> fullEdges, HashMap<EdgeTime, HashMap<Path, Object>> congestedEdges,
			int tstart) {
		int count = tstart;
		for (int i = 0; i < psd.getVertexSize() - 1; i++) {
			Pair<Integer, Integer> edge = new Pair<Integer, Integer>(psd.get_vertices().get(i).get_id(),
					psd.get_vertices().get(i + 1).get_id());
			invertedList.get(edge).remove(psd);
			for (int t = count; t < count + graph.get_edge_weight(edge); t++) {
				load.get(edge)[t]--;

				// can only be equal
				EdgeTime pair = new EdgeTime(edge.o1, edge.o2, t);
				if (congestedEdges.containsKey(pair) && load.get(edge)[t] == graph.getCapacityOf(edge)) {
					// now eliminate from congestededges add to fulledges
					fullEdges.put(pair, congestedEdges.get(pair));
					congestedEdges.remove(pair);
					// if not congested any more add it to the fullEdges list.
				} else if (fullEdges.containsKey(pair) && load.get(edge)[t] < graph.getCapacityOf(edge)) {
					// if a full edge is not full any more, remove it from full edge list
					fullEdges.remove(pair);
				}

			}
			count += graph.get_edge_weight(edge);

		}

	}

	private void updatePaths(HashMap<EdgeTime, HashMap<Path, Object>> oldConstraints,
			Map<Pair<Integer, Integer>, Map<Path, Map<Integer, Object>>> invertedList,
			HashMap<EdgeTime, HashMap<Path, Object>> congestedEdges, HashMap<EdgeTime, HashMap<Path, Object>> fullEdges,
			PriorityQueue<Path> heap, HashMap<Pair<Integer, Integer>, Path> problematicPaths, int tstart) {
		// if no change dont change the any thing, a little bit lose because of
		// full edges maybe
		// nthing has changed
		if (oldConstraints.isEmpty())
			return;
		// still needs alternatives
		HashMap<Pair<Integer, Integer>, Object> pathlist = new HashMap<Pair<Integer, Integer>, Object>();

		// System.out.println(congestedEdges.size() + " new congested");
		for (EdgeTime pair : congestedEdges.keySet()) {
			// System.err.println(pair);
			for (Path edgepaths : congestedEdges.get(pair).keySet()) {
				// System.out.print(edgepaths);
				pathlist.put(new Pair<Integer, Integer>(edgepaths.get_vertices().get(0).get_id(),
						edgepaths.get_vertices().get(edgepaths.getVertexSize() - 1).get_id()), null);
			}

		}

		// continue with the following list
		List<Path> heapPaths = new ArrayList<Path>();
		
		Iterator<Path> heapIterator = heap.iterator();
		Path temp;

		while (heapIterator.hasNext()) {
			temp = heapIterator.next();
			
			if (pathlist.containsKey(new Pair<Integer, Integer>(temp.get_vertices().get(0).get_id(),
					temp.get_vertices().get(temp.getVertexSize() - 1).get_id())))
				heapPaths.add(temp);
		}

		HashMap<EdgeTime, HashMap<Path, Object>> specializedFull;

		// clear and put again
		heap.clear();
		for (Path heapPathOld : heapPaths) {
			specializedFull = new HashMap<EdgeTime, HashMap<Path, Object>>();
			for (Entry<EdgeTime, HashMap<Path, Object>> fullEdgePaths : fullEdges.entrySet()) {
				if (!fullEdgePaths.getValue().containsKey(heapPathOld)) {
					
					specializedFull.put(fullEdgePaths.getKey(), fullEdgePaths.getValue());
				}

			}
			// line 21 define constraints
			specializedFull.putAll(congestedEdges);
			Path newPath = constraintBased(heapPathOld.get_vertices().get(0),
					heapPathOld.get_vertices().get(heapPathOld.getVertexSize() - 1), specializedFull, tstart);
			if (newPath == null) {
				if (infeasible == null)
					infeasible = new ArrayList<Path>();
				
				Pair<Integer, Integer> query = new Pair<Integer, Integer>(heapPathOld.get_vertices().get(0).get_id(),
						heapPathOld.get_vertices().get(heapPathOld.getVertexSize() - 1).get_id());
				infeasible.add(problematicPaths.get(query));
				backtrace(problematicPaths.get(query), invertedList, fullEdges, congestedEdges, tstart);
			} else {
				// assign delta
				double weightSP = problematicPaths
						.get(new Pair<Integer, Integer>(heapPathOld.get_vertices().get(0).get_id(),
								heapPathOld.get_vertices().get(heapPathOld.getVertexSize() - 1).get_id()))
						.get_weight();
				newPath.setDelta((newPath.get_weight() - weightSP) / weightSP);
				heap.add(newPath);
				
			}
		}
		
	}

	public void printLoad() {
		for (Pair<Integer, Integer> pair : load.keySet()) {
			System.out.print(pair + "\t");
			for (int t : load.get(pair))
				System.out.print(t + " ");
			System.out.println();
		}
	}

	private HashMap<EdgeTime, HashMap<Path, Object>> update(Path psd, Path minPath,
			Map<Pair<Integer, Integer>, Map<Path, Map<Integer, Object>>> invertedList,
			HashMap<EdgeTime, HashMap<Path, Object>> congestedEdges, HashMap<EdgeTime, HashMap<Path, Object>> fullEdges,
			int tstart) {
		// we need inverted lists because: if an edge is full, we need to get its paths
		if (psd.equals(minPath))
			return new HashMap<EdgeTime, HashMap<Path, Object>>();
		// returns old edgetime
		// update load full and congested edges
		// deprecated congestions
		HashMap<EdgeTime, HashMap<Path, Object>> deprecatedCongestions = new HashMap<EdgeTime, HashMap<Path, Object>>();
		int count = tstart;

		for (int i = 0; i < psd.getVertexSize() - 1; i++) {
			Pair<Integer, Integer> edge = new Pair<Integer, Integer>(psd.get_vertices().get(i).get_id(),
					psd.get_vertices().get(i + 1).get_id());
			invertedList.get(edge).remove(psd);
			for (int t = count; t < count + graph.get_edge_weight(edge); t++) {
				// remove from load and inverted lists
				load.get(edge)[t]--;

				// can only be equal
				EdgeTime pair = new EdgeTime(edge.o1, edge.o2, t);
				if (congestedEdges.containsKey(pair) && load.get(edge)[t] == graph.getCapacityOf(edge)) {
					// now eliminate from congestededges add to fulledges
					deprecatedCongestions.put(pair, congestedEdges.get(pair));
					fullEdges.put(pair, congestedEdges.get(pair));
					congestedEdges.remove(pair);
					
				} else if (fullEdges.containsKey(pair) && load.get(edge)[t] < graph.getCapacityOf(edge)) {
					
					fullEdges.remove(pair);
				}

			}
			count += graph.get_edge_weight(edge);

		}
		Pair<Integer, Integer> edge;
		count = tstart;
		for (int i = 0; i < minPath.getVertexSize() - 1; i++) {
			edge = new Pair<Integer, Integer>(minPath.get_vertices().get(i).get_id(),
					minPath.get_vertices().get(i + 1).get_id());
			Map<Path, Map<Integer, Object>> listOfEdge = invertedList.get(edge);
			Map<Integer, Object> timeMap = new HashMap<Integer, Object>();
			// if it is not in the invertedlist, put it
			if (listOfEdge == null) {
				listOfEdge = new HashMap<Path, Map<Integer, Object>>();
				invertedList.put(edge, listOfEdge);
			}

			listOfEdge.put(minPath, timeMap);
			int[] loadOfEdge = load.get(edge);
			int max = count + (int) graph.get_edge_weight(edge);
			if (loadOfEdge == null) {
				loadOfEdge = new int[max];
				load.put(edge, loadOfEdge);
			}
			if (loadOfEdge.length < max) {
				loadOfEdge = Arrays.copyOf(loadOfEdge, max + 50);
				load.put(edge, loadOfEdge);
			}

			// put it along the weight of edge
			// update load matrix from given first to second
			for (int t = count; t < count + graph.get_edge_weight(edge); t++) {
				loadOfEdge[t]++;
				timeMap.put(t, null);
				// constraint based does not allow to exceed the capacity, just control full edges
				if (loadOfEdge[t] == graph.getCapacityOf(edge)) {
					// full edge
					// int is newly join to the full
					EdgeTime pair = new EdgeTime(edge.o1, edge.o2, t);
					HashMap<Path, Object> list = new HashMap<Path, Object>();
					fullEdges.put(pair, list);
					for (Entry<Path, Map<Integer, Object>> pathT : invertedList.get(edge).entrySet()) {
						if (pathT.getValue().containsKey(t))
							list.put(pathT.getKey(), null);
					}
				}
			}
			count += graph.get_edge_weight(edge);
		}
		return deprecatedCongestions;

	}

	private Path constraintBased(BaseVertex sId, BaseVertex eId,
			HashMap<EdgeTime, HashMap<Path, Object>> specializedFull, int tstart) {
		ConstraintBasedDijkstra constBased = new ConstraintBasedDijkstra(graph);
		ArrayList<EdgeTime> consts = new ArrayList<EdgeTime>();
		// appropriate to constraint based
		for (EdgeTime edgetime : specializedFull.keySet()) {
			edgetime.setT(edgetime.getT() - tstart);
			consts.add(edgetime);
		}

		Path p = constBased.constraintBasedUpgraded(consts, sId, eId);
		for (EdgeTime edgetime : specializedFull.keySet()) {
			edgetime.setT(edgetime.getT() + tstart);
		}
		return p;
	}

	private void update(Path sp, Map<Pair<Integer, Integer>, Map<Path, Map<Integer, Object>>> invertedList, int t) {
		Pair<Integer, Integer> tempEdge;
		for (int i = 0; i < sp.getVertexSize() - 1; i++) {
			// for each vertex
			tempEdge = new Pair<Integer, Integer>(sp.get_vertices().get(i).get_id(),
					sp.get_vertices().get(i + 1).get_id());

			Map<Path, Map<Integer, Object>> listOfEdge = invertedList.get(tempEdge);
			HashMap<Integer, Object> timeMap = new HashMap<Integer, Object>();
			;

			// if it is not in the invertedlist, put it
			if (listOfEdge == null) {
				listOfEdge = new HashMap<Path, Map<Integer, Object>>();
				invertedList.put(tempEdge, listOfEdge);

			}
			// newly added
			listOfEdge.put(sp, timeMap);

			// put it along the weight of edge
			for (int j = t + (int) sp.get_vertices().get(i).get_weight(); j < t
					+ (int) sp.get_vertices().get(i + 1).get_weight(); j++)
				timeMap.put(j, null);

			// update load matrix from given first to second
			// the weights keep the arrival times
			updateLoad(tempEdge, t + (int) sp.get_vertices().get(i).get_weight(),
					t + (int) sp.get_vertices().get(i + 1).get_weight());
		}
	}

	private void updateLoad(Pair<Integer, Integer> tempEdge, int s, int d) {
		int[] loadOfEdge = load.get(tempEdge);
		if (loadOfEdge == null) {
			loadOfEdge = new int[d];
			load.put(tempEdge, loadOfEdge);
		}
		if (loadOfEdge.length < d) {
			loadOfEdge = Arrays.copyOf(loadOfEdge, d + 50);
			load.put(tempEdge, loadOfEdge);
		}
		// now ready to increase
		for (int i = s; i < d; i++) {
			loadOfEdge[i]++;
		}
	}

	public List<Path> getInfeasibles() {
		return infeasible;
	}

	public List<Path> dijkstraBasedHeuristic(List<Pair<Integer, Integer>> queryList, int tstart, String yens) {
		// if some is full, then put the full t s as constraint and search new.
		// in every iteration control the load matrix

		// while using dijsktra heuristic, use timebasedvariable graph
		List<Path> pathList = new ArrayList<Path>();
		Graph graph1 = new Graph(yens);
		DijkstraShortestPathAlg dij = new DijkstraShortestPathAlg(graph1);
		TimeBasedVariableGraph2 graph = (TimeBasedVariableGraph2) this.graph;
		Set<EdgeTime> constraints = getConstraints(tstart);
		System.out.println("dij constraints");
		for (EdgeTime e : constraints)
			System.err.println(e);
		DijkstraConstraintBased dijConstrainted = new DijkstraConstraintBased(graph);

		for (Pair<Integer, Integer> query : queryList) {
			graph.set_rem_edge_hashcode_set(constraints);
			Path newPath = dijConstrainted.get_shortest_path(graph.get_vertex(query.o1), graph.get_vertex(query.o2),
					tstart);

			if (newPath.getVertexSize() == 0) {
				if (infeasible == null)
					infeasible = new ArrayList<Path>();
				infeasible.add(dij.get_shortest_path(graph1.get_vertex(query.o1), graph1.get_vertex(query.o2)));
				System.out.println("here");
			} else {
				pathList.add(newPath);
				addConstraint(constraints, newPath, tstart);
			}
		}
		for (Path p : pathList)
			p.set_weight(p.get_weight() - tstart);
		return pathList;
	}

	private Set<EdgeTime> getConstraints(int tstart) {
		Set<EdgeTime> problematicEdges = new HashSet<EdgeTime>();
		for (Pair<Integer, Integer> edge : load.keySet()) {
			for (int t = tstart; t < load.get(edge).length; t++) {

				if (load.get(edge)[t] == graph.getCapacityOf(edge)) {
					// full edge
					// int is newly join to the full
					problematicEdges.add(new EdgeTime(edge.o1, edge.o2, t));

				}
			}
		}
		return problematicEdges;
	}

	private void addConstraint(Set<EdgeTime> willBeRenewedSet, Path newPath, int tstart) {
		Pair<Integer, Integer> edge;
		for (int i = 0; i < newPath.getVertexSize() - 1; i++) {
			edge = new Pair<Integer, Integer>(newPath.get_vertices().get(i).get_id(),
					newPath.get_vertices().get(i + 1).get_id());
			int[] loadOfEdge = load.get(edge);
			// here the weights includes the tstart because it starts with
			// tstart for the initial vertex
			int max = (int) newPath.get_vertices().get(i + 1).get_weight();
			if (loadOfEdge == null) {
				loadOfEdge = new int[max];
				load.put(edge, loadOfEdge);
			}
			if (loadOfEdge.length < max) {
				loadOfEdge = Arrays.copyOf(loadOfEdge, max + 50);
				load.put(edge, loadOfEdge);
			}
			for (int t = (int) newPath.get_vertices().get(i).get_weight(); t < max; t++) {
				load.get(edge)[t]++;


				if (load.get(edge)[t] == graph.getCapacityOf(edge)) {
					// full edge
					// int is newly join to the full

					willBeRenewedSet.add(new EdgeTime(edge.o1, edge.o2, t));
				}
			}
		}
	}

}

package DataStructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the implementation of path for the shortest path algorithms in
 * Graph class.
 * 
 * @see Graph
 * 
 */
public class Path implements Serializable {
	private List<Vertex> vertexList;
	private double travelTime;

	public List<Vertex> getVertexList() {
		return vertexList;
	}

	public void setVertexList(List<Vertex> vertexList) {
		this.vertexList = vertexList;
	}

	/** Returns the total travel time of the path. */
	public double getTravelTime() {
		return travelTime;
	}

	public Path(List<Vertex> vertexList, double travelTime) {
		super();
		this.vertexList = vertexList;
		this.travelTime = travelTime;
	}

	public Path() {
		super();
	}

	/**
	 * Returns the vertex ID lists of the path instead of Vertex objects
	 * including additional information such as latitude-longitude pair.
	 */
	public Iterable<Integer> simpleVertexList() {
		ArrayList<Integer> nList = new ArrayList<>();
		for (Vertex v : vertexList)
			nList.add(v.getvID());
		return nList;
	}

	@Override
	public String toString() {
		if (vertexList == null || vertexList.size() == 0)
			return "null";
		String s = "Travel time for the pair(" + vertexList.get(0) + "-" + vertexList.get(vertexList.size() - 1) + "):"
				+ String.valueOf(travelTime) + " ";
		for (Vertex v : vertexList)
			s += v.getvID() + ",";

		return s;
	}

	public Vertex source() {
		if (vertexList == null || vertexList.size() == 0)
			return new Vertex(-1, -1, -1);
		return vertexList.get(0);
	}

	public Vertex destination() {
		if (vertexList == null || vertexList.size() == 0)
			return new Vertex(-1, -1, -1);
		return vertexList.get(vertexList.size() - 1);
	}

	public int size() {
		return vertexList.size();
	}

	/**
	 * Returns whether vertex set of this Path object is the same as in the
	 * given Path.
	 * 
	 * @param oPath
	 *            a Path object to compare
	 */
	@Override
	public boolean equals(Object oPath) {
		Path p = (Path) oPath;
		if (p.size() != this.size())
			return false;
		for (int i = 0; i < size(); i++) {
			if (p.vertexList.get(i) != vertexList.get(i))
				return false;
		}
		return true;
	}

}

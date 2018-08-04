package DataStructures;

import java.util.ArrayList;
import java.util.List;

public class Trajectory {
	private int initial;
	private int last;
	private DateTime start;
	private double duration;
	private ArrayList<Tuple<Integer, Integer>> edgeList;
	private ArrayList<Integer> vertexList;
	private String id;

	public Trajectory() {
		edgeList = new ArrayList<Tuple<Integer, Integer>>();
		vertexList = new ArrayList<Integer>();
	}

	public Trajectory(String id, int initial, int last, DateTime start, double duration) {
		this.id = id;
		this.duration = duration;
		this.initial = initial;
		this.last = last;
		this.start = start;
		edgeList = new ArrayList<Tuple<Integer, Integer>>();
		vertexList = new ArrayList<Integer>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getInitial() {
		return initial;
	}

	public void setInitial(int initial) {
		this.initial = initial;
	}

	public int getLast() {
		return last;
	}

	public void setLast(int last) {
		this.last = last;
	}

	public DateTime getStart() {
		return start;
	}

	public void setStart(DateTime start) {
		this.start = start;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public ArrayList<Tuple<Integer, Integer>> getEdgeList() {
		return edgeList;
	}

	public void setEdgeList(ArrayList<Tuple<Integer, Integer>> edgeList) {
		this.edgeList = edgeList;
	}

	public ArrayList<Integer> getIntegerList() {
		return vertexList;
	}

	public Tuple<Integer, Integer> getEdge(int i) {
		return edgeList.get(i);
	}

	public void setVerIntegert(ArrayList<Integer> vertexList) {
		this.vertexList = vertexList;
	}

	public void addToVertexList(Integer v) {
		vertexList.add(v);
	}

	public void addToVertexList(List<Integer> list) {
		vertexList.addAll(list);
	}

	public void addEdge(Tuple<Integer, Integer> edge) {
		edgeList.add(edge);
	}

	private String vertexList() {
		String s = "";
		for (int v : vertexList)
			s = s.concat(v + " ");
		return s;
	}

	@Override
	public String toString() {

		return initial + "-" + last + ", start:" + start.toString() + ", duration: " + duration + "\n" + vertexList();
	}

}

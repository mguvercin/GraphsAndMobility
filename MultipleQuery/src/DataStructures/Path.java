package DataStructures;

import java.io.Serializable;
import java.util.ArrayList;

import DataStructures.Tuple;

import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

public class Path implements Comparable<Path>, Serializable {

	private ArrayList<Integer> vertexList;
	private Tuple<Integer, Integer> stPair;
	private int length;
	private final int uniqueID;
	private static int id = 0;
	private double distanceFromSP;// can be rate or exact difference
	private String date;

	/** from v to w */
	// TODO code and pseudo code should put the w to the end of the path
	// control
	public Path(int v, int w) {
		vertexList = new ArrayList<Integer>();
		stPair = new Tuple<Integer, Integer>(v, w);
		uniqueID = ++id;// assigned
	}

	// TODO a overload with getting v with the weight of the one and its
	// successor
	public void addVertexToHead(int v) {
		vertexList.add(0, v);
	}

	public void add(int v) {
		vertexList.add(v);
	}

	public void increaseLength(int w) {
		length += w;
	}

	public int size() {
		return vertexList.size();
	}

	public int get(int i) {
		return vertexList.get(i);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		// String s = "source target: " + stPair.getS() + " " + stPair.getT() +
		// "\n";
		// for (int el : vertexList)
		// s += el + "->";
		// s += "\n" + length;
		String s = "[";
		for (int el : vertexList)
			s += el + ",";
		s.substring(0, s.length() - 1);
		s += "]:" + length;
		return s;
	}

	public double getDistanceFromSP() {
		return distanceFromSP;
	}

	public int getUniqueID() {
		return uniqueID;
	}

	public void setDistanceFromSP(double distanceFromSP) {
		this.distanceFromSP = distanceFromSP;
	}

	public int getV() {
		return stPair.getS();
	}

	public int getW() {
		return stPair.getT();
	}

	@Override
	public int compareTo(Path o) {
		// TODO Auto-generated method stub
		if (this.distanceFromSP - o.distanceFromSP > 0)
			return 1;
		else if (this.distanceFromSP - o.distanceFromSP < 0)
			return -1;
		else
			return 0;
	}

	public Tuple<Integer, Integer> getSTPair() {
		return stPair;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}

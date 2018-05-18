package EdgeWeightedDigraph;

import java.util.ArrayList;

import DataStructures.Tuple;
import DataStructures.Path;

public class InvertedListForEdge {

	int t;
	private ArrayList<Path> arrList;

	public InvertedListForEdge(int t, Path p) {
		this.t = t;
		arrList = new ArrayList<Path>();
		arrList.add(p);
	}

	public int getLoad() {
		return arrList.size();
	}

	public void addQuery(Path st) {
		arrList.add(st);
	}

	@Override
	public boolean equals(Object obj) {
		return t == ((InvertedListForEdge) obj).t;
	}


	public ArrayList<Path> getArrList() {
		return arrList;
	}

	public void setArrList(ArrayList<Path> arrList) {
		this.arrList = arrList;
	}
//	
//	@Override
//	public int hashCode() {
//		// TODO Auto-generated method stub
//		return ((Integer) t).hashCode();
//	}
	
}

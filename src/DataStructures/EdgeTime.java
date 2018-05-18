package DataStructures;

import edu.asu.emit.qyan.alg.model.Pair;
import edu.asu.emit.qyan.alg.model.TripleDepricated;

public class EdgeTime implements Comparable<EdgeTime> {
	int start_id;
	int end_id;
	int t;

	public int getStart_id() {
		return start_id;
	}

	public void setStart_id(int start_id) {
		this.start_id = start_id;
	}

	public int getEnd_id() {
		return end_id;
	}

	public void setEnd_id(int end_id) {
		this.end_id = end_id;
	}

	public int getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

	public EdgeTime(int start_id, int end_id) {
		super();
		this.start_id = start_id;
		this.end_id = end_id;
	}

	public EdgeTime(int start_id, int end_id, int t) {
		super();
		this.start_id = start_id;
		this.end_id = end_id;
		this.t = t;
	}

	public Pair<Integer, Integer> sourceDest() {
		return new Pair<Integer, Integer>(start_id, end_id);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "s:" + start_id + " e:" + end_id + " t:" + t;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		EdgeTime other = (EdgeTime) obj;
		return start_id == other.start_id && end_id == other.end_id && t == other.t;
	}

	@Override
	public int hashCode() {
		return new String(start_id + " " + end_id + " " + t).hashCode();
		// int result = (int) (start_id ^ (start_id >>> 32));
		// result = 31 * result + (int) (end_id^ (end_id >>> 32));
		// result = 31 * result + (int) (t ^ (t >>> 32));
		// return result;
	}

	@Override
	public int compareTo(EdgeTime o) {
		if (this.t < o.t)
			return -1;
		else if (this.t > o.t)
			return 1;
		else
			return 0;
	}

}

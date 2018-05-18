package DataStructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Query implements Serializable {
	private int initial;
	private int last;
	private DateTime start;

	private String id;

	public Query() {

	}

	public Query(String id, int initial, int last, DateTime start) {
		this.id = id;
		this.initial = initial;
		this.last = last;
		this.start = start;
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

	@Override
	public String toString() {

		return id + ":" + initial + "-" + last + ", start:" + start.toString();
	}

}

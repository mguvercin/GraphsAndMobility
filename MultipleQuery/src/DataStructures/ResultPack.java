package DataStructures;

import edu.asu.emit.qyan.alg.model.Path;

public class ResultPack implements Comparable<ResultPack> {
	public int initial;
	public int last;
	public DateTimeModified start;

	public String id;

	public Path p;

	public ResultPack(int initial, int last, DateTimeModified start, String id, Path p) {
		super();
		this.initial = initial;
		this.last = last;
		this.start = start;
		this.id = id;
		this.p = p;
	}

	@Override
	public int compareTo(ResultPack o) {
		// TODO Auto-generated method stub
		double min = start.difference(o.start);
		if (min > 0)
			return 1;
		else if (min < 0)
			return -1;
		else
			return 0;
	}

}

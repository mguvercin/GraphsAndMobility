package DataStructures;

/**
 * This class is the implementation of the edge of the given network.
 * 
 * @version 1.0
 * @since August 2016
 */
public class Edge {
	/**
	 * The properties of an edge including the start and end vertex IDs, the static network weight, i.e, fixed, and
	 * time varying weights, i.e., daily.
	 */

	private int initial, last;
	private double[] daily;
	private double fixed;

	
	public Edge(int initial, int last) {
		this.initial = initial;
		this.last = last;
	}

	public Edge(int initial, int last, double[] daily) {
		super();
		this.initial = initial;
		this.last = last;
		this.daily = daily;

	}

	public Edge(int initial, int last, double[] daily, double fixed) {
		super();
		this.initial = initial;
		this.last = last;
		this.daily = daily;
		this.fixed = fixed;

	}

	public double getFixed() {
		return fixed;
	}

	public void setFixed(double fixed) {
		this.fixed = fixed;
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

	public double[] getDaily() {
		return daily;
	}

	public void setDaily(double[] daily) {
		this.daily = daily;
	}

	public double getTravelTime(Time dtime) {
		return daily[dtime.getTimeSlot()];
	}

	public double getTravelTime() {
		return fixed;
	}

	@Override
	public String toString() {

		return "s-d:" + initial + " " + last + "\t fixed=" + fixed + "\n daily weights:" + getDailyString();
	}

	private String getDailyString() {
		String s = "";
		for (double d : daily)
			s = s.concat(d + " ");

		return s;
	}
}

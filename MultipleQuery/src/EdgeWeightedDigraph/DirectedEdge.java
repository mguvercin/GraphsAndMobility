/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EdgeWeightedDigraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import DataStructures.PriorityQueueItem;
import IO.StdOut;

/*************************************************************************
 *  Compilation:  javac DirectedEdge.java
 *  Execution:    java DirectedEdge
 *
 *  Immutable weighted directed edge.
 *
 *************************************************************************/
/**
 * The <tt>DirectedEdge</tt> class represents a weighted edge in an
 * {@link EdgeWeightedDigraph}. Each edge consists of two integers (naming the
 * two vertices) and a real-value weight. The data type provides methods for
 * accessing the two endpoints of the directed edge and the weight.
 * <p>
 * For additional documentation, see
 * <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 * 
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
// TODO add wayID, oncesinde control whether one edge can have more than one
// wayID
public class DirectedEdge implements Serializable {
	private final int v;
	private final int w;
	private int travelTime;
	private int capacity;
	private int speed;
	private double length;
	private int[] timeDependentLoad = new int[100];

	// TODO bazý t anlarýnda, artýk onceki tlerle ilgili olan tum pathler
	// bittiyse t arrayini reduce et
	// kullanýlmayanlarý at

	/**
	 * Initializes a directed edge from vertex <tt>v</tt> to vertex <tt>w</tt>
	 * with the given <tt>weight</tt>.
	 * 
	 * @param v
	 *            the tail vertex
	 * @param w
	 *            the head vertex
	 * @param weight
	 *            the weight of the directed edge
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if either <tt>v</tt> or <tt>w</tt> is a negative integer
	 * @throws IllegalArgumentException
	 *             if <tt>weight</tt> is <tt>NaN</tt>
	 */
	public DirectedEdge(int v, int w, int weight, double length, int speed, int capacity) {
		if (v < 0)
			throw new IndexOutOfBoundsException("Vertex names must be nonnegative integers");
		if (w < 0)
			throw new IndexOutOfBoundsException("Vertex names must be nonnegative integers");
		if (Double.isNaN(weight))
			throw new IllegalArgumentException("Weight is NaN");
		this.v = v;
		this.w = w;
		this.travelTime = weight;
		this.capacity = capacity;
		this.length = length;
		this.speed = speed;
	}

	public DirectedEdge(int v, int w, int weight, int capacity) {
		if (v < 0)
			throw new IndexOutOfBoundsException("Vertex names must be nonnegative integers");
		if (w < 0)
			throw new IndexOutOfBoundsException("Vertex names must be nonnegative integers");
		if (Double.isNaN(weight))
			throw new IllegalArgumentException("Weight is NaN");
		this.v = v;
		this.w = w;
		this.travelTime = weight;
		this.capacity = capacity;
	}

	/**
	 * Returns the tail vertex of the directed edge.
	 * 
	 * @return the tail vertex of the directed edge
	 */
	public int from() {
		return v;
	}

	/**
	 * Returns the head vertex of the directed edge.
	 * 
	 * @return the head vertex of the directed edge
	 */
	public int to() {
		return w;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int[] getTimeDependentLoad() {
		return timeDependentLoad;
	}

	public void setTimeDependentLoad(int[] timeDependentLoad) {
		this.timeDependentLoad = timeDependentLoad;
	}

	public int getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(int travelTime) {
		this.travelTime = travelTime;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public void increaseAtTime(int t) {// could be think more efficient way but
										// not now
		if (t >= timeDependentLoad.length) {
			int[] newOne = Arrays.copyOf(timeDependentLoad, t + 100);
			timeDependentLoad = newOne;
			timeDependentLoad[t]++;
		} else
			timeDependentLoad[t]++;
	}

	// capacity is available at time t for this edge
	public boolean isIncrementAvailable(int t, double forTime) {// capacity nin
		// if (capacity >= 10)
		// capacity = capacity / 10;// to do more tight
		// orijinalinde
		// 0dan buyuk oldugunu assume
		// ediyorum
		if (t >= timeDependentLoad.length)
			return true;
		else if (t + forTime < timeDependentLoad.length) {
			for (int j = t; j < t + forTime; j++) {
				if (timeDependentLoad[j] >= capacity)
					return false;

			}
			return true;
		} else {
			for (int j = t; j < timeDependentLoad.length; j++)
				if (timeDependentLoad[j] >= capacity)
					return false;
			return true;
		}

	}

	/**
	 * Returns a string representation of the directed edge.
	 * 
	 * @return a string representation of the directed edge
	 */
	public String toString() {
		return v + "->" + w + " " + travelTime;
	}

	@Override
	public int hashCode() {
		return v ^ w;
	};

	/**
	 * Unit tests the <tt>DirectedEdge</tt> data type.
	 */
	public static void main(String[] args) {
		DirectedEdge e = new DirectedEdge(12, 23, 3, 2, 2, 2);
		ArrayList<PriorityQueueItem> s = new ArrayList<PriorityQueueItem>();
		s.add(new PriorityQueueItem(3, 2));
		s.add(new PriorityQueueItem(4, 1));
		System.out.println(s.get(0).getDistance());
		Collections.sort(s);
		System.out.println(s.get(0).getDistance());

		StdOut.println(e);
		e.increaseAtTime(101);
		e.increaseAtTime(4);
		boolean d = false;
		System.out.println(d);
		// System.out.println(e.timeDependentLoad[2]+" "
		// +e.timeDependentLoad[101]);
	}

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EdgeWeightedDigraph;

import DataStructures.Bag;
import IO.In;
import DataStructures.Stack;
import IO.StdOut;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/*************************************************************************
 *  Compilation:  javac EdgeWeightedDigraph.java
 *  Execution:    java EdgeWeightedDigraph V E
 *  Dependencies: Bag.java DirectedEdge.java
 *
 *  An edge-weighted digraph, implemented using adjacency lists.
 *
 *************************************************************************/

/**
 * The <tt>EdgeWeightedDigraph</tt> class represents a edge-weighted digraph of
 * vertices named 0 through <em>V</em> - 1, where each directed edge is of type
 * {@link DirectedEdge} and has a real-valued weight. It supports the following
 * two primary operations: add a directed edge to the digraph and iterate over
 * all of edges incident from a given vertex. It also provides methods for
 * returning the number of vertices <em>V</em> and the number of edges
 * <em>E</em>. Parallel edges and self-loops are permitted.
 * <p>
 * This implementation uses an adjacency-lists representation, which is a
 * vertex-indexed array of @link{Bag} objects. All operations take constant time
 * (in the worst case) except iterating over the edges incident from a given
 * vertex, which takes time proportional to the number of such edges.
 * <p>
 * For additional documentation, see <a
 * href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of <i>Algorithms,
 * 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 * 
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class ExtendedEdgeWeightedDigraph implements Serializable {
	private final int V;
	private int E;
	private Bag<ExtandableDirectedEdge>[] adj;

	/**
	 * Initializes an empty edge-weighted digraph with <tt>V</tt> vertices and 0
	 * edges. param V the number of vertices
	 * 
	 * @throws java.lang.IllegalArgumentException
	 *             if <tt>V</tt> < 0
	 */
	public ExtendedEdgeWeightedDigraph(int V) {
		if (V < 0)
			throw new IllegalArgumentException(
					"Number of vertices in a Digraph must be nonnegative");
		this.V = V;
		this.E = 0;
		adj = (Bag<ExtandableDirectedEdge>[]) new Bag[V];
		for (int v = 0; v < V; v++)
			adj[v] = new Bag<ExtandableDirectedEdge>();
	}

	/**
	 * Initializes a random edge-weighted digraph with <tt>V</tt> vertices and
	 * <em>E</em> edges. param V the number of vertices param E the number of
	 * edges
	 * 
	 * @throws java.lang.IllegalArgumentException
	 *             if <tt>V</tt> < 0
	 * @throws java.lang.IllegalArgumentException
	 *             if <tt>E</tt> < 0
	 */
	public ExtendedEdgeWeightedDigraph(int V, int E) {
		this(V);
		if (E < 0)
			throw new IllegalArgumentException(
					"Number of edges in a Digraph must be nonnegative");
		for (int i = 0; i < E; i++) {
			int v = (int) (Math.random() * V);
			int w = (int) (Math.random() * V);
			int weight = (int)Math.round(100 * Math.random()) ;// dont do
																	// this
			ExtandableDirectedEdge e = new ExtandableDirectedEdge(v, w, weight, 1,1,1);
			addEdge(e);
		}
	}

	/**
	 * Initializes an edge-weighted digraph from an input stream. The format is
	 * the number of vertices <em>V</em>, followed by the number of edges
	 * <em>E</em>, followed by <em>E</em> pairs of vertices and edge weights,
	 * with each entry separated by whitespace.
	 * 
	 * @param in
	 *            the input stream
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if the endpoints of any edge are not in prescribed range
	 * @throws java.lang.IllegalArgumentException
	 *             if the number of vertices or edges is negative
	 */
	public ExtendedEdgeWeightedDigraph(In in) {
		this(in.readInt());
		int E = in.readInt();
		if (E < 0)
			throw new IllegalArgumentException(
					"Number of edges must be nonnegative");
		for (int i = 0; i < E; i++) {
			int v = in.readInt();
			int w = in.readInt();
			if (v < 0 || v >= V)
				throw new IndexOutOfBoundsException("vertex " + v
						+ " is not between 0 and " + (V - 1));
			if (w < 0 || w >= V)
				throw new IndexOutOfBoundsException("vertex " + w
						+ " is not between 0 and " + (V - 1));
			// double weight = in.readDouble();
			int weight = 1;
			addEdge(new ExtandableDirectedEdge(v, w, weight, 1,1,1));
			addEdge(new ExtandableDirectedEdge(w, v, weight, 1,1,1));
		}
	}

	public ExtendedEdgeWeightedDigraph(In in, int weight, boolean i0) {
		this(in.readInt());
		int E = in.readInt();
		if (E < 0)
			throw new IllegalArgumentException(
					"Number of edges must be nonnegative");
		for (int i = 0; i < E; i++) {
			int v = in.readInt();
			int w = in.readInt();
			if (v < 0 || v >= V)
				throw new IndexOutOfBoundsException("vertex " + v
						+ " is not between 0 and " + (V - 1));
			if (w < 0 || w >= V)
				throw new IndexOutOfBoundsException("vertex " + w
						+ " is not between 0 and " + (V - 1));
			if (!i0) {
				v--;
				w--;
			}
			addEdge(new ExtandableDirectedEdge(v, w, weight, 1,1,1));
			/* Important modification */
			addEdge(new ExtandableDirectedEdge(w, v, weight, 1,1,1));
		}
	}

	public ExtendedEdgeWeightedDigraph(In in, boolean doesStartFromZero) {
		this(in.readInt());
		int E = in.readInt();
		if (E < 0)
			throw new IllegalArgumentException(
					"Number of edges must be nonnegative");
		int v, w;
		for (int i = 0; i < E; i++) {
			v = in.readInt() - 1;
			w = in.readInt() - 1;
			if (v < 0 || v >= V)
				throw new IndexOutOfBoundsException("vertex " + v
						+ " is not between 0 and " + (V - 1));
			if (w < 0 || w >= V)
				throw new IndexOutOfBoundsException("vertex " + w
						+ " is not between 0 and " + (V - 1));
		//	double weight = in.readDouble();
			// weight = 0.5;
			addEdge(new ExtandableDirectedEdge(v, w, 1,1,1,1));
		}
	}

	/**
	 * Initializes a new edge-weighted digraph that is a deep copy of <tt>G</tt>
	 * .
	 * 
	 * @param G
	 *            the edge-weighted graph to copy
	 */
	public ExtendedEdgeWeightedDigraph(ExtendedEdgeWeightedDigraph G) {
		this(G.V());
		this.E = G.E();
		for (int v = 0; v < G.V(); v++) {
			// reverse so that adjacency list is in same order as original
			Stack<ExtandableDirectedEdge> reverse = new Stack<ExtandableDirectedEdge>();
			for (ExtandableDirectedEdge e : G.adj[v]) {
				reverse.push(e);
			}
			for (ExtandableDirectedEdge e : reverse) {
				adj[v].add(e);
			}
		}
	}

	/**
	 * Returns the number of vertices in the edge-weighted digraph.
	 * 
	 * @return the number of vertices in the edge-weighted digraph
	 */
	public int V() {
		return V;
	}

	/**
	 * Returns the number of edges in the edge-weighted digraph.
	 * 
	 * @return the number of edges in the edge-weighted digraph
	 */
	public int E() {
		return E;
	}

	/**
	 * Adds the directed edge <tt>e</tt> to the edge-weighted digraph.
	 * 
	 * @param e
	 *            the edge
	 */
	public void addEdge(ExtandableDirectedEdge e) {
		int v = e.from();
		adj[v].add(e);
		E++;
	}

	public void removeEdge(ExtandableDirectedEdge e) {
		int v = e.from();
		Iterator<ExtandableDirectedEdge> it = adj[v].iterator();
		while (it.hasNext()) {
			ExtandableDirectedEdge oe = it.next();
			if (oe.from() == e.from() && oe.to() == e.to()) {
				it.remove();
				this.E--;
			}
		}
	}

	/**
	 * Returns the directed edges incident from vertex <tt>v</tt>.
	 * 
	 * @return the directed edges incident from vertex <tt>v</tt> as an Iterable
	 * @param v
	 *            the vertex
	 * @throws java.lang.IndexOutOfBoundsException
	 *             unless 0 <= v < V
	 */
	public Iterable<ExtandableDirectedEdge> adj(int v) {
		if (v < 0 || v >= V)
			throw new IndexOutOfBoundsException("vertex " + v
					+ " is not between 0 and " + (V - 1));
		return adj[v];
	}

	/**
	 * Returns all directed edges in the edge-weighted digraph. To iterate over
	 * the edges in the edge-weighted graph, use foreach notation:
	 * <tt>for (ExtandableDirectedEdge e : G.edges())</tt>.
	 * 
	 * @return all edges in the edge-weighted graph as an Iterable.
	 */
	public Iterable<ExtandableDirectedEdge> edges() {
		Bag<ExtandableDirectedEdge> list = new Bag<ExtandableDirectedEdge>();
		for (int v = 0; v < V; v++) {
			for (ExtandableDirectedEdge e : adj(v)) {
				list.add(e);
			}
		}
		return list;
	}

	public ArrayList<ExtandableDirectedEdge> getEdges() {
		ArrayList<ExtandableDirectedEdge> list = new ArrayList<ExtandableDirectedEdge>();
		for (int v = 0; v < V; v++) {
			for (ExtandableDirectedEdge e : adj(v)) {
				list.add(e);

			}
		}
		return list;
	}

	/**
	 * Returns the number of directed edges incident from vertex <tt>v</tt>.
	 * This is known as the <em>outdegree</em> of vertex <tt>v</tt>.
	 * 
	 * @return the number of directed edges incident from vertex <tt>v</tt>
	 * @param v
	 *            the vertex
	 * @throws java.lang.IndexOutOfBoundsException
	 *             unless 0 <= v < V
	 */
	public int outdegree(int v) {
		if (v < 0 || v >= V)
			throw new IndexOutOfBoundsException("vertex " + v
					+ " is not between 0 and " + (V - 1));
		return adj[v].size();
	}

	/**
	 * Returns a string representation of the edge-weighted digraph. This method
	 * takes time proportional to <em>E</em> + <em>V</em>.
	 * 
	 * @return the number of vertices <em>V</em>, followed by the number of
	 *         edges <em>E</em>, followed by the <em>V</em> adjacency lists of
	 *         edges
	 */
	public String toString() {
		String NEWLINE = System.getProperty("line.separator");
		StringBuilder s = new StringBuilder();
		s.append(V + " " + E + NEWLINE);
		for (int v = 0; v < V; v++) {
			s.append(v + ": ");
			for (ExtandableDirectedEdge e : adj[v]) {
				s.append(e + "  ");
			}
			s.append(NEWLINE);
		}
		return s.toString();
	}

	/**
	 * Unit tests the <tt>EdgeWeightedDigraph</tt> data type.
	 */
	public static void main(String[] args) {
		In in = new In(args[0]);
		ExtendedEdgeWeightedDigraph G = new ExtendedEdgeWeightedDigraph(in);
		StdOut.println(G);
	}

	public ExtandableDirectedEdge getEdgeOf(int v, int w) {// w is to v is from
		Bag<ExtandableDirectedEdge> list = adj[v];
		ExtandableDirectedEdge edge = null;
		for (ExtandableDirectedEdge e : list) {
			if (e.to() == w)
				edge = e;
		}
		return edge;

	}

}
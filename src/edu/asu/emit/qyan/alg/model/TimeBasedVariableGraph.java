/*
 *
 * Copyright (c) 2004-2008 Arizona State University.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ARIZONA STATE UNIVERSITY ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL ARIZONA STATE UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package edu.asu.emit.qyan.alg.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import DataStructures.EdgeTime;
import edu.asu.emit.qyan.alg.control.DijkstraShortestPathAlg;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 769 $
 * @latest $Id: VariableGraph.java 769 2009-06-11 01:23:44Z qyan $
 */
public class TimeBasedVariableGraph extends Graph {
	// Set<Integer> _rem_vertex_id_set = new HashSet<Integer>();
	Set<EdgeTime> _rem_edge_set = new HashSet<EdgeTime>();
	// TODO pair degil triple ekliicem

	/**
	 * Default constructor
	 */
	public TimeBasedVariableGraph() {
	};

	/**
	 * Constructor 1
	 * 
	 * @param data_file_name
	 */
	public TimeBasedVariableGraph(String data_file_name) {
		super(data_file_name);
	}

	/**
	 * Constructor 2
	 * 
	 * @param graph
	 */
	public TimeBasedVariableGraph(Graph graph) {
		super(graph);
	}

	/**
	 * Set the set of edges to be removed from the graph
	 * 
	 * @param _rem_edge_hashcode_set
	 */
	public void set_rem_edge_hashcode_set(Collection<EdgeTime> rem_edge_collection) {
		_rem_edge_set.addAll(rem_edge_collection);
	}

	/**
	 * Add an edge to the set of removed edges
	 * 
	 * @param edge
	 */
	public void remove_edge(EdgeTime edge) {
		_rem_edge_set.add(edge);
	}

	/**
	 * Add a vertex to the set of removed vertices
	 * 
	 * @param vertex_id
	 */

	public void recover_removed_edges() {
		_rem_edge_set.clear();
	}

	public void recover_removed_edge(Pair<Integer, Integer> edge) {
		_rem_edge_set.remove(edge);
	}

	/**
	 * Return the weight associated with the input edge.
	 * 
	 * @param source
	 * @param sink
	 * @return
	 */
	public double get_edge_weight(BaseVertex source, BaseVertex sink, int t) {
		int source_id = source.get_id();
		int sink_id = sink.get_id();

		if (_rem_edge_set.contains(new EdgeTime(source_id, sink_id, t))) {
			return Graph.DISCONNECTED;
		}
		return super.get_edge_weight(source, sink);
	}

	/**
	 * Return the weight associated with the input edge.
	 * 
	 * @param source
	 * @param sink
	 * @return
	 */
	public double get_edge_weight_of_graph(BaseVertex source, BaseVertex sink) {
		return super.get_edge_weight(source, sink);
	}

	/**
	 * Return the set of fan-outs of the input vertex. here the t is TODO su an
	 * buradayım, get adjecy ye bakarken o edge in travel time ı boyunca
	 * doluluk var mı yok mu bakcaz, sadece tek t anı degl
	 * 
	 * @param vertex
	 * @return
	 */
	public Set<BaseVertex> get_adjacent_vertices(BaseVertex vertex, int t) {
		// System.out.println(vertex.get_id() + " at " + t);
		// System.out.println("removed edge size " + _rem_edge_set.size());
		// System.out.println("3 and 4 " + get_edge_weight(this.get_vertex(3),
		// this.get_vertex(4)));
		Set<BaseVertex> ret_set = new HashSet<BaseVertex>();
		int starting_vertex_id = vertex.get_id();

		Set<BaseVertex> adj_vertex_set = super.get_adjacent_vertices(vertex);
		if (_rem_edge_set == null || _rem_edge_set.isEmpty())
			return adj_vertex_set;
		for (BaseVertex cur_vertex : adj_vertex_set) {
			int ending_vertex_id = cur_vertex.get_id();
			boolean isOk = true;

			for (int i = t; i < t + super.get_edge_weight(this.get_vertex(starting_vertex_id),
					this.get_vertex(ending_vertex_id)); i++) {

				// TODO if some edge is full then no increase
				if (_rem_edge_set.contains(new EdgeTime(starting_vertex_id, ending_vertex_id, i))) {
					isOk = false;
					break;
				}
			}
			// if no problem along the edge length, add to the vertex
			if (isOk)
				ret_set.add(cur_vertex);
		}

		return ret_set;
	}

	/**
	 * Get the list of vertices in the graph, except those removed.
	 * 
	 * @return
	 */
	public List<BaseVertex> get_vertex_list() {
		List<BaseVertex> ret_list = new Vector<BaseVertex>();
		for (BaseVertex cur_vertex : super.get_vertex_list()) {
			// if(_rem_vertex_id_set.contains(cur_vertex.get_id())) continue;
			ret_list.add(cur_vertex);
		}
		return ret_list;
	}

	/**
	 * Get the vertex corresponding to the input 'id', if exist.
	 * 
	 * @param id
	 * @return
	 */
	public BaseVertex get_vertex(int id) {

		return super.get_vertex(id);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Welcome to the class VariableGraph!");

		TimeBasedVariableGraph graph = new TimeBasedVariableGraph("data/test_50");
		// graph.remove_edge(new Pair<Integer, Integer>(26, 41));
		DijkstraShortestPathAlg alg = new DijkstraShortestPathAlg(graph);
		System.out.println(alg.get_shortest_path(graph.get_vertex(0), graph.get_vertex(20)));
	}
}

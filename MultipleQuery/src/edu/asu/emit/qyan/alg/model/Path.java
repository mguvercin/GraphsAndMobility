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

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import edu.asu.emit.qyan.alg.model.abstracts.BaseElementWithWeight;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 673 $
 * @latest $Date: 2009-02-05 01:19:18 -0700 (Thu, 05 Feb 2009) $
 */
public class Path implements BaseElementWithWeight, Comparable<Path> {
	List<BaseVertex> _vertex_list = new Vector<BaseVertex>();
	double _weight = -1;
	int constraintIndex = -1;
	int constPathIndex = -1;
	int tempConsPathIndex = -1;
	double delta;

	public double getDelta() {
		return delta;
	}

//	public static Path parse(String s) {
//		Path n = new Path();
//		List<BaseVertex> list = new ArrayList<BaseVertex>();
//		s = s.substring(1, s.indexOf("]"));
//		Scanner scan = new Scanner(s);
//		while(scan.hasNext()){
//			list.add(e)
//		}
//		return n;
//	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	public int getStart() {
		return _vertex_list.get(0).get_id();
	}

	public int getLast() {
		return _vertex_list.get(_vertex_list.size() - 1).get_id();
	}

	public int getConstPathIndex() {
		return constPathIndex;
	}

	public void setConstPathIndex(int constPathIndex) {
		this.constPathIndex = constPathIndex;
	}

	// index of path
	ArrayList<Integer> includedSatisfiedConstraintIndices;
	ArrayList<Integer> includedSatisfiedPathIndices;

	public void addIncludedSatisfied(int consIndex, int pathIndex) {
		if (includedSatisfiedConstraintIndices == null) {
			includedSatisfiedConstraintIndices = new ArrayList<Integer>();
			includedSatisfiedPathIndices = new ArrayList<Integer>();
		}

		includedSatisfiedConstraintIndices.add(consIndex);
		includedSatisfiedPathIndices.add(pathIndex);
	}

	// for root path
	public int getLastSatisfiedBefore(int indexOfPath) {
		if (includedSatisfiedConstraintIndices == null)
			return -1;
		else {
			int last = -1, i;
			for (i = 0; i < includedSatisfiedPathIndices.size();) {
				if (includedSatisfiedPathIndices.get(i) <= indexOfPath) {
					last = includedSatisfiedConstraintIndices.get(i);
					i++;
				} else {
					break;
				}
			}
			tempConsPathIndex = i - 1;
			return last;
		}
	}

	public int getPathCorrespondingOf() {
		return tempConsPathIndex;
	}

	public int getConstraintIndex() {
		return constraintIndex;
	}

	public void setConstraintIndex(int constraintIndex) {
		this.constraintIndex = constraintIndex;
	}

	public Path() {
	};

	public Path(List<BaseVertex> _vertex_list, double _weight) {
		this._vertex_list = _vertex_list;
		this._weight = _weight;
	}

	public double get_weight() {
		return _weight;
	}

	public void set_weight(double weight) {
		_weight = weight;
	}

	public List<BaseVertex> get_vertices() {
		return _vertex_list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object right) {
		if (right instanceof Path) {
			Path r_path = (Path) right;
			return _vertex_list.equals(r_path._vertex_list);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return _vertex_list.hashCode();
	}

	public String toString() {
		return _vertex_list.toString() + ":" + _weight;
	}

	public int getVertexSize() {
		return _vertex_list.size();
	}

	@Override
	public int compareTo(Path o) {
		if (this.delta - o.delta > 0)
			return 1;
		else if (this.delta - o.delta == 0)
			return 0;
		else
			return -1;
	}
}

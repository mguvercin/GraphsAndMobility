package ConstraintBased;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import DataStructures.EdgeTime;
import DataStructures.Tuple;
import edu.asu.emit.qyan.alg.control.DijkstraShortestPathAlg;
import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.Pair;
import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.QYPriorityQueue;
import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.asu.emit.qyan.alg.model.abstracts.BaseEdge;
import edu.asu.emit.qyan.alg.model.abstracts.BaseGraph;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

public class ConstraintBasedDijkstra {
	// can be a parameter, not a local variable
	ArrayList<EdgeTime> constraintList;
	// as in yen s source code
	private VariableGraph _graph = null;
	HashMap<Pair<Integer, Integer>, HashMap<Integer, Object>> edgeToConstraintTime = null;
	// intermediate variables
	private List<Path> _result_list = new Vector<Path>();
	private Map<Path, ArrayList<Integer>> arrivalTimes = new HashMap<Path, ArrayList<Integer>>();

	// HashMap<Path, BaseVertex>();
	private QYPriorityQueue<Path> _path_candidates = new QYPriorityQueue<Path>();
	// the ending vertices of the paths
	private BaseVertex _source_vertex = null;
	private BaseVertex _target_vertex = null;

	private Map<List<BaseVertex>, ArrayList<Integer>> rootMap = new HashMap<List<BaseVertex>, ArrayList<Integer>>();
	// variables for debugging and testing
	private int _generated_path_num = 0;

	public ConstraintBasedDijkstra(BaseGraph graph) {
		super();
		// this.constraintList = constraintList;
		_graph = new VariableGraph((Graph) graph);
		// _init();
	}

	/**
	 * Initiate members in the class.
	 */
	private void _init() {
		clear();
		// get the shortest path by default if both source and target exist
		if (_source_vertex != null && _target_vertex != null) {
			Path shortest_path = get_shortest_path(_source_vertex, _target_vertex);
			if (!shortest_path.get_vertices().isEmpty()) {
				_path_candidates.add(shortest_path);
				ArrayList<Integer> arrivals = new ArrayList<Integer>();
				for (int i = 0; i < shortest_path.getVertexSize(); i++) {
					arrivals.add((int) shortest_path.get_vertices().get(i).get_weight());
				}
				arrivalTimes.put(shortest_path, arrivals);
				// _derivativesOf(shortest_path);
				// _path_derivation_vertex_index.put(shortest_path,
				// _source_vertex);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void _derivativesOf(Path path, ArrayList<EdgeTime> constraints) {
		List<BaseVertex> rootPath;
		BaseVertex spur;
		// satisfy etmedigi constraint
		int constraintPathCursor = path.getConstPathIndex();
		// ArrayList<Integer> arrival = arrivalTimes.get(path);
		// tum satisfaction olan spur pathleri al
		for (int i = 0; i < constraintPathCursor + 1; i++) {
			// System.out.println("i " + i);
			spur = path.get_vertices().get(i);

			rootPath = path.get_vertices().subList(0, i + 1);

			// System.out.println("root p " + rootPath.size());
			for (Path p : _result_list) {
				if (p.getVertexSize() <= i)
					continue;
				// the initial control fro getting sublist
				if (p.getVertexSize() > i && rootPath.hashCode() == p.get_vertices().subList(0, i + 1).hashCode()) {
					_graph.remove_edge(new Pair<Integer, Integer>(p.get_vertices().get(i).get_id(),
							p.get_vertices().get(i + 1).get_id()));
				}
			}
			// get weight
			int weight;
			ArrayList<Integer> rootArrival;
			// if (rootMap.containsKey(rootPath)) {
			// weight = rootMap.get(rootPath);
			// } else {
			// weight=rootPath.get(rootPath.size()-1).get_weight();
			weight = 0;
			rootArrival = rootMap.get(rootPath);
			// TODO why prefix is ok then it is assumed to be in the map? i can
			// try without map, in a version.
			if (rootArrival != null && rootArrival.size() == rootPath.size()) {
				rootArrival = (ArrayList<Integer>) rootMap.get(rootPath).clone();
			} else {
				rootArrival = new ArrayList<Integer>();
				rootArrival.add(0);
				for (int j = 0; j < rootPath.size() - 1; j++) {
					weight += _graph.get_edge_weight(rootPath.get(j), rootPath.get(j + 1));
					rootArrival.add(weight);
					// rootMap.put(rootPath, weight);
				}
				rootMap.put(rootPath, rootArrival);
			}

			// }
			// remove before nodes
			for (int j = 0; j < i; j++) {
				_graph.remove_vertex(rootPath.get(j).get_id());
			}

			DijkstraShortestPathAlg dijkstra = new DijkstraShortestPathAlg(_graph);
			Path spurpath = dijkstra.get_shortest_path_new(spur, path.get_vertices().get(path.getVertexSize() - 1));

			// update spurpath
			if (spurpath.getVertexSize() != 0) {
				spurpath.get_vertices().remove(0);
				for (int j = 0; j < spurpath.getVertexSize(); j++) {
					rootArrival.add((int) (spurpath.get_vertices().get(j).get_weight() + weight));
					// spurpath.get_vertices().get(j).set_weight(spurpath.get_vertices().get(j).get_weight()
					// + weight);
				}
				spurpath.get_vertices().addAll(0, rootPath);

				// System.out.println("root "+weight+":"+rootPath.size()+ " spur
				// "+spurpath.get_weight()+":"+spurpath.getVertexSize());
				spurpath.set_weight(rootArrival.get(rootArrival.size() - 1));

				// spurpath.set_weight(weight + spurpath.get_weight());
				int spurConsIndex = path.getLastSatisfiedBefore(i);
				int spurPathConsIndex = path.getPathCorrespondingOf();
				spurpath.setConstPathIndex(spurPathConsIndex);
				spurpath.setConstraintIndex(spurConsIndex);
				_path_candidates.add(spurpath);
				arrivalTimes.put(spurpath, rootArrival);
			}
			// 3.5 restore everything
			_graph.recover_removed_edges();
			_graph.recover_removed_vertices();

		}
	}

	
	private Path newNext(ArrayList<EdgeTime> constraintList) {
		if (_path_candidates.isEmpty()) {
			System.err.println("There is no other path");
			return null;
		}
		// **** n

		// ***
		Path cur_path = _path_candidates.poll();
		// just in case, to eliminate repetitive paths
		while (_result_list.contains(cur_path)) {
			if ((!_path_candidates.isEmpty()))
				cur_path = _path_candidates.poll();
			else
				return null;
		}

		int constIndex = cur_path.getConstraintIndex() + 1;
		int first, second = 0;
		// edge existence control

		// control loop exceptions! TODO
		if (constIndex == -1)
			constIndex = 0;

		while (constIndex < constraintList.size()) {
			if ((first = cur_path.get_vertices()
					.indexOf(_graph.get_vertex(constraintList.get(constIndex).getStart_id()))) != -1
					&& (second = cur_path.get_vertices()
							.indexOf(_graph.get_vertex(constraintList.get(constIndex).getEnd_id()))) != -1
					&& first == second - 1) {
				// it means we have that edge
				if (constraintList.get(constIndex).getT() >= arrivalTimes.get(cur_path).get(second)
						|| constraintList.get(constIndex).getT() < arrivalTimes.get(cur_path).get(first)) {
					// constraint is satisfied
					// ya edge e varmadan ya da edge i gectikten sonra
					// bu path tarafindan exactly satisfied, not pseudo
					// eger path in bundan onceki
					cur_path.addIncludedSatisfied(constIndex, second);
					constIndex++;
				} else {
					break;
				}

			} else {
				// this constraint edge is not included in the path so continue
				// to increase constrIndex
				constIndex++;
			}
		}

		// now the constindex is not satisfied, but -1 is
		// now all paths added to A array has a constraint index other than
		// -1;
		// (if -1 it is like that)
		cur_path.setConstraintIndex(constIndex - 1);
		// hic constraint level ini astiysafln bir sey incelemedim su an
		cur_path.setConstPathIndex(second - 1);
		_result_list.add(cur_path);
		_derivativesOf(cur_path, constraintList);
		return cur_path;
	}

//	private Path newNextN(ArrayList<EdgeTime> constraintList, int tstart) {
//
//		Path cur_path = _path_candidates.poll();
//		// just in case, to eliminate repetitive paths
//		while (_result_list.contains(cur_path)) {
//			if ((!_path_candidates.isEmpty()))
//				cur_path = _path_candidates.poll();
//			else
//				return null;
//		}
//		Pair<Integer, Integer> nPair; // = new Pair<Integer,
//										// Integer>(cur_path.getStart(),
//										// cur_path.getLast());
//		HashMap<Integer, Object> map;
//		int count = tstart;
//		int until = cur_path.getVertexSize() - 2;
//		for (int i = 0; i < cur_path.getVertexSize() - 1; i++) {
//			nPair = new Pair<Integer, Integer>(cur_path.get_vertices().get(i).get_id(),
//					cur_path.get_vertices().get(i + 1).get_id());
//			map = edgeToConstraintTime.get(nPair);
//			int length = (int) _graph.get_edge_weight(nPair);
//			if (map != null) {
//				for (int j = count; j < count + length; j++) {
//					if (map.containsKey(j)) {
//						until = i;
//						break;
//					}
//				}
//			}
//			if (until != cur_path.getVertexSize() - 2)
//				break;
//
//			count += length;
//		}
//
//		// int constIndex = cur_path.getConstraintIndex() + 1;
//		// int first, second = 0;
//		// // edge existence control
//		//
//		// // control loop exceptions! TODO
//		// if (constIndex == -1)
//		// constIndex = 0;
//		// while (constIndex < constraintList.size()) {
//		// if ((first = cur_path.get_vertices()
//		// .indexOf(_graph.get_vertex(constraintList.get(constIndex).getStart_id())))
//		// != -1
//		// && (second = cur_path.get_vertices()
//		// .indexOf(_graph.get_vertex(constraintList.get(constIndex).getEnd_id())))
//		// != -1
//		// && first == second - 1) {
//		// // it means we have that edge
//		// if (constraintList.get(constIndex).getT() >=
//		// arrivalTimes.get(cur_path).get(second)
//		// || constraintList.get(constIndex).getT() <
//		// arrivalTimes.get(cur_path).get(first)) {
//		// // constraint is satisfied
//		// // ya edge e varmadan ya da edge i gectikten sonra
//		// // bu path tarafindan exactly satisfied, not pseudo
//		// // eger path in bundan onceki
//		// cur_path.addIncludedSatisfied(constIndex, second);
//		// constIndex++;
//		// } else {
//		// break;
//		// }
//		//
//		// } else {
//		// // this constraint edge is not included in the path so continue
//		// // to increase constrIndex
//		// constIndex++;
//		// }
//		// }
//		//
//		// // now the constindex is not satisfied, but -1 is
//		// // now all paths added to A array has a constraint index other than
//		// // -1;
//		// // (if -1 it is like that)
//		// cur_path.setConstraintIndex(constIndex - 1);
//		// // hic constraint level ini astiysafln bir sey incelemedim su an
//		// cur_path.setConstPathIndex(second - 1);
//		_result_list.add(cur_path);
//		_derivativesOf(cur_path, until, constraintList);
//		return cur_path;
//	}
	// public List<Path> getTopk(int k) {
	// _init();
	// for (int i = 0; i < k; i++) {
	// if (has_next())
	// newNext();
	// else
	// return _result_list;
	//
	// }
	// return _result_list;
	// }


	public Path constraintBasedUpgraded(ArrayList<EdgeTime> constraints, BaseVertex start, BaseVertex end) {
		_source_vertex = start;
		_target_vertex = end;
		constraintList = constraints;

		Collections.sort(constraints);

		// // TODO newly added 21 may

		// _graph.remove_edge(new Pair<Integer, Integer>(28, 27));
		_init();
		// int count = 0;
		while (has_next()) {
			Path path = newNext(constraints);
			// count++;
			if (path == null)
				return null;
			else if (path.getConstraintIndex() == constraints.size() - 1) {
				return path;
			}
		}

		// no such path satisfying the constraints
		return null;
	}

	/**
	 * Check if there exists a path, which is the shortest among all candidates.
	 * 
	 * @return
	 */
	public boolean has_next() {
		return !_path_candidates.isEmpty();
	}

	/**
	 * Clear the variables of the class.
	 */
	public void clear() {
		_path_candidates = new QYPriorityQueue<Path>();
		_result_list.clear();
		_generated_path_num = 0;
	}

	public void setSourceDest(BaseVertex s, BaseVertex t) {
		_source_vertex = s;
		_target_vertex = t;
		clear();
	}

	public ArrayList<Integer> getArrivals(Path p) {
		return arrivalTimes.get(p);
	}

	/**
	 * Obtain the shortest path connecting the source and the target, by using
	 * the classical Dijkstra shortest path algorithm.
	 * 
	 * @param source_vt
	 * @param target_vt
	 * @return
	 */
	public Path get_shortest_path(BaseVertex source_vt, BaseVertex target_vt) {
		DijkstraShortestPathAlg dijkstra_alg = new DijkstraShortestPathAlg(_graph);
		return dijkstra_alg.get_shortest_path_new(source_vt, target_vt);
	}

	/**
	 * Return the list of results generated on the whole. (Note that some of
	 * them are duplicates)
	 * 
	 * @return
	 */
	public List<Path> get_result_list() {
		return _result_list;
	}

	/**
	 * The number of distinct candidates generated on the whole.
	 * 
	 * @return
	 */
	public int get_cadidate_size() {
		return _path_candidates.size();
	}

	public int get_generated_path_size() {
		return _generated_path_num;
	}

}

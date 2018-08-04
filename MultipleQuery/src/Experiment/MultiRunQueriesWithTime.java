package Experiment;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;

import DataStructures.DateTime;
import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.Pair;
import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.TimeBasedVariableGraph;
import edu.asu.emit.qyan.alg.model.TimeBasedVariableGraph2;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

public class MultiRunQueriesWithTime {
	// This class is for the experiments that we set the number of queries in
	// one bunch. And it computes the shortest
	// paths with the top k algorithm.
	public static void main(String[] args) throws IOException {

		try {
			// FOr only top k no need for dij
			// String yensGraph = args[0];
			// String queries = args[1];// will be splitted for each setup
			// String outFile = args[2];

			// int freq = Integer.parseInt(args[3]);
			// int start = Integer.parseInt(args[4]);

			String yensGraph = "file/graph.txt";
			String queries = "file/200.txt";
			String outFile = "file/initial5";
			int freq = 100;
			int start = 0;
			
			new File(outFile).mkdir();
			List<String> lines = Files.readAllLines(Paths.get(queries));
			// pair with traj id and dij length

			// merged data 7.5-8_30file
			lines.remove(0);
			int count = start;
//			1 is path replacement, 0 is dij
			int algoType = 1;

			while (count <= lines.size()) {

				String line;
				ArrayList<Pair<Integer, Integer>> list = new ArrayList<Pair<Integer, Integer>>();

				// pair to traj id and dij length
				HashMap<Pair<Integer, Integer>, Pair<String, Double>> trajIDmap = new HashMap<Pair<Integer, Integer>, Pair<String, Double>>();
				HashMap<Pair<Integer, Integer>, String> dijs = new HashMap<Pair<Integer, Integer>, String>();// dj
																												// text

				for (int j = 0; j < count; j++) {
					line = lines.get(j);
					String[] splitted = line.split("\t");
					Pair<Integer, Integer> pair = new Pair<Integer, Integer>(
							Integer.parseInt(splitted[1]),
							Integer.parseInt(splitted[2]));
					Pair<String, Double> trajInfo = trajIDmap.get(pair);

					// to check duplicates
					if (trajInfo == null) {
						trajIDmap.put(
								pair,
								new Pair<String, Double>(splitted[0], Double
										.parseDouble(splitted[4]
												.substring(splitted[4]
														.indexOf(':') + 1))));
						dijs.put(pair, splitted[4]);

						list.add(pair);
					}

				}

				// put size to filename
				String out = "\ttrajID\tfrom\tto\tbunchID\toriginal dijktra length\talgorithm type\talgorithm length\texact dij\n";

				TopkImprovementReplaceHeuristic heuristic = null;
				List<Path> paths = null;
				Graph gr = null;
				long stime = 0;
				System.out.println(list.size() + " size");
				stime = System.currentTimeMillis();
				// TODO for top k
				gr = new Graph(yensGraph);
				heuristic = new TopkImprovementReplaceHeuristic(gr);
				paths = heuristic.pathReplacementHeuristic(list, 0);
				// TODO for dij
				// gr = new TimeBasedVariableGraph2(yensGraph);
				// heuristic = new
				// TopkImprovementReplaceHeuristic((TimeBasedVariableGraph2)
				// gr);
				// paths = heuristic.dijkstraBasedHeuristic(list, 0, yensGraph);
				stime = System.currentTimeMillis() - stime;
				// I put them here because there is one bunch
				for (Path p : paths) {
					Pair<Integer, Integer> pair = new Pair<Integer, Integer>(
							p.getStart(), p.getLast());
					System.out.println(p.get_weight());

					List<BaseVertex> list2 = new ArrayList<BaseVertex>();
					String s = dijs.get(pair);
					s = s.substring(1, s.indexOf("]"));
					Scanner scan = new Scanner(s);
					scan.useDelimiter(", ");
					while (scan.hasNextInt()) {
						list2.add(gr.get_vertex(scan.nextInt()));
					}

					scan.close();
					// if not equal 1
					// query bunch 0 always
					out = out
							.concat(trajIDmap.get(pair).o1
									+ "\t"
									+ p.getStart()
									+ "\t"
									+ p.getLast()
									+ "\t"
									+ 0
									+ "\t"
									+ trajIDmap.get(pair).o2
									+ "\t"
									+ algoType
									+ "\t"
									+ p.get_weight()
									+ "\t"
									+ ((list2.equals(p.get_vertices())) ? 0 : 1)
									+ "\n");

				}

				for (int i = 0; i < heuristic.getInfeasibleSize(); i++) {
					Path p = heuristic.infeasible.get(i);
					Pair<Integer, Integer> pair = new Pair<Integer, Integer>(
							p.getStart(), p.getLast());
					out = out.concat(trajIDmap.get(pair).o1 + "\t"
							+ p.getStart() + "\t" + p.getLast() + "\t" + 0
							+ "\t" + trajIDmap.get(pair).o2 + "\t" + algoType
							+ "\t" + -1 + "\t" + 0 + "\n");
				}
				out = out.concat("time:" + stime / 1000 + "\n");

				System.out.println("infeasible "
						+ heuristic.getInfeasibleSize());
				PrintWriter writer = new PrintWriter(new File(outFile + "/"
						+ count + ".txt"));
				writer.write(out);
				writer.close();
				count += freq;
			}
		} catch (ArrayIndexOutOfBoundsException exp) {
			System.out
					.println("the format is graph queryfile outfile frequencySize");
		}
	}

}

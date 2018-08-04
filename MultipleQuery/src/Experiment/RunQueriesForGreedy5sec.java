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

public class RunQueriesForGreedy5sec {

	// -the most updated one 20.5.2017
//	The main method runs the queries 
//	It pretends the queries belonging to the same 5-second time window retrieved at the same time (to intensify)
//	you can change it by changing the code in the following TODOs
	public static void main(String[] args) throws IOException {
		 String yensGraph = args[0];
		 String queries = args[1];
		 String outFile = args[2];
		 String algoType = args[3];
//		String yensGraph = "C:/Users/Elif/Desktop/Multiple Query Path/subways-graph-secondForTravelTime.txt";
//		String queries = "8-10-60.txt";
//		String outFile = "results/700.txt";
//		String algoType = "1";

		List<String> lines = Files.readAllLines(Paths.get(queries));

		// pair with traj id and dij length
		HashMap<Pair<Integer, Integer>, Pair<String, Double>> trajIDmap = new HashMap<Pair<Integer, Integer>, Pair<String, Double>>();
		HashMap<Pair<Integer, Integer>, String> dijs = new HashMap<Pair<Integer, Integer>, String>();// dj
																										// text

		// merged data 7.5-8_30file
		lines.remove(0);
		String startTime = lines.get(0).split("\t")[3];
		DateTime start = DateTime.parseTime(startTime);
		HashMap<Integer, ArrayList<Pair<Integer, Integer>>> querybunches = new HashMap<Integer, ArrayList<Pair<Integer, Integer>>>();


		for (String line : lines) {
			// System.out.println(line);
			String[] splitted = line.split("\t");


			Pair<Integer, Integer> pair = new Pair<Integer, Integer>(Integer.parseInt(splitted[1]),
					Integer.parseInt(splitted[2]));
			Pair<String, Double> trajInfo = trajIDmap.get(pair);
			if (trajInfo == null) {
				trajIDmap.put(pair, new Pair<String, Double>(splitted[0],
						Double.parseDouble(splitted[4].substring(splitted[4].indexOf(':') + 1))));
				
				// to merge five seconds TODO
		
				int diff = DateTime.parseTime(splitted[3]).diffSecond(start) / 5 ;
				dijs.put(pair, splitted[4]);

				ArrayList<Pair<Integer, Integer>> list = querybunches.get(diff);

				if (list == null) {
					list = new ArrayList<Pair<Integer, Integer>>();
					querybunches.put(diff, list);
				}

				list.add(pair);
			}
			
		}

		String out = "trajID\tfrom\tto\tbunchID\toriginal dijktra length\talgorithm type\talgorithm length\texact dij\n";

		int invisibleCursor = 0;

		TopkImprovementReplaceHeuristic heuristic = null;
		List<Path> paths;
		Graph gr;

		// TODO for one bunch, toogle comment
		// long stime = 0;
		// stime = System.currentTimeMillis();
		for (Entry<Integer, ArrayList<Pair<Integer, Integer>>> entry : querybunches.entrySet()) {
			if (algoType.equals("0")) {
				gr = new Graph(yensGraph);
				heuristic = new TopkImprovementReplaceHeuristic(gr);
				paths = heuristic.pathReplacementHeuristic(entry.getValue(), entry.getKey());
			} else {
				gr = new TimeBasedVariableGraph2(yensGraph);
				heuristic = new TopkImprovementReplaceHeuristic((TimeBasedVariableGraph2) gr);
				paths = heuristic.dijkstraBasedHeuristic(entry.getValue(), entry.getKey(), yensGraph);
				
			}

			for (Path p : paths) {
				Pair<Integer, Integer> pair = new Pair<Integer, Integer>(p.getStart(), p.getLast());
				System.out.println(p.get_weight());

				List<BaseVertex> list = new ArrayList<BaseVertex>();
				String s = dijs.get(pair);
				s = s.substring(1, s.indexOf("]"));
				Scanner scan = new Scanner(s);
				scan.useDelimiter(", ");
				while (scan.hasNextInt()) {
					list.add(gr.get_vertex(scan.nextInt()));
				}

				scan.close();
				// if not equal 1
				out = out.concat(trajIDmap.get(pair).o1 + "\t" + p.getStart() + "\t" + p.getLast() + "\t"
						+ entry.getKey() + "\t" + trajIDmap.get(pair).o2 + "\t" + algoType + "\t" + p.get_weight()
						+ "\t" + ((list.equals(p.get_vertices())) ? 0 : 1) + "\n");
				
			}

			for (int i = invisibleCursor; i < heuristic.getInfeasibleSize(); i++) {
				Path p = heuristic.infeasible.get(i);
				Pair<Integer, Integer> pair = new Pair<Integer, Integer>(p.getStart(), p.getLast());
				out = out.concat(
						trajIDmap.get(pair).o1 + "\t" + p.getStart() + "\t" + p.getLast() + "\t" + entry.getKey() + "\t"
								+ trajIDmap.get(pair).o2 + "\t" + algoType + "\t" + -1 + "\t" + 0 + "\n");
			}

			invisibleCursor = heuristic.getInfeasibleSize();
		}

		System.out.println("infeasible " + heuristic.getInfeasibleSize());
		// out = out.concat("time:" + stime);
		PrintWriter writer = new PrintWriter(new File(outFile));
		writer.write(out);
		writer.close();

	}

	
}

package Experiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import DataStructures.DateTimeModified;
import DataStructures.Query;
import DataStructures.ResultPack;
import edu.asu.emit.qyan.alg.control.DijkstraShortestPathAlg;
import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.Path;

//The main method of this class computes the dijkstra solution path between given queries and writes the results to the given folder.
//args = queries input folder, graph url, out folder path
//"queries_of_days/",
//"subways-graph-secondForTravelTime.txt",
//"ordered dij queries/"
public class RunAllQueriesInDijkstra {
	public static void main(String[] args) throws Exception {
		FileInputStream fin;
		ObjectInputStream oin;
		String inputFolder = args[0];
		String graphUrl = args[1];
		String outFolder = args[2];
		new File(outFolder).mkdir();
		
		File[] files = new File(inputFolder).listFiles();
		ArrayList<Query> queryList;

		Graph graph = new Graph(graphUrl);
		DijkstraShortestPathAlg dij = new DijkstraShortestPathAlg(graph);
		
		PrintWriter pw;
		String fileTxt;
		LinkedList<ResultPack> resultList;

		for (File file : files) {
			fileTxt = "";
			resultList = new LinkedList<ResultPack>();
			System.out.println(file.getName());
			fin = new FileInputStream(file);
			oin = new ObjectInputStream(fin);
			int count = 0;
			queryList = (ArrayList<Query>) oin.readObject();
			for (Query query : queryList) {
				System.out.println(count++);
				if (query.getInitial() == query.getLast())
					continue;
				Path p = dij.get_shortest_path(graph.get_vertex(query.getInitial()), graph.get_vertex(query.getLast()));

				if (p.get_vertices().size() != 0)
					resultList
							.add(new ResultPack(query.getInitial(),
									query.getLast(), new DateTimeModified(query.getStart().getHour(),
											query.getStart().getMinute(), query.getStart().getSecond()),
									query.getId(), p));
				
			}
			Collections.sort(resultList);

			for (ResultPack r : resultList)
				fileTxt = fileTxt.concat(r.id + "\t" + r.initial + "\t" + r.last + "\t" + r.start + "\t" + r.p + "\n");
			
			oin.close();
			fin.close();
			pw = new PrintWriter(new File(outFolder + file.getName()));
			pw.write(fileTxt);
			pw.close();
		}

	}

}

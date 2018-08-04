package Experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import DataStructures.DateTime;
import edu.asu.emit.qyan.alg.control.DijkstraShortestPathAlg;
import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.Pair;
import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

public class DataGeneration {
//	the main method of this class retrieves the queries belonging to the given interval and writes all to a file .
	public static void main(String[] args) throws Exception {
		
		LocalTime startTime = LocalTime.parse("08:00:00");
		LocalTime endTime = LocalTime.parse("10:00:00");
		int howManyFiles = 61;
		String infolder = "ordered dij queries";

		String out = "C:/Users/Elif/Desktop/Multiple Query Path/8-10-60.txt";

		 intervalBasedDataCombine(infolder, startTime, endTime, howManyFiles,
		 out);
		System.out.println("done");

	}

	// gets the in folder name and out file, based on the given time interval,
	// retrieve the entries between the interval and writes to out
	public static void intervalBasedDataCombine(String folder, LocalTime start, LocalTime end, int howManyFile,
			String out) throws IOException {
		String[] fileNames = new File(folder).list();
		ArrayList<File> files = new ArrayList<>();
		Random rand = new Random();
		ArrayList<Integer> randoms = new ArrayList<Integer>();
		if (howManyFile != fileNames.length) {
			while (files.size() < howManyFile) {
				int randNumber = rand.nextInt(fileNames.length);
				while (randoms.contains(randNumber))
					randNumber = rand.nextInt(fileNames.length);
				randoms.add(randNumber);
				files.add(new File(folder + "/" + fileNames[randNumber]));
			}

		} else {
			for (String file : fileNames) {
				files.add(new File(folder + "/" + file));
			}
		}

		ArrayList<LineWithTime> listOflines = new ArrayList<LineWithTime>();
		String[] split;
		String line;
		BufferedReader reader;
		LocalTime time;
		for (File f : files) {
			reader = new BufferedReader(new FileReader(f));
			line = reader.readLine();
			String timeTxt;
			while (true) {
				split = line.split("\t");
				// System.out.println(line);
				if (split[3].length() != 8) {
					timeTxt = properTxt(split[3]);
				} else
					timeTxt = split[3];
				time = LocalTime.parse(timeTxt);

				if (time.isBefore(start)) {
					line = reader.readLine();
					continue;
				} else if (time.isAfter(end))
					break;
				else {
					// the desired case
					listOflines.add(new LineWithTime(time, line));
					line = reader.readLine();
				}

			}
			reader.close();
		}
		Collections.sort(listOflines);

		StringBuffer buff = new StringBuffer();

		buff.append("trajID\tstart\tend\tstartTime\tdijSol\n");
		for (LineWithTime lineT : listOflines) {
			buff.append(lineT.line + "\n");
		}

		PrintWriter pw = new PrintWriter(out);
		pw.write(buff.toString());
		pw.close();
	}

	// TODO test
	private static String properTxt(String string) {
		String txt = "";
		String[] split = string.split(":");
		for (int i = 0; i < split.length; i++) {
			if (split[i].length() != 2)
				split[i] = 0 + split[i];
			txt += split[i];
			if (i != split.length - 1)
				txt += ":";
		}
		return txt;
	}

	// get random x entries from a random file in the folder
	public static void selectRandomXEntries(String infolder, int howManyEntry, String out) throws IOException {
		Random rand = new Random();
		String f = new File(infolder).list()[rand.nextInt(new File(infolder).list().length)];
		List<String> lines = Files.readAllLines(Paths.get(infolder + "/" + f));
		ArrayList<String> newLines = new ArrayList<>();
		while (newLines.size() < howManyEntry) {
			newLines.add(lines.get(rand.nextInt(lines.size())));
		}

		StringBuffer buff = new StringBuffer();
		buff.append("trajID\tstart\tend\tstartTime\tdijSol\n");
		for (String line : newLines) {
			String[] split = line.split("\t");
			buff.append(split[0] + "\t" + split[1] + "\t" + split[2] + "\t00:00:00\t" + split[4] + "\n");
		}

		PrintWriter pw = new PrintWriter(out);
		pw.write(buff.toString());
		pw.close();

	}

	public static void renewGraphviaCapLim(String graphUrl, String out, int cap) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(graphUrl));
		String[] split;
		String newTxt = lines.get(0) + "\n";
		lines.remove(0);
		int c = 0;
		for (String line : lines) {
			System.out.println(c++);
			split = line.split("\t");
			if (Integer.parseInt(split[3]) > cap) {
				newTxt = newTxt.concat(split[0] + "\t" + split[1] + "\t" + split[2] + "\t" + cap + "\n");
			} else {
				newTxt = newTxt.concat(line + "\n");
			}
		}

		PrintWriter pw = new PrintWriter(new File(out + "_cap" + cap + ".txt"));
		pw.write(newTxt);
		pw.close();

	}

	public static void renewGraphviaVertexSize(String graphUrl, String out, int vSize) throws IOException {

		Graph gr = new Graph(graphUrl);
		String newTxt = "248561\n";// for the same graph
		int randvertex = 102687;
		ArrayList<Pair<Integer, Integer>> edgeList = new ArrayList<Pair<Integer, Integer>>();
		ArrayList<Integer> vertices = new ArrayList<Integer>();
		vertices.add(randvertex);

		Set<BaseVertex> tempset;
		Pair<Integer, Integer> tempPair;
		int index = 0;
		// at first it has the rand vertex
		while (vertices.size() < vSize) {
			tempset = gr.get_adjacent_vertices(gr.get_vertex(vertices.get(index)));
			for (BaseVertex v : tempset) {
				if (!vertices.contains(v.get_id())) {
					vertices.add(v.get_id());
				}
				tempPair = new Pair<Integer, Integer>(vertices.get(index), v.get_id());
				if (!edgeList.contains(tempPair)) {
					edgeList.add(tempPair);
				}

			}

			tempset = gr.get_precedent_vertices(gr.get_vertex(vertices.get(index)));
			for (BaseVertex v : tempset) {
				if (!vertices.contains(v.get_id())) {
					vertices.add(v.get_id());
				}
				tempPair = new Pair<Integer, Integer>(v.get_id(), vertices.get(index));
				if (!edgeList.contains(tempPair)) {
					edgeList.add(tempPair);
				}
			}
			index++;
		}

		while (index < vertices.size()) {
			tempset = gr.get_adjacent_vertices(gr.get_vertex(vertices.get(index)));
			for (BaseVertex v : tempset) {

				tempPair = new Pair<Integer, Integer>(vertices.get(index), v.get_id());
				if (!edgeList.contains(tempPair)) {
					edgeList.add(tempPair);
				}

			}

			tempset = gr.get_precedent_vertices(gr.get_vertex(vertices.get(index)));
			for (BaseVertex v : tempset) {

				tempPair = new Pair<Integer, Integer>(v.get_id(), vertices.get(index));
				if (!edgeList.contains(tempPair)) {
					edgeList.add(tempPair);
				}
			}
			index++;
		}

		for (Pair<Integer, Integer> pair : edgeList) {
			newTxt = newTxt.concat(pair.o1 + "\t" + pair.o2 + "\t" + (int) gr.get_edge_weight(pair) + "\t"
					+ gr.getCapacityOf(pair) + "\n");
		}

		PrintWriter pw = new PrintWriter(new File(out + "_vertex" + vSize + ".txt"));
		pw.write(newTxt);
		pw.close();

	}

	public static void buidDatasetFromGraph(String in, String out, int querySize) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(in));
		lines.remove(0);
		List<String> vertices = new ArrayList<String>();
		String[] split;
		System.out.println("ff");
		int cc = 0;
		for (String line : lines) {
			System.out.println(cc++);
			split = line.split("\t");
			if (!vertices.contains(split[0])) {
				vertices.add(split[0]);
			}
			if (!vertices.contains(split[1])) {
				vertices.add(split[1]);
			}

		}
		System.out.println("ver " + vertices.size());
		Random rand = new Random();
		Graph gr = new Graph(in);
		DijkstraShortestPathAlg dij = new DijkstraShortestPathAlg(gr);
		List<Pair<Integer, Integer>> pairs = new ArrayList<Pair<Integer, Integer>>();
		int v1, v2;
		Pair<Integer, Integer> pair;
		int c = 0;
		String txt = "trajID\tstart\tend\tstartTime\tdijSol\n";
		while (pairs.size() < querySize) {
			System.out.println(c++);
			v1 = Integer.parseInt(vertices.get(rand.nextInt(vertices.size())));
			v2 = Integer.parseInt(vertices.get(rand.nextInt(vertices.size())));
			pair = new Pair<Integer, Integer>(v1, v2);
			Path path;
			if (v1 != v2 && !pairs.contains(pair)) {
				path = dij.get_shortest_path(gr.get_vertex(v1), gr.get_vertex(v2));
				if (path.get_weight() != Graph.DISCONNECTED) {
					pairs.add(pair);
					txt = txt.concat(c + "\t" + v1 + "\t" + v2 + "\t00:00:00\t" + path.toString() + "\n");
				}
			}
		}

		PrintWriter pw = new PrintWriter(new File(out + "query" + querySize + ".txt"));
		pw.write(txt);
		pw.close();

	}
}

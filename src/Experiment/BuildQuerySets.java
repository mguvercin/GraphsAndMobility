package Experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.text.html.parser.Entity;

import DataStructures.DateTime;
import DataStructures.Query;
import DataStructures.Tuple;
import edu.asu.emit.qyan.alg.model.Path;

public class BuildQuerySets {
//	SUBWAY means ROAD SEGMENT means sequential edge group
	// The main method of this class builds query bunches.
	// args = detailed road segment list, floating car path data (input folder),
	// daily queries (out folder)
	// args[0] = detailed_road_segment_list.txt
	// args[1] = floating_with_path
	// args[2] = queries_of_days

	public static void main(String[] args) throws Exception {
		ArrayList<Query> list;

		HashMap<Tuple<Integer, Integer>, List<Tuple<Integer, Integer>>> subways = subwayListNew(args[0]);
		System.out.println("road segment size: " + subways.size());
		System.out.println("road segments are read");

		String inFolder = args[1];
		String outFolder = args[2];

		File[] files = new File(inFolder).listFiles();
		String line, edgeInf;
		Query traj;
		BufferedReader reader;
		ObjectOutputStream out;
		FileOutputStream fout;
		StringTokenizer tokenizer;
		
		for (File file : files) {
			System.out.println(file);
			// String textBased = "";
			reader = new BufferedReader(new FileReader(file));
			list = new ArrayList<Query>();
			line = reader.readLine();
			Tuple<Integer, Integer> tup;
			int count = 0;
			while (line != null) {
				System.out.println(count++);
				tokenizer = new StringTokenizer(line, ";");
				traj = new Query();
				traj.setId(tokenizer.nextToken());
				traj.setStart(DateTime.parseTime(tokenizer.nextToken()));
				tokenizer.nextToken();// end time
				edgeInf = tokenizer.nextToken();
				// first one
				tup = new Tuple<Integer, Integer>(Integer.parseInt(edgeInf.substring(0, edgeInf.indexOf('-'))),
						Integer.parseInt(edgeInf.substring(edgeInf.indexOf('-') + 1)));

				// now it keeps the subway
				tup = subwayOfNew(tup, subways);
				if (tup == null) {
					line = reader.readLine();
					continue;
				}
				traj.setInitial(tup.getS());
				while (tokenizer.hasMoreTokens()) {
					edgeInf = tokenizer.nextToken();
				}

				// last one
				tup = new Tuple<Integer, Integer>(Integer.parseInt(edgeInf.substring(0, edgeInf.indexOf('-'))),
						Integer.parseInt(edgeInf.substring(edgeInf.indexOf('-') + 1)));
				tup = subwayOfNew(tup, subways);
				if (tup == null) {
					line = reader.readLine();
					continue;
				}
				// again initial node
				traj.setLast(tup.getS());

				list.add(traj);
				line = reader.readLine();
			}

			reader.close();

			fout = new FileOutputStream(
					new File(outFolder + file.getName().substring(0, file.getName().indexOf(".")) + ".out"));
			out = new ObjectOutputStream(fout);
			out.writeObject(list);
			out.close();
			fout.close();

			
		}

	}

	public static Tuple<Integer, Integer> subwayOf(Tuple<Integer, Integer> edge,
			HashMap<Tuple<Integer, Integer>, List<Integer>> subways) {

		for (Tuple<Integer, Integer> subway : subways.keySet()) {

			int index1 = subways.get(subway).indexOf(edge.getS()), index2 = subways.get(subway).indexOf(edge.getT());

			if (index1 != -1 && index2 != -1 && index2 == index1 + 1) {
				System.out.println(edge);
				System.out.println(subways.get(subway));
				return subway;
			}
		}

		return null;
	}

	public static Tuple<Integer, Integer> subwayOfNew(Tuple<Integer, Integer> edge,
			HashMap<Tuple<Integer, Integer>, List<Tuple<Integer, Integer>>> subways) {

		for (Entry<Tuple<Integer, Integer>, List<Tuple<Integer, Integer>>> subway : subways.entrySet()) {

			if (subway.getValue().contains(edge)) {
			
				return subway.getKey();
			}
		}

		return null;
	}

	// public static HashMap<Tuple<ıN, R>, V>

	
	public static HashMap<Tuple<Integer, Integer>, List<Integer>> subwayList(String path) throws IOException {
		HashMap<Tuple<Integer, Integer>, List<Integer>> n = new HashMap<Tuple<Integer, Integer>, List<Integer>>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
		String line = reader.readLine();
		Scanner scan;
		ArrayList<Integer> list;
		while (line != null) {
			scan = new Scanner(line);
			list = new ArrayList<>();
			scan.useDelimiter(",");
			while (scan.hasNext()) {
				list.add(scan.nextInt());
			}
			n.put(new Tuple<Integer, Integer>(list.get(0), list.get(list.size() - 1)), list);
			line = reader.readLine();
		}

		reader.close();
		return n;
	}

	public static HashMap<Tuple<Integer, Integer>, List<Tuple<Integer, Integer>>> subwayListNew(String path)
			throws IOException {
		HashMap<Tuple<Integer, Integer>, List<Tuple<Integer, Integer>>> n = new HashMap<Tuple<Integer, Integer>, List<Tuple<Integer, Integer>>>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
		String line = reader.readLine();
		Scanner scan;
		ArrayList<Tuple<Integer, Integer>> list;
		int prev, cur;
		while (line != null) {
			scan = new Scanner(line);
			list = new ArrayList<Tuple<Integer, Integer>>();
			scan.useDelimiter(",");
			prev = scan.nextInt();
			while (scan.hasNext()) {
				cur = scan.nextInt();
				list.add(new Tuple<Integer, Integer>(prev, cur));
				prev = cur;
			}
			n.put(new Tuple<Integer, Integer>(list.get(0).getS(), list.get(list.size() - 1).getT()), list);
			line = reader.readLine();
		}

		reader.close();
		return n;
	}
}

package Experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

import DataStructures.DateTime;
import DataStructures.Query;

public class TestSetHistogramBuild {
	public static void main(String[] args) throws Exception {
		mergeTheHistogram();
	}

	public static void mergeTheHistogram() throws Exception {
		String infolder = "query histogram/";
		File[] files = new File(infolder).listFiles();

		HashMap<DateTime, Integer> map = new HashMap<DateTime, Integer>();
		DateTime d = new DateTime(0, 0, 0);
		for (int i = 0; i < 86400; i++) {
			map.put(d, 0);
			d = d.addSecond(1);
		}

		for (File f : files) {
			System.out.println(f.getName());
			List<String> lines = Files.readAllLines(f.toPath());
			lines.forEach(line -> {
				DateTime d1 = DateTime.parseTime(line.substring(0, line.indexOf('\t')));
				map.put(d1, map.get(d1) + Integer.valueOf(line.substring(line.indexOf('\t') + 1)));
			});
		}
		String whole = "";
		for (Entry<DateTime, Integer> e : map.entrySet()) {
			whole = whole.concat(e.getKey() + "\t" + e.getValue() + "\n");
		}
		PrintWriter pw = new PrintWriter(
				new File("histogramOfAllInSeconds.txt"));
		pw.write(whole);
		pw.close();

	}

	public void divideTheSets() {
		String infolder = "ordered dij queries/";
		String out = "divided queries/";
		new File(out).mkdir();

		File[] files = new File(infolder).listFiles();

		for (File file : files) {

			try {
				HashMap<DateTime, Integer> total = new HashMap<DateTime, Integer>();
				HashMap<DateTime, ArrayList<Query>> queries = new HashMap<DateTime, ArrayList<Query>>();
				DateTime d = new DateTime(0, 0, 0);
				
				for (int i = 0; i < 86400; i++) {
					total.put(d, 0);
					// System.out.println(d);
					queries.put(d, new ArrayList<Query>());
					d = d.addSecond(1);
				}
				List<String> lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
				for (String line : lines) {
					String[] splitted = line.split("\t");
					Query q = new Query(splitted[0], Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]),
							DateTime.parseTime(splitted[3]));

					queries.get(q.getStart()).add(q);
					total.put(q.getStart(), total.get(q.getStart()) + 1);
				}
				String whole = "";
				for (Entry<DateTime, Integer> e : total.entrySet()) {
					whole = whole.concat(e.getKey() + "\t" + e.getValue() + "\n");
				}
				PrintWriter pw = new PrintWriter(
						new File("query histogram/" + file.getName()));
				pw.write(whole);
				pw.close();
				
				FileOutputStream fout = new FileOutputStream(new File(out + file.getName()));
				ObjectOutputStream os = new ObjectOutputStream(fout);
				os.writeObject(queries);
				os.close();
				fout.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

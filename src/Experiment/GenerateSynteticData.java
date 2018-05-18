package Experiment;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class GenerateSynteticData {
//	generates random graph data
	public static void main(String[] args) throws Exception {
		PrintWriter writer = new PrintWriter(new File("toyExample.txt"));
		writer.write(generate(10, 20, 5));
		writer.close();
	}

	private static String generate(int vertexSize, int travelTimeLimit, int capacityLimit) {
		// make all connected
		String text = "";
		text = text.concat(vertexSize + "\n");
		Random random = new Random();
		for (int i = 0; i < vertexSize; i++) {
			ArrayList<Integer> randomNodes = new ArrayList<Integer>();
			while (randomNodes.size() < 5) {
				int rand = random.nextInt(vertexSize);
				if (rand != i && !randomNodes.contains(rand))
					randomNodes.add(rand);
			}
			for (int j : randomNodes) {
				int randTr = random.nextInt(travelTimeLimit);
				while (randTr == 0) {
					randTr = random.nextInt(travelTimeLimit);
				}

				int randC = random.nextInt(capacityLimit);
				while (randC == 0) {
					randC = random.nextInt(capacityLimit);
				}
				text = text.concat(i + "\t" + j + "\t" + randTr + "\t" + randC + "\n");
			}

		}

		return text;
	}

}

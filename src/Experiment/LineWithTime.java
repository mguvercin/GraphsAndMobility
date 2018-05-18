package Experiment;

import java.time.LocalTime;

public class LineWithTime implements Comparable<LineWithTime> {
	// a class to have ordered query list, retrieved from more than one file.
	LocalTime time;
	String line;

	public LineWithTime(LocalTime time, String line) {
		this.time = time;
		this.line = line;
	}

	@Override
	public int compareTo(LineWithTime o) {
		return time.compareTo(o.time);
	}

}

package DataStructures;

import java.io.Serializable;
import java.util.Scanner;

/**
 * It is a minimal Time class including hour, minute, and second properties.
 * 
 */
public class Time implements Serializable {
	private int hour;
	private int minute;
	private int second;

	public Time(int hour, int minute, int second) {
		super();
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public static Time parseTime(String s) throws Exception {
		// TODO control with regex
		String[] time = s.split(":");
		Time t;
		try {
			t = new Time(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));

		} catch (Exception e) {
			throw new Exception("Wrong formaat! The correct format is a time with ##:##:##");
		}
		return t;
	}

	// at minute
	public double difference(Time other) {
		int sec = this.second - other.second;
		int min = this.minute - other.minute;
		int h = this.hour - other.hour;
		if (sec < 0) {
			sec += 60;
			min--;
		}
		if (min < 0) {
			min += 60;
			h--;
		}

		return Math.abs(h * 60 + min + sec / 60.0);
	}

	/**
	 * @param t
	 *            is the minute amount to be added to the current time.
	 */
	public Time add(double t) {
		int nMinute, nSecond, nHour;
		nSecond = (int) ((t - Math.floor(t)) * 60) + second;

		nMinute = (int) Math.floor(minute + t);
		if (nSecond > 60) {
			// only includes one 60, from carry
			nMinute += 1;
			nSecond = nSecond % 60;
		}
		int hourExceed = nMinute / 60;
		nHour = hour;
		if (hourExceed > 0) {
			nHour += hourExceed;
			nMinute = nMinute % 60;
		}

		if (nHour > 23) {
			nHour = nHour % 24;
		}

		return new Time(nHour, nMinute, nSecond);
	}

	/**
	 * Returns corresponding time slot(5 min-periods) of a Time object within a
	 * day.
	 * 
	 * @return time slot index out of 288 slots
	 */
	public int getTimeSlot() {
		return (hour * 60 + minute) / 5;
	}

	@Override
	public String toString() {

		return hour + ":" + minute + ":" + second;
	}

}

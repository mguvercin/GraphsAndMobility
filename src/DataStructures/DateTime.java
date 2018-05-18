package DataStructures;

import java.io.Serializable;
import java.sql.Date;

public class DateTime implements Serializable {
	private int hour;
	private int minute;
	private int second;

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

	public static DateTime parseTime(String s) {
		// TODO regex control and exception
		String[] time = s.split(":");
		return new DateTime(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
	}

	// at minute
	public double difference(DateTime other) {
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

	// at minute
	public int diffSecond(DateTime other) {
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

		return h * 60 * 60 + min * 60 + sec;
	}

	public DateTime(int hour, int minute, int second) {
		super();
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	// for not days
	public DateTime add(double t) {
		int nMinute, nSecond, nHour;
		nSecond = (int) ((t - Math.floor(t)) * 60) + second;

		nMinute = (int) Math.floor(minute + t);
		if (nSecond > 59) {
			// only includes one 60
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

		return new DateTime(nHour, nMinute, nSecond);
	}

	// for not days
	public DateTime addSecond(int t) {
		int nMinute, nSecond, nHour;
		nSecond = second + t;

		nMinute = minute;
		if (nSecond > 59) {
			// only includes one 60
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

		return new DateTime(nHour, nMinute, nSecond);
	}

	public DateTime addWithDays(double t) {
		// TODO
		return null;
	}

	@Override
	public String toString() {

		return hour + ":" + minute + ":" + second;
	}

	@Override
	public int hashCode() {

		return second + minute * 60 + hour * 60 * 60;
	}

	@Override
	public boolean equals(Object obj) {
		DateTime other = (DateTime) obj;
		return hour == other.hour && minute == other.minute && second == other.second;
	}
}

// TODO Old version
// package DataStructures;
//
// import java.util.Scanner;
//
// public class DateTime {
// private WEEK_DAYS day = null;
// private int hour;
// private int minute;
// private int second;
//
// public WEEK_DAYS getDay() {
// return day;
// }
//
// public void setDay(WEEK_DAYS day) {
// this.day = day;
// }
//
// public int getHour() {
// return hour;
// }
//
// public void setHour(int hour) {
// this.hour = hour;
// }
//
// public int getMinute() {
// return minute;
// }
//
// public void setMinute(int minute) {
// this.minute = minute;
// }
//
// public int getSecond() {
// return second;
// }
//
// public void setSecond(int second) {
// this.second = second;
// }
//
// public DateTime(WEEK_DAYS day, int hour, int minute, int second) {
// super();
// this.day = day;
// this.hour = hour;
// this.minute = minute;
// this.second = second;
// }
//
// public static DateTime parseTime(String s) {
// // TODO regex control and exception
// String[] time = s.split(":");
// return new DateTime(Integer.parseInt(time[0]), Integer.parseInt(time[1]),
// Integer.parseInt(time[2]));
// }
//
// //at minute
// public double difference(DateTime other) {
// int sec = this.second - other.second;
// int min = this.minute - other.minute;
// int h = this.hour - other.hour;
// if (sec < 0) {
// sec += 60;
// min--;
// }
// if (min < 0) {
// min += 60;
// h--;
// }
//
// return Math.abs(h * 60 + min + sec / 60.0);
// }
//
// public DateTime(int hour, int minute, int second) {
// super();
// this.hour = hour;
// this.minute = minute;
// this.second = second;
// }
//
// // for not days
// public DateTime add(double t) {
// int nMinute, nSecond, nHour;
// nSecond = (int) ((t - Math.floor(t)) * 60) + second;
//
// nMinute = (int) Math.floor(minute + t);
// if (nSecond > 60) {
// // only includes one 60
// nMinute += 1;
// nSecond = nSecond % 60;
// }
// int hourExceed = nMinute / 60;
// nHour = hour;
// if (hourExceed > 0) {
// nHour += hourExceed;
// nMinute = nMinute % 60;
// }
//
// if (nHour > 23) {
// nHour = nHour % 24;
// }
//
// return new DateTime(nHour, nMinute, nSecond);
// }
//
// public DateTime addWithDays(double t) {
// // TODO
// return null;
// }
//
// @Override
// public String toString() {
//
// return hour + ":" + minute + ":" + second;
// }
//
// }

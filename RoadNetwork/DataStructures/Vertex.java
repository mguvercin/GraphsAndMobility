package DataStructures;
/**
 * This class is the implementation of the vertex of the given network.
 * 
 * @version 1.0
 * @since August 2016
 */
public class Vertex {
	private double latitude;
	private double longtitude;
	private int vID;

	
	public int getvID() {
		return vID;
	}

	public void setvID(int vID) {
		this.vID = vID;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	public Vertex(double latitude, double longtitude, int vID) {
		super();
		this.latitude = latitude;
		this.vID = vID;
		this.longtitude = longtitude;
	}

	@Override
	public String toString() {

		return vID + ": " + latitude + "-" + longtitude;
	}

}

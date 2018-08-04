package DataStructures;

public class PriorityQueueItem implements Comparable<PriorityQueueItem> {
	private int vertex;
	private int distance ;
	
	public int getVertex() {
		return vertex;
	}

	public void setVertex(int vertex) {
		this.vertex = vertex;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public PriorityQueueItem(int vertex, int distance) {
		super();
		this.vertex = vertex;
		this.distance = distance;
	}

	@Override
	public int compareTo(PriorityQueueItem o) {
		return distance-o.distance;
	}
}

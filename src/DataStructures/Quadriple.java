package DataStructures;

public class Quadriple {

	private double dij, base, greedy, knap;

	public double getDij() {
		return dij;
	}

	public void setDij(double dij) {
		this.dij = dij;
	}

	public double getBase() {
		return base;
	}

	public void setBase(double base) {
		this.base = base;
	}

	public double getGreedy() {
		return greedy;
	}

	public void setGreedy(double greedy) {
		this.greedy = greedy;
	}

	public Quadriple(double dij, double base, double greedy, double knap) {
		super();
		this.dij = dij;
		this.base = base;
		this.greedy = greedy;
		this.knap = knap;
	}

	public double getKnap() {
		return knap;
	}

	public void setKnap(double knap) {
		this.knap = knap;
	}

}

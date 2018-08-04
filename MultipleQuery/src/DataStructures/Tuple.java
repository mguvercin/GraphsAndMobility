/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

/**
 * 
 * @author root
 */
public class Tuple<S, R> implements java.io.Serializable {

	private final S S;
	private final R T;

	public Tuple(S left, R right) {
		this.S = left;
		this.T = right;
	}

	public S getS() {
		return S;
	}

	public R getT() {
		return T;
	}

	@Override
	public int hashCode() {
		return S.hashCode() ^ T.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Tuple))
			return false;
		Tuple pairo = (Tuple) o;
		return this.S.equals(pairo.getS()) && this.T.equals(pairo.getT());
	}

	@Override
	public String toString() {
		return S + " " + T;
	}

}
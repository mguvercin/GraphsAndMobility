package Exceptions;

import DBOp.Graph;

/**
 * The exception is thrown when there is no such vertex ID in the database
 * @see Graph
 */
public class NoVIDFoundException extends Exception {
	public NoVIDFoundException() {
		super("Unidentified vertex ID.");
	}

}

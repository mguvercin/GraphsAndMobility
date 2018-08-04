package Exceptions;

import DBOp.Graph;

/**
 * The exception is thrown when the given location is out of the boundary of the road network
 * network database
 * 
 * @see Graph
 */
public class OutOfBoundaryException extends Exception {
	public OutOfBoundaryException() {
		super("The points are not in the road map. \nMinimum Latitude: 45.33  Maximum Latitude: 45.58\nMinimum Longtitude: 8.94  Maximum Longtitude: 9.38");
	}

}

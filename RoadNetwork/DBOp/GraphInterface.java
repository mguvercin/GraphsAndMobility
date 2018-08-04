package DBOp;

import java.util.List;

import DataStructures.Time;
import DataStructures.Edge;
import DataStructures.Path;
import DataStructures.Vertex;
import Exceptions.NoVIDFoundException;
import Exceptions.OutOfBoundaryException;

/**
 * This is the interface class for the Graph class. The properties can be found
 * here.
 * 
 * @author Elif
 * @see Graph
 */
public interface GraphInterface {
	/**
	 * It gives the Vertex object correspondence of a given vertex ID
	 * 
	 * @param vertex
	 *            ID whose object is desired
	 * @return vertex of given vertex ID
	 */
	public Vertex getVertexOf(int vID) throws NoVIDFoundException;

	/**
	 * It gives the Vertex object correspondence of a given vertex ID
	 * 
	 * @param latitude
	 *            exact latitude value of the desired vertex
	 * @param longitude
	 *            exact longitude value of the desired vertex
	 * @return vertex having the given location values
	 * @throws OutOfBoundaryException
	 */
	public Vertex getVertexOf(double latitude, double longtitude) throws OutOfBoundaryException;;

	/**
	 * Returns the closest vertex of the given geo-location point
	 * 
	 * @param latitude
	 *            the latitude value of the desired vertex
	 * @param longitude
	 *            exact longitude value of the desired vertex
	 * @return a Vertex object
	 */
	public Vertex closestVertexTo(double latitude, double longtitude) throws OutOfBoundaryException;

	/**
	 * @return an Edge object between the given vertex IDs
	 * @throws NoVIDFoundException
	 *             occurs when there are invalid vertex IDs
	 */
	public Edge edgeBetween(int startVertex, int endVertex) throws NoVIDFoundException;

	/**
	 * @return an Edge object between the given vertices
	 * @throws NoVIDFoundException
	 *             occurs when there are invalid vertex IDs
	 */
	public Edge edgeBetween(Vertex startVertex, Vertex endVertex) throws NoVIDFoundException;

	/**
	 * @return list of both outgoing and incoming edges of vID
	 @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public List<Edge> edgesOf(int vID) throws NoVIDFoundException;

	/**
	 * @return list of both outgoing and incoming edges of Vertex v
	  @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public List<Edge> edgesOf(Vertex v) throws NoVIDFoundException;

	/**
	 * @return list of incoming edges of Vertex v
	  @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public List<Edge> incomingEdgesOf(Vertex v) throws NoVIDFoundException;

	/**
	 * @return list of outgoing edges of Vertex v
	  @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public List<Edge> outgoingEdgesOf(Vertex v) throws NoVIDFoundException;

	/**
	 * @return list of incoming edges of vID
	  @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public List<Edge> incomingEdgesOf(int vID) throws NoVIDFoundException;

	/**
	 * @return list of outgoing edges of vID
	  @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public List<Edge> outgoingEdgesOf(int vID) throws NoVIDFoundException;

	/**
	 * @return list of outgoing vertices from Vertex v
	 * @throws NoVIDFoundException
	 *             throws exception with invalid vertex ID
	 */
	public List<Vertex> giveNeighbors(Vertex v) throws NoVIDFoundException;

	/**
	 * @return list of vertices with a given direction
	 * @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public List<Vertex> giveNeighbors(int vID) throws NoVIDFoundException;

	/**
	 * @return list of vertices with a given direction
	 * @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public List<Vertex> giveNeighbors(Vertex v, Direction direction) throws NoVIDFoundException;

	/**
	 * @return list of vertices with a given direction
	 * @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public List<Vertex> giveNeighbors(int vID, Direction direction) throws NoVIDFoundException;

	public double calculateTravelTimeFixed(List<Vertex> list) throws NoVIDFoundException;

	/**
	 * @return A path having minimum distance between the given vertices
	 *  @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public Path getShortestPath(Vertex start, Vertex end) throws NoVIDFoundException;

	/**
	 * @return A path having minimum distance between the given vertices
	  @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public Path getShortestPath(int start, int end) throws NoVIDFoundException;

	/**
	 * @return A path having minimum distance between the given points over the
	 *         database It founds the closest vertices to the geolocations
	 * @throws NoVIDFoundException
	 * @throws OutOfBoundaryException
	 * 
	 */
	public Path getShortestPath(double lat1, double long1, double lat2, double long2)
			throws NoVIDFoundException, OutOfBoundaryException;

	/**
	 * @return A path having minimum time varying distance between the given
	 *         vertices having the ids
	  @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public Path getShortestPath(int start, int end, Time dtime) throws Exception;

	/**
	 * @return A path having minimum time varying distance between the given
	 *         vertices
	  @throws NoVIDFoundException
	 *             invalid vertex ID
	 */
	public Path getShortestPath(Vertex start, Vertex end, Time dtime) throws Exception;

	
	double calculateTravelTime(List<Vertex> list, Time dTime) throws NoVIDFoundException;

	/**
	 * @return Iterable object of all vertices in the database
	 */
	public Iterable<Vertex> allVertices();

	/**
	 * @return Iterable object of all edges with the given edge type
	 */
	public Iterable<Edge> allEdges();

	/**
	 * @return the number of nodes
	 */
	public int countNodes();

	/**
	 * @return the number of edges with the given type
	 */
	public int countEdges();

	/**
	 * Closes the graph database
	 */
	public void close();

}

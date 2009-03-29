package net.sf.sdedit.util.graph;

/**
 * An interface for an edge of an undirected graph, ready for being traversed
 * by a {@linkplain DepthFirstSearch}.
 * 
 * @author Markus Strauch
 *
 */
public interface Edge {
	
	/**
	 * Returns one endpoint of this <tt>Edge</tt>.
	 * 
	 * @return
	 */
	public Node getNode1 ();
	
	/**
	 * Returns the other endpoint of this <tt>Edge</tt>. 
	 * 
	 * @return
	 */
	public Node getNode2 ();
	
	/**
	 * Sets a flag denoting if this <tt>Edge</tt> has already been visited
	 * by a {@linkplain DepthFirstSearch}.
	 * 
	 * @param visited
	 */
	public void setVisited (boolean visited);
	
	/**
	 * Returns a flag denoting if this <tt>Edge</tt> has already been visited
	 * by a {@linkplain DepthFirstSearch}.
	 * 
	 * @return
	 */
	public boolean isVisited ();

}

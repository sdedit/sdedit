package net.sf.sdedit.util.graph;

import java.util.List;

/**
 * An interface for a node of an undirected graph.
 * 
 * @author Markus Strauch
 */
public interface Node {

	/**
	 * Returns the list of (undirected) {@linkplain Edge}s this <tt>Node</tt> is
	 * incident to.
	 * 
	 * @return the list of (undirected) {@linkplain Edge}s this <tt>Node</tt> is
	 *         incident to
	 */
	public List<Edge> getEdges();

	/**
	 * Returns a representative node that is inside the same component as
	 * this <tt>Node</tt>.
	 * 
	 * @return
	 */
	public Node getTRoot();


	/**
	 * Sets a representative node that is inside the same component as
	 * this <tt>Node</tt>.
	 * 
	 * @return
	 */
	public void setTRoot(Node tRoot);

	
	public String getName();
}

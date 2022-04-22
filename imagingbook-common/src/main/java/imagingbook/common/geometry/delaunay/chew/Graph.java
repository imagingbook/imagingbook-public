package imagingbook.common.geometry.delaunay.chew;

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Straightforward undirected graph implementation.
 * Nodes are generic type N.
 *
 * @author Paul Chew
 *
 * Created November, December 2007.  For use in Delaunay/Voronoi code.
 *
 */
public class Graph<N> {

    private Map<N, Set<N>> theNeighbors =    // Node -> adjacent nodes
        new HashMap<N, Set<N>>();
    private Set<N> theNodeSet =              // Set view of all nodes
        Collections.unmodifiableSet(theNeighbors.keySet());

    /**
     * Add a node.  If node is already in graph then no change.
     * @param node the node to add
     */
    public void add (N node) {
        if (theNeighbors.containsKey(node)) return;
        theNeighbors.put(node, new ArraySet<N>());
     }

    /**
     * Add a link. If the link is already in graph then no change.
     * @param nodeA one end of the link
     * @param nodeB the other end of the link
     * @throws NullPointerException if either endpoint is not in graph
     */
    public void add (N nodeA, N nodeB) throws NullPointerException {
        theNeighbors.get(nodeA).add(nodeB);
        theNeighbors.get(nodeB).add(nodeA);
    }

    /**
     * Remove node and any links that use node. If node not in graph, nothing
     * happens.
     * @param node the node to remove.
     */
    public void remove (N node) {
        if (!theNeighbors.containsKey(node)) return;
        for (N neighbor: theNeighbors.get(node))
            theNeighbors.get(neighbor).remove(node);    // Remove "to" links
        theNeighbors.get(node).clear();                 // Remove "from" links
        theNeighbors.remove(node);                      // Remove the node
    }

    /**
     * Remove the specified link. If link not in graph, nothing happens.
     * @param nodeA one end of the link
     * @param nodeB the other end of the link
     * @throws NullPointerException if either endpoint is not in graph
     */
    public void remove (N nodeA, N nodeB) throws NullPointerException {
        theNeighbors.get(nodeA).remove(nodeB);
        theNeighbors.get(nodeB).remove(nodeA);
    }

    /**
     * Report all the neighbors of node.
     * @param node the node
     * @return the neighbors of node
     * @throws NullPointerException if node does not appear in graph
     */
    public Set<N> neighbors (N node) throws NullPointerException {
        return Collections.unmodifiableSet(theNeighbors.get(node));
    }

    /**
     * Returns an unmodifiable Set view of the nodes contained in this graph.
     * The set is backed by the graph, so changes to the graph are reflected in
     * the set.
     * @return a Set view of the graph's node set
     */
    public Set<N> nodeSet () {
        return theNodeSet;
    }

}

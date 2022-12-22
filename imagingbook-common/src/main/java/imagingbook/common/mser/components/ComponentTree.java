/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.mser.components;

import ij.process.ByteProcessor;
import imagingbook.common.mser.MserData;
import imagingbook.common.mser.components.PixelMap.Pixel;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * This class represents a tree of extremal image components.
 * 
 * @param <T> the data type of components (e.g., {@link MserData})
 * @author WB
 * @version 2022/11/19
 */
public abstract class ComponentTree<T> implements Iterable<Component<T>> {
	
	/**
	 * Enumeration specifying the method (algorithm) for building the component tree.
	 */
	public enum Method {
		/** Specifies the classic (global immersion) algorithm, see {@link ComponentTreeGlobalImmersion}. */
		GlobalImmersion,
		/** Specifies the linear-time algorithm, see {@link ComponentTreeLinearTime}. */
		LinearTime;
	}
	
	// --------------------------------------------------------------
	
	/** Constructor (non-public). */
	ComponentTree() {}

	/**
	 * Creates a new component tree for the specified image using the default method ({@link Method#LinearTime}).
	 *
	 * @param <T> the generic node type
	 * @param ip the input image
	 * @return the component tree for the specified image
	 */
	public static <T> ComponentTree<T> from(ByteProcessor ip) {
		return from(ip, Method.LinearTime);
	}

	/**
	 * Creates a new component tree for the specified image.
	 *
	 * @param <T> the data type of components (e.g., {@link MserData})
	 * @param ip the input (gray-level) image
	 * @param method the method for building the component tree
	 * @return the component tree
	 */
	public static <T> ComponentTree<T> from(ByteProcessor ip, Method method) {
		return from(new PixelMap(ip), method);
	}

	/**
	 * Creates a new component tree for the specified {@link PixelMap}.
	 *
	 * @param <T> the data type of components (e.g., {@link MserData})
	 * @param pm a {@link PixelMap} instance
	 * @param method the method for building the component tree
	 * @return the component tree
	 */
	public static <T> ComponentTree<T> from(PixelMap pm, Method method) {
		switch (method) {
		case LinearTime:  return new ComponentTreeLinearTime<>(pm);
		case GlobalImmersion: return new ComponentTreeGlobalImmersion<>(pm);
		}
		return null;
	}
	
	// --------------------------------------------------------------

	/**
	 * Returns the root component of this component tree. To be implemented by concrete subclasses.
	 *
	 * @return the root component
	 */
	public abstract Component<T> getRoot();

	/**
	 * Returns an unordered collection of all tree components. To be implemented by concrete subclasses.
	 *
	 * @return all tree components
	 */
	public abstract Collection<Component<T>> getComponents();
	
	@Override
	public Iterator<Component<T>> iterator() {
		return getComponents().iterator();
	}

	/**
	 * Finds and returns a collection of all leaf components of this tree. A leaf is a component which has no children.
	 *
	 * @return all leaf components
	 */
	public Collection<Component<T>> getLeaves() {
		Collection<Component<T>> leaves = new LinkedList<>();
		for (Component<T> c : getComponents()) {
			if (c.getChildren().isEmpty()) {
				leaves.add(c);
			}
		}	
		return leaves;
	}
	
	// -------------------------------------------------------------------------------
	
	/**
	 * Performs integrity checks on this component tree.
	 * @return true iff all checks are passed
	 */
	public boolean validate() {
		if (!checkNodes()) {
			System.out.println("checkNodes failed");
			return false;
		}
		if (!checkParents()) {
			System.out.println("checkParents failed");
			return false;
		}
		if (!checkChildren()) {
			System.out.println("checkChildren failed");
			return false;
		}
		if (!checkRoot()) {
			System.out.println("checkRoot failed");
			return false;
		}
		if (!checkLevels()) {
			System.out.println("checkLevels failed");
			return false;
		}
		if (!checkSize()) {
			System.out.println("checkSize failed");
			return false;
		}
		return true;
	}

	/**
	 * Checks if all tree nodes reachable through root are contained in components list and the tree has no duplicate
	 * nodes (and no cycles).
	 *
	 * @return true iff OK
	 */
	private boolean checkNodes() {
		Set<Component<T>> componentSet = new HashSet<>(); // empty hash set
		int duplicateID = traverse(getRoot(), componentSet);
		if (duplicateID >= 0) {		// traverse() registers all visited components
			System.out.println("*** component tree has duplicate node: " + duplicateID);
			return false;
		}
		if (getComponents().size() != componentSet.size()) {
			System.out.println("*** some nodes in components list can not be reached!");
			return false;
		}
		return true;
	}
	
	private int traverse(Component<T> c, Set<Component<T>> cS) {
		if (cS.add(c)) {
			for (Component<T> cc : c.getChildren()) {
				int duplicateId = traverse(cc, cS);
				if (duplicateId >= 0) {	// duplicate node
					return duplicateId;
				};
			}
			return -1;	// everything OK, no duplicate node
		}
		else {	// c was already in set
			System.out.println("*** duplicate tree node: " + c.ID);
			return c.ID;
		}
	}

	/**
	 * Checks if all parent relations are consistent.
	 * @return true iff OK
	 */
	private boolean checkParents() {
		Collection<Component<T>> comps = getComponents();
		HashSet<Component<T>> compsH = new HashSet<>(comps); // to improve lookup speed
		
		for (Component<?> c : comps) {
			Component<?> p = c.getParent();
			if (p == null) {
				continue;
			}
			
			// check if parent is listed in 'components' (use HashSet for speed)
			if (!compsH.contains(p)) {
				System.out.println("*** component not contained in components list: " + c.ID);
				return false;
			}
						
			// check if parent is not c itself
			if (p == c) {
				System.out.println("*** self-referring parent link in component " + c.ID);
				return false;
			}				
		}
		return true;
	}
	
	/**
	 * Checks if all child relations are consistent.
	 * 
	 * @return true iff OK
	 */
	private boolean checkChildren() {
		for (Component<?> c : getComponents()) {
			// check if children is a list (not null)
			if (c.getChildren() == null) {
				System.out.println("*** children is null (not an empty list) in component " + c.ID);
				return false;
			}
			
			// check if all children of c have c as parent
			for (Component<?> child : c.getChildren()) {
				// check if c is parent of its child:
				if (child.getParent() != c) {
					System.out.println("*** incorrect parent link in component " + child.ID);
					return false;
				}
			}	
		}
		return true;
	}
	
	/**
	 * Checks if the tree has a unique root.
	 * 
	 * @return true iff OK
	 */
	private boolean checkRoot() {
		// check if a single root exists and is properly marked (with null parent)
		int rootcnt = 0;
		Component<T> root = getRoot();
		for (Component<?> c : getComponents()) {
			if (c.getParent() == null) {
				rootcnt++;
				if (c != root) {
					System.out.println("*** found non-root node without parent: " + c.ID);
					return false;
				}
			}
		}
		if (rootcnt > 1) {
			System.out.println("*** found multiple roots: " + rootcnt);
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if all local pixels of have the same value as {@link Component#getLevel()}.
	 * 
	 * @return true iff OK
	 */
	private boolean checkLevels() {
		for (Component<?> c : getComponents()) {
			int lc = c.getLevel();
			
			// check if parent's level is greater than this component's level:
			Component<?> p = c.getParent();
			if (p != null) {
				int lp = p.getLevel();
				if (lc >= lp) {	// must be lc < lp
					throw new RuntimeException("checkLevels: value lc >= lp for component " + c.ID);
				}
			}
			
			// check if all local points have the same level as this component's level:
			for (Pixel x : c.getLocalPixels()) {
				if (x.val != lc) {
					throw new RuntimeException("checkLevels: local pixel with wrong value in component " + c.ID);
					// return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the size of each component is the same as the number of local pixels plus the combined size of all
	 * children.
	 *
	 * @return true iff OK
	 */
	private boolean checkSize() {
		for (Component<?> c : getComponents()) {
			int csize = c.getLocalPixels().size();
			for (Component<?> cc : c.getChildren()) {
				csize = csize + cc.getSize();
			}
			if (csize != c.getSize()) {
				System.out.println("*** wrong size in component " + c.ID);
				return false;
			}
		}
		return true;
	}
	
	// ---------------------------------------------------------------
	
	@Override
	public String toString() { 
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		this.printToStream(ps);
		return os.toString();
	}
	
	private void printToStream(PrintStream strm) {
		strm.print(this.getClass().getSimpleName() + ": ");
		strm.format("   number of components: %d\n", getComponents().size());
		for (Component<?> r : getComponents()) {
			r.printToStream(strm);
			strm.println();
		}
		strm.println("root = " + getRoot());
	}
	
	// ---------------------------------------------------------------------
	
	private static int INDENT = 4;
	
	// print a forest with multiple roots
	@SuppressWarnings("unused")
	private static String toStringRecursive(ComponentTree<?> rt) { 
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		
		for (Component<?> c : rt) {
			if (c.getParent() == null || c.getParent() == c) {	// start at any root
				printToStreamRecursive(c, ps, 0);
			}	
		}	
		return os.toString();
	}
	
	// print tree with a single root
	@SuppressWarnings("unused")
	private static String toStringRecursive(Component<?> rt) { 
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		printToStreamRecursive(rt, ps, 0);	
		return os.toString();
	}
	
	
	private static void printToStreamRecursive(Component<?> c, PrintStream ps, int indent) {
		for (int i=0; i < indent; i++) {
			ps.append('|');
			for (int j = 0; j < INDENT; j++) {
				ps.append(' ');
			}
		}
		c.printToStream(ps);
		ps.println();
		for (Component<?> child : c.getChildren()) {
			printToStreamRecursive(child, ps, indent + 1);
		}
	}

}

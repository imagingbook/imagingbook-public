package imagingbook.common.mser.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.mser.MserData;
import imagingbook.common.mser.components.PixelMap.Pixel;

/**
 * This class is a re-implementation of the "quasi-linear-time" component
 * tree algorithm which is based on efficient, tree-based union finding as described in 
 * L. Vincent and P. Soille, "Watersheds in digital spaces: An efficient 
 * algorithm based on immersion simulations", IEEE Transactions on 
 * Pattern Analysis and Machine Intelligence 13(6), 583–598 (1991).
 * This algorithm is used in the original MSER paper by Matas et al. (2004).
 * 
 * @author WB
 *
 * @param <T> the type of properties to be attached to components
 */
public class ComponentTreeGlobalImmersion<T> extends ComponentTree<T> {
	
	private final Collection<Component<T>> components;
	private final Component<T> root;
	
	/**
	 * Constructor. Creates a new component tree from a {@link PixelMap}
	 * representing a gray-level image.
	 * @param pm a {@link PixelMap}
	 */
	public ComponentTreeGlobalImmersion(PixelMap pm) {
		root = collectComponents(pm);
		components = new ArrayList<>();
		buildTree();
	}
	
	@Override
	public Component<T> getRoot() {
		return root;
	}

	@Override
	public Collection<Component<T>> getComponents() {
		return components;
	}
	
	// ------------------------------------------------------------
	
	/**
	 * Collects all image pixels (in sorted order) and creates a preliminary
	 * component tree whose root is returned.
	 * 
	 * @param P a {@link PixelMap} (the input image)
	 * @return
	 */
	private Component<T> collectComponents(PixelMap P) {
		final Pixel[] Q = P.getPixelVector();
		Arrays.sort(Q);		// sorts pixels in P by increasing gray-level (value)
		
		final ComponentMap<T> C = new ComponentMap<T>(P.width, P.height);
		Component<T> r = null;			// root of the component tree
		
		for (Pixel p : Q) {
			Component<T> cp = C.makeComponent(p); // creates a new sub-tree (with cp as root)
			Pixel n = p.getNextNeighbor();
			while (n != null) {
				Component<T> cn = C.getComponent(n);
				if (cn != null) {
					// the neighbor component is already part of a tree)
					// find the associated roots
					Component<T> rp = cp.findRoot();	// root of p
					Component<T> rn = cn.findRoot();	// root of n

					if(rp != rn) {	// p and n have different roots
						if (rp.getLevel() == rn.getLevel() &&  rp.getHeight() < rn.getHeight()) {
							// n's root becomes the parent of p's root
							join(rp, rn);
							r = rn;
						} else {
							// rp.val > rn.val, p' root becomes the parent of n's root
							join(rn, rp);
							r = rp;
						}
					}
				}	
				//else: the neighbor is in no forest, i.e. unprocessed so we don't care
				n = p.getNextNeighbor();
			}
		}
		
		return r;
	}
	
	/**
	 *  Joins the disjoint trees with root nodes r1 and r2.
	 *  Makes the tree rooted at r1 a sub-tree under root r2, i.e.,
	 *  r1 becomes a child node of r2. Node r2 is the root of
	 *  the joined tree.
	 *  
	 * @param r1 the root of the first (child) sub-tree
	 * @param r2 the root of the second (master) sub-tree
	 */
	private void join(Component<T> r1, Component<T> r2) {
		r1.setParent(r2);
		r2.addChild(r1);
		r2.addToSize(r1.getSize());
		r2.setHeight(Math.max(r2.getHeight(), r1.getHeight() + 1));
	}
	
	// ----------------------------------------------------------------
	
	/**
	 * Builds the tree starting at the {@link #root} component.
	 */
	private void buildTree() {
		// Find all extremal regions and connect them:
		linkExtremalComponents(root, root);	// fills components
		//components.addAll(linkExtremalComponents2(root, root));
		
		// Reduce the remaining (non-extremal) components
		// into the associated (ancestor) extremal components:
		for (Component<T> c : components) {
			reduceNonExtremalComponents(c);
		}
	}
	
	// -----------------------------------------------------------------
	
	/**
	 * Recursively links extremal components by omitting intermediate
	 * non-extremal components. Initially applied to the root component.
	 * Collects extremal regions into {@link #components}.
	 * @param c the current component (to be inspected)
	 * @param e the closest extremal component on the ancestor path
	 */
	private void linkExtremalComponents(Component<T> c, Component<T> e) {
		if (c.isExtremal()) {
			components.add(c);
			//System.out.println("          adding " + c.toString() + " extremalRegions.size()=" + extremalRegions.size());
			if (c != e) {
				c.setParent(e);
				e.addChild(c);	// children is a HashSet, i.e., no duplicate entries!
			}
			e = c;
		}
		// do the same for all children
		for (Component<T> child : new ArrayList<>(c.getChildren())) {// copy needed since recursive call may change the child list
			linkExtremalComponents(child, e);
		}
	}
	
	// Version 2: returns a set of extremal components (book version)
	@SuppressWarnings("unused")
	private Collection<Component<T>> linkExtremalComponents2(Component<T> c, Component<T> e) {
		Collection<Component<T>> E = new LinkedList<>();
		if (c.isExtremal()) {
			E.add(c);
			//System.out.println("          adding " + c.toString() + " extremalRegions.size()=" + extremalRegions.size());
			if (c != e) {
				c.setParent(e);
				e.addChild(c);	// children is a HashSet, i.e., no duplicate entries!
			}
			e = c;
		}
		// do the same for all children
		for (Component<T> child : new ArrayList<>(c.getChildren())) {// copy needed since recursive call may change the child list
			E.addAll(linkExtremalComponents2(child, e));
		}
		return E;
	}
	
	/**
	 * Eliminates all non-extremal components attached to the given component.
	 * Initially invoked on an extremal component.
	 * All local pixels from a non-extremal child components are collected
	 * and merged to the parent component.
	 * @param c an extremal component
	 */
	private void reduceNonExtremalComponents(Component<T> c) {
		if (!c.getChildren().isEmpty()) {		
			// visit each child and (if not extremal) collect all its points into c
			Iterator<Component<T>> iter = c.getChildren().iterator();
			while (iter.hasNext()) {
				Component<T> cc = iter.next();
				if (!cc.isExtremal()) {
					reduceNonExtremalComponents(cc);		// recursive call
					c.getLocalPixels().addAll(cc.getLocalPixels());
					cc.getLocalPixels().clear();
					iter.remove();  // remove child from c's children
				}
			}
		}
	}
		
	// -------------------------------------------------------------------------------------
	
	/**
	 * This nested class represents a 2D container for elements of type {@link Component}.
	 * The array is of the same size as the image, with one component for every image pixel.
	 * The array is initially empty, i.e. contains only {@code null} values.
	 * Components are created on demand (by {@link #makeComponent(Pixel)} during building
	 * of the component tree.
	 * The method {@link #getComponent(Pixel)} is used to check if the component for
	 * a specific image point exists.

	 * @param <T> the type of data that may be attached to components (e.g., {@link MserData})
	 */
	private static class ComponentMap<T> implements Iterable<Component<T>> {
		
		private final int width, height;
		private final Object[] compArr;
		
		public ComponentMap(int width, int height) {
			this.width = width;
			this.height = height;
			this.compArr = new Object[this.width * this.height];
		}
		
		/**
		 * Creates and returns a new component for the specified image point.
		 * An exception is thrown if a component already exists for this
		 * point.
		 * @param p the image point
		 * @return the new component
		 */
		public Component<T> makeComponent(Pixel p) {
			int idx = width * p.y + p.x;
			if (compArr[idx] != null) {
				throw new RuntimeException("component already exists for point " + ((PntInt)p).toString());
			}
			Component<T> c = new Component<>(p.val, idx);	// new Component<>(p.val);
			c.addPoint(p);
			compArr[idx] = c;
			return c;
		}
		
		/**
		 * Returns the component for the specified image point or
		 * {@code null} if it does not exist.
		 * @param p the image point
		 * @return the associated component or {@code null}
		 */
		@SuppressWarnings("unchecked")
		public Component<T> getComponent(Pixel p) {
			return (Component<T>) compArr[p.y * width + p.x];
		}
		
		/**
		 * Iterator to loop over all {@link Component} elements of this {@link ComponentMap}.
		 * Note that this iterator may return null elements!
		 */
		@Override
		public Iterator<Component<T>> iterator() {
			return new Iterator<Component<T>>() {
				private int pos = 0;

				@Override
				public boolean hasNext() {
					return pos < compArr.length;
				}

				@Override
				public Component<T> next() {
					@SuppressWarnings("unchecked")
					Component<T> c = (Component<T>) compArr[pos];
					pos++;
					return c;
				}
			};
		}

	}
	
}

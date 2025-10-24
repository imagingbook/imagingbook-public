/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.mser.components;

import imagingbook.common.mser.components.PixelMap.Pixel;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class represents a connected component (i.e., a binary image region). Instances of this class form the nodes of
 * a {@link ComponentTree}.
 *
 * @param <T> the type of properties that can be attached to instances of this class
 * @author WB
 * @version 2022/11/19
 */
public class Component<T> {
	
	/** The ID number of this component (only used for debugging). */
	public final int ID;
	
	// ------------------------------------------------------------
	
	private final int level;				// intensity level associated with this region
	private final List<Pixel> points;		// local points in this component (does not include points in child components)	
	private int size;						// the total size of this component (number of points, includes children) 
	
	private Component<T> parent;			// reference to the parent component (null if this is the root)
	private Component<T> shortcut;			// reference to a component in the same tree but closer to the root
	private final Set<Component<T>> children; // the components at the next lower level
	private int height;						// the height of the sub-tree rooted at this component
	
	private T properties = null;			// additional component properties to be attached

	// ------------------------------------------------------------
	
	/**
	 * Constructor.
	 * @param level the maximum pixel value in this component
	 * @param id a unique component id (assigned by the factory)
	 */
	protected Component(int level, int id) {
		this.ID = id;
		this.level = level;
		this.points = new LinkedList<>();
		this.parent = null;
		this.shortcut = null;
		this.children = new HashSet<>();
		this.size = 0;
		this.height = 0;
	}
	
	// --------------------------------------------------------

	/**
	 * Returns the parent component of this component ({@code null} if this node is the tree's root).
	 *
	 * @return the parent component
	 */
	public Component<T> getParent() {
		return parent;
	}

	void setParent(Component<T> parent) {
		this.parent = parent;
		this.shortcut = parent;
	}
	
	void addToSize(int sizeInc) {
		size = size + sizeInc;
	}

	/**
	 * Returns the size (number of pixels) of this component.
	 * 
	 * @return the size of this component
	 */
	public int getSize() {
		return size;
	}
	
	void addChild(Component<T> c) {
		children.add(c);
	}
	
	/**
	 * Returns the list of this component's child components.
	 * 
	 * @return a (possibly empty) list of components, never {@code null}
	 */
	public Collection<Component<T>> getChildren() {
		if (children == null) {
			return Collections.emptyList();
		}
		else {
			return children;
		}
	}

	/**
	 * Returns the level (max. gray value) of this component.
	 * 
	 * @return the component's level
	 */
	public int getLevel() {
		return this.level;
	}
	
	void addPoint(Pixel p) {
		points.add(p);
		size = size + 1;
	}
	
	/**
	 * Returns the height of the sub-tree which this component is the root of.
	 * 
	 * @return the height of the sub-tree rooted by this component
	 */
	public int getHeight() {
		return this.height;
	}
	
	void setHeight(int newHeight) {
		height = newHeight;
	}

	/**
	 * Returns a list of "local" pixels that directly belong to this component All these pixels have the same 'level' as
	 * the component itself.
	 *
	 * @return the component's local pixels
	 */
	public Collection<Pixel> getLocalPixels() {
		return this.points;
	}

	/**
	 * Returns a collection of all pixels of this component, including the component's local pixels and the pixels of
	 * all child components. Not needed in actual code, used only for debugging.
	 *
	 * @return all pixels contained in this component
	 */
	public Collection<Pixel> getAllPixels() {
		Collection<Pixel> compPoints = this.getChildPixels();
		compPoints.addAll(this.points);
		return compPoints;
	}
	
//	public Collection<Pixel> getAllPixels() {
//		Collection<Pixel> allPoints = new ArrayList<>(this.size);
//		allPoints.addAll(this.points);
//		allPoints.addAll(this.getChildPixels());
//		return allPoints;
//	}

	/**
	 * Returns a collection of all pixels contained in the children of this component.
	 *
	 * @return all pixels from child components
	 */
	public Collection<Pixel> getChildPixels() {
		Collection<Pixel> childPoints = new ArrayList<>(this.size);
		for (Component<T> child : this.children) {
			childPoints.addAll(child.getAllPixels());
		}
		return childPoints;
	}
	
	/**
	 * Sets the properties of this component.
	 * 
	 * @param properties a property object (of type T)
	 */
	public void setProperties(T properties) {
		this.properties = properties;
	}
	
	/**
	 * Returns the properties attached to this component.
	 * @return the properties attached to this component
	 */
	public T getProperties() {
		return properties;
	}
	// ------------------------------------------------------------------

	/**
	 * Recursively locates the root of the tree that contains this component, returning the first ancestor node that has
	 * no parent, which my be this node itself. The {@link #shortcut} field is used to quickly move up to nodes closer
	 * to the root. The {@link #shortcut} field is updated "on the way back", i.e., by the unwinding recursion.
	 *
	 * @return the root of the sub-tree containing this component
	 */
	Component<T> findRoot() {
		if (this.isRoot()) {
			return this;
		}
		else {
			shortcut = shortcut.findRoot(); // unwinding recursion updates shortcut 
			return shortcut;
		}
	}

	/**
	 * Returns {@code true} if this component is the root of the associated {@link ComponentTree}.
	 *
	 * @return {@code true} if this component is the root
	 */
	public boolean isRoot() {	// TODO: change/clean/check!
		return parent == null; // || this.parent == this;
	}
	
	/**
	 * Returns {@code true} if this component represents an extremal region.
	 * 
	 * @return {@code true} if the component is extremal
	 */
	public boolean isExtremal() {
		return isRoot() || parent.level > this.level;
	}
	
	// ----------------------------------------------------------------------
	
//	@Override
//	public int compareTo(Component<?> other) {
//		return Integer.compare(this.level, other.level);
//	}	
	
	// -------------------------------------------------------------------------
	
	@SuppressWarnings("unused")
	private String listChildIds() {
		int[] ids = new int[this.children.size()];
		int i = 0;
		for (Component<T> child : children) {
			ids[i] = child.ID;
			i++;
		}
		return Arrays.toString(ids);
	}


	/**
	 * Sorts a list of Components by (decreasing) component size, i.e., the largest component (with the most pixels)
	 * becomes the first.
	 *
	 * @param components a list of {@link Component} instances
	 */
	public static void sortBySize(List<? extends Component<?>> components) {
		Comparator<Component<?>> cmp = new Comparator<Component<?>>() {
			@Override
			public int compare(Component<?> mser1, Component<?> mser2) {
				return Integer.compare(mser2.getSize(), mser1.getSize());
			}
		};
		Collections.sort(components, cmp);
	}

	/**
	 * Sorts a list of Components by (increasing) component level, i.e., the component with the lowest level becomes the
	 * first.
	 *
	 * @param components a list of {@link Component} instances
	 */
	public static void sortByLevel(List<? extends Component<?>> components) {
		Comparator<Component<?>> cmp = new Comparator<Component<?>>() {
			@Override
			public int compare(Component<?> mser1, Component<?> mser2) {
				return Integer.compare(mser1.level, mser2.level);
			}
		};
		Collections.sort(components, cmp);
	}
	

	// ---------------------------
	
	void printToStream(PrintStream strm) {
		strm.format("Component %d(%d): size=%d locPts=%d chldPts=%d allPts=%d parent=%s",
				this.ID, this.level, 
				this.size,
				this.points.size(),
				this.getChildPixels().size(),
				this.getAllPixels().size(),
				(this.parent == null) ? "x" : (this.parent.ID + "(" + this.parent.level + ")")
				);
	}
	
	@Override
	public String toString() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		this.printToStream(ps);
		return os.toString();
	}
	
}
package imagingbook.common.mser.components;

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

import imagingbook.common.mser.components.PixelMap.Pixel;

/**
 * This class represents a connected component (binary image region), i.e.,
 * the node of a component tree.
 *
 * @param <T> the type of properties that can be attached to instances
 */
public class Component<T> { // implements Comparable<Component<?>>
	
	static int NEXT_ID = 0;	// IDs are only used for debugging
	public final int ID;				// unique component ID, for debugging only	
	
	// ------------------------------------------------------------
	
	private final int level;			// intensity level associated with this region
	private final List<Pixel> points;	// local points in this component (does not include points in child components)	
	private int size;					// the total size of this component (number of points, includes children) 
	
	private Component<T> parent;			// reference to the parent component (null if this is the root)
	private Component<T> shortcut;			// reference to a component in the same tree but closer to the root
	private final Set<Component<T>> children; // the components at the next lower level
	private int height;						// the height of the sub-tree rooted at this component
	
	private T properties = null;			// additional component properties to be attached

	// ------------------------------------------------------------
	
	/**
	 * Constructor. 
	 * Assigns a unique component id.
	 * @param level the maximum pixel value in this component
	 */
	public Component(int level) {
		this(level, NEXT_ID++);
	}
	
	/**
	 * Constructor.
	 * @param level the maximum pixel value in this component
	 * @param id a unique component id (assigned by the factory)
	 */
	Component(int level, int id) {
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
	
	public int getSize() {
		return size;
	}
	
	void addChild(Component<T> c) {
		children.add(c);
	}
	
	/**
	 * Returns the list of this component's child components.
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

	public int getLevel() {
		return this.level;
	}
	
	void addPoint(Pixel p) {
		points.add(p);
		size = size + 1;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	void setHeight(int newHeight) {
		height = newHeight;
	}
	
	/**
	 * Returns a list of "local" pixels that directly belong to this component
	 * All these pixels have the same 'level' as the component itself.
	 * @return the component's local pixels
	 */
	public Collection<Pixel> getLocalPixels() {
		return this.points;
	}
	
	/**
	 * Returns a collection of all pixels of this component,
	 * including the component's local pixels
	 * and the pixels of all child components.
	 * Not needed in actual code, used only for debugging.
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
	 * Returns a collection of all pixels contained in the
	 * children of this component.
	 * @return all pixels from child components
	 */
	public Collection<Pixel> getChildPixels() {
		Collection<Pixel> childPoints = new ArrayList<>(this.size);
		for (Component<T> child : this.children) {
			childPoints.addAll(child.getAllPixels());
		}
		return childPoints;
	}
	
	public void setProperties(T properties) {
		this.properties = properties;
	}
	
	public T getProperties() {
		return properties;
	}
	// ------------------------------------------------------------------
	
	/**
	 * Recursively locates the root of the tree that contains this component,
	 * returning the first ancestor node that has no parent, which my be this
	 * node itself. 
	 * The {@link #shortcut} field is used to quickly move up to nodes closer to
	 * the root. The {@link #shortcut} field is updated "on the way back", i.e.,
	 * by the unwinding recursion.
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
	
	public boolean isRoot() {	// TODO: change/clean/check!
		return parent == null; // || this.parent == this;
	}
	
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
	 * Sorts a list of Components by (decreasing) component size, i.e.,
	 * the largest component (with the most pixels) becomes the first.
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
	 * Sorts a list of Components by (increasing) component level, i.e.,
	 * the component with the lowest level becomes the first.
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
	
//	public void printToStream(PrintStream strm) {
//		strm.format("Component %d(%d): size=%d locPts=%d chldPts=%d allPts=%d parent=%s shortcut=%s children=%s extrml=%b", 
//				this.ID, this.level, 
//				this.size,
//				this.points.size(),
//				this.getChildPoints().size(),
//				this.getAllPoints().size(),
//				(this.parent == null) ? "x" : (this.parent.ID + "(" + this.parent.level + ")"),
//				(this.shortcut == null) ? "x" : (this.shortcut.ID + "(" + this.shortcut.level + ")"),
//				//this.findRoot().ID,
//				this.listChildIds(),
//				this.isExtremal()
//				);
//	}
	
	public void printToStream(PrintStream strm) {
		strm.format("Component %d(%d): size=%d locPts=%d chldPts=%d allPts=%d parent=%s",
				this.ID, this.level, 
				this.size,
				this.points.size(),
				this.getChildPixels().size(),
				this.getAllPixels().size(),
				(this.parent == null) ? "x" : (this.parent.ID + "(" + this.parent.level + ")")
				);
	}
	
	public String toString() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		this.printToStream(ps);
		return os.toString();
	}
	
	public String toStringShort() {
		return String.format("%d(%d)", ID, level);
	}
	

	
	public String levelsToString() {
		int[] levels = new int[this.getSize()];
		int i = 0;
		for (Pixel p : this.points) {
			levels[i] = p.val;
			i++;
		}
		Arrays.sort(levels);
		return Arrays.toString(levels);
	}
	
}
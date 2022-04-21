package imagingbook.common.regions.segment;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.Contour;

/**
 * Binary region backed by the label array of a region segmenter.
 * A {@link SegmentationBackedRegion} instance does not have its own list or array of 
 * contained pixel coordinates but refers to the label array of the
 * enclosing {@link BinaryRegionSegmentation} instance.
 */
public class SegmentationBackedRegion extends BinaryRegion {

	private final int label;									// the label of this region
	private final BinaryRegionSegmentation segmentation;		// the segmentation backing this region
	private int size = 0;
	private int left = Integer.MAX_VALUE;
	private int right = -1;
	private int top = Integer.MAX_VALUE;
	private int bottom = -1;

	private Contour outerContour = null;
	private List<Contour> innerContours = null;

	// summation variables used for various statistics
	private long x1Sum = 0;
	private long y1Sum = 0;
	private long x2Sum = 0;
	private long y2Sum = 0;
	private long xySum = 0;

	// ------- constructor --------------------------

	SegmentationBackedRegion(int label, BinaryRegionSegmentation seg) {
		this.label = label;
		this.segmentation = seg;
	}

	// ------- public methods --------------------------

	protected int getLabel() {
		return this.label;
	}

	@Override
	public int getSize() {
		return this.size;
	}
	
	@Override
	public long getX1Sum() {
		return x1Sum;
	}

	@Override
	public long getY1Sum() {
		return y1Sum;
	}

	@Override
	public long getX2Sum() {
		return x2Sum;
	}

	@Override
	public long getY2Sum() {
		return y2Sum;
	}

	@Override
	public long getXYSum() {
		return xySum;
	}

	/**
	 * Get the x/y axes-parallel bounding box as a rectangle
	 * (including the extremal coordinates).
	 * @return the bounding box rectangle.
	 */
	@Override
	public Rectangle getBoundingBox() {
		if (right < 0)
			return null;
		else
			return new Rectangle(left, top, right-left + 1, bottom - top + 1);
	}

	@Override
	public Iterator<Pnt2d> iterator() {
		return new RegionPixelIterator();
	}

	/**
	 * Adds a single pixel to this region and updates summation
	 * and boundary variables used to calculate various region statistics.
	 * 
	 * @param u x-position
	 * @param v y-position
	 */
	protected void addPixel(int u, int v) {
		size = size + 1;
		x1Sum = x1Sum + u;
		y1Sum = y1Sum + v;
		x2Sum = x2Sum + u * u;
		y2Sum = y2Sum + v * v;
		xySum = xySum + u * v;
		if (u < left)   left = u;
		if (v < top)    top = v;
		if (u > right)  right = u;
		if (v > bottom)	bottom = v;
	}

	/**
	 * Updates the region's statistics. 
	 * Does nothing but may be overridden by inheriting classes.
	 */
	protected void update() {
	}

	/**
	 * Get the (single) outer contour of this region.
	 * Points on an outer contour are arranged in clockwise
	 * order.
	 * @return the outer contour.
	 */
	@Override
	public Contour getOuterContour() {
		return outerContour;
	}

	public void setOuterContour(Contour.Outer contr) {
		outerContour = contr;
	}

	/**
	 * Get all inner contours of this region.
	 * Points on inner contours are arranged in counter-clockwise order.
	 * @return the list of inner contours.
	 */
	@Override
	public List<Contour> getInnerContours() {	// sort!!!
		return innerContours;
	}

	public void addInnerContour(Contour.Inner contr) {
		if (innerContours == null) {
			innerContours = new LinkedList<>();
		}
		innerContours.add(contr);
	}

	/**
	 * Checks if the given pixel position is contained in this
	 * {@link SegmentationBackedRegion} instance.
	 * @param u x-coordinate
	 * @param v y-coordinate
	 * @return true if (u,v) is contained in this region
	 */
	@Override
	public boolean contains(int u, int v) {
		return segmentation.getLabel(u, v) == this.label;
	}
	
//	@Override
//	public String toString() {
//		Formatter fm = new Formatter(new StringBuilder(), Locale.US);
//		fm.format("Region %d", label);
//		fm.format(", area = %d", size);
//		fm.format(", bounding box = (%d, %d, %d, %d)", left, top, right, bottom );
//		fm.format(", centroid = (%.2f, %.2f)", xc, yc);
//		if (innerContours != null)
//			fm.format(", holes = %d", innerContours.size());
//		String s = fm.toString();
//		fm.close();
//		return s;
//	}

	// --------------------------------------------------------------------------------

	/**
	 * Instances of this class are returned by {@link SegmentationBackedRegion#iterator()},
	 * which implements  {@link Iterable} for instances of class {@link Pnt2d}.
	 */
	private class RegionPixelIterator implements Iterator<Pnt2d> {
		private final int label;					// the corresponding region's label
		private final int uMin, uMax, vMin, vMax;	// coordinates of region's bounding box
		private int uCur, vCur;						// current pixel position
		private PntInt pNext;						// coordinates of the next region pixel
		private boolean first;						// control flag

		RegionPixelIterator() {
			label = SegmentationBackedRegion.this.getLabel();
			Rectangle bb = SegmentationBackedRegion.this.getBoundingBox();
			first = true;
			uMin = bb.x;
			uMax = bb.x + bb.width;
			vMin = bb.y;
			vMax = bb.y + bb.height;
			uCur = uMin;
			vCur = vMin;
			pNext = null;
		}

		/** 
		 * Search from position (uCur, vCur) for the next valid region pixel.
		 * Return the next position as a Point or null if no such point can be found.
		 * Don't assume that (uCur, vCur) is a valid region pixel!
		 * 
		 * @return the next point
		 */
		private Pnt2d.PntInt findNext() {
			// start search for next region pixel at (u,v):
			int u = (first) ? uCur : uCur + 1;
			int v = vCur;
			first = false;
			while (v <= vMax) {
				while (u <= uMax) {
					if (segmentation.getLabel(u, v) == label) { // next pixel found (uses surrounding labeling)
						uCur = u;
						vCur = v;
						return PntInt.from(uCur, vCur);
					}
					u++;
				}
				v++;
				u = uMin;
			}
			uCur = uMax + 1;	// just to make sure we'll never enter the loop again
			vCur = vMax + 1;
			return null;		// no next pixel found
		}

		@Override
		public boolean hasNext() {
			if (pNext != null) {	// next element has been queried before but not consumed
				return true;
			}
			else {
				pNext = findNext();	// keep next pixel coordinates in pNext
				return (pNext != null);
			}
		}

		// Returns: the next element in the iteration
		// Throws: NoSuchElementException - if the iteration has no more elements.
		@Override
		public PntInt next() {
			if (pNext != null || hasNext()) {
				PntInt pn = pNext;
				pNext = null;		// "consume" pNext
				return pn;
			}
			else {
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	} // end of class RegionPixelIterator

} // end of class BinaryRegion
/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.regions;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

/**
 * Defines a binary region that is backed by the label array of a region
 * segmentation. A {@link SegmentationBackedRegion} instance does not have its
 * own list or array of contained pixel coordinates but refers to the label
 * array of the associated {@link BinaryRegionSegmentation} instance.
 * 
 * @author WB
 * @version 2020/12/21
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

	/**
	 * Constructor.
	 * @param label the label (number) assigned to this region
	 * @param seg the backing region segmentation
	 */
	SegmentationBackedRegion(int label, BinaryRegionSegmentation seg) {
		this.label = label;
		this.segmentation = seg;
	}

	// ------- public methods --------------------------

	/**
	 * Returns the label (number) of this region.
	 * @return the region label
	 */
	public int getLabel() {
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
	void addPixel(int u, int v) {
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
	void update() {
	}

	@Override
	public Contour getOuterContour() {	// TODO: invoke some contour tracer if contour not available?
		return outerContour;
	}

	@Override
	void setOuterContour(Contour.Outer contr) {
		outerContour = contr;
	}

	@Override
	public List<Contour> getInnerContours() {
		return (innerContours != null) ? innerContours : Collections.emptyList();
//		return innerContours;
	}

	void addInnerContour(Contour.Inner contr) {
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
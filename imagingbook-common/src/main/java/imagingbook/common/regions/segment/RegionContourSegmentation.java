/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.regions.segment;

import static imagingbook.common.geometry.basic.NeighborhoodType2D.N4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.Contour;
import imagingbook.common.regions.ContourTracer;
import imagingbook.common.util.tuples.Tuple2;

/**
 * <p>
 * Binary region segmenter based on a combined region labeling
 * and contour tracing algorithm as described in [1].
 * Detected regions and contours are 4- or 8-connected.
 * See Sec. 8.2.2 of [2] for additional details.
 * </p>
 * <p>
 * [1] F. Chang, C. J. Chen, and C. J. Lu. A linear-time component labeling
 * algorithm using contour tracing technique. Computer Vision, Graphics,
 * and Image Processing: Image Understanding 93(2), 206-220 (2004).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2020/04/01
 * @version 2022/09/28 revised
 */
public class RegionContourSegmentation extends BinaryRegionSegmentation implements ContourTracer { 
	
	static private final int VISITED = -1;
	
	private List<Contour.Outer> outerContours;
	private List<Contour.Inner> innerContours;
	
	/**
	 * Constructor. Creates a combined region and contour segmenter from the
	 * specified image, which is not modified. The input image is considered binary,
	 * with 0 values for background pixels and values &ne; 0 for foreground pixels.
	 * The 4-neighborhood is used by default ({@link DEFAULT_NEIGHBORHOOD}).
	 * 
	 * @param ip the binary input image to be segmented
	 */
	public RegionContourSegmentation(ByteProcessor ip) {
		this(ip, DEFAULT_NEIGHBORHOOD);
	}
	
	/**
	 * Constructor. Creates a combined region and contour segmenter from the
	 * specified image and neighborhood type (4- or 8-neighborhood). The input image
	 * is considered binary, with 0 values for background pixels and values &ne; 0
	 * for foreground pixels.
	 * 
	 * @param ip the binary input image to be segmented
	 * @param nh the neighborhood type (4- or 8-neighborhood)
	 */
	public RegionContourSegmentation(ByteProcessor ip, NeighborhoodType2D nh) {
		super(ip, nh);
		attachOuterContours();	// attach the outer contour to the corresponding region
		attachInnerContours();	// attach all inner contours to the corresponding region
	}
	
	@Override
	public List<Contour.Outer> getOuterContours() {
		return getOuterContours(false);
	}
	
	@Override
	public List<Contour.Outer> getOuterContours(boolean sort) {
		Contour.Outer[] oca = outerContours.toArray(new Contour.Outer[0]);
		if (sort) {
			Arrays.sort(oca);
		}
		return Arrays.asList(oca);
	}
	
	@Override
	public List<Contour.Inner> getInnerContours() {
		return getInnerContours(false);
	}
	
	@Override
	public List<Contour.Inner> getInnerContours(boolean sort) {
		Contour.Inner[] ica = innerContours.toArray(new Contour.Inner[0]);
		if (sort) {
			Arrays.sort(ica);
		}
		return Arrays.asList(ica);
	}
	
	// non-public methods ------------------------------------------------------------------
	
	@Override
	int[][] makeLabelArray() {
		// Create a label array which is "padded" by 1 pixel, i.e., 
		// 2 rows and 2 columns larger than the image:
		return new int[width + 2][height + 2];	// label array, initialized to zero
	}
	
	@Override
	boolean applySegmentation() {
		outerContours = new ArrayList<>();
		innerContours = new ArrayList<>();
		for (int v = 0; v < height; v++) {	// scan top to bottom, left to right
			int label = 0;					// reset label, scan through horiz. row:
			for (int u = 0; u < width; u++) {
				if (ip.getPixel(u, v) > 0) {	// hit an unlabeled FOREGROUND pixel
					if (label != 0) { // keep using the same label
						setLabel(u, v, label);
					}
					else {	// label == 0
						label = getLabel(u, v);
						if (label == 0) {	// new (unlabeled) region is hit
							label = getNextLabel(); // assign a new region label
							PntInt xs = PntInt.from(u, v);
							int ds = 0;
							Contour.Outer c = traceContour(xs, ds, new Contour.Outer(label));
							outerContours.add(c);
							setLabel(u, v, label);
						}
					}
				} 
				else {	// hit a BACKGROUND pixel
					if (label != 0) { // exiting a region
						if (getLabel(u, v) == BACKGROUND) { // unlabeled - new inner contour
							PntInt xs = PntInt.from(u - 1, v);
							int ds = (NT == N4) ? 2 : 1;
							Contour.Inner c = traceContour(xs, ds, new Contour.Inner(label));
							innerContours.add(c);
						}
						label = 0;
					}
				}
			}
		}
		return true;
	}
	
	// Trace one contour starting at Xs in direction ds	
	private <T extends Contour> T traceContour(PntInt xs, final int ds, T C) {
		final int label = C.getLabel();	// C: empty inner or outer contour		
		
		Tuple2<PntInt, Integer> next = findNextContourPoint(xs, ds);
		PntInt x = next.get0();
		int d = next.get1();	
		C.addPoint(x);
		
		final PntInt xt = x;
		boolean home = xs.equals(xt);
		
		while (!home) {
			setLabel(x, label);
			PntInt xp = x;
			int dn = (d + 6) % 8;
			next = findNextContourPoint(x, dn);
			x = next.get0();
			d = next.get1();
			// are we back at the starting position?
			home = (xp.equals(xs) && x.equals(xt)); // back at start pos.
			if (!home) {
				C.addPoint(x);
			}
		}
		return C;
	}
	
	private static final int[][] delta = {
			{ 1,0}, { 1, 1}, {0, 1}, {-1, 1}, 
			{-1,0}, {-1,-1}, {0,-1}, { 1,-1}};

	// --------------------------------------------------------------------
	
	// Starts at point X0 in direction d0, returns a tuple holding
	// the next point and the direction in which it was found if successful.
	// Returns null if no successor point is found.
	private Tuple2<PntInt, Integer> findNextContourPoint(final PntInt x0, final int d0) {
		final int step = (NT == N4) ? 2 : 1;
		PntInt x = null;
		int d = d0;
		int i = 0;
		boolean done = false;
		while (i < 7 && !done) {	// N4: i = 0,2,4,6  N8: i = 0,1,2,3,4,5,6
			x = x0.plus(delta[d][0], delta[d][1]);
			if (ip.getPixel(x.x, x.y) == BACKGROUND) {
				setLabel(x, VISITED);	// mark this background pixel not to be visited again
				d = (d + step) % 8;
			} 
			else {	// found a non-background pixel (next pixel to follow)
				done = true;
			}
			i = i + step;
		}
		return (done) ? 
				Tuple2.from(x, d) : 
				Tuple2.from(x0, d0);	// no successor found
	}
	
	// ------------------------------------------------------------------------------
	
	private void attachOuterContours() {
		for (Contour.Outer c : outerContours) {
			int label = c.getLabel();
			BinaryRegion reg = getRegion(label);
			if (reg == null) {
				throw new RuntimeException("could not associate outer contour with label " + label);
			}
			else {
				reg.setOuterContour(c);
			}
		}
	}
	
	private void attachInnerContours() {
		for (Contour.Inner c : innerContours) {
			int label = c.getLabel();
			SegmentationBackedRegion reg = getRegion(label);
			if (reg == null) {
				throw new RuntimeException("could not associate inner contour with label " + label);
			}
			else {
				reg.addInnerContour(c);
			}
		}
	}

	// access methods to the (padded) label array
	@Override
	public int getLabel(int u, int v) {	// (u,v) are image coordinates
		if (u >= -1 && u <= width && v >= -1 && v <= height)
			return labelArray[u + 1][v + 1]; 	// label array is padded (offset = 1)
		else
			return BACKGROUND;
	}
	
	@Override
	void setLabel(int u, int v, int label) { // (u,v) are image coordinates
		if (u >= -1 && u <= width && v >= -1 && v <= height) {
			labelArray[u + 1][v + 1] = label;
		}
	}
	
	private void setLabel(PntInt p, int label) { // (u,v) are image coordinates
		setLabel(p.x, p.y, label);
	}
	
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.regions;

import static imagingbook.common.geometry.basic.NeighborhoodType2D.N4;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;

/**
 * <p>
 * Performs region segmentation on a given binary image. See Ch. 8 of [1] for
 * additional details. This class is abstract, since the implementation depends
 * on the concrete region segmentation algorithm being used. Concrete
 * implementations (subclasses of this class) are
 * {@link BreadthFirstSegmentation}, {@link DepthFirstSegmentation},
 * {@link RecursiveSegmentation}, {@link SequentialSegmentation},
 * {@link RegionContourSegmentation}. Most of the work is done by the
 * constructor(s). If the segmentation has failed for some reason
 * {@link #getRegions()} returns {@code null}.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2021/12/22
 */
public abstract class BinaryRegionSegmentation {
	
	/** The default neighborhood type. */
	public static final NeighborhoodType2D DefaultNeighborhoodT = N4;
	
	static final int Background = 0;
	static final int Foreground = 1;
	
//	final ImageProcessor ip;
	final int width;
	final int height;	
	final NeighborhoodType2D NT;
	
	final int[][] labelArray;
	// label values in labelArray can be:
	//  0 ... unlabeled
	// -1 ... previously visited background pixel
	// >0 ... valid label
	
	private final Map<Integer, SegmentationBackedRegion> regions;
	private final boolean isSegmented;
	
	private final int minLabel = 2;
	private int currentLabel = -1;	// the maximum label assigned sofar
	private int maxLabel = -1;		// the maximum label assigned total
	
	// -------------------------------------------------------------------------
	
	BinaryRegionSegmentation(ByteProcessor ip, NeighborhoodType2D nh) {
//		this.ip = ip;
		this.NT = nh;
		this.width  = ip.getWidth();
		this.height = ip.getHeight();
		this.labelArray = makeLabelArray(ip);
		this.isSegmented = applySegmentation(ip);
		this.regions = (isSegmented) ? collectRegions() : Collections.emptyMap();
	}
	
	int[][] makeLabelArray(ByteProcessor ip) {
		int[][] lA = new int[width][height];	// label array
		// set all pixels to either FOREGROUND or BACKGROUND (by thresholding)
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				lA[u][v] = (ip.get(u, v) != 0) ? Foreground : Background;
			}
		}
		return lA;
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * This method must be implemented by all concrete sub-classes.
	 * @param ip TODO
	 * 
	 * @return true if segmentation was successful
	 */
	abstract boolean applySegmentation(ByteProcessor ip);
	
	/**
	 * Returns the width of the segmented image. 
	 * @return the width of the segmented image
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the height of the segmented image. 
	 * @return the height of the segmented image
	 */
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Returns the minimum label assigned by this segmentation.
	 * @return the minimum label
	 */
	public int getMinLabel() {
		return minLabel;
	}
	
	/**
	 * Returns the maximum label assigned by this segmentation.
	 * @return the maximum label
	 */
	public int getMaxLabel() {
		return maxLabel;
	}
	
	/**
	 * Returns true if the segmentation did complete successfully,
	 * false otherwise.
	 * 
	 * @return true if segmentation was successful
	 */
	public boolean isSegmented() {
		return isSegmented;
	}
	
	/**
	 * Returns an unsorted list of all regions associated with this region labeling.
	 * The returned list is empty if no regions were detected.
	 * See also {@link #getRegions(boolean)}.
	 * 
	 * @return a (possibly empty) list of detected regions
	 */
	public List<BinaryRegion> getRegions() {
		return getRegions(false);	// unsorted
	}
	
	/**
	 * Returns a (optionally sorted) list of all regions associated with this region labeling.
	 * The returned list is empty if no regions were detected.
	 * 
	 * @param sort set {@code true} to sort regions by size (largest regions first).
	 * @return the list of detected regions or {@code null} if the segmentation has failed.
	 */
	public List<BinaryRegion> getRegions(boolean sort) {
		if (regions == null) {
			throw new RuntimeException("regions is null, this should never happen");
		}
		BinaryRegion[] ra = regions.values().toArray(new BinaryRegion[0]);
		if (sort) {
			Arrays.sort(ra);
		}
		return Arrays.asList(ra);
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * Creates a (map) container of {@link BinaryRegion} objects,
	 * collects the region pixels from the label image
	 * and calls {@link SegmentationBackedRegion#update()} to compute
	 * the statistics for each region.
	 * Region label numbers serve as map keys.
	 * @return a map of {@link BinaryRegion} instances.
	 */
	Map<Integer, SegmentationBackedRegion> collectRegions() {
		SegmentationBackedRegion[] regionArray = new SegmentationBackedRegion[maxLabel + 1];
		for (int label = minLabel; label <= maxLabel; label++) {
			regionArray[label] = new SegmentationBackedRegion(label, this);
		}
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int label = getLabel(u, v);
				if (label >= minLabel && label <= maxLabel && regionArray[label] != null) {
					regionArray[label].addPixel(u, v);
				}
			}
		}
		// create a list of regions to return, collect nonempty regions
		Map<Integer, SegmentationBackedRegion> regionMap = new LinkedHashMap<>();
		for (SegmentationBackedRegion r: regionArray) {
			if (r != null && r.getSize() > 0) {
				r.update();	// compute the statistics for this region
				regionMap.put(r.getLabel(), r); //add(r);
			}
		}
		return regionMap;
	}
	
	/**
	 * Returns the label number for the specified image coordinate.
	 * -1 is returned for out-of-image coordinates.
	 * 
	 * @param u the horizontal coordinate.
	 * @param v the vertical coordinate.
	 * @return the label number for the given position or -1 if outside the image
	 */
	public int getLabel(int u, int v) {
		return (u >= 0 && u < width && v >= 0 && v < height) ? labelArray[u][v] : -1;
	}
	
	void setLabel(int u, int v, int label) {
		if (u >= 0 && u < width && v >= 0 && v < height)
			labelArray[u][v] = label;
	}
	
	int getNextLabel() {
		currentLabel = (currentLabel < 1) ? minLabel : currentLabel + 1;
		maxLabel = currentLabel;
		return currentLabel;
	}
	
	boolean isRegionLabel(int i) {
		return (i >= minLabel);
	}

	// --------------------------------------------------

	/**
	 * Finds the region associated to the specified label
	 * or {@code null} if no region for that label exists.
	 * 
	 * @param label the region's label number
	 * @return the region object associated with the given label
	 * 		or {@code null} if it does not exist
	 */
	public SegmentationBackedRegion getRegion(int label) {
		return (label < minLabel || label > maxLabel) ? null : regions.get(label);
	}
	
	/**
	 * Returns the {@link BinaryRegion} instance associated with
	 * the given image position or {@code null} if the segmentation
	 * contains no region covering the given position.
	 * 
	 * @param u the horizontal position.
	 * @param v the vertical position.
	 * @return The associated {@link BinaryRegion} object or {@code null} if
	 * 		this {@link BinaryRegionSegmentation} has no region at the given position.
	 */
	public SegmentationBackedRegion getRegion(int u, int v) {
		return getRegion(getLabel(u, v));
	}

}

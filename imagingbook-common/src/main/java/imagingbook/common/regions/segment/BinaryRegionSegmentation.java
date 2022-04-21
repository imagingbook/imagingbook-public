/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.regions.segment;

import static imagingbook.common.geometry.basic.NeighborhoodType2D.N4;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.regions.BinaryRegion;

/**
 * Performs region segmentation on a given binary image.
 * This class is abstract, since the implementation depends
 * on the concrete region segmentation algorithm being used.
 * Concrete implementations (subclasses of this class) are 
 * {@link BreadthFirstSegmentation},
 * {@link DepthFirstSegmentation},
 * {@link RecursiveSegmentation},
 * {@link SequentialSegmentation},
 * {@link RegionContourSegmentation}.
 * 
 * Practically all work is done by the constructor(s).
 * If the segmentation has failed for some reason
 * {@link #getRegions()} returns {@code null}.
 * 
 * @version 2021/12/22
 */
public abstract class BinaryRegionSegmentation {
	
	public static final NeighborhoodType2D DEFAULT_NEIGHBORHOOD = N4;
	
	public static final int BACKGROUND = 0;
	public static final int FOREGROUND = 1;
	
	protected ImageProcessor ip = null;
	protected final int width;
	protected final int height;	
	protected final NeighborhoodType2D NT;
	
	protected final int[][] labelArray;
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
	
	protected BinaryRegionSegmentation(ByteProcessor ip, NeighborhoodType2D nh) {
		this.ip = ip;
		this.NT = nh;
		this.width  = ip.getWidth();
		this.height = ip.getHeight();
		this.labelArray = makeLabelArray();
		this.isSegmented = applySegmentation();
		this.regions = (isSegmented) ? collectRegions() : null;
		this.ip = null;	// release image
	}
	
	protected int[][] makeLabelArray() {
		int[][] lA = new int[width][height];	// label array
		// set all pixels to either FOREGROUND or BACKGROUND (by thresholding)
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				lA[u][v] = (ip.getPixel(u, v) > 0) ? FOREGROUND : BACKGROUND;
			}
		}
		return lA;
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * This method must be implemented by any concrete sub-class.
	 * @return true if successful.
	 */
	protected abstract boolean applySegmentation();
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getMinLabel() {
		return minLabel;
	}
	
	public int getMaxLabel() {
		return maxLabel;
	}
	
	/**
	 * Returns an unsorted list of all regions associated with this region labeling.
	 * The returned list is empty if no regions were detected.
	 * See also {@link #getRegions(boolean)}.
	 * @return the list of detected regions or {@code null} if the segmentation has failed.
	 */
	public List<BinaryRegion> getRegions() {
		return getRegions(false);	// unsorted
//		return new ArrayList<>(regions.values());
	}
	
	/**
	 * Returns a (optionally sorted) list of all regions associated with this region labeling.
	 * The returned list is empty if no regions were detected.
	 * @param sort set {@code true} to sort regions by size (largest regions first).
	 * @return the list of detected regions or {@code null} if the segmentation has failed.
	 */
	public List<BinaryRegion> getRegions(boolean sort) {
		if (regions == null) {
			throw new RuntimeException("regions is null, this should not happen");
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
	protected Map<Integer, SegmentationBackedRegion> collectRegions() {
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
	 * Get the label number for the specified image coordinate.
	 * @param u the horizontal coordinate.
	 * @param v the vertical coordinate.
	 * @return the label number for the given position.
	 */
	public int getLabel(int u, int v) {
		return (u >= 0 && u < width && v >= 0 && v < height) ? labelArray[u][v] : -1;
	}
	
	protected void setLabel(int u, int v, int label) {
		if (u >= 0 && u < width && v >= 0 && v < height)
			labelArray[u][v] = label;
	}
	
	protected int getNextLabel() {
		currentLabel = (currentLabel < 1) ? minLabel : currentLabel + 1;
		maxLabel = currentLabel;
		return currentLabel;
	}
	
	protected boolean isLabel(int i) {
		return (i >= minLabel);
	}

	// --------------------------------------------------

	/**
	 * Finds the region associated to the given label.
	 * @param label the region's label number.
	 * @return the region object associated with the given label
	 * 		or {@code null} if it does not exist.
	 */
	public SegmentationBackedRegion getRegion(int label) {
		return (label < minLabel || label > maxLabel) ? null : regions.get(label);
	}
	
	/**
	 * Finds the {@link BinaryRegion} instance associated with
	 * the given image position.
	 * @param u the horizontal position.
	 * @param v the vertical position.
	 * @return The associated {@link BinaryRegion} object or null if
	 * 		this {@link BinaryRegionSegmentation} has no region at the given position.
	 */
	public SegmentationBackedRegion getRegion(int u, int v) {
		return getRegion(getLabel(u, v));
	}

}

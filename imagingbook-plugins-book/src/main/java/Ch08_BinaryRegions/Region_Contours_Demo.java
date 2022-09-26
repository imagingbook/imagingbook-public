/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch08_BinaryRegions;

import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.segment.Display;
import imagingbook.common.regions.segment.RegionContourSegmentation;
import imagingbook.common.regions.utils.ContourOverlay;

/**
 * This ImageJ plugin demonstrates the use of the class
 * {@link RegionContourSegmentation} to perform both region labeling and contour
 * tracing simultaneously. See Sec. 8.2.2 of [1] for additional details.
 * Requires a binary image. Zero-value pixels are considered background, all
 * other pixels are foreground. The resulting contours are displayed as a
 * non-destructive vector overlay, the original image is not modified.
 * </p>
 * <p>
 * Note that (different to ImageJ's built-in morphological operators) this
 * implementation does not incorporate the current display lookup-table (LUT).
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2020/12/20
 */
public class Region_Contours_Demo implements PlugInFilter {
	
	/** Neighborhood type (4- or 8-neighborhood). */
	public static NeighborhoodType2D Neighborhood = NeighborhoodType2D.N8;
	/** Set true to list detected regions to the text console. */
	public static boolean ListRegions = false;
	/** Set true to show detected regions in a separate image. */
	public static boolean ShowContours = true;
	
	@Override
	public int setup(String arg, ImagePlus im) { 
		return DOES_8G + NO_CHANGES; 
	}
	
	@Override
	public void run(ImageProcessor ip) {
		
	   	if (!IjUtils.isBinary(ip)) {
				IJ.showMessage("Plugin requires a binary image!");
				return;
		}
	   	
	   	if (!runDialog())
    		return;
	   	
	   	// Make sure we have a proper byte image:
	   	ByteProcessor I = ip.convertToByteProcessor();
	   	
	   	// Create the region segmenter / contour tracer:
		RegionContourSegmentation seg = new RegionContourSegmentation(I, Neighborhood);
		
		// Get a list of detected regions (sorted by size):
		List<BinaryRegion> regions = seg.getRegions(true);
		if (regions == null || regions.isEmpty()) {
			IJ.showMessage("No regions detected!");
			return;
		}

		if (ListRegions) {
			IJ.log("\nDetected regions: " + regions.size());
			for (BinaryRegion R : regions) {
				IJ.log(R.toString());
			}
		}
		
		// Display the contours if desired:
		if (ShowContours) {
			ImageProcessor lip = Display.makeLabelImage(seg, false);
			ImagePlus lim = new ImagePlus("Region labels and contours", lip);
			Overlay oly = new ContourOverlay(seg);
			lim.setOverlay(oly);
			lim.show();
		}
	}
	
	// --------------------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(Region_Contours_Demo.class.getSimpleName());
		gd.addEnumChoice("Neighborhood type", Neighborhood);
		gd.addCheckbox("List regions", ListRegions);
		gd.addCheckbox("Show contours", ShowContours);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		Neighborhood = gd.getNextEnumChoice(NeighborhoodType2D.class);
		ListRegions  = gd.getNextBoolean();
		ShowContours = gd.getNextBoolean();
		return true;
	}
}

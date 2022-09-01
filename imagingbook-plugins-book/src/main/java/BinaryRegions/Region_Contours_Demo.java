/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package BinaryRegions;

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
import imagingbook.common.regions.segment.RegionContourSegmentation;
import imagingbook.common.regions.utils.ContourOverlay;
import imagingbook.common.regions.utils.Display;

/**
 * This ImageJ plugin demonstrates the use of the class {@link RegionContourSegmentation}
 * to perform both region labeling and contour tracing simultaneously.
 * The resulting contours are displayed as a non-destructive vector overlay.
 * 
 * @author WB
 * @version 2020/12/20
 */
public class Region_Contours_Demo implements PlugInFilter {
	
	static NeighborhoodType2D NT = NeighborhoodType2D.N8;
	
	static boolean ListRegions = true;
//	static boolean ListContours = true;
	static boolean ShowContours = true;
	
	public int setup(String arg, ImagePlus im) { 
		return DOES_8G + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
		
	   	if (!IjUtils.isBinary(ip)) {
				IJ.showMessage("Plugin requires a binary image!");
				return;
		}
	   	
	   	if (!getUserInput())
    		return;
	   	
	   	// Make sure we have a proper byte image:
	   	ByteProcessor I = ip.convertToByteProcessor();
	   	
	   	// Create the region segmenter / contour tracer:
		RegionContourSegmentation seg = new RegionContourSegmentation(I, NT);
		
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
		
		// Get the largest region:
//		BinaryRegion Rmax = regions.get(0);
		
//		// Get the outer contour of the largest region:
//		Contour oc =  Rmax.getOuterContour();
//		IJ.log("Points on outer contour of largest region:");
//		for (Pnt2d p : oc) {
//			IJ.log("Point " + p);
//		}
	
//		// Get all inner contours of the largest region:
//		if (ListContours) {
//			IJ.log("\nCountours:");
//			for (BinaryRegion R : regions) {
//				IJ.log("   " + R.toString());
//				IJ.log("       " + oc);
//				
//				List<Contour> ics = R.getInnerContours();
//				if (ics != null && !ics.isEmpty()) {
//					for(Contour ic : R.getInnerContours()) {
//						IJ.log("       " + ic);
//					}
//				}
//			}
//		}
		
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
	
	private boolean getUserInput() {
		GenericDialog gd = new GenericDialog(Region_Contours_Demo.class.getSimpleName());
		gd.addEnumChoice("Neighborhood type", NT);
		gd.addCheckbox("List regions", ListRegions);
//		gd.addCheckbox("List contours", ListContours);
		gd.addCheckbox("Show contours", ShowContours);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		NT = gd.getNextEnumChoice(NeighborhoodType2D.class);
		ListRegions  = gd.getNextBoolean();
//		ListContours = gd.getNextBoolean();
		ShowContours = gd.getNextBoolean();
		return true;
	}
}

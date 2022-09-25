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
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.segment.BinaryRegionSegmentation;
import imagingbook.common.regions.segment.BreadthFirstSegmentation;
import imagingbook.common.regions.segment.DepthFirstSegmentation;
import imagingbook.common.regions.segment.RecursiveSegmentation;
import imagingbook.common.regions.segment.RegionContourSegmentation;
import imagingbook.common.regions.segment.SequentialSegmentation;
import imagingbook.common.regions.utils.Display;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of the region
 * labeling classes in the imagingbook's "regions" package:
 * </p>
 * <ul>
 * <li>{@link BreadthFirstSegmentation},</li>
 * <li>{@link DepthFirstSegmentation},</li>
 * <li>{@link RecursiveSegmentation},</li>
 * <li>{@link RegionContourSegmentation},</li>
 * <li>{@link SequentialSegmentation}.</li>
 * </ul>
 * One of four labeling types can be selected (see the {@link #run(ImageProcessor)} method).
 * All should produce the same results (except {@link RegionContourSegmentation},
 * which may run out of memory easily).
 * Requires a binary (segmented) image.
 * 
 * @author WB
 * @version 2020/12/20
 * 
 */
public class Region_Segmentation_Demo implements PlugInFilter {
	
	public enum LabelingMethod {
		BreadthFirst, 
		DepthFirst, 
		Sequential,
		RegionAndContours,
		Recursive
	}

	public static LabelingMethod Method = LabelingMethod.BreadthFirst;
	public static NeighborhoodType2D Neighborhood = NeighborhoodType2D.N8;
	
	public static boolean ColorRegions = false;
	public static boolean ListRegions = false;
	
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
		
    	if (!getUserInput())
    		return;
    	
    	if (Method == LabelingMethod.Recursive && 
    			!IJ.showMessageWithCancel("Recursive labeling", "This may run out of stack memory!\n" + "Continue?")) {
			return;
    	}
    	
    	// Copy the original to a new byte image:
    	ByteProcessor bp = ip.convertToByteProcessor(false);
    	
    	
		BinaryRegionSegmentation segmenter = null;
		switch(Method) {
		case BreadthFirst : 	
			segmenter = new BreadthFirstSegmentation(bp, Neighborhood); break;
		case DepthFirst : 		
			segmenter = new DepthFirstSegmentation(bp, Neighborhood); break;
		case Sequential : 		
			segmenter = new SequentialSegmentation(bp, Neighborhood); break;
		case RegionAndContours : 
			segmenter = new RegionContourSegmentation(bp, Neighborhood); break;
		case Recursive : 
			segmenter = new RecursiveSegmentation(bp, Neighborhood); break;
		}

		// Retrieve the list of detected regions:
		List<BinaryRegion> regions = segmenter.getRegions(true);	// regions are sorted by size
		
		if (regions == null || regions.isEmpty()) {
			IJ.showMessage("No regions detected!");
			return;
		}
		
		if (ListRegions) {
			IJ.log("Regions sorted by size: " + regions.size());
			for (BinaryRegion r: regions) {
				IJ.log(r.toString());
			}
		}
		
		// Show the resulting labeling as a random color image
		ImageProcessor labelIp = Display.makeLabelImage(segmenter, ColorRegions);
		(new ImagePlus(Method.name(), labelIp)).show();
		
		// Example for processing all regions:
//		for (BinaryRegion r : regions) {
//			double mu11 = mu_11(r);	// example for calculating region statistics (see below)
//			IJ.log("Region " + r.getLabel() + ": mu11=" + mu11);
//		}
    }
    
//	/**
//	 * This method demonstrates how a particular region's central moment
//     * mu_11 could be calculated from the finished region labeling.
//	 * @param r a binary region
//	 * @return
//	 */
//    private double mu_11 (BinaryRegion r) {
//    	Point ctr = r.getCenterPoint();
//    	final double xc = ctr.getX();	// centroid of this region
//    	final double yc = ctr.getY();
//    	double mu11 = 0;
//    	// iterate through all pixels of regions r:
//    	for (Point p : r) {
//    		mu11 = mu11 + (p.getX() - xc) * (p.getY() - yc);
//    	}
//    	return mu11;
//    }
    
    private boolean getUserInput() {
		GenericDialog gd = new GenericDialog(Region_Segmentation_Demo.class.getSimpleName());
		gd.addEnumChoice("Segmentation method", Method);
		gd.addEnumChoice("Neighborhood type", Neighborhood);
		gd.addCheckbox("Color result", ColorRegions);
		gd.addCheckbox("List regions", ListRegions);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		Method = gd.getNextEnumChoice(LabelingMethod.class);
		Neighborhood = gd.getNextEnumChoice(NeighborhoodType2D.class);
		ColorRegions = gd.getNextBoolean();
		ListRegions = gd.getNextBoolean();
		return true;
	}
    
    
}




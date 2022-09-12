/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package BinaryRegions;

import java.awt.Color;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.hulls.ConvexHull;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.segment.RegionContourSegmentation;

/**
 * This ImageJ plugin demonstrates the use of the {@link ConvexHull} class.
 * It performs region segmentation, calculates the convex hull
 * for each region found and then draws the result into a new color
 * image.
 * Requires a binary image.
 * 
 * 
 * @author WB
 * @version 2022/06/24
 * 
 */
public class Convex_Hull_Demo implements PlugInFilter {
	
	static Color ConvexHullColor = Color.blue;
	
	private ImagePlus im = null;
	
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
		
		if (!IjUtils.isBinary(ip)) {
			IJ.showMessage("Plugin requires a binary image!");
			return;
		}
		
		RegionContourSegmentation segmenter = new RegionContourSegmentation((ByteProcessor) ip);
//		RegionLabeling segmenter = new DepthFirstLabeling((ByteProcessor) ip);
		
		List<BinaryRegion> regions = segmenter.getRegions();
		if (regions.isEmpty()) {
			IJ.error("No regions detected!");
			return;
		}
		
		ImageProcessor ip2 = ip.duplicate();
		ip2.add(128);
		
		// draw convex hulls as vector overlay
		Overlay oly = new Overlay();
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
		ola.setStroke(new ColoredStroke(0.5, ConvexHullColor));
		
		for (BinaryRegion r: regions) {
			//ConvexHull hull = new ConvexHull(r);					// takes all region points
			ConvexHull hull = new ConvexHull(r.getOuterContour());	// takes only outer contour points
			ola.addShapes(hull.getShapes());
		}

		ImagePlus im2 = new ImagePlus(im.getShortTitle() + "-convex-hulls", ip2);
		im2.setOverlay(oly);
		im2.show();
	}
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/

package BinaryRegions;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.hulls.ConvexHull;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.segment.RegionContourSegmentation;

/**
 * This plugin demonstrates the use of the {@link ConvexHull} class.
 * It performs region segmentation, calculates the convex hull
 * for each region found and then draws the result into a new color
 * image.
 * Requires a binary (segmented) image.
 * 
 * TODO: convert to overlay display
 * 
 * @author W. Burger
 * @version 2020/12/17
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
		
		ColorProcessor cp = ip.convertToColorProcessor();
		cp.add(128);
		
		for (BinaryRegion r: regions) {
			//ConvexHull hull = new ConvexHull(r);					// takes all region points
			ConvexHull hull = new ConvexHull(r.getOuterContour());	// takes only outer contour points
			
			Line2D[] segments = hull.getSegments();
			drawHull(cp, segments);
		}

		(new ImagePlus(im.getShortTitle() + "-convex-hulls", cp)).show();
	}
	
	// ----------------------------------------------------
	
	private void drawHull(ImageProcessor ip, Line2D[] segments) {
		for (Line2D line : segments) {
			drawSegment(ip, PntDouble.from(line.getP1()), PntDouble.from(line.getP2()));
		}
	}

	private void drawSegment(ImageProcessor ip, Pnt2d p1, Pnt2d p2) {
		int x1 = (int) Math.round(p1.getX());
		int y1 = (int) Math.round(p1.getY());
		int x2 = (int) Math.round(p2.getX());
		int y2 = (int) Math.round(p2.getY());
		ip.drawLine(x1, y1, x2, y2);
	}

}

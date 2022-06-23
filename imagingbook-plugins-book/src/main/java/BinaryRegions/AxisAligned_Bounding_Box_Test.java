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
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Random;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.hulls.AxisAlignedBoundingBox;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.core.plugin.IjPluginName;

/**
 * This plugin creates a binary region segmentation, calculates 
 * the center and major axis and subsequently the major axis-aligned
 * bounding box for each region.
 * Requires a binary image.
 * 
 * @author WB
 * @version 2022/06/23
 */
@IjPluginName("Axis-Aligned Bounding Box")
public class AxisAligned_Bounding_Box_Test implements PlugIn {
	
	static Color CenterColor = Color.magenta;
	static Color BoundingBoxColor = Color.blue;
	
	
	@Override
	public void run(String arg) {
		int N = 100;
		Random rg = new Random(33);
		Pnt2d[] pointArray = new Pnt2d[N];
		for (int i = 0; i < N; i++) {
			pointArray[i] = Pnt2d.from(10 + 100 * rg.nextDouble(), 10 + 100 * rg.nextDouble());
		}
		runPointTest(Arrays.asList(pointArray));
		
	}
	
	private static void runPointTest(Iterable<Pnt2d> points) {
		AxisAlignedBoundingBox box = new AxisAlignedBoundingBox(points);
		
		Pnt2d[] corners = box.getCornerPoints();
		
		Path2D poly = box.getShape(1);
		
		// check if all sample points are inside the bounding box
		for (Pnt2d p : points) {
			if (!poly.contains(p.getX(), p.getY())) {
				IJ.log("point not contained in bounding box: " + p);
				ByteProcessor bp = new ByteProcessor(120, 120);
				bp.invert();
				ImagePlus im = new ImagePlus("Test2", bp);
				Overlay oly = new Overlay();
				ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
				
				ola.setStroke(new ColoredStroke(0.5, Color.blue));
				for (Pnt2d pp : points) {
					ola.addShape(pp.getShape(1));
				}
				ola.setStroke(new ColoredStroke(0.5, Color.red));
				ola.addShape(p.getShape(1));
				
				ola.setStroke(new ColoredStroke(0.5, Color.green));
				ola.addShape(poly);
				im.setOverlay(oly);
				im.show();
				//return;
			}
			//assertTrue("point not contained in bounding box: " + p, poly.contains(p.getX(), p.getY()));
		}
	}


}

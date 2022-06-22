/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package FittingCircles;


import java.awt.Color;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.io.LogStream;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.utils.CircleSampler;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.util.ParameterBundle;

/**
 * Samples points on a perfect circle and creates a new 8-bit image with
 * foreground points (value 255) of the sample points.
 * Image size, circle parameters and noise can be specified.
 * 
 * @author WB
 *
 */
public class Circle_Sample_To_Image implements PlugIn {
	
	static {
		LogStream.redirectSystem();
		PrintPrecision.set(6);
	}
	
	private static String title = Circle_Sample_To_Image.class.getSimpleName();
	private static int W = 400;
	private static int H = 400;
	private static boolean ShowRealCircle = true;
	
	private static double StrokeWidth = 1.0;
	private static Color RealCurveColor = 		new Color(0,176,80);
	
	public static class Parameters implements ParameterBundle {
		
		@DialogLabel("number of points")
		public int N = 100;
		
		@DialogLabel("circle center (xc)")
		public double XC = 200;
		
		@DialogLabel("circle center (yc)")
		public double YC = 190;
		
		@DialogLabel("circle radius (r)")
		public double R = 150;
		
		@DialogLabel("start angle (deg)")
		public double Angle0 = 0;
		
		@DialogLabel("stop angle (deg)")
		public double Angle1 = 45; // was Math.PI/4;
		
		@DialogLabel("x/y noise sigma")
		public double SigmaNoise = 5.0; //2.0;
	};
	
	static Parameters params = new Parameters();
	
	@Override
	public void run(String arg) {
		
		if (!runDialog()) {
			return;
		}
		
		GeometricCircle realCircle = new GeometricCircle(params.XC, params.YC, params.R);
		Pnt2d[] points = new CircleSampler(realCircle).getPoints(params.N, params.Angle0, params.Angle1, params.SigmaNoise);
		
		ImageProcessor ip = new ByteProcessor(W, H);
		ip.invertLut();
		
		for (Pnt2d p : points) {
			//roi.addPoint(p.getX(), p.getY());
			int u = (int) Math.rint(p.getX());
			int v = (int) Math.rint(p.getY());
			ip.putPixel(u, v, 255);
		}	
		
		ImagePlus im = new ImagePlus(title, ip);
		
		if (ShowRealCircle) {
			Overlay oly = new Overlay();
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
			ColoredStroke outerStroke = new ColoredStroke(StrokeWidth, RealCurveColor, 0);
			ola.addShapes(realCircle.getShapes(3), outerStroke);
			im.setOverlay(oly);
		}
		
		im.show();
	}
	
	// ------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		
		gd.addStringField("Title", title, 12);
		gd.addNumericField("image width", W, 0);
		gd.addNumericField("image height", W, 0);
		
		params.addToDialog(gd);
		gd.addCheckbox("show real circle", ShowRealCircle);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		title = gd.getNextString();
		W = (int) gd.getNextNumber();
		H = (int) gd.getNextNumber();
		
		params.getFromDialog(gd);
		ShowRealCircle = gd.getNextBoolean();

		return params.validate();
	}

	

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package FittingLines;


import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.io.LogStream;
import ij.plugin.PlugIn;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.fitting.line.LineSampler;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.util.ParameterBundle;

/**
 * Samples points on a given (ideal) line and creates a new image with 
 * the sample points contained in a {@link PointRoi}.
 * Image size, circle parameters and noise can be specified.
 * The result can be used as a test image for line fitting.
 * 
 * @author WB
 *
 */
public class Line_Sample_To_Roi implements PlugIn {
	
	static {
		LogStream.redirectSystem();
		PrintPrecision.set(6);
	}
	
	private static String title = Line_Sample_To_Roi.class.getSimpleName();
	private static int W = 400;
	private static int H = 400;
	private static boolean ShowRealCircle = true;
	
	private static double StrokeWidth = 1.0;
	private static BasicAwtColor StrokeColor = BasicAwtColor.Green;
	
	public static class Parameters implements ParameterBundle {
		
		@DialogLabel("number of points")
		public int n = 20;
		
		@DialogLabel("start point (x1)")
		public double x1 = 90;
		
		@DialogLabel("start point (y1)")
		public double y1 = 40;
		
		@DialogLabel("end point (x2)")
		public double x2 = 300;
		
		@DialogLabel("end point (y2)")
		public double y2 = 270;
		
		@DialogLabel("x/y noise sigma")
		public double sigma = 5.0; //2.0;
	};
	
	private static Parameters params = new Parameters();
	
	
	@Override
	public void run(String arg) {
		
		if (!runDialog()) {
			return;
		}
		
		Pnt2d pStart = Pnt2d.from(params.x1, params.y1);
		Pnt2d pEnd = Pnt2d.from(params.x2, params.y2);
		AlgebraicLine realLine = AlgebraicLine.from(pStart, pEnd);
		
		LineSampler ls = new LineSampler(pStart, pEnd);
		Pnt2d[] points = ls.getPoints(params.n, params.sigma);	
		PointRoi roi = RoiUtils.toPointRoi(points);
		
		ImagePlus im = NewImage.createByteImage(title, W, H, 1, NewImage.FILL_WHITE);  //new ImagePlus(title, ip);
		im.setRoi(roi);
		
		if (ShowRealCircle) {
			Overlay oly = new Overlay();
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
			ColoredStroke lineStroke = new ColoredStroke(StrokeWidth, StrokeColor.getColor());
			ola.addShape(realLine.getShape(W, H), lineStroke);
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
		addToDialog(params, gd);
		gd.addCheckbox("show real circle", ShowRealCircle);
		gd.addEnumChoice("circle color", StrokeColor);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		title = gd.getNextString();
		W = (int) gd.getNextNumber();
		H = (int) gd.getNextNumber();
		getFromDialog(params, gd);
		ShowRealCircle = gd.getNextBoolean();
		StrokeColor = gd.getNextEnumChoice(BasicAwtColor.class);

		return params.validate();
	}

}

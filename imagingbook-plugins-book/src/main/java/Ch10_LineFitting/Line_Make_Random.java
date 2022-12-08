/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch10_LineFitting;


import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.gui.PointRoi;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.fitting.line.utils.LineSampler;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.ij.DialogUtils.DialogStringColumns;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.util.ParameterBundle;

/**
 * Samples points on a given (ideal) line and creates a new image with the sample points contained in a
 * {@link PointRoi}. Image size, circle parameters and noise can be specified. The result can be used as a test image
 * for line fitting. Random seed can be set for repeatable results.
 *
 * @author WB
 * @version 2022/12/08
 */
public class Line_Make_Random implements PlugIn {
	
	public static class Parameters implements ParameterBundle<Line_Make_Random> {		
		
		@DialogLabel("image title")@DialogStringColumns(12)
		public String Title = "RandomLine"; // Line_Make_Random.class.getSimpleName();
		
		@DialogLabel("image width")
		public int W = 400;
		@DialogLabel("image height")
		public int H = 400;
		
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
		public double sigma = 5.0;
		@DialogLabel("Random seed (0 = none)")
		public int seed = 0;

		@DialogLabel("show real line")
		public boolean ShowRealCurve = true;
		@DialogLabel("line color")
		public BasicAwtColor StrokeColor = BasicAwtColor.Green;
		@DialogLabel("stroke width")
		public double StrokeWidth = 1.0;
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
		
		LineSampler ls = new LineSampler(pStart, pEnd, params.seed);
		Pnt2d[] points = ls.getPoints(params.n, params.sigma);	
		
		ImagePlus im = NewImage.createByteImage(params.Title, params.W, params.H, 1, NewImage.FILL_BLACK);
		im.setRoi(RoiUtils.toPointRoi(points));
		
		ImageProcessor ip = im.getProcessor();
		for (Pnt2d p : points) {
			int u = (int) Math.rint(p.getX());
			int v = (int) Math.rint(p.getY());
			ip.putPixel(u, v, 255);
		}
		ip.invertLut();
		
		
		if (params.ShowRealCurve) {
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
			ColoredStroke lineStroke = new ColoredStroke(params.StrokeWidth, params.StrokeColor.getColor());
			ola.addShape(realLine.getShape(params.W, params.H), lineStroke);
			im.setOverlay(ola.getOverlay());
		}
		
		im.show();
	}
	
	// ------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addMessage(DialogUtils.makeLineSeparatedString(
				"This plugin samples points on a given (ideal) line and",
				"creates a new image with the sample points marked and also",
				"contained in a ROI (float coordinates)."
				));
		
		addToDialog(params, gd);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		getFromDialog(params, gd);
		return params.validate();
	}

}

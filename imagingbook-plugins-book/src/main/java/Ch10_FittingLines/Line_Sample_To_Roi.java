/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch10_FittingLines;


import static ij.gui.NewImage.FILL_WHITE;
import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.plugin.PlugIn;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.fitting.line.LineSampler;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.ij.DialogUtils.DialogStringColumns;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.util.ParameterBundle;

/**
 * Samples points on a given (ideal) line and creates a new image with 
 * the sample points contained in a {@link PointRoi}.
 * Image size, circle parameters and noise can be specified.
 * The result can be used as a test image for line fitting.
 * 
 * @author WB
 * @version 2022/09/30
 * @see Line_Fit_From_Roi
 */
public class Line_Sample_To_Roi implements PlugIn {
	
	public static class Parameters implements ParameterBundle {		
		@DialogLabel("title")@DialogStringColumns(12)
		public String Title = Line_Sample_To_Roi.class.getSimpleName();
		
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
		
		@DialogLabel("show real line")
		public boolean ShowRealLine = true;
		@DialogLabel("line color")
		public BasicAwtColor LineColor = BasicAwtColor.Green;
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
		
		LineSampler ls = new LineSampler(pStart, pEnd);
		Pnt2d[] points = ls.getPoints(params.n, params.sigma);	
		PointRoi roi = RoiUtils.toPointRoi(points);
		
		ImagePlus im = NewImage.createByteImage(params.Title, params.W, params.H, 1, FILL_WHITE);
		im.setRoi(roi);
		
		if (params.ShowRealLine) {
			Overlay oly = new Overlay();
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
			ColoredStroke lineStroke = new ColoredStroke(params.StrokeWidth, params.LineColor.getColor());
			ola.addShape(realLine.getShape(params.W, params.H), lineStroke);
			im.setOverlay(oly);
		}
		
		im.show();
	}
	
	// ------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		addToDialog(params, gd);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		getFromDialog(params, gd);
		return params.validate();
	}

}

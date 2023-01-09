/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch11_Circle_Ellipse_Fitting;


import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.gui.PointRoi;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.utils.CircleSampler;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.ij.DialogUtils.DialogStringColumns;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.util.ParameterBundle;
import imagingbook.core.plugin.JavaDocHelp;

import static imagingbook.common.ij.DialogUtils.addToDialog;

/**
 * Samples points on a given (ideal) circle and creates a new image with the
 * sample points marked and also contained in a {@link PointRoi}. Image size,
 * circle parameters and noise can be specified. The result can be used as a
 * test image for circle fitting. Note that the resulting image has an inverted
 * LUT, i.e., the background value is 0 and marked points have value 255.
 * 
 * @author WB
 * @version 2022/10/03
 */
public class Circle_Make_Random implements PlugIn, JavaDocHelp {
	
	public static class Parameters implements ParameterBundle<Circle_Make_Random> {
		
		@DialogLabel("image title")@DialogStringColumns(12)
		public String Title = "RandomCircle"; // Circle_Make_Random.class.getSimpleName();
		
		@DialogLabel("image width")
		public int W = 400;
		@DialogLabel("image height")
		public int H = 400;
		
		@DialogLabel("number of points")
		public int n = 20;
		
		@DialogLabel("circle center (xc)")
		public double xc = 200;
		
		@DialogLabel("circle center (yc)")
		public double yc = 190;
		
		@DialogLabel("circle radius (r)")
		public double r = 150;
		
		@DialogLabel("start angle (deg)")
		public double angle0 = 0;
		
		@DialogLabel("stop angle (deg)")
		public double angle1 = 180;
		
		@DialogLabel("x/y noise (sigma)")
		public double sigma = 5.0; //2.0;
		@DialogLabel("Random seed (0 = none)")
		public int seed = 0;
		
		@DialogLabel("show real circle")
		public boolean ShowRealCurve = true;
		@DialogLabel("circle color")
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
		
		GeometricCircle realCircle = new GeometricCircle(params.xc, params.yc, params.r);
		CircleSampler sampler = new CircleSampler(realCircle, params.seed);
		Pnt2d[] points =
				sampler.getPoints(params.n, Math.toRadians(params.angle0), Math.toRadians(params.angle1), params.sigma);

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
			ColoredStroke circleStroke = new ColoredStroke(params.StrokeWidth, params.StrokeColor.getColor());
			ola.addShapes(realCircle.getShapes(3), circleStroke);
			im.setOverlay(ola.getOverlay());
		}
		
		im.show();
	}
	
	// ------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addMessage(DialogUtils.formatText(50,
				"This plugin samples points on a given (ideal) circle and",
				"creates a new image with the sample points marked and also",
				"contained in a ROI (float coordinates)."
				));
		
		addToDialog(params, gd);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		return params.validate();
	}

}

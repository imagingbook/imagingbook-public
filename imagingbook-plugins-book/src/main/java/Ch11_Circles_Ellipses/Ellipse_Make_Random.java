/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch11_Circles_Ellipses;


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
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.geometry.fitting.ellipse.utils.EllipseSampler;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.ij.DialogUtils.DialogStringColumns;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.util.ParameterBundle;

/**
 * Samples points on a given (ideal) circle and creates a new image with the
 * sample points marked and also contained in a {@link PointRoi}. Image size,
 * ellipse parameters and noise can be specified. The result can be used as a
 * test image for circle fitting. Note that the resulting image has an inverted
 * LUT, i.e., the background value is 0 and marked points have value 255.
 * 
 * @author WB
 * @version 2022/10/03
 */
public class Ellipse_Make_Random implements PlugIn {
	
	public static class Parameters implements ParameterBundle<Ellipse_Make_Random> {
		
		@DialogLabel("image title")@DialogStringColumns(12)
		public String Title = "RandomEllipse"; // Ellipse_Make_Random.class.getSimpleName();
		
		@DialogLabel("image width")
		public int W = 400;
		@DialogLabel("image height")
		public int H = 400;
		
		@DialogLabel("number of points")
		public int n = 20;
		
		@DialogLabel("ellipse center (xc)")
		public double xc = 200;
		
		@DialogLabel("ellipsecenter (yc)")
		public double yc = 190;
		
		@DialogLabel("major axis radius (ra)")
		public double ra = 170;
		
		@DialogLabel("minor axis radius (rb)")
		public double rb = 120;
		
		@DialogLabel("start angle (deg)")
		public double angle0 = 0;
		
		@DialogLabel("stop angle (deg)")
		public double angle1 = 180; // was Math.PI/4;
		
		@DialogLabel("ellipse orientation (deg)")
		public double theta = 45;
		
		@DialogLabel("x/y noise (sigma)")
		public double sigma = 5.0; //2.0;
		
		@DialogLabel("show real ellipse")
		public boolean ShowRealCurve = true;
		@DialogLabel("ellipse color")
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
		
		GeometricEllipse realEllipse = new GeometricEllipse(params.ra, params.rb, params.xc, params.yc, 
				Math.toRadians(params.theta));
		Pnt2d[] points = new EllipseSampler(realEllipse).getPoints(params.n, 
				Math.toRadians(params.angle0), Math.toRadians(params.angle1), params.sigma);
		
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
			ola.addShapes(realEllipse.getShapes(3), circleStroke);
			im.setOverlay(ola.getOverlay());
		}
		
		im.show();
	}
	
	// ------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addMessage(DialogUtils.makeLineSeparatedString(
				"This plugin samples points on a given (ideal) ellipse and",
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

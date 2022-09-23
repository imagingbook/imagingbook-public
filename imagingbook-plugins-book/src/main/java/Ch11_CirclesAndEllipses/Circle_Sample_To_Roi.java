/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch11_CirclesAndEllipses;


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
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.utils.CircleSampler;
import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.util.ParameterBundle;

/**
 * Samples points on a given (ideal) circle and creates a new image with 
 * the sample points contained in a {@link PointRoi}.
 * Image size, circle parameters and noise can be specified.
 * The result can be used as a test image for circle fitting.
 * 
 * @author WB
 *
 */
public class Circle_Sample_To_Roi implements PlugIn {
	
	private static String title = Circle_Sample_To_Roi.class.getSimpleName();
	private static int W = 400;
	private static int H = 400;
	private static boolean ShowRealCircle = true;
	
	private static double StrokeWidth = 1.0;
	private static BasicAwtColor StrokeColor = BasicAwtColor.Green;
	
	public static class Parameters implements ParameterBundle {
		
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
	};
	
	private static Parameters params = new Parameters();
	
	@Override
	public void run(String arg) {
		if (!runDialog()) {
			return;
		}
		
		GeometricCircle realCircle = new GeometricCircle(params.xc, params.yc, params.r);
		Pnt2d[] points = new CircleSampler(realCircle).getPoints(params.n, 
				Math.toRadians(params.angle0), Math.toRadians(params.angle1), params.sigma);
		PointRoi roi = RoiUtils.toPointRoi(points);
		
		ImagePlus im = NewImage.createByteImage(title, W, H, 1, NewImage.FILL_WHITE);  //new ImagePlus(title, ip);
		im.setRoi(roi);
		
		if (ShowRealCircle) {
			Overlay oly = new Overlay();
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
			ColoredStroke circleStroke = new ColoredStroke(StrokeWidth, StrokeColor.getColor());
			ola.addShapes(realCircle.getShapes(3), circleStroke);
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
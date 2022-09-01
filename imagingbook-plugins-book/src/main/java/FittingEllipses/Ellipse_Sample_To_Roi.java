/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package FittingEllipses;


import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.plugin.PlugIn;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.geometry.fitting.ellipse.utils.EllipseSampler;
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
public class Ellipse_Sample_To_Roi implements PlugIn {
	
	private static String title = Ellipse_Sample_To_Roi.class.getSimpleName();
	private static int W = 400;
	private static int H = 400;
	private static boolean ShowRealCircle = true;
	
	private static double StrokeWidth = 1.0;
	private static BasicAwtColor StrokeColor = BasicAwtColor.Green;
	
	public static class Parameters implements ParameterBundle {
		
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
		PointRoi roi = RoiUtils.toPointRoi(points);
		
		ImagePlus im = NewImage.createByteImage(title, W, H, 1, NewImage.FILL_WHITE);  //new ImagePlus(title, ip);
		im.setRoi(roi);
		
		if (ShowRealCircle) {
			Overlay oly = new Overlay();
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
			ColoredStroke circleStroke = new ColoredStroke(StrokeWidth, StrokeColor.getColor());
			ola.addShapes(realEllipse.getShapes(3), circleStroke);
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
		gd.addEnumChoice("circle color", StrokeColor);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		title = gd.getNextString();
		W = (int) gd.getNextNumber();
		H = (int) gd.getNextNumber();
		params.getFromDialog(gd);
		ShowRealCircle = gd.getNextBoolean();
		StrokeColor = gd.getNextEnumChoice(BasicAwtColor.class);

		return params.validate();
	}

}

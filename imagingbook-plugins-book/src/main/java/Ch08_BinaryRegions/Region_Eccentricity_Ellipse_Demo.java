/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger (WB), Mark J. Burge (MJB). 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch08_BinaryRegions;

import static imagingbook.common.ij.IjUtils.noCurrentImage;
import static imagingbook.common.ij.IjUtils.requestSampleImage;
import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.core.plugin.IjPluginName;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * Performs binary region segmentation, then displays each region's major axis
 * (scaled by eccentricity) and equivalent ellipse as a vector overlay. See Sec.
 * 8.6.2 and 8.6.3 of [1] for additional details. This plugin expects a binary
 * (black and white) image with background = 0 and foreground &gt; 0. Display
 * lookup tables (LUTs) are not considered. Eccentricity values are limited to
 * {@link #MaxEccentricity}, axes are marked red if exceeded. Axes for regions
 * with {@code NaN} eccentricity value (single-pixel regions) are not displayed.
 * Axis and ellipse parameters are calculated from the region's central moments.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2021/04/18
 * @version 2022/09/27 revised ellipse generation, overlay
 */
@IjPluginName("Region Eccentricity/Ellipse Demo")
public class Region_Eccentricity_Ellipse_Demo implements PlugInFilter {
	
	/** Neighborhood type used for region segmentation (4- or 8-neighborhood). */
	public static NeighborhoodType2D Neighborhood = NeighborhoodType2D.N4;
	
	/** Eccentricity scale factor applied to the length of the region's major axis. */
	public static double 	AxisEccentricityScale = 1.0;
	/** Minimum region size, smaller regions are ignored. */
	public static int 		MinRegionSize = 10;
	/** Maximum eccentricity, greater eccentricity values are clipped. */
	public static double 	MaxEccentricity = 100;
	
	/** Color used for drawing the major axis. */
	public static Color 	AxisColor = Color.magenta;
	/** Color used for drawing the major axis if maximum eccentricity exceeded. */
	public static Color 	AxisColorCLipped = Color.red;
	/** Color used for drawing the region's center. */
	public static Color		CenterColor = Color.orange;
	/** Color used for drawing the region's equivalent ellipse. */
	public static Color		EllipseColor = Color.green;
	/** Line width used for drawing the region's axes. */
	public static double 	AxisLineWidth = 1.5;
	/** Size (radius) of the region's center mark. */
	public static double 	CenterMarkSize = 3;
	/** Line width used for drawing the region's center. */
	public static double 	CenterLineWidth = 0.75;
	
	/** Set true to show the regions's centroid. */
	public static boolean 	ShowCenterMark = true;
	/** Set true to show the region's major axis (length scaled by eccentricity). */
	public static boolean 	ShowMajorAxis = true;
	/** Set true to show the region's equivalent ellipse. */
	public static boolean 	ShowEllipse = true;
	
	private ImagePlus im = null;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Region_Eccentricity_Ellipse_Demo() {
		if (noCurrentImage()) {
			requestSampleImage(GeneralSampleImage.ToolsSmall);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		
		if (!IjUtils.isBinary(ip)) {
			IJ.showMessage("Plugin requires a binary image!");
			return;
		}
		
		if (!runDialog()) {
			return;
		}
		
		int w = ip.getWidth();
		int h = ip.getHeight();
		double unitLength = sqrt(w * h) * 0.005 * AxisEccentricityScale;			

		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		
		// perform region segmentation:
		RegionContourSegmentation segmenter = new RegionContourSegmentation((ByteProcessor) ip, Neighborhood);
		List<BinaryRegion> regions = segmenter.getRegions();

		for (BinaryRegion r : regions) {
			int n = r.getSize();
			if (n < MinRegionSize) {
				continue;
			}
			
			Pnt2d ctr = r.getCenter();
			final double xc = ctr.getX();
			final double yc = ctr.getY();
			
			if (ShowCenterMark) {
				ola.setStroke(new ColoredStroke(CenterLineWidth, CenterColor));
				ola.addShape(makeCenterMark(xc, yc));
			}
			
			double[] mu = r.getCentralMoments();	// = (mu10, mu01, mu20, mu02, mu11)
			double mu20 = mu[0];
			double mu02 = mu[1];
			double mu11 = mu[2];
			
			double theta = 0.5 * Math.atan2(2 * mu11, mu20 - mu02);	// axis angle	
				
			// calculate eccentricity from 2nd-order region moments:
			double A = mu20 + mu02;
			double B = sqr(mu20 - mu02) + 4 * sqr(mu11);
			if (B < 0) {
				throw new RuntimeException("negative value in eccentricity calculation: B = " + B); // this should never happen
			}		
			double a1 = A + sqrt(B);		// see book 2nd ed, eq. 10.34
			double a2 = A - sqrt(B);
			double ecc = a1 / a2;		
			
			if (ShowMajorAxis && !Double.isNaN(ecc)) {
				Color axisCol = AxisColor;			// default color
				if (ecc > MaxEccentricity) {		// limit eccentricity (may be infinite)
					ecc = MaxEccentricity;
					axisCol = AxisColorCLipped;		// mark as beyond maximum
				}
				double len = ecc * unitLength;
				double dx = Math.cos(theta) * len;
				double dy = Math.sin(theta) * len;;
				ola.setStroke(new ColoredStroke(AxisLineWidth, axisCol));
				ola.addShape(new Line2D.Double(xc, yc, xc + dx, yc + dy));
			}
			
			if (ShowEllipse) {
				GeometricEllipse ellipse = r.getEquivalentEllipse();
				if (ellipse != null) {
					ola.setStroke(new ColoredStroke(AxisLineWidth, EllipseColor));
					ola.addShape(ellipse.getShape());
				}
			}	
		}
		
		im.setOverlay(ola.getOverlay());
	}
	
	@SuppressWarnings("unused")
	private Shape makeEllipse(double xc, double yc, double ra, double rb, double theta) {
		Ellipse2D e = new Ellipse2D.Double(-ra, -rb, 2 * ra, 2 * rb);
		AffineTransform t = new AffineTransform();
		t.translate(xc, yc);
		t.rotate(theta);
		return t.createTransformedShape(e);
	}
	
	private Shape makeCenterMark(double x, double y) {
		double r = CenterMarkSize;
		Path2D.Double path = new Path2D.Double();
		path.moveTo(x - r, y - r);
		path.lineTo(x + r, y + r);
		path.moveTo(x + r, y - r);
		path.lineTo(x - r, y + r);
		return path;
	}
	
	// -----------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Neighborhood type", Neighborhood);
		gd.addNumericField("Min. region size (pixels)", MinRegionSize, 0);
		gd.addNumericField("Max. eccentricity", MaxEccentricity, 0);
		gd.addNumericField("Axis scale", AxisEccentricityScale, 1);
		gd.addCheckbox("Show center marks", ShowCenterMark);
		gd.addCheckbox("Show major axes", ShowMajorAxis);
		gd.addCheckbox("Show ellipses", ShowEllipse);
				
		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		Neighborhood = gd.getNextEnumChoice(NeighborhoodType2D.class);
		MinRegionSize = (int) gd.getNextNumber();
		MaxEccentricity = gd.getNextNumber();
		AxisEccentricityScale = gd.getNextNumber();
		
		ShowCenterMark = gd.getNextBoolean();
		ShowMajorAxis = gd.getNextBoolean();
		ShowEllipse = gd.getNextBoolean();
		return true;
	}

}
/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch08_BinaryRegions;

import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Locale;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.segment.RegionContourSegmentation;

/**
 * <p>
 * Performs binary region segmentation, then displays each region's major axis
 * (scaled by eccentricity) and equivalent ellipse as a vector overlay. See Sec.
 * 8.6.2 and 8.6.3 of [1] for additional details. Eccentricity values are
 * limited to {@link #MaxEccentricity}, axes are marked red if exceeded. Axes
 * for regions with {@code NaN} eccentricity value (single-pixel regions) are
 * not displayed. Axis and ellipse parameters are calculated from the region's
 * central moments. 
 * <br>
 * This plugin expects a binary (black and white) image with background = 0 and
 * foreground &gt; 0.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2021/04/18
 */
public class Region_Eccentricity_Ellipse_Demo implements PlugInFilter {	// TODO: convert to ShapeOverlayAdapter
	
	static {
		Locale.setDefault(Locale.US);
	}
	
	public static NeighborhoodType2D Neighborhood = NeighborhoodType2D.N4;
	
	public static double 	AxisScale = 1.0;
	public static int 		MinRegionSize = 10;
	public static double 	MaxEccentricity = 100;
	
	public static Color 	AxisColor = Color.magenta;
	public static Color 	AxisColorVoid = Color.red;
	public static Color		MarkerColor = Color.orange;
	public static Color		EllipseColor = Color.green;
	
	public static double 	AxisLineWidth = 1.5;
	public static double 	MarkerRadius = 3;
	public static double 	MarkerLineWidth = 0.75;
	
	public static boolean 	ShowCenterMark = true;
	public static boolean 	ShowMajorAxis = true;
	public static boolean 	ShowEllipse = true;
	
	private ImagePlus im = null;

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
		double unitLength = sqrt(w * h) * 0.005 * AxisScale;
				
		Overlay oly = new Overlay();
		
		// perform region segmentation:
		RegionContourSegmentation segmenter = new RegionContourSegmentation((ByteProcessor) ip, Neighborhood);
		List<BinaryRegion> regions = segmenter.getRegions();

		for (BinaryRegion r : regions) {
			int n = r.getSize();
			if (n < MinRegionSize) {
				continue;
			}
			
			Pnt2d ctr = r.getCenter();
			double xc = ctr.getX() + 0.5;
			double yc = ctr.getY() + 0.5;
			
			if (ShowCenterMark) {
				Roi marker = makeCenterMark(xc, yc, MarkerRadius);
				marker.setStrokeColor(MarkerColor);
				marker.setStrokeWidth(MarkerLineWidth);
				oly.add(marker);
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
				throw new RuntimeException("negative B: " + B); // this should never happen
			}		
			double a1 = A + sqrt(B);		// see book 2nd ed, eq. 10.34
			double a2 = A - sqrt(B);
			double ecc = a1 / a2;			// = (A + sqrt(B)) / (A - sqrt(B))				
			
			if (ShowMajorAxis && !Double.isNaN(ecc)) {
				// ignore single pixel regions: A + sqrt(B) == A - sqrt(B) == 0
				Color axisCol = AxisColor;			// default color
				if (ecc > MaxEccentricity) {		// limit eccentricity (may be infinite)
					ecc = MaxEccentricity;
					axisCol = AxisColorVoid;		// mark as beyond maximum
				}
				double len = ecc * unitLength;
				double dx = Math.cos(theta) * len;
				double dy = Math.sin(theta) * len;
				Roi roi = new ShapeRoi(new Line2D.Double(xc, yc, xc + dx, yc + dy));
				roi.setStrokeWidth(AxisLineWidth);
				roi.setStrokeColor(axisCol);
				oly.add(roi);
			}
			
			if (ShowEllipse) {
				double ra = sqrt(2 * a1 / n);
				double rb = sqrt(2 * a2 / n);
				Roi roi = makeEllipse(xc, yc, ra, rb, theta);
				roi.setStrokeWidth(AxisLineWidth);
				roi.setStrokeColor(EllipseColor);
				oly.add(roi);
				
//				// same result via Eigenvalues:
//				Eigensolver2x2 es = new Eigensolver2x2(mu20, mu11, mu11, mu02);
//				double lambda0 = es.getEigenvalue(0);
//				double lambda1 = es.getEigenvalue(1);
//				double ra2 = 2 * sqrt(lambda0 / n);
//				double rb2 = 2 * sqrt(lambda1 / n);
//				IJ.log(String.format("V1: ra=%.2f rb=%.2f", ra, rb));
//				IJ.log(String.format("V2: ra=%.2f rb=%.2f", ra2, rb2));
//				IJ.log("");
			}	
		}
		
		im.setOverlay(oly);
	}
	
	public Roi makeEllipse(double xc, double yc, double ra, double rb, double theta) {
		Ellipse2D e = new Ellipse2D.Double(-ra, -rb, 2 * ra, 2 * rb);
		AffineTransform t = new AffineTransform();
		t.translate(xc, yc);
		t.rotate(theta);
		return new ShapeRoi(t.createTransformedShape(e));
	}
	
	private Roi makeCenterMark(double x, double y, double r) {
		Path2D.Double m = new Path2D.Double();
		m.moveTo(x - r, y - r);
		m.lineTo(x + r, y + r);
		m.moveTo(x + r, y - r);
		m.lineTo(x - r, y + r);
		return new ShapeRoi(m);
	}
	
	// -----------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Neighborhood type", Neighborhood);
		gd.addNumericField("Min. region size", MinRegionSize, 0);
		gd.addNumericField("Max. eccentricity", MaxEccentricity, 0);
		gd.addNumericField("Axis scale", AxisScale, 1);
		gd.addCheckbox("Show center marks", ShowCenterMark);
		gd.addCheckbox("Show major axes", ShowMajorAxis);
		gd.addCheckbox("Show ellipses", ShowEllipse);
				
		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		Neighborhood = gd.getNextEnumChoice(NeighborhoodType2D.class);
		MinRegionSize = (int) gd.getNextNumber();
		MaxEccentricity = gd.getNextNumber();
		AxisScale = gd.getNextNumber();
		
		ShowCenterMark = gd.getNextBoolean();
		ShowMajorAxis = gd.getNextBoolean();
		ShowEllipse = gd.getNextBoolean();
		return true;
	}

}
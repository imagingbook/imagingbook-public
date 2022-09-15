/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package LucasKanade;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.mappings.linear.ProjectiveMapping2D;
import imagingbook.common.lucaskanade.LucasKanadeForwardMatcher;
import imagingbook.common.lucaskanade.LucasKanadeInverseMatcher;
import imagingbook.common.lucaskanade.LucasKanadeMatcher;
import imagingbook.common.lucaskanade.utils.ImageExtractor;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;


/**
 * This ImageJ plugin is a minimalistic example of using the Lucas-Kanade
 * matchers. It performs a single test run on the current image, which is used
 * as the search image I. A rectangular selection is required, which is used 
 * to calculate the initial mapping (Tinit) for the matcher. This is where the 
 * search starts. 
 * The reference image R is extracted from I out of a warped patch, which is 
 * obtained by perturbing (adding Gaussian noise with specified sigma) the 
 * corners of the selected ROI. Note that the disturbance is applied to all 4 
 * corners of the ROI, thus only a projective (4-point homography) warp can 
 * recover an accurate match!
 * Note that the plotted boundaries are 1 pixel smaller (in x/y) than the original
 * ROI, because the original quad vertices are assumed to be positioned at pixel 
 * centers. Thus if the ROI is 2 pixels wide, the corresponding quad only has width 1.
 * 
 * <p>The following steps are performed:</p>
 * <ul>
 * <li>Step 0: Create the search image I.</li>
 * <li>Step 1: Get the rectangle of the ROI, create the (empty) reference image R of the same size.</li>
 * <li>Step 2: Get the corner points (Q) of the ROI.</li>
 * <li>Step 3: Perturb the ROI corners to form the quad QQ and extract the reference image R.</li>
 * <li>Step 4: Create the Lucas-Kanade matcher (forward or inverse).</li>
 * <li>Step 5: Calculate the initial mapping Tinit from (centered) R &rarr; Q.</li>
 * <li>Step 6: Calculate the real mapping from (centered) R &rarr; QQ (for validation only).</li>
 * <li>Step 7: Initialize the matcher and run the matching loop.</li>
 * <li>Step 8: Evaluate the results.</li>
 * </ul>
 * 
 * @author WB
 */
public class LucasKanade_Demo implements PlugInFilter {
	
	static int maxIterations = 100;
	static double tolerance = 0.0001;
	static double sigma = 2.5;
	static boolean drawBoundaries = true;
	static boolean forwardMatcher = true;
	
	static {PrintPrecision.set(6);}
	
	ImagePlus img = null;
	Overlay oly = null;

	public int setup(String args, ImagePlus img) {
		this.img = img;
		return DOES_8G + ROI_REQUIRED;
	}

	public void run(ImageProcessor ip) {		
		Roi roi = img.getRoi();
		if (roi != null && roi.getType() != Roi.RECTANGLE) {
			IJ.error("Rectangular selection required!)");
			return;
		}
		
		// Step 1: Create the search image I:
		FloatProcessor I = ip.convertToFloatProcessor();
		
		// Step 2: Create the (empty) reference image $R$:
		Rectangle roiR = roi.getBounds();
		FloatProcessor R = new FloatProcessor(roiR.width, roiR.height);
		
		// Step 3: Perturb the ROI corners to form the quad QQ and extract the reference image R:
		Pnt2d[] Q  = getCornerPoints(roiR);
		Pnt2d[] QQ = perturbGaussian(Q);
		(new ImageExtractor(I)).extractImage(R, QQ);
		//(new ImagePlus("R", R)).show();
		
		// Step 4: Create the Lucas-Kanade matcher (forward or inverse):
		LucasKanadeMatcher matcher = (forwardMatcher) ?
				new LucasKanadeForwardMatcher(I, R) :
				new LucasKanadeInverseMatcher(I, R);
		
		// Step 5: Calculate the initial mapping Tinit from (centered) R -> Q:
		ProjectiveMapping2D Tinit = matcher.getReferenceMappingTo(Q);
		IJ.log("Tinit = " + Matrix.toString(matcher.getParameters(Tinit)));

		// Step 6: Calculate the real mapping from (centered) R -> QQ (for validation only):
		ProjectiveMapping2D Treal = matcher.getReferenceMappingTo(QQ);
		IJ.log("Treal = " + Matrix.toString(matcher.getParameters(Treal)));
		
		// --------------------------------------------------------------------------
		// Step 7: Initialize the matcher and run the matching loop:
		ProjectiveMapping2D T = Tinit;
		do {
			T = matcher.iterateOnce(T);		// returns null if iteration failed
			int i = matcher.getIteration();
			double err = matcher.getRmsError();
			IJ.log(String.format("Iteration = %d, RMS error = %.2f", i , err));
		} 
		while (T != null && !matcher.hasConverged() && 
				matcher.getIteration() < maxIterations);
		// --------------------------------------------------------------------------
		
		// Step 8: Evaluate the results:
		if (T == null || !matcher.hasConverged()) {
			IJ.log("no match found!");
			return;
		}
		else {
			ProjectiveMapping2D Tfinal = T;
			
			IJ.log(" ++++++++++++++++ Summary +++++++++++++++++++");
			// convert all mappings to projective (for comparison)
			ProjectiveMapping2D TinitP = new ProjectiveMapping2D(Tinit);
			ProjectiveMapping2D TrealP = new ProjectiveMapping2D(Treal);
			ProjectiveMapping2D TfinalP = new ProjectiveMapping2D(Tfinal);
			IJ.log("Matcher type: " + matcher.getClass().getSimpleName());
			IJ.log("Match found after " + matcher.getIteration() + " iterations.");
			IJ.log("Final RMS error " + matcher.getRmsError());
			IJ.log("  Tinit  = " + Matrix.toString(matcher.getParameters(TinitP)));
			IJ.log("  Treal  = " + Matrix.toString(matcher.getParameters(TrealP)));
			IJ.log("  Tfinal = " + Matrix.toString(matcher.getParameters(TfinalP)));
	
			IJ.log("Corners of reference patch:");
			Pnt2d[] ptsRef = Treal.applyTo(matcher.getReferencePoints());
			for(Pnt2d pt : ptsRef) {
				IJ.log("  pt = " + pt.toString());
			}
			IJ.log("Corners for best match:");
			Pnt2d[] ptsFinal = Tfinal.applyTo(matcher.getReferencePoints());
			for(Pnt2d pt : ptsFinal) {
				IJ.log("  pt = " + pt.toString());
			}
			
			Pnt2d test0 = PntInt.from(0, 0);
			IJ.log(" (0,0) by Treal  = " + Treal.applyTo(test0).toString());
			IJ.log(" (0,0) by Tfinal = " + Tfinal.applyTo(test0).toString());
			
			if (drawBoundaries) {
				oly = new Overlay();
				img.setOverlay(null);
				img.setOverlay(oly);
				
				oly.add(makePolygon(Q, 0.2, Color.green));
				oly.add(makePolygon(QQ, 0.5, Color.blue));
				oly.add(makePolygon(ptsFinal, 0.2, Color.red));
				img.updateAndDraw();
			}
		}
	}
	
	// ----------------------------------------------------------
	
	private Pnt2d[] getCornerPoints(Rectangle2D r) {	
		//IJ.log("getpoints2:  r = " + r.toString());
		double x = r.getX();
		double y = r.getY();
		double w = r.getWidth();
		double h = r.getHeight();
		Pnt2d[] pts = new Pnt2d[4];
		pts[0] = PntDouble.from(x, y);
		pts[1] = PntDouble.from(x + w - 1, y);	// TODO: does -1 matter? YES - it seems WRONG!!!
		pts[2] = PntDouble.from(x + w - 1, y + h - 1);
		pts[3] = PntDouble.from(x, y + h - 1);
		//IJ.log("getpoints2:  p1-4 = " + pts[0] + ", " + pts[1] + ", " + pts[2] + ", " + pts[3]);
		return pts;
	}
	
	private final Random rg = new Random();
	
	private Pnt2d perturbGaussian(Pnt2d p) {
		double x = p.getX();
		double y = p.getY();
		x = x + rg.nextGaussian() * sigma;
		y = y + rg.nextGaussian() * sigma;
		return PntDouble.from(x, y);
	}
	
	private Pnt2d[] perturbGaussian(Pnt2d[] pntsIn) {
		Pnt2d[] pntsOut = pntsIn.clone();
		for (int i = 0; i < pntsIn.length; i++) {
			pntsOut[i] = perturbGaussian(pntsIn[i]);
		}
		return pntsOut;
	}
	
	private Roi makePolygon(Pnt2d[] points, double strokeWidth, Color color) {
		Path2D poly = new Path2D.Double();
		if (points.length > 0) {
			poly.moveTo(points[0].getX(), points[0].getY());
			for (int i = 1; i < points.length; i++) {
				poly.lineTo(points[i].getX(), points[i].getY());
			}
			poly.closePath();
		}
		Roi shapeRoi = new ShapeRoi(poly);
		shapeRoi.setStrokeWidth(strokeWidth);
		shapeRoi.setStrokeColor(color);
		return shapeRoi;
	}

}

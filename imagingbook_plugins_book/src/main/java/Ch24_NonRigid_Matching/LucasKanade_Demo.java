/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch24_NonRigid_Matching;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.mappings.linear.ProjectiveMapping2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.image.matching.lucaskanade.ImageExtractor;
import imagingbook.common.image.matching.lucaskanade.LucasKanadeForwardMatcher;
import imagingbook.common.image.matching.lucaskanade.LucasKanadeInverseMatcher;
import imagingbook.common.image.matching.lucaskanade.LucasKanadeMatcher;
import imagingbook.common.math.PrintPrecision;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;


/**
 * <p>
 * This ImageJ plugin is a minimalistic example of using the Lucas-Kanade matchers. It performs a single test run on the
 * current image, which is used as the <strong>search image I</strong>. A rectangular selection is required, which
 * defines the initial mapping (Tinit) for the matcher, i.e., where it starts the search for the optimal match.
 * </p>
 * <p>
 * The ROI rectangle is also used to extract the <strong>reference image R</strong> by random perturbation of its corner
 * coordinates. The reference image R is extracted from this warped rectangle, whose corner coordinates are not known to
 * the matcher. Given this warped template, the task of the Lucas-Kanade matcher is to find out from where in image I is
 * was extracted and under which transformation, using only the pixel brightness information. Since all 4 corners of the
 * ROI are perturbed,  only a projective (4-point homography) transformation can recover an accurate match!
 * </p>
 * <p>The following steps are performed:</p>
 * <ul>
 * <li>Step 0: Create the search image I.</li>
 * <li>Step 1: Get the rectangle of the ROI, create the (empty) reference image R of the same size.</li>
 * <li>Step 2: Get the corner points (Q) of the ROI.</li>
 * <li>Step 3: Perturb the ROI corners to form the quad QQ and use it to extract the reference image R from I.</li>
 * <li>Step 4: Create the Lucas-Kanade matcher (forward or inverse).</li>
 * <li>Step 5: Calculate the initial mapping Tinit from (centered) R &rarr; Q.</li>
 * <li>Step 6: Calculate the real mapping from (centered) R &rarr; QQ (for validation only).</li>
 * <li>Step 7: Initialize the matcher and run the matching loop.</li>
 * <li>Step 8: Evaluate the results.</li>
 * </ul>
 *
 * @author WB
 * @version 2022/12/16
 * @see LucasKanadeMatcher
 * @see LucasKanadeForwardMatcher
 * @see LucasKanadeInverseMatcher
 * @see ImageExtractor
 */
public class LucasKanade_Demo implements PlugInFilter, JavaDocHelp {
	
	private static int MaxIterations = 100;
	private static double PositionNoiseSigma = 2.5;
	private static double PixelNoiseSigma = 0;			// forward matcher: singular Hessian
	private static boolean UseForwardMatcher = true;	// inverse matcher has convergence problems!
	private static boolean ShowReferenceImage = true;
	private static boolean DrawBoundaries = true;
	private static boolean ShowResultLog = true;

	private static BasicAwtColor InitialQuadColor = BasicAwtColor.Green;
	private static BasicAwtColor PerturbedQuadColor = BasicAwtColor.Blue;
	private static BasicAwtColor FinalQuadColor = BasicAwtColor.Red;
	
	static {PrintPrecision.set(6);}
	
	private ImagePlus im;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public LucasKanade_Demo() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.IrishManor);
			ImagePlus imp = IJ.getImage();
			imp.setRoi(240, 90, 60, 60);
		}
	}

	public int setup(String args, ImagePlus im) {
		this.im = im;
		return DOES_8G + ROI_REQUIRED;
	}

	public void run(ImageProcessor ip) {
		Rectangle roi = ip.getRoi();
		if (roi == null) {
			IJ.showMessage("Rectangular selection required!");
			return;
		}

		if (!runDialog()) {
			return;
		}

		// Step 1: Create the search image I:
		FloatProcessor I = ip.convertToFloatProcessor();
		
		// Step 2: Create the (empty) reference image R:
		FloatProcessor R = new FloatProcessor(roi.width, roi.height);
		
		// Step 3: Perturb the ROI corners to form the quad QQ and extract the reference image R:
		Pnt2d[] Q  = getCornerPoints(roi);	// corners of the original quad
		Pnt2d[] QQ = perturbGaussian(Q);	// corners of the perturbed quad
		new ImageExtractor(I).extractImage(R, QQ);

		if (PixelNoiseSigma > 0) {	// add Gaussian brightness noise to R
			int wR = R.getWidth();
			int hR = R.getHeight();
			Random rg = new Random();
			for (int u = 0; u < wR; u++) {
				for (int v = 0; v < hR; v++) {
					double x = R.get(u, v) + rg.nextGaussian() * PixelNoiseSigma;
					R.setf(u, v, (float) x);
				}
			}
		}

		if (ShowReferenceImage) {
			new ImagePlus("R", R).show();
		}
		
		// Step 4: Create the Lucas-Kanade matcher (forward or inverse):
		LucasKanadeMatcher matcher = (UseForwardMatcher) ?
				new LucasKanadeForwardMatcher(I, R) :
				new LucasKanadeInverseMatcher(I, R);
		
		// Step 5: Calculate the initial mapping Tinit from (centered) R -> Q:
		ProjectiveMapping2D Tinit = matcher.getReferenceMappingTo(Q);

		// Step 6: Calculate the real mapping from (centered) R -> QQ (for validation only):
		ProjectiveMapping2D Treal = matcher.getReferenceMappingTo(QQ);

		if (ShowResultLog) {
			// IJ.log("Tinit = " + Matrix.toString(matcher.getParameters(Tinit)));
			// IJ.log("Treal = " + Matrix.toString(matcher.getParameters(Treal)));
		}
		
		// --------------------------------------------------------------------------
		// Step 7: Initialize the matcher and run the matching loop:
		ProjectiveMapping2D T = Tinit;
		do {
			T = matcher.iterateOnce(T);		// returns null if iteration failed
			int i = matcher.getIteration();
			double err = matcher.getRmsError();
			if (ShowResultLog) {
				IJ.log(String.format("Iteration = %d, RMS error = %.2f", i, err));
			}
		} 
		while (T != null && !matcher.hasConverged() && matcher.getIteration() < MaxIterations);
		// --------------------------------------------------------------------------
		
		// quit if the matcher did not converge
		if (T == null || !matcher.hasConverged()) {
			IJ.log("no match found!");
			return;
		}

		// Step 8: Evaluate the results:
		ProjectiveMapping2D Tfinal = T;
		Pnt2d[] ptsFinal = Tfinal.applyTo(matcher.getReferencePoints());

		if (DrawBoundaries) {
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
			ola.setStroke(new ColoredStroke(0.2, InitialQuadColor.getColor()));
			ola.addShape(makePolygon(Q));

			ola.setStroke(new ColoredStroke(0.5, PerturbedQuadColor.getColor()));
			ola.addShape(makePolygon(QQ));

			ola.setStroke(new ColoredStroke(0.2, FinalQuadColor.getColor()));
			ola.addShape(makePolygon(ptsFinal));
			im.setOverlay(ola.getOverlay());
			im.setRoi((Roi) null);
		}

		if (ShowResultLog) {
			IJ.log(" ++++++++++++++++ Summary +++++++++++++++++++");
			// convert all mappings to projective (for comparison)
			ProjectiveMapping2D TinitP = new ProjectiveMapping2D(Tinit);
			ProjectiveMapping2D TrealP = new ProjectiveMapping2D(Treal);
			ProjectiveMapping2D TfinalP = new ProjectiveMapping2D(Tfinal);
			IJ.log("Matcher type: " + matcher.getClass().getSimpleName());
			IJ.log("Match found after " + matcher.getIteration() + " iterations.");
			IJ.log("Final RMS error " + matcher.getRmsError());
			// IJ.log("  Tinit  = " + Matrix.toString(matcher.getParameters(TinitP)));
			// IJ.log("  Treal  = " + Matrix.toString(matcher.getParameters(TrealP)));
			// IJ.log("  Tfinal = " + Matrix.toString(matcher.getParameters(TfinalP)));

			IJ.log("Corners of reference patch:");
			Pnt2d[] ptsRef = Treal.applyTo(matcher.getReferencePoints());
			for (Pnt2d pt : ptsRef) {
				IJ.log("  pt = " + pt.toString());
			}
			IJ.log("Corners for best match:");

			for (Pnt2d pt : ptsFinal) {
				IJ.log("  pt = " + pt.toString());
			}

			// Pnt2d test0 = PntInt.from(0, 0);
			// IJ.log(" (0,0) by Treal  = " + Treal.applyTo(test0).toString());
			// IJ.log(" (0,0) by Tfinal = " + Tfinal.applyTo(test0).toString());
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
		x = x + rg.nextGaussian() * PositionNoiseSigma;
		y = y + rg.nextGaussian() * PositionNoiseSigma;
		return PntDouble.from(x, y);
	}
	
	private Pnt2d[] perturbGaussian(Pnt2d[] pntsIn) {
		Pnt2d[] pntsOut = pntsIn.clone();
		for (int i = 0; i < pntsIn.length; i++) {
			pntsOut[i] = perturbGaussian(pntsIn[i]);
		}
		return pntsOut;
	}

	@Deprecated
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


	private Path2D makePolygon(Pnt2d[] points) {
		Path2D poly = new Path2D.Double();
		if (points.length > 0) {
			poly.moveTo(points[0].getX(), points[0].getY());
			for (int i = 1; i < points.length; i++) {
				poly.lineTo(points[i].getX(), points[i].getY());
			}
			poly.closePath();
		}
		return poly;
	}

	// ----------------------------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());

		gd.addNumericField("Maximum iterations", MaxIterations, 0);
		gd.addNumericField("Position noise sigma", PositionNoiseSigma, 2);
		// gd.addNumericField("Position noise sigma", PixelNoiseSigma, 2);
		// gd.addCheckbox("Use forward matcher", UseForwardMatcher);
		gd.addCheckbox("Show reference image", ShowReferenceImage);
		gd.addCheckbox("Draw boundaries", DrawBoundaries);
		gd.addCheckbox("Show result log", ShowResultLog);

		gd.addEnumChoice("Initial quad color", InitialQuadColor);
		gd.addEnumChoice("Perturbed quad color", PerturbedQuadColor);
		gd.addEnumChoice("Final quad color", FinalQuadColor);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		MaxIterations = (int) gd.getNextNumber();
		PositionNoiseSigma = gd.getNextNumber();
		// PixelNoiseSigma = gd.getNextNumber();
		// UseForwardMatcher = gd.getNextBoolean();
		ShowReferenceImage = gd.getNextBoolean();
		DrawBoundaries = gd.getNextBoolean();
		ShowResultLog = gd.getNextBoolean();

		InitialQuadColor = gd.getNextEnumChoice(BasicAwtColor.class);
		PerturbedQuadColor = gd.getNextEnumChoice(BasicAwtColor.class);
		FinalQuadColor = gd.getNextEnumChoice(BasicAwtColor.class);

		return true;
	}

}

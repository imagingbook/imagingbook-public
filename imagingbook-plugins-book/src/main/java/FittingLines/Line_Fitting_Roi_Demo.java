/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package FittingLines;

import static imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic.FitType.Pratt;
import static imagingbook.common.geometry.fitting.circle.geometric.CircleFitGeometric.FitType.DistanceBased;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic;
import imagingbook.common.geometry.fitting.circle.geometric.CircleFitGeometric;
import imagingbook.common.geometry.fitting.line.LineFit;
import imagingbook.common.geometry.fitting.line.LinearRegressionFit;
import imagingbook.common.geometry.fitting.line.OrthogonalLineFitEigen;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.hough.HoughLine;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;


/**
 * Performs line fitting on the point set specified by the current ROI.
 * 
 * @author WB
 *
 */
public class Line_Fitting_Roi_Demo implements PlugInFilter {
	
	static CircleFitAlgebraic.FitType AlgebraicFitMethod = Pratt;
	static CircleFitGeometric.FitType GeometricFitMethod = DistanceBased;
	
	private static BasicAwtColor RegressionFitColor = BasicAwtColor.Red;
	private static BasicAwtColor OrthogonalFitColor = BasicAwtColor.Blue;
	private static double StrokeWidth = 0.5;
	
	private ImagePlus im;
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL + ROI_REQUIRED;
	}

	@Override
	public void run(ImageProcessor ip) {
		Roi roi = im.getRoi();
		int width = ip.getWidth();
		int height = ip.getHeight();
		
		if (!runDialog()) {
			return;
		}
		
		Pnt2d[] points = RoiUtils.getOutlinePointsFloat(roi);
		IJ.log("Found points " + points.length);
		if (points.length < 2) {
			IJ.error("At least 2 points are required, but found only " + points.length);
			return;
		}
		
		Overlay oly = im.getOverlay();
		if (oly == null) {
			oly = new Overlay();
			im.setOverlay(oly);
		}
		
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
		
		// ------------------------------------------------------------------------
		LineFit fitO = new OrthogonalLineFitEigen(points);
		AlgebraicLine lineO = fitO.getLine();
		// ------------------------------------------------------------------------
		
		if (lineO == null) {
			IJ.log("Orthogonal line fit: no result!");
			return;
		}
		
		
		IJ.log("Orthogonal line fit:");
		IJ.log("  line: " + lineO.toString());
//		IJ.log(String.format(Locale.US, "  error = %.3f", initCircle.getMeanSquareError(points)));
		
		ColoredStroke orthogonalStroke = new ColoredStroke(StrokeWidth, OrthogonalFitColor.getColor());
		ola.addShape(new HoughLine(lineO).getShape(width, height), orthogonalStroke);

		// ------------------------------------------------------------------------
		LineFit fitR = new LinearRegressionFit(points);
		AlgebraicLine lineR = fitR.getLine();
		// ------------------------------------------------------------------------
		
		if (lineR == null) {
			IJ.log("Regression fit: no result!");
			return;
		}
		
		IJ.log("Regression line fit:");
		IJ.log("  line: " + lineR.toString());
//		IJ.log(String.format(Locale.US, "  error = %.3f", finalCircle.getMeanSquareError(points)));


		ColoredStroke regressionStroke = new ColoredStroke(StrokeWidth, RegressionFitColor.getColor());
		ola.addShape(new HoughLine(lineR).getShape(width, height), regressionStroke);

	}

	// ------------------------------------------
	
	private boolean runDialog() {
//		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
//		gd.addEnumChoice("algebraic fit method", AlgebraicFitMethod);
//		gd.addEnumChoice("algebraic ellipse color", AlgebraicFitColor);
//		
//		gd.addEnumChoice("geometric fit method", GeometricFitMethod);
//		gd.addEnumChoice("geometric ellipse color", GeometricFitColor);
//		
//		gd.showDialog();
//		if (gd.wasCanceled())
//			return false;
//		
//		AlgebraicFitMethod = gd.getNextEnumChoice(CircleFitAlgebraic.FitType.class);
//		AlgebraicFitColor = gd.getNextEnumChoice(BasicAwtColor.class);
//		
//		GeometricFitMethod = gd.getNextEnumChoice(CircleFitGeometric.FitType.class);
//		GeometricFitColor = gd.getNextEnumChoice(BasicAwtColor.class);
//		
		return true;
	}



}

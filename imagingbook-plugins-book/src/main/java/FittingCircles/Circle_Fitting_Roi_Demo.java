/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package FittingCircles;

import static imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic.FitType.Pratt;
import static imagingbook.common.geometry.fitting.circle.geometric.CircleFitGeometric.FitType.DistanceBased;

import java.util.Locale;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.io.LogStream;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic;
import imagingbook.common.geometry.fitting.circle.geometric.CircleFitGeometric;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.math.PrintPrecision;


/**
 * Performs algebraic ellipse fitting on the current ROI
 * to find an initial ellipse, followed by geometric fitting.
 * Algebraic and geometric fit methods can be selected.
 * If successful, the resulting ellipses are displayed as a vector overlay
 * (color can be chosen).
 * 
 * @author WB
 *
 */
public class Circle_Fitting_Roi_Demo implements PlugInFilter {
	
	static {
		LogStream.redirectSystem();
		PrintPrecision.set(3);
	}
	
	static CircleFitAlgebraic.FitType AlgebraicFitMethod = Pratt;
	static CircleFitGeometric.FitType GeometricFitMethod = DistanceBased;
	
	private static BasicAwtColor AlgebraicFitColor = BasicAwtColor.Red;
	private static BasicAwtColor GeometricFitColor = BasicAwtColor.Blue;
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
		
		if (!runDialog()) {
			return;
		}
		
		Pnt2d[] points = RoiUtils.getOutlinePointsFloat(roi);
		IJ.log("Found points " + points.length);
		if (points.length < 3) {
			IJ.error("At least 3 points are required, but found only " + points.length);
			return;
		}
		
		Overlay oly = im.getOverlay();
		if (oly == null) {
			oly = new Overlay();
			im.setOverlay(oly);
		}
		
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);	
		
		// ------------------------------------------------------------------------
		CircleFitAlgebraic fitA = CircleFitAlgebraic.getFit(AlgebraicFitMethod, points);
		// ------------------------------------------------------------------------
		
		GeometricCircle ac = fitA.getGeometricCircle();
		if (ac == null) {
			IJ.log("Algebraic fit: no result!");
			return;
		}
		
		GeometricCircle initCircle = ac;
		
		IJ.log("Initial fit (algebraic):");
		IJ.log("  circle: " + initCircle.toString());
		IJ.log(String.format(Locale.US, "  error = %.3f", initCircle.getMeanSquareError(points)));
		
		ColoredStroke initialStroke = new ColoredStroke(StrokeWidth, AlgebraicFitColor.getColor());
		ola.addShapes(initCircle.getShapes(3), initialStroke);

		// ------------------------------------------------------------------------
		CircleFitGeometric fitG = CircleFitGeometric.getFit(GeometricFitMethod, points, initCircle);
		// ------------------------------------------------------------------------
		
		GeometricCircle finalCircle = fitG.getCircle();
		if (finalCircle == null) {
			IJ.log("Geometric fit: no result!");
			return;
		}
		
		IJ.log("Final fit (geometric):");
		IJ.log("  circle: " + finalCircle.toString());
		IJ.log(String.format(Locale.US, "  error = %.3f", finalCircle.getMeanSquareError(points)));
		IJ.log("  iterations = " + fitG.getIterations());

		ColoredStroke finalStroke = new ColoredStroke(StrokeWidth, GeometricFitColor.getColor());
		ola.addShapes(finalCircle.getShapes(3), finalStroke);
	}

	// ------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("algebraic fit method", AlgebraicFitMethod);
		gd.addEnumChoice("algebraic ellipse color", AlgebraicFitColor);
		
		gd.addEnumChoice("geometric fit method", GeometricFitMethod);
		gd.addEnumChoice("geometric ellipse color", GeometricFitColor);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		AlgebraicFitMethod = gd.getNextEnumChoice(CircleFitAlgebraic.FitType.class);
		AlgebraicFitColor = gd.getNextEnumChoice(BasicAwtColor.class);
		
		GeometricFitMethod = gd.getNextEnumChoice(CircleFitGeometric.FitType.class);
		GeometricFitColor = gd.getNextEnumChoice(BasicAwtColor.class);
		
		return true;
	}



}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch11_CirclesAndEllipses;

import static imagingbook.common.geometry.fitting.ellipse.algebraic.EllipseFitAlgebraic.FitType.FitzgibbonStable;
import static imagingbook.common.geometry.fitting.ellipse.geometric.EllipseFitGeometric.FitType.DistanceBased;
import static imagingbook.common.ij.DialogUtils.askYesOrCancel;
import static imagingbook.common.ij.IjUtils.noCurrentImage;
import static imagingbook.common.ij.IjUtils.runPlugIn;

import java.util.Locale;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.AlgebraicEllipse;
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.geometry.fitting.ellipse.algebraic.EllipseFitAlgebraic;
import imagingbook.common.geometry.fitting.ellipse.geometric.EllipseFitGeometric;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;

/**
 * ImageJ plugin, performs algebraic ellipse fitting on the current ROI
 * to find an initial ellipse, followed by geometric fitting.
 * Algebraic and geometric fit methods can be selected.
 * If successful, the resulting ellipses are displayed as a vector overlay
 * (color can be chosen).
 * Sample points are either collected from the ROI (if available) or collected
 * as foreground pixels (values > 0) from the image.
 * If no image is currently open, the user is asked to create a suitable sample
 * image.
 * 
 * @author WB
 * @version 2022/10/03
 *
 */
public class Ellipse_Fitting implements PlugInFilter {
	
	static EllipseFitAlgebraic.FitType AlgebraicFitMethod = FitzgibbonStable;
	static EllipseFitGeometric.FitType GeometricFitMethod = DistanceBased;
	static boolean UsePointsFromRoi = false;
	
	private static BasicAwtColor AlgebraicFitColor = BasicAwtColor.Red;
	private static BasicAwtColor GeometricFitColor = BasicAwtColor.Blue;
	private static double StrokeWidth = 0.5;
	
	private ImagePlus im;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Ellipse_Fitting() {
		if (noCurrentImage()) {
			if (askYesOrCancel("Create sample image", "No image is currently open.\nCreate a sample image?")) {
				runPlugIn(Ellipse_Make_Random.class);
			}			
		}
	}
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		Roi roi = im.getRoi();
		UsePointsFromRoi = (roi != null);
		
		if (!runDialog()) {
			return;
		}
		
		Pnt2d[] points = (UsePointsFromRoi) ?
				RoiUtils.getOutlinePointsFloat(roi) :
				IjUtils.collectNonzeroPoints(ip);
		
		IJ.log("Found points " + points.length);
		if (points.length < 5) {
			IJ.error("At least 5 points are required, but found only " + points.length);
			return;
		}
		
		Overlay oly = im.getOverlay();
		if (oly == null) {
			oly = new Overlay();
			im.setOverlay(oly);
		}
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);	
		
		Pnt2d xref = Pnt2d.from(ip.getWidth()/2, ip.getHeight()/2);	// reference point for ellipse fitting
		
		// ------------------------------------------------------------------------
		EllipseFitAlgebraic fitA = EllipseFitAlgebraic.getFit(AlgebraicFitMethod, points, xref);
		// ------------------------------------------------------------------------
		
		AlgebraicEllipse ae = fitA.getEllipse();		
		if (ae == null) {
			IJ.log("Algebraic fit: no result!");
			return;
		}
		
		GeometricEllipse initEllipse = new GeometricEllipse(ae);
		
		IJ.log("Initial fit (algebraic):");
		IJ.log("  ellipse: " + initEllipse.toString());
		IJ.log(String.format(Locale.US, "  error = %.3f", initEllipse.getMeanSquareError(points)));
		
		ColoredStroke initialStroke = new ColoredStroke(StrokeWidth, AlgebraicFitColor.getColor());
		ola.addShapes(initEllipse.getShapes(3), initialStroke);

		// ------------------------------------------------------------------------
		EllipseFitGeometric fitG = EllipseFitGeometric.getFit(GeometricFitMethod, points, initEllipse);
		// ------------------------------------------------------------------------
		
		GeometricEllipse finalEllipse = fitG.getEllipse();
		if (finalEllipse == null) {
			IJ.log("Geometric fit: no result!");
			return;
		}
		
		IJ.log("Final fit (geometric):");
		IJ.log("  ellipse: " + finalEllipse.toString());
		IJ.log(String.format(Locale.US, "  error = %.3f", finalEllipse.getMeanSquareError(points)));
		IJ.log("  iterations = " + fitG.getIterations());

		ColoredStroke finalStroke = new ColoredStroke(StrokeWidth, GeometricFitColor.getColor());
		ola.addShapes(finalEllipse.getShapes(3), finalStroke);
	}

	// ------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		
		gd.addMessage(DialogUtils.makeLineSeparatedString(
				"This plugin performs algebraic + geometric ellipse fitting,",
				"either to ROI points (if available) or foreground points",
				"collected from the pixel image."
				));
		
		gd.addCheckbox("Use ROI (float) points", UsePointsFromRoi);
		gd.addEnumChoice("Algebraic fit method", AlgebraicFitMethod);
		gd.addEnumChoice("Algebraic ellipse color", AlgebraicFitColor);
		gd.addEnumChoice("Geometric fit method", GeometricFitMethod);
		gd.addEnumChoice("Geometric ellipse color", GeometricFitColor);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		AlgebraicFitMethod = gd.getNextEnumChoice(EllipseFitAlgebraic.FitType.class);
		AlgebraicFitColor = gd.getNextEnumChoice(BasicAwtColor.class);
		
		GeometricFitMethod = gd.getNextEnumChoice(EllipseFitGeometric.FitType.class);
		GeometricFitColor = gd.getNextEnumChoice(BasicAwtColor.class);
		
		return true;
	}



}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch11_CirclesAndEllipses;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic.FitType;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;

/**
 * ImageJ plugin, collects all non-zero pixels (at least 3) in the current image
 * and performs an algebraic circle fit on their coordinates. 
 * The algebraic circle fit method can be selected.
 * 
 * @author WB
 * @version 2022/09/30
 */
public class Circle_Fit_Algebraic_Image implements PlugInFilter {
	
	public static boolean ShowLog = true;
	public static CircleFitAlgebraic.FitType algType = FitType.Pratt;
	public static boolean ShowFittedCircle = true;
	public static BasicAwtColor CircleColor = BasicAwtColor.Red;
	public static double StrokeWidth = 1.0;
	
	private ImagePlus im;
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		
		if (!runDialog()) {
			return;
		}
		
		Pnt2d[] points = IjUtils.collectNonzeroPoints(ip);
		if (ShowLog) {
			IJ.log("Found points " + points.length);
		}
		
		if (points.length < 3) {
			IJ.error("At least 3 points are required, found only " + points.length);
			return;
		}
		
		// ------------------------------------------------------------------------
		CircleFitAlgebraic fitter = CircleFitAlgebraic.getFit(algType, points);
		// ------------------------------------------------------------------------
		
		GeometricCircle fitCircle = fitter.getGeometricCircle();
		if (fitCircle == null) {
			IJ.log("Algebraic fit failed!");
			return;
		}
		
		if (ShowLog) {
			IJ.log("  fit: " + fitCircle);
		}
		
//		IJ.log(String.format(Locale.US, "  error real = %.3f", realCircle.getMeanSquareError(points)));
//		IJ.log(String.format(Locale.US, "  error fit  = %.3f", fitCircle.getMeanSquareError(points)));


		// ----- draw fitted circle ----------------------------------
		if (ShowFittedCircle) {
			Overlay oly = im.getOverlay();
			if (oly == null) {
				oly = new Overlay();
				im.setOverlay(oly);
			}
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
			ColoredStroke outerStroke = new ColoredStroke(StrokeWidth, CircleColor.getColor());
			ola.addShapes(fitCircle.getShapes(3), outerStroke);
		}
		
		im.show();
	}

	
	// ------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("fit method", algType);
		gd.addCheckbox("show fitted circle", ShowFittedCircle);
		gd.addEnumChoice("circle color", CircleColor);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		algType = gd.getNextEnumChoice(CircleFitAlgebraic.FitType.class);
		ShowFittedCircle = gd.getNextBoolean();
		CircleColor = gd.getNextEnumChoice(BasicAwtColor.class);
		
		return true;
	}
	
}

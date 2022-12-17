/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch11_Circle_Ellipse_Fitting;

import static imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic.FitType.Pratt;
import static imagingbook.common.geometry.fitting.circle.geometric.CircleFitGeometric.FitType.DistanceBased;

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
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitAlgebraic;
import imagingbook.common.geometry.fitting.circle.geometric.CircleFitGeometric;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;


/**
 * <p>
 * ImageJ plugin, performs algebraic circle fitting on the current ROI to find an initial circle, followed by geometric
 * fitting. Algebraic and geometric fitting methods can be selected (see Sec. 11.1 of [1] for details). If successful,
 * the resulting ellipses are displayed as a vector overlay (color can be chosen). Sample points are either collected
 * from the ROI (if available) or collected as foreground pixels (values &gt; 0) from the image. If no image is
 * currently open, the user is asked to create a suitable sample image.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/10/03
 */
public class Circle_Fitting implements PlugInFilter {
	
	static CircleFitAlgebraic.FitType AlgebraicFitMethod = Pratt;
	static CircleFitGeometric.FitType GeometricFitMethod = DistanceBased;
	static boolean UsePointsFromRoi = false;
	
	private static BasicAwtColor AlgebraicFitColor = BasicAwtColor.Red;
	private static BasicAwtColor GeometricFitColor = BasicAwtColor.Blue;
	private static double StrokeWidth = 0.5;
	
	private ImagePlus im;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Circle_Fitting() {
		if (IjUtils.noCurrentImage()) {
			if (DialogUtils.askForSampleImage()) {
				IjUtils.run(new Circle_Make_Random()); //runPlugIn(Circle_Make_Random.class);
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
		
		gd.addMessage(DialogUtils.makeLineSeparatedString(
				"This plugin performs algebraic + geometric circle fitting,",
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
		
		UsePointsFromRoi = gd.getNextBoolean();
		AlgebraicFitMethod = gd.getNextEnumChoice(CircleFitAlgebraic.FitType.class);
		AlgebraicFitColor = gd.getNextEnumChoice(BasicAwtColor.class);
		GeometricFitMethod = gd.getNextEnumChoice(CircleFitGeometric.FitType.class);
		GeometricFitColor = gd.getNextEnumChoice(BasicAwtColor.class);
		
		return true;
	}



}

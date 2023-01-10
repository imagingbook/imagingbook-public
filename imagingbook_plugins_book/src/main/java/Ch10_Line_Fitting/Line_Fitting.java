/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch10_Line_Fitting;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.fitting.line.LineFit;
import imagingbook.common.geometry.fitting.line.LinearRegressionFit;
import imagingbook.common.geometry.fitting.line.OrthogonalLineFitEigen;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.hough.HoughLine;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.core.jdoc.JavaDocHelp;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * Performs line fitting on the point set specified by the current ROI. Two fitting methods are employed: (a) linear
 * regression fitting, (b) orthogonal regression fitting. The result of the first varies with rotation, while orthogonal
 * fitting is rotation-invariant. See Sec. 10.2 (Fig. 10.4) of [1] for additional details. Sample points are either
 * collected from the ROI (if available) or collected as foreground pixels (values &gt; 0) from the image. If no image
 * is currently open, the user is asked to create a suitable sample image.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/10/03
 */
public class Line_Fitting implements PlugInFilter, JavaDocHelp { // TODO: activate dialog
	
	public static BasicAwtColor RegressionFitColor = BasicAwtColor.Red;
	public static BasicAwtColor OrthogonalFitColor = BasicAwtColor.Blue;
	static boolean UsePointsFromRoi = false;
	public boolean ShowLog = true;
	
	public static double StrokeWidth = 0.5;
	
	private ImagePlus im;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Line_Fitting() {
		if (noCurrentImage()) {
			if (DialogUtils.askForSampleImage()) {
				IjUtils.run(new Line_Make_Random()); //runPlugIn(Line_Make_Random.class);
			}			
		}	
	}
	
	// ------------------------------------------------------------------------------------
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		int width = ip.getWidth();
		int height = ip.getHeight();
		
		Roi roi = im.getRoi();
		UsePointsFromRoi = (roi != null);
		
		if (!runDialog()) {
			return;
		}
		
		Pnt2d[] points = (UsePointsFromRoi) ?
				RoiUtils.getOutlinePointsFloat(roi) :
				IjUtils.collectNonzeroPoints(ip);
		
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
			IJ.log("Orthogonal line fit failed!");
			return;
		}
		
		if (ShowLog) {
			IJ.log("Orthogonal line fit: " + lineO.toString());
		}
		
		ColoredStroke orthogonalStroke = new ColoredStroke(StrokeWidth, OrthogonalFitColor.getColor());
		ola.addShape(new HoughLine(lineO).getShape(width, height), orthogonalStroke);

		// ------------------------------------------------------------------------
		LineFit fitR = new LinearRegressionFit(points);
		AlgebraicLine lineR = fitR.getLine();
		// ------------------------------------------------------------------------
		
		if (lineR == null) {
			IJ.log("Regression fit failed!");
			return;
		}
		
		if (ShowLog) {
			IJ.log("Regression line fit: " + lineR.toString());
		}

		ColoredStroke regressionStroke = new ColoredStroke(StrokeWidth, RegressionFitColor.getColor());
		ola.addShape(new HoughLine(lineR).getShape(width, height), regressionStroke);
	}

	// ------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addMessage(DialogUtils.formatText(50,
				"This plugin performs algebraic + geometric circle fitting,",
				"either to ROI points (if available) or foreground points",
				"collected from the pixel image."
				));
		
		gd.addCheckbox("Use ROI (float) points", UsePointsFromRoi);
		gd.addEnumChoice("Regression fit color", RegressionFitColor);
		gd.addEnumChoice("Orthogonal fit color", OrthogonalFitColor);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		UsePointsFromRoi = gd.getNextBoolean();
		RegressionFitColor = gd.getNextEnumChoice(BasicAwtColor.class);
		OrthogonalFitColor = gd.getNextEnumChoice(BasicAwtColor.class);
		
		return true;
	}
}

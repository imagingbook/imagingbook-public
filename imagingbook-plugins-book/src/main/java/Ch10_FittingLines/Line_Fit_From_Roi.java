/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch10_FittingLines;

import static imagingbook.common.ij.DialogUtils.askYesOrCancel;
import static imagingbook.common.ij.IjUtils.noCurrentImage;
import static imagingbook.common.ij.IjUtils.runPlugIn;

import ij.IJ;
import ij.ImagePlus;
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
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;


/**
 * <p>
 * Performs line fitting on the point set specified by the current ROI. Two
 * fitting methods are employed: (a) linear regression fitting, (b) orthogonal
 * regression fitting. The result of the first varies with rotation, while
 * orthogonal fitting is rotation-invariant. See Sec. 10.2 (Fig. 10.4) of [1]
 * for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/09/30
 * @see Line_Sample_To_Roi
 */
public class Line_Fit_From_Roi implements PlugInFilter { // TODO: activate dialog
	
	public boolean ShowLog = true;
	public static BasicAwtColor RegressionFitColor = BasicAwtColor.Red;
	public static BasicAwtColor OrthogonalFitColor = BasicAwtColor.Blue;
	public static double StrokeWidth = 0.5;
	
	private ImagePlus im;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Line_Fit_From_Roi() {
		if (noCurrentImage()) {
			if (askYesOrCancel("Create sample image", "No image is currently open.\nCreate a sample image?")) {
				runPlugIn(Line_Sample_To_Roi.class);
			}			
		}
	}
	
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
//		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		return true;
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package HoughTransform;

import java.awt.Color;
import java.awt.geom.Path2D;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.hough.HoughLine;
import imagingbook.common.hough.HoughTransformLines;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;

/** 
 * This ImageJ plugin demonstrates the use of the {@link HoughTransformLines}
 * class for detecting straight lines in images.
 * It expects a binary input image with background = 0 and foreground (contour) 
 * pixels with values &gt; 0.
 * A vector overlay is used to display the detected lines.
 * 
 * @author W. Burger
 * @version 2022/04/01
 */
public class Hough_Lines_Detect implements PlugInFilter {

	static int MaxLines = 5;			// number of strongest lines to be found
	static int MinPointsOnLine = 50;	// min. number of points on each line

	static boolean ShowAccumulator = true;
	static boolean ShowExtendedAccumulator = false;
	static boolean ShowAccumulatorPeaks = false;
	static boolean ListStrongestLines = false;
	static boolean ShowLines = true;
	static boolean InvertOriginal = true;
	
	static double  LineWidth = 0.5;
	static Color   LineColor = Color.magenta;
	
	static boolean ShowReferencePoint = true;
	static Color   ReferencePointColor = Color.green;

	ImagePlus im;	

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		if (!IjUtils.isBinary(ip)) {
			IJ.showMessage("Binary (edge) image required!");
			return;
		}

		HoughTransformLines.Parameters params = new HoughTransformLines.Parameters();

		if (!showDialog(params)) //dialog canceled or error
			return; 

		// compute the Hough Transform and retrieve the strongest lines:
		HoughTransformLines ht = new HoughTransformLines((ByteProcessor)ip, params);
		HoughLine[] lines = ht.getLines(MinPointsOnLine, MaxLines);

		if (lines.length == 0) {
			IJ.log("No lines detected - check the input image and parameters!");
		}

		if (ShowAccumulator){
			FloatProcessor accIp = ht.getAccumulatorImage();
			(new ImagePlus("Accumulator for " + im.getTitle(), accIp)).show();
		}

		if (ShowExtendedAccumulator){
			FloatProcessor accEx = ht.getAccumulatorImageExtended();
			(new ImagePlus("Extended accumulator for " + im.getTitle(), accEx)).show();
		}

		if (ShowAccumulatorPeaks) {
			FloatProcessor maxIp = ht.getAccumulatorMaxImage();
			(new ImagePlus("Accumulator maxima for " + im.getTitle(), maxIp)).show();
		}

		if (ListStrongestLines) {
			for (int i = 0; i < lines.length; i++) {
				IJ.log(i + ": " + lines[i].toString());
			}
		}
		
		if (ShowLines) {
			ImageProcessor lineIp = ip.duplicate();
			if (InvertOriginal) lineIp.invert();
			double lineLength = Math.hypot(ip.getWidth(), ip.getHeight());
			
			Overlay oly = new Overlay();
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
			ola.setStroke(new ColoredStroke(LineWidth, LineColor));
			for (HoughLine hl : lines) {
				ola.addShape(hl.getShape(lineLength));
			}
			
			if (ShowReferencePoint) {
				ola.setStroke(new ColoredStroke(0.5, ReferencePointColor));
				ola.addShape(markPoint(ht.getXref(), ht.getYref()));
			}

			ImagePlus lim = new ImagePlus(im.getShortTitle()+"-lines", lineIp);
			lim.setOverlay(oly);
			lim.show();
		}
	}

	// -----------------------------------------------------------------

	private boolean showDialog(HoughTransformLines.Parameters params) {
		GenericDialog dlg = new GenericDialog("Hough Transform (lines)");
		dlg.addNumericField("Axial steps", params.nAng, 0);
		dlg.addNumericField("Radial steps", params.nRad, 0);
		dlg.addNumericField("Max. number of lines to show", MaxLines, 0);
		dlg.addNumericField("Min. number of points per line", MinPointsOnLine, 0);
		dlg.addCheckbox("Show accumulator", ShowAccumulator);
		dlg.addCheckbox("Show extended accumulator", ShowExtendedAccumulator);
		dlg.addCheckbox("Show accumulator peaks", ShowAccumulatorPeaks);
		dlg.addCheckbox("List strongest lines", ListStrongestLines);
		dlg.addCheckbox("Show lines", ShowLines);
		dlg.addNumericField("Line width", LineWidth, 1);
		dlg.addCheckbox("Show reference point", ShowReferencePoint);
		
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;
		
		params.nAng = (int) dlg.getNextNumber();
		params.nRad = (int) dlg.getNextNumber();
		MaxLines = (int) dlg.getNextNumber();
		MinPointsOnLine = (int) dlg.getNextNumber();
		ShowAccumulator = dlg.getNextBoolean();
		ShowExtendedAccumulator = dlg.getNextBoolean();
		ShowAccumulatorPeaks = dlg.getNextBoolean();
		ListStrongestLines = dlg.getNextBoolean();
		ShowLines = dlg.getNextBoolean();
		LineWidth = dlg.getNextNumber();
		ShowReferencePoint = dlg.getNextBoolean();
		
		if(dlg.invalidNumber()) {
			IJ.showMessage("Error", "Invalid input number");
			return false;
		}
		return true;
	}
	
	private Path2D markPoint(double xc, double yc) {
		double markerSize = 2.0;
		Path2D path = new Path2D.Double();
		path.moveTo(xc - markerSize, yc);
		path.lineTo(xc + markerSize, yc);
		path.moveTo(xc, yc - markerSize);
		path.lineTo(xc, yc + markerSize);
		return path;
	}

}

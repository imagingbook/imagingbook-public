/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch12_Ransac_Hough;

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
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.Color;
import java.awt.geom.Path2D;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of the {@link HoughTransformLines}
 * class for detecting straight lines in images (see Sec. 12.2 of [1] for
 * details). It expects a binary input image with background = 0 and foreground
 * (contour) pixels with values &gt; 0. A vector overlay is used to display the
 * detected lines. If no image is currently open, the plugin optionally loads a
 * suitable sample image.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/10/03
 */
public class Hough_Line_Detect implements PlugInFilter {

	private static int MaxLines = 5;			// number of strongest lines to be found
	private static int MinPointsOnLine = 50;	// min. number of points on each line

	private static boolean ShowAccumulator = true;
	private static boolean ShowExtendedAccumulator = false;
	private static boolean ShowAccumulatorPeaks = false;
	private static boolean ListStrongestLines = false;
	private static boolean ShowLines = true;
	private static boolean InvertOriginal = false;
	
	private static double  LineWidth = 0.5;
	private static Color   LineColor = Color.magenta;
	
	private static boolean ShowReferencePoint = true;
	private static Color   ReferencePointColor = Color.green;

	private ImagePlus im;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Hough_Line_Detect() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.NoisyLines);
		}
	}
	
	// -------------------------------------------------------------------------

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + NO_CHANGES;
	}

	@Override
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
//			return;
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
			(new ImagePlus("Accumulator peaks for " + im.getTitle(), maxIp)).show();
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
		GenericDialog gd = new GenericDialog("Hough Transform (lines)");
		gd.addNumericField("Axial steps", params.nAng, 0);
		gd.addNumericField("Radial steps", params.nRad, 0);
		gd.addNumericField("Max. number of lines to show", MaxLines, 0);
		gd.addNumericField("Min. number of points per line", MinPointsOnLine, 0);
		gd.addCheckbox("Show accumulator", ShowAccumulator);
		gd.addCheckbox("Show extended accumulator", ShowExtendedAccumulator);
		gd.addCheckbox("Show accumulator peaks", ShowAccumulatorPeaks);
		gd.addCheckbox("List strongest lines", ListStrongestLines);
		gd.addCheckbox("Show lines", ShowLines);
		gd.addNumericField("Line width", LineWidth, 1);
		gd.addCheckbox("Show reference point", ShowReferencePoint);
		
		gd.showDialog();
		if(gd.wasCanceled())
			return false;
		
		params.nAng = (int) gd.getNextNumber();
		params.nRad = (int) gd.getNextNumber();
		MaxLines = (int) gd.getNextNumber();
		MinPointsOnLine = (int) gd.getNextNumber();
		ShowAccumulator = gd.getNextBoolean();
		ShowExtendedAccumulator = gd.getNextBoolean();
		ShowAccumulatorPeaks = gd.getNextBoolean();
		ListStrongestLines = gd.getNextBoolean();
		ShowLines = gd.getNextBoolean();
		LineWidth = gd.getNextNumber();
		ShowReferencePoint = gd.getNextBoolean();
		
		if(gd.invalidNumber()) {
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

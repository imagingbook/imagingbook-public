/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package More_;

import static imagingbook.common.ij.IjUtils.noCurrentImage;
import static imagingbook.common.ij.IjUtils.requestSampleImage;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.util.Arrays;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.fd.FourierDescriptor;
import imagingbook.common.geometry.fd.FourierDescriptorTrigonometric;
import imagingbook.common.geometry.fd.Utils;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.math.Complex;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * This ImageJ plugin demonstrates the "trigonometric" construction of elliptic
 * Fourier descriptors. See Sec. 26.3.7 of [1] for details. The input is a
 * user-defined polygon selection (ROI). The number of Fourier coefficient pairs
 * can be specified in the user dialog. The plugin then displays the original polygon
 * and the approximate contour reconstruction from the Fourier descriptor using
 * all specified coefficient pairs. Increasing the number of coefficient pairs
 * improves the reconstruction.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction Using Java</em>, 2nd ed, Springer (2016).
 * </p>
 * 
 * @author WB
 * @version 2022/10/28
 */
public class Fourier_Descriptor_Trigonometic implements PlugInFilter {
	
	private static int FourierCoefficientPairs = 3;
	
	// visualization-related settings
	private static boolean ShowOriginalContour = true;
	private static boolean ShowFullReconstruction = true;
	private static int ReconstructionPoints = 100;
	
	private static Color ContourColor = new Color(0, 60, 255);
	private static double ContourStrokeWidth = 0.25;
	private static Color ReconstructionColor = new Color(0, 185, 15);
	private static double ReconstructionStrokeWidth = 0.5;
	
	private ImagePlus im;
	
	// ----------------------------------------------------------------
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Fourier_Descriptor_Trigonometic() {
		if (noCurrentImage()) {
			requestSampleImage(GeneralSampleImage.HouseRoi_tif);
		}
	}
	
	// ----------------------------------------------------------------
	
	@Override
	public int setup(String arg, ImagePlus im) { 
    	this.im = im;
    	return DOES_ALL + ROI_REQUIRED + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		
		if (!runDialog()) {
			return;
		}
		
		Roi anyRoi = im.getRoi();
		Pnt2d[] V = RoiUtils.getOutlinePointsFloat(anyRoi); // toPointArray(anyRoi);
		IJ.log("V=" + Arrays.toString(V));
		FourierDescriptor fd = FourierDescriptorTrigonometric.from(V, FourierCoefficientPairs);

		Complex ctr = fd.getCoefficient(0);
		
		ImagePlus imA = makeBackgroundImage();
		imA.show();
		
		ColoredStroke contourStroke = new ColoredStroke(ContourStrokeWidth, ContourColor);
		ColoredStroke reconstructionStroke = new ColoredStroke(ReconstructionStrokeWidth, ReconstructionColor);
		
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		
		if (ShowOriginalContour) {
			ola.addShape(toClosedPath(V), contourStroke);
			ola.addShape(Pnt2d.from(ctr.re, ctr.im).getShape());
		}
		
		if (true) {
			for (Pnt2d v : V) {
				ola.addShape(v.getShape(3), contourStroke);
			}
		}

		if (ShowFullReconstruction) { // draw the shape reconstructed from all FD-pairs
			Path2D rec = Utils.toPath(fd.getShapeFull(ReconstructionPoints));
			ola.addShape(rec, reconstructionStroke);
		}

		imA.setOverlay(ola.getOverlay());
		imA.updateAndDraw();
				
	}

	// -------------------------------------------------------------
	
	private ImagePlus makeBackgroundImage() {
		ByteProcessor bp = im.getProcessor().convertToByteProcessor();
		String title = String.format("%s-partial-%03d", im.getShortTitle(), FourierCoefficientPairs);
		
		if (bp.isInvertedLut()) {
			bp.invert();
			bp.invertLut();
		}
		brighten(bp, 220);	
		return new ImagePlus(title, bp);
	}
	
	private Path2D toClosedPath(Pnt2d[] pnts) {
		Path2D path = new Path2D.Float();
		path.moveTo(pnts[0].getX(), pnts[0].getY());
		for (int i = 1; i < pnts.length; i++) {
			path.lineTo(pnts[i].getX(), pnts[i].getY());
		}
		path.closePath();
		return path;
	}
	
	private void brighten(ByteProcessor ip, int minGray) {
		 float scale = (255 - minGray) / 255f;
		 int[] table = new int[256];
		 for (int i=0; i<256; i++) {
			 table[i] = Math.round(minGray + scale * i);
			 
		 }
		 ip.applyTable(table);
	}
	
	// -------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Number of FD pairs (min. 1)", FourierCoefficientPairs, 0);
		gd.addNumericField("Reconstruction Points", ReconstructionPoints, 0);
		gd.addCheckbox("Show Original Contour", ShowOriginalContour);
		gd.addCheckbox("Show Full Reconstruction", ShowFullReconstruction);

		gd.showDialog();
		if(gd.wasCanceled()) 
			return false;
		
		FourierCoefficientPairs = Math.max(1, (int) gd.getNextNumber());
		ReconstructionPoints = (int) gd.getNextNumber();
		ShowOriginalContour = gd.getNextBoolean();
		ShowFullReconstruction = gd.getNextBoolean();
		return true;
	}
	
}

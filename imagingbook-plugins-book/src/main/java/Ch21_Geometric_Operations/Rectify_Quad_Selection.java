/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch21_Geometric_Operations;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.mappings.linear.LinearMapping2D;
import imagingbook.common.geometry.mappings.linear.ProjectiveMapping2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.image.ImageMapper;
import imagingbook.common.image.interpolation.InterpolationMethod;
import imagingbook.common.math.PrintPrecision;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * ImageJ plugin, performs 4-point projective mapping from a selected polygon
 * ROI to the specified paper proportions (A4 or Letter, in portrait format).
 * See Sec. 21.1 (Exercise 21.5 and Fig. 21.17) of [1] for more details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author W. Burger
 * @version 2022/11/28
 * 
 * @see ProjectiveMapping2D
 * @see LinearMapping2D
 * @see ImageMapper
 */
public class Rectify_Quad_Selection implements PlugInFilter {
	
	private static PaperFormatType PaperFormat = PaperFormatType.A4;
	private static double OutputPixelSize = 0.5;	// pixel size in mm
	private static InterpolationMethod IPM = InterpolationMethod.Bilinear;
	private static boolean ListTransformationMatrix = true;
	
	private ImagePlus im = null;
	
	/**
	 * Constructor, asks to create sample image if no other image
	 * is currently open.
	 */
	public Rectify_Quad_Selection() {
		if (IjUtils.noCurrentImage() && DialogUtils.askForSampleImage()) {
			ImagePlus imp = GeneralSampleImage.PostalPackageSmall.getImagePlus();
			float[] xpts = {22, 330, 981, 756};	// manually selected!
			float[] ypts = {347, 71, 207, 591};
			Roi roi = new PolygonRoi(xpts, ypts, Roi.POLYGON);
			imp.setRoi(roi);
			imp.show();
		}
	}
	
	// -----------------------------------------------
	
	@Override
	public int setup(String arg0, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES + ROI_REQUIRED;
	}

	@Override
	public void run(ImageProcessor source) {
		Roi roi = im.getRoi();
		if (!(roi instanceof PolygonRoi)) {
			IJ.error("Polygon selection required!");
			return;
		}
		
		Pnt2d[] sourceCorners = RoiUtils.getOutlinePointsFloat(roi);
		if (sourceCorners.length < 4) {
			IJ.error("At least 4 points must be selected!");
			return;
		}
		
		if (!runDialog()) {
			return;
		}
		
		double targetWidth = PaperFormat.width;		// in millimeters
		double targetHeight = PaperFormat.height;
				
		int tWidth  = (int) Math.round(targetWidth  / OutputPixelSize);	// pixels
		int tHeight = (int) Math.round(targetHeight / OutputPixelSize);
		
		Pnt2d[] targetCorners = {
				PntInt.from(0, 0),
				PntInt.from(tWidth, 0),
				PntInt.from(tWidth, tHeight),
				PntInt.from(0, tHeight)};
		
		LinearMapping2D mp = // inverse mapping (target to source)
				ProjectiveMapping2D.fromPoints(sourceCorners, targetCorners).getInverse();	

		if (ListTransformationMatrix) {
			PrintPrecision.set(6);
			IJ.log("Inverse transformation (target to source): M = \n" + mp.toString());
		}
	
		ImageProcessor target = source.createProcessor(tWidth, tHeight);
		ImageMapper mapper = new ImageMapper(mp, null, IPM);
		mapper.map(source, target);
		new ImagePlus("target", target).show();
	}
	
	// --------------------------------------------
	
	enum PaperFormatType {
		A4(210, 297),
		Letter(216, 279);
		
		final double width, height;		// paper dimensions in mm
		
		PaperFormatType(double width, double height) {
			this.width = width;
			this.height = height;
		}
	}
	
	// --------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addMessage(DialogUtils.makeLineSeparatedString(
				"How to use: Select the four corners of the quad to be rectified",
				"with a polygon ROI, in clockwise direction, starting at the point",
				"that should map to the top-left corner (in portrait mode).",
				"Note that only the first 4 points are used!"));
		
		gd.addEnumChoice("Output paper format", PaperFormat);
		gd.addNumericField("Output pixel size (mm)", OutputPixelSize, 2);
		gd.addEnumChoice("Pixel interpolation method", IPM);
		gd.addCheckbox("List transformation matrix", ListTransformationMatrix);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
	
		PaperFormat = gd.getNextEnumChoice(PaperFormatType.class);
		OutputPixelSize = gd.getNextNumber();
		IPM = gd.getNextEnumChoice(InterpolationMethod.class);
		ListTransformationMatrix = gd.getNextBoolean();
		
		return true;
	}
	
}

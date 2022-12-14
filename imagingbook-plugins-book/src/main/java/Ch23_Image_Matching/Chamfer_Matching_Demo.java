/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch23_Image_Matching;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.image.matching.ChamferMatcher;
import imagingbook.common.image.matching.DistanceNorm;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * This ImageJ plugin demonstrates the use of the {@link ChamferMatcher} class. The active (search) image is assumed to
 * be binary (not checked). The reference (template) image is selected interactively by the user.
 *
 * @author WB
 * @version 2022/12/13
 * @see ChamferMatcher
 */
public class Chamfer_Matching_Demo implements PlugInFilter {

	private static DistanceNorm distNorm = DistanceNorm.L2;

	private ImagePlus imgI;		// the search image
	private ImagePlus imgR;		// the reference image (smaller)

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Chamfer_Matching_Demo() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.CirclesSquares);
			ImagePlus imp = IJ.getImage();
			imp.setRoi(39, 40, 58, 58);
		}
	}

	@Override
    public int setup(String arg, ImagePlus imp) {
    	this.imgI = imp;
        return DOES_8G + + ROI_REQUIRED + NO_CHANGES;
    }

	@Override
    public void run(ImageProcessor ipI) {
		if (!IjUtils.isBinary(ipI)) {
			IJ.showMessage("Current image is not binary!");
			return;
		}

		Rectangle roi = ipI.getRoi();
		if (roi == null) {
			IJ.showMessage("Rectangular selection required!");
			return;
		}

		if (!runDialog()) {
			return;
		}

		ByteProcessor I = (ByteProcessor) ipI;			// search image I
    	ByteProcessor R = (ByteProcessor) ipI.crop(); 	// reference image R
		new ImagePlus("Reference image (R)", R).show();

    	ChamferMatcher matcher = new ChamferMatcher(I, distNorm);
		float[][] Q = matcher.getMatch(R);

		ImagePlus matchIm = new ImagePlus("Match of " + imgI.getTitle(), new FloatProcessor(Q));
		matchIm.show();
		IJ.run(matchIm, "Find Maxima...", "prominence=10000 light output=[Point Selection]");
    }

	// -------------------------------------------------
 
    private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Distance norm", distNorm);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		distNorm = gd.getNextEnumChoice(DistanceNorm.class);
		return true;
    }
		
}

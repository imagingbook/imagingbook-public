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
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.image.LocalMinMaxFinder;
import imagingbook.common.image.LocalMinMaxFinder.ExtremalPoint;
import imagingbook.common.image.matching.ChamferMatcher;
import imagingbook.common.image.matching.DistanceNorm;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.*;

/**
 * This ImageJ plugin demonstrates the use of the {@link ChamferMatcher} class. The active (search) image is assumed to
 * be binary (not checked). The reference (template) image is selected interactively by the user.
 *
 * @author WB
 * @version 2022/12/13
 * @see ChamferMatcher
 */
public class Chamfer_Matching_Demo implements PlugInFilter {

	private static boolean ShowReferenceImage = true;
	private static boolean ShowScoreMap = true;

	private static DistanceNorm distNorm = DistanceNorm.L2;
	private static int MaxLocalMinimaCount = 20;
	private static double MarkerSize = 2;
	private static double MarkerStrokeWidth = 0.5;
	private static BasicAwtColor BestMatchColor = BasicAwtColor.Green;
	private static BasicAwtColor OtherMatchColor = BasicAwtColor.Orange;

	private ImagePlus imgI;		// the search image

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

		if (ShowReferenceImage) {
			new ImagePlus("Reference image (R)", R).show();
		}

    	ChamferMatcher matcher = new ChamferMatcher(I, distNorm);
		float[][] Q = matcher.getMatch(R);				// 2D match score function
		FloatProcessor fQ = new FloatProcessor(Q);

		// find local minima in 2D score function Q:
		LocalMinMaxFinder locator = new LocalMinMaxFinder(fQ);
		ExtremalPoint[] minima = locator.getMinima(MaxLocalMinimaCount);

		if (ShowScoreMap) {
			// create graphic overlay:
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter();

			// mark the "best" matching position
			ola.setStroke(new ColoredStroke(MarkerStrokeWidth, BestMatchColor.getColor()));
			ola.addShape(minima[0].getShape(MarkerSize));

			// mark the remaining local minima
			ola.setStroke(new ColoredStroke(MarkerStrokeWidth, OtherMatchColor.getColor()));
			for (int i = 1; i < minima.length; i++) {
				ola.addShape(minima[i].getShape(MarkerSize));
			}

			ImagePlus matchIm = new ImagePlus("Match of " + imgI.getTitle(), fQ);
			matchIm.setOverlay(ola.getOverlay());
			matchIm.show();
		}
    }

	// -------------------------------------------------
 
    private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Distance norm", distNorm);
		gd.addCheckbox("Show reference image", ShowReferenceImage);
		gd.addNumericField("Max. local minima count", MaxLocalMinimaCount, 0);
		gd.addEnumChoice("Best match color", BestMatchColor);
		gd.addEnumChoice("Other match color", OtherMatchColor);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		distNorm = gd.getNextEnumChoice(DistanceNorm.class);
		ShowReferenceImage = gd.getNextBoolean();
		MaxLocalMinimaCount = (int) gd.getNextNumber();
		BestMatchColor = gd.getNextEnumChoice(BasicAwtColor.class);
		OtherMatchColor = gd.getNextEnumChoice(BasicAwtColor.class);
		return true;
    }
		
}

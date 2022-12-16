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
import ij.gui.Roi;
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

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of the {@link ChamferMatcher} class. The active (search) image is assumed to
 * be binary (checked). The reference (template) image is extracted from the required rectangular selection (ROI) in the
 * search image and then corrupted with binary (salt-and-pepper) noise (i.e., a certain percentage of its pixels is
 * randomly flipped). Increasing noise leads to poorer match results and matching eventually fails when the noise level
 * is too high. Detected matches are shown as graphic overlays on the input image. Also, the matching score surface and
 * its local minima are optionally displayed. See Sec. 23.2.3 (Alg. 23.3) of [1] for details on Chamfer matching.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/16
 * @see ChamferMatcher
 * @see LocalMinMaxFinder
 */
public class Chamfer_Matching implements PlugInFilter {

	private static boolean ShowReferenceImage = true;
	private static boolean ShowScoreMap = true;

	private static boolean ReferenceAddNoise = true;
	private static double ReferenceNoiseLevel = 2.5 / 100;
	private static boolean ShowScoreValues = true;
	private static int ScoreValueFontSize = 5;

	private static DistanceNorm distNorm = DistanceNorm.L2;
	private static int MaxLocalMinimaCount = 5;
	private static double MarkerSize = 2;
	private static double MarkerStrokeWidth = 0.5;
	private static BasicAwtColor BestMatchColor = BasicAwtColor.Green;
	private static BasicAwtColor OtherMatchColor = BasicAwtColor.Orange;

	private ImagePlus imgI;		// the search image

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Chamfer_Matching() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.CirclesSquares);
			ImagePlus imp = IJ.getImage();
			imp.setRoi(39, 40, 58, 58);
		}
	}

	@Override
    public int setup(String arg, ImagePlus imp) {
    	this.imgI = imp;
        return DOES_8G + ROI_REQUIRED + NO_CHANGES;
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

		ByteProcessor I = (ByteProcessor) ipI;			// search image
    	ByteProcessor R = (ByteProcessor) ipI.crop(); 	// reference image
		int wR = R.getWidth();
		int hR = R.getHeight();

		if (ReferenceAddNoise) {
			Random rg = new Random();
			for (int u = 0; u < wR; u++) {
				for (int v = 0; v < hR; v++) {
					if (rg.nextDouble() < ReferenceNoiseLevel) {
						R.set(u, v, (R.get(u, v) > 0) ? 0 : 255);
					}
				}
			}
		}

		if (ShowReferenceImage) {
			new ImagePlus("Reference image (R)", R).show();
		}

    	ChamferMatcher matcher = new ChamferMatcher(I, distNorm);
		float[][] Q = matcher.getMatch(R);				// 2D match score function
		FloatProcessor fQ = new FloatProcessor(Q);

		// find local minima in 2D score function Q:
		LocalMinMaxFinder locator = new LocalMinMaxFinder(fQ);
		ExtremalPoint[] minima = locator.getMinima(MaxLocalMinimaCount);

		// show result on search image:
		{
			imgI.setRoi((Roi) null);	// remove ROI selection
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
			ColoredStroke bestStroke = new ColoredStroke(MarkerStrokeWidth, BestMatchColor.getColor());
			ColoredStroke otherStroke = new ColoredStroke(MarkerStrokeWidth, OtherMatchColor.getColor());

			// mark matches in search image:
			ola.setStroke(bestStroke);
			ola.setTextColor(BestMatchColor.getColor());
			ola.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, ScoreValueFontSize));
			for (int i = 0; i < minima.length; i++) {
				if (i > 0) {
					ola.setStroke(otherStroke);
					ola.setTextColor(OtherMatchColor.getColor());
				}
				ExtremalPoint ep = minima[i];
				ola.addShape(new Rectangle2D.Double(minima[i].x, minima[i].y, wR, hR));
				if (ShowScoreValues) {
					ola.addText(ep.x + 1, ep.y, String.format("%.0f", minima[i].q));
				}
			}
			imgI.setOverlay(ola.getOverlay());
		}

		if (ShowScoreMap) {
			// create graphic overlay:
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
			ColoredStroke bestStroke = new ColoredStroke(MarkerStrokeWidth, BestMatchColor.getColor());
			ColoredStroke otherStroke = new ColoredStroke(MarkerStrokeWidth, OtherMatchColor.getColor());

			// mark local minima
			ola.setStroke(bestStroke);
			ola.setTextColor(BestMatchColor.getColor());
			ola.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, ScoreValueFontSize));
			for (int i = 0; i < minima.length; i++) {
				if (i > 0) {
					ola.setStroke(otherStroke);
					ola.setTextColor(OtherMatchColor.getColor());
				}
				ExtremalPoint ep = minima[i];
				ola.addShape(ep.getShape(MarkerSize));
				if (ShowScoreValues) {
					ola.addText(ep.x + MarkerSize, ep.y, String.format("%.0f", minima[i].q));
				}
			}

			ImagePlus matchIm = new ImagePlus("Match of " + imgI.getTitle(), fQ);
			matchIm.setOverlay(ola.getOverlay());
			matchIm.show();
		}
    }

	// -------------------------------------------------
 
    private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		if (imgI.isInvertedLut()) {
			gd.setInsets(0, 0, 0);
			gd.addMessage("NOTE: Image has inverted LUT (0 = white)!");
		}
		gd.addCheckbox("Add noise to reference image", ReferenceAddNoise);
		gd.addNumericField("Noise level (%)", ReferenceNoiseLevel * 100, 1);
		gd.addEnumChoice("Distance transform norm", distNorm);
		gd.addNumericField("Max. local minima count", MaxLocalMinimaCount, 0);
		gd.addEnumChoice("Best match color", BestMatchColor);
		gd.addEnumChoice("Other match color", OtherMatchColor);
		gd.addCheckbox("Show score values", ShowScoreValues);
		gd.addNumericField("Score value font size", ScoreValueFontSize, 0);
		gd.addCheckbox("Show reference image", ShowReferenceImage);
		gd.addCheckbox("Show score map", ShowScoreMap);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		ReferenceAddNoise = gd.getNextBoolean();
		ReferenceNoiseLevel = gd.getNextNumber() / 100;
		distNorm = gd.getNextEnumChoice(DistanceNorm.class);
		MaxLocalMinimaCount = (int) gd.getNextNumber();
		BestMatchColor = gd.getNextEnumChoice(BasicAwtColor.class);
		OtherMatchColor = gd.getNextEnumChoice(BasicAwtColor.class);
		ShowScoreValues = gd.getNextBoolean();
		ScoreValueFontSize = (int) gd.getNextNumber();
		ShowReferenceImage = gd.getNextBoolean();
		ShowScoreMap = gd.getNextBoolean();
		return true;
    }
		
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch23_Image_Matching;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.image.LocalMinMaxFinder;
import imagingbook.common.image.LocalMinMaxFinder.ExtremalPoint;
import imagingbook.common.image.matching.CorrCoeffMatcher;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Locale;
import java.util.Random;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of the {@link CorrCoeffMatcher} class. The active (grayscale) image is taken
 * as the search image. The reference (template) image is extracted from the required rectangular selection (ROI) in the
 * search image and then corrupted by Gaussian noise with user-specified sigma. The correlation coefficient matcher is
 * quite resistant to high noise levels. Detected matches are shown as graphic overlays on the input image. Also, the
 * matching score surface and its local maxima are optionally displayed. See Sec. 23.1.1 (Alg. 23.1) of [1] for
 * additional details on correlation coefficient matching.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/16
 * @see CorrCoeffMatcher
 * @see LocalMinMaxFinder
 */
public class CorrelCoefficient_Matching implements PlugInFilter {

	private static boolean ShowReferenceImage = true;
	private static boolean ShowScoreMap = true;

	private static boolean ReferenceAddNoise = true;
	private static double ReferenceNoiseSigma = 20.0;
	private static double AcceptanceThreshold = 0.60;
	private static boolean ShowScoreValues = true;
	private static int ScoreValueFontSize = 5;

	private static int MaxLocalMinimaCount = 10;
	private static double MarkerSize = 2;
	private static double MarkerStrokeWidth = 0.5;
	private static BasicAwtColor BestMatchColor = BasicAwtColor.Green;
	private static BasicAwtColor OtherMatchColor = BasicAwtColor.Orange;

	private ImagePlus imgI;		// the search image

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public CorrelCoefficient_Matching() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.IrishManor);
			ImagePlus imp = IJ.getImage();
			imp.setRoi(190, 41, 30, 30);
		}
	}

	@Override
    public int setup(String arg, ImagePlus imp) {
    	this.imgI = imp;
        return DOES_8G + ROI_REQUIRED + NO_CHANGES;
    }

	@Override
    public void run(ImageProcessor ipI) {

		Rectangle roi = ipI.getRoi();
		if (roi == null) {
			IJ.showMessage("Rectangular selection required!");
			return;
		}

		if (!runDialog()) {
			return;
		}

		ImageProcessor I = ipI;			// search image
    	ImageProcessor R = ipI.crop(); 	// reference image
		int wR = R.getWidth();
		int hR = R.getHeight();

		if (ReferenceAddNoise) {
			Random rg = new Random();
			for (int u = 0; u < wR; u++) {
				for (int v = 0; v < hR; v++) {
					double x = R.get(u, v) + rg.nextGaussian() * ReferenceNoiseSigma;
					R.putPixel(u, v, (int) Math.round(x));
				}
			}
		}

		if (ShowReferenceImage) {
			new ImagePlus("Reference image (R)", R).show();
		}


		CorrCoeffMatcher matcher = new CorrCoeffMatcher(I);
		float[][] Q = matcher.getMatch(R);				// 2D match score function
		FloatProcessor fQ = new FloatProcessor(Q);

		// find local minima in 2D score function Q:
		LocalMinMaxFinder locator = new LocalMinMaxFinder(fQ);
		ExtremalPoint[] maxima = locator.getMaxima(MaxLocalMinimaCount);

		// show result on the search image:
		{
			imgI.setRoi((Roi) null);	// remove ROI selection
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
			ColoredStroke bestStroke = new ColoredStroke(MarkerStrokeWidth, BestMatchColor.getColor());
			ColoredStroke otherStroke = new ColoredStroke(MarkerStrokeWidth, OtherMatchColor.getColor());

			// mark matches in search image:
			ola.setStroke(bestStroke);
			ola.setTextColor(BestMatchColor.getColor());
			ola.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, ScoreValueFontSize));
			for (int i = 0; i < maxima.length; i++) {
				ExtremalPoint ep = maxima[i];
				if (ep.q < AcceptanceThreshold) {
					break;
				}
				if (i > 0) {
					ola.setStroke(otherStroke);
					ola.setTextColor(OtherMatchColor.getColor());
				}
				ola.addShape(new Rectangle2D.Double(maxima[i].x, maxima[i].y, wR, hR));
				if (ShowScoreValues) {
					ola.addText(ep.x + 1, ep.y, String.format(Locale.US, "%.3f", maxima[i].q));
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
			for (int i = 0; i < maxima.length; i++) {
				ExtremalPoint ep = maxima[i];
				if (ep.q < AcceptanceThreshold) {
					break;
				}
				if (i > 0) {
					ola.setStroke(otherStroke);
					ola.setTextColor(OtherMatchColor.getColor());
				}
				ola.addShape(ep.getShape(MarkerSize));
				if (ShowScoreValues) {
					ola.addText(ep.x + MarkerSize, ep.y, String.format(Locale.US, "%.3f", maxima[i].q));
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
		gd.addCheckbox("Add noise to reference image", ReferenceAddNoise);
		gd.addNumericField("Reference noise sigma", ReferenceNoiseSigma, 2);
		gd.addNumericField("Acceptance threshold (< 1.0)", AcceptanceThreshold, 2);
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
		ReferenceNoiseSigma = gd.getNextNumber();
		AcceptanceThreshold = gd.getNextNumber();
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

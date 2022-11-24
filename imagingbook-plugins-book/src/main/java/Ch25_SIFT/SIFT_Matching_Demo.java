/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package Ch25_SIFT;

import static imagingbook.common.color.sets.ColorEnumeration.getColors;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.common.sift.SiftColors;
import imagingbook.common.sift.SiftDescriptor;
import imagingbook.common.sift.SiftDetector;
import imagingbook.common.sift.SiftMatch;
import imagingbook.common.sift.SiftMatcher;
import imagingbook.common.sift.SiftParameters;
import imagingbook.sampleimages.SiftSampleImage;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of the SIFT detection and matching
 * framework. See Sec. 25.5 of [1] for details.
 * </p>
 * <p>
 * The plugin takes a single image, which is assumed to be composed of a left
 * and right frame. The input image is split horizontally, then SIFT detection
 * and matching is applied to the two sub-images. The input image is always
 * converted to grayscale (and normalized to [0,1]) before SIFT feature
 * detection is performed. The result is displayed as a graphic overlay by
 * connecting and annotating the best-matching features. When saved as a TIFF
 * image the overlay is preserved.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/20
 */
public class SIFT_Matching_Demo implements PlugInFilter {
	
	// matching parameters:
	private static NormType DistanceNormType = SiftMatcher.DefaultNormType;
	private static double MaxDistanceRatio = SiftMatcher.DefaultRMax;

	// display parameters:
	private static int NumberOfMatchesToShow = 25;
	private static double FeatureScale = 1.0;
	private static boolean ShowFeatureLabels = true;

	private static double MatchLineCurvature = 0.25;
	private static double FeatureStrokewidth = 1.0;

	private static BasicAwtColor MatchLineColor = BasicAwtColor.Magenta;
	private static BasicAwtColor LabelColor = BasicAwtColor.Yellow;
	private static Font LabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	
	private static Color[] ScaleLevelColors = getColors(SiftColors.class);
	
	private ImagePlus im = null;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public SIFT_Matching_Demo() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(SiftSampleImage.RamsesSmall);
		}
	}
	
	@Override
	public int setup(String arg0, ImagePlus im) {
		this.im = im;
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {		
		if (!runDialog()) {
			return;
		}

		final int w = ip.getWidth();
		final int h = ip.getHeight();
		final int w2 = w / 2;
		
		FloatProcessor Ia = IjUtils.crop(ip, 0, 0, w2, h).convertToFloatProcessor();
		FloatProcessor Ib = IjUtils.crop(ip, w2, 0, w2, h).convertToFloatProcessor();
		
		SiftParameters siftParams = new SiftParameters();
		// modify SIFT parameters here if needed
		
		// we use the same parameters on left and right image
		SiftDetector sdA = new SiftDetector(Ia, siftParams);
		SiftDetector sdB = new SiftDetector(Ib, siftParams);
		
		List<SiftDescriptor> fsA = sdA.getSiftFeatures();
		List<SiftDescriptor> fsB = sdB.getSiftFeatures();

		IJ.log("SIFT features found in left image: "  + fsA.size());
		IJ.log("SIFT features found in right image: " + fsB.size());

		// create a SIFT matcher and perform matching:
		SiftMatcher sm = new SiftMatcher(DistanceNormType, MaxDistanceRatio);
		List<SiftMatch> matches = sm.match(fsA, fsB);	// matches are sorted by decreasing quality
		
		IJ.log("Matches found: " + matches.size());
		if (matches.isEmpty()) {
			return;
		}

		// --------------------------------------------------
		
		ColoredStroke matchLineStroke = new ColoredStroke(0.2, MatchLineColor.getColor());
		ColoredStroke[] fStrokes = new ColoredStroke[ScaleLevelColors.length];
		for (int i = 0; i < ScaleLevelColors.length; i++) {
			fStrokes[i] = new ColoredStroke(FeatureStrokewidth, ScaleLevelColors[i]);
		}
		
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		ola.setTextColor(LabelColor.getColor());
		ola.setFont(LabelFont);
		
		AffineTransform trans = AffineTransform.getTranslateInstance(w2, 0);
		
		// add vertical separator between left and right image:
		ola.addShape(new Line2D.Double(w2 - 0.5, 0, w2 - 0.5, h), 
				new ColoredStroke(0.2, Color.green, 5)); 
		
		// add SIFT markers, connecting lines and number labels
		int n = 1;
		for (SiftMatch m : matches) {
			SiftDescriptor dA = m.getDescriptor1();
			SiftDescriptor dB = m.getDescriptor2();
			
			// draw the matched SIFT markers:
			Shape sA = dA.getShape(FeatureScale);
			Shape sB = trans.createTransformedShape(dB.getShape(FeatureScale));
			ola.addShape(sA, fStrokes[dA.getScaleLevel() % fStrokes.length]);
			ola.addShape(sB, fStrokes[dB.getScaleLevel() % fStrokes.length]);
			
			// draw the connecting lines:
			Shape cAB = makeConnectingShape(dA, dB.plus(w2, 0));
			ola.addShape(cAB, matchLineStroke);
			
			// draw the numeric feature labels on both sides:
			if (ShowFeatureLabels) {
				String label = Integer.toString(n);	
				ola.addText(dA.getX(), dA.getY(), label);
				ola.addText(dB.getX() + w2, dB.getY(), label);
			}
			if (++n > NumberOfMatchesToShow) break;
		}

		im.setOverlay(ola.getOverlay());
	}
	
	// -------------------------
	
	private Shape makeConnectingShape(Pnt2d p1, Pnt2d p2) {
		double x1 = p1.getX(); 
		double y1 = p1.getY();
		double x2 = p2.getX(); 
		double y2 = p2.getY();
		double dx = x2 - x1;
		double dy = y2 - y1;
		double ctrlx = (x1 + x2) / 2 - MatchLineCurvature * dy;
		double ctrly = (y1 + y2) / 2 + MatchLineCurvature * dx;
		return new QuadCurve2D.Double(x1, y1, ctrlx, ctrly, x2, y2);
	}
	
	// -------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());	
		gd.addMessage("This plugin expects a single image composed of a left and right frame.");
		
		gd.addMessage("SIFT matching parameters:");
		gd.addEnumChoice("Distance norm type", DistanceNormType);
		gd.addNumericField("Max. ratio between 1st/2nd match (rMax)", MaxDistanceRatio, 2);
		
		gd.addMessage("Display parameters:");
		gd.addNumericField("Number of matches to show", NumberOfMatchesToShow, 0);
		gd.addNumericField("Feature scale", FeatureScale, 2);
		gd.addNumericField("Match line curvature", MatchLineCurvature, 2);
		gd.addEnumChoice("Match line color", MatchLineColor);
		gd.addEnumChoice("Label color", LabelColor);
		gd.addCheckbox("Show feature labels", ShowFeatureLabels);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		DistanceNormType = gd.getNextEnumChoice(NormType.class);
		MaxDistanceRatio = gd.getNextNumber();
		NumberOfMatchesToShow = (int) gd.getNextNumber();
		FeatureScale = gd.getNextNumber();
		MatchLineCurvature = gd.getNextNumber();
		MatchLineColor = gd.getNextEnumChoice(BasicAwtColor.class);
		LabelColor = gd.getNextEnumChoice(BasicAwtColor.class);
		ShowFeatureLabels = gd.getNextBoolean();
		
		return true;
	}

}

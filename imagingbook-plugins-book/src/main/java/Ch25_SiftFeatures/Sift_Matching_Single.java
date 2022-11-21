/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package Ch25_SiftFeatures;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.ShapeRoi;
import ij.gui.TextRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.common.sift.SiftDescriptor;
import imagingbook.common.sift.SiftDetector;
import imagingbook.common.sift.SiftMatch;
import imagingbook.common.sift.SiftMatcher;
import imagingbook.sampleimages.SiftSampleImage;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of the SIFT detection and matching
 * framework. The plugin takes a single grayscale image, which is assumed to be
 * composed of a left and right frame. The input image is split horizontally,
 * then SIFT detection and matching is applied to the two sub-images.
 * </p>
 * <p>
 * The result is displayed as a graphic overlay by connecting and annotating the
 * best-matching features. When saved as a TIFF image the overlay is preserved.
 * </p>
 * 
 * @author WB
 * @version 2022/11/20
 */
public class Sift_Matching_Single implements PlugInFilter {

	private static NormType DistanceNormType = SiftMatcher.DefaultNormType;
	private static double MaxDistanceRatio = SiftMatcher.DefaultRMax;

	private static int NumberOfMatchesToShow = 25;
	private static double FeatureScale = 1.0;
	private static boolean ShowFeatureLabels = true;

	private static double MatchLineCurvature = 0.25;
	private static double FeatureStrokewidth = 1.0;

	private static Color DescriptorColor1 = Color.green;
	private static Color DescriptorColor2 = Color.green;
	private static Color MatchLineColor = 	Color.magenta;
	private static Color LabelColor = 		Color.yellow;
	private static Font LabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	
	
	private ImagePlus im = null;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Sift_Matching_Single() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(SiftSampleImage.RamsesSmallStack_tif);
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

		int w = ip.getWidth();
		int h = ip.getHeight();
		int w2 = w / 2;
		
		FloatProcessor Ia = IjUtils.crop(ip, 0, 0, w2, h).convertToFloatProcessor();
		FloatProcessor Ib = IjUtils.crop(ip, w2, 0, w2, h).convertToFloatProcessor();
		
		SiftDetector.Parameters siftParams = new SiftDetector.Parameters();
		// modify SIFT parameters here if needed
		// we use the same parameters on left and right image
		SiftDetector sdA = new SiftDetector(Ia, siftParams);
		SiftDetector sdB = new SiftDetector(Ib, siftParams);
		
		List<SiftDescriptor> fsA = sdA.getSiftFeatures();
		List<SiftDescriptor> fsB = sdB.getSiftFeatures();

		IJ.log("SIFT features found in left image: "  + fsA.size());
		IJ.log("SIFT features found in right image: " + fsB.size());

		// --------------------------------------------------

		IJ.log("matching ...");
		// create a SIFT matcher:
		SiftMatcher sm = new SiftMatcher(DistanceNormType, MaxDistanceRatio);
		// perform matching:
		List<SiftMatch> matches = sm.match(fsA, fsB);
		IJ.log("Matches found: " + matches.size());

		// --------------------------------------------------

		Overlay oly = new Overlay();
		oly.add(makeStraightLine(w, 0, w, h, Color.black));	// vertical separator
		int xoffset = w2;

		// draw the matched SIFT markers:
		// TODO: convert to CustomOverlay !
		int count = 1;
		for (SiftMatch m : matches) {
			SiftDescriptor dA = m.getDescriptor1();
			SiftDescriptor dB = m.getDescriptor2();
			oly.add(makeSiftMarker(dA, 0, 0, DescriptorColor1));
			oly.add(makeSiftMarker(dB, xoffset, 0, DescriptorColor2));
			count++;
			if (count > NumberOfMatchesToShow) break;
		}

		// draw the connecting lines:
		count = 1;
		for (SiftMatch m : matches) {
			SiftDescriptor dA = m.getDescriptor1();
			SiftDescriptor dB = m.getDescriptor2();
			oly.add(makeConnectingLine(dA, dB, xoffset, 0, MatchLineColor));
			count++;
			if (count > NumberOfMatchesToShow) break;
		}

		// draw the labels:
		if (ShowFeatureLabels) {
			count = 1;
			for (SiftMatch m : matches) {
				SiftDescriptor dA = m.getDescriptor1();
				SiftDescriptor dB = m.getDescriptor2();
				String label = Integer.toString(count);
				oly.add(makeSiftLabel(dA, 0, 0, label));
				oly.add(makeSiftLabel(dB, xoffset, 0, label));
				count++;
				if (count > NumberOfMatchesToShow) break;
			}
		}

		if (oly != null) {
			im.setOverlay(oly);
		}
	}
	
	// -------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		
		gd.addMessage("SIFT matching parameters:");
		gd.addEnumChoice("Distance norm type", DistanceNormType);
		gd.addNumericField("Max. ratio between 1st/2nd match (rMax)", MaxDistanceRatio, 2);
		
		gd.addMessage("Display parameters:");
		gd.addNumericField("Number of matches to show", NumberOfMatchesToShow, 0);
		gd.addNumericField("Feature scale", FeatureScale, 2);
		gd.addCheckbox("Show feature labels", ShowFeatureLabels);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		DistanceNormType = gd.getNextEnumChoice(NormType.class);
		MaxDistanceRatio = gd.getNextNumber();
		NumberOfMatchesToShow = (int) gd.getNextNumber();
		FeatureScale = gd.getNextNumber();
		ShowFeatureLabels = gd.getNextBoolean();
		
		return true;
	}
	

	// drawing methods -------------------------------------------------
	
	private ShapeRoi makeStraightLine(double x1, double y1, double x2, double y2, Color col) {
		Path2D poly = new Path2D.Double();
		poly.moveTo(x1, y1);
		poly.lineTo(x2, y2);
		ShapeRoi roi = new ShapeRoi(poly);
		roi.setStrokeWidth((float)FeatureStrokewidth);
		roi.setStrokeColor(col);
		return roi;
	}

	private ShapeRoi makeSiftMarker(SiftDescriptor d, double xo, double yo, Color col) {
		double x = d.getX() + xo; 
		double y = d.getY() + yo; 
		double scale = FeatureScale * d.getScale();
		double orient = d.getOrientation();
		double sin = Math.sin(orient);
		double cos = Math.cos(orient);
		Path2D poly = new Path2D.Double();	
		poly.moveTo(x + (sin - cos) * scale, y - (sin + cos) * scale);
		//poly.lineTo(x, y);
		poly.lineTo(x + (sin + cos) * scale, y + (sin - cos) * scale);
		poly.lineTo(x, y);
		poly.lineTo(x - (sin - cos) * scale, y + (sin + cos) * scale);
		poly.lineTo(x - (sin + cos) * scale, y - (sin - cos) * scale);
		poly.closePath();
		ShapeRoi roi = new ShapeRoi(poly);
		roi.setStrokeWidth((float)FeatureStrokewidth);
		roi.setStrokeColor(col);
		return roi;
	}

	private ShapeRoi makeConnectingLine(SiftDescriptor f1, SiftDescriptor f2, double xo, double yo, Color col) {
		double x1 = f1.getX(); 
		double y1 = f1.getY();
		double x2 = f2.getX() + xo; 
		double y2 = f2.getY() + yo;
		double dx = x2 - x1;
		double dy = y2 - y1;
		double ctrlx = (x1 + x2) / 2 - MatchLineCurvature * dy;
		double ctrly = (y1 + y2) / 2 + MatchLineCurvature * dx;
		Shape curve = new QuadCurve2D.Double(x1, y1, ctrlx, ctrly, x2, y2);
		ShapeRoi roi = new ShapeRoi(curve);
		roi.setStrokeWidth((float)FeatureStrokewidth);
		roi.setStrokeColor(col);
		return roi;
	}

	private TextRoi makeSiftLabel(SiftDescriptor d, double xo, double yo, String text) {
		double x = d.getX() + xo; 
		double y = d.getY() + yo; 
		TextRoi roi = new TextRoi((int)Math.rint(x), (int)Math.rint(y), text, LabelFont);
		roi.setStrokeColor(LabelColor);
		return roi;
	}

}

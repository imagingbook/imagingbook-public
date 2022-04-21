package Sift;

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.gui.ShapeRoi;
import ij.gui.TextRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.sift.SiftDescriptor;
import imagingbook.common.sift.SiftDetector;
import imagingbook.common.sift.SiftMatch;
import imagingbook.common.sift.SiftMatcher;

/**
 * This ImageJ plugin demonstrates the use of the SIFT detection and matching
 * framework. The plugin takes a stack of at least 2 grayscale images, finds 
 * interest points in the first 2 images and determines the best matches between
 * feature points.
 * To display the results, the two input images are mounted side-by-side
 * in a combined image and the best-matching features are connected.
 * Vector overlays are used to draw the results on top of the images.
 * 
 * @author W. Burger
 * @version 2016/01/05
 */
public class Sift_Matching_Demo implements PlugInFilter {

	static int NumberOfMatchesToShow = 25;
	static double MatchLineCurvature = 0.25;
	static double FeatureScale = 1.0;
	static double FeatureStrokewidth = 1.0;

	static boolean ShowFeatureLabels = true;

	static Color SeparatorColor = 	Color.black;
	static Color DescriptorColor1 = Color.green;
	static Color DescriptorColor2 = Color.green;
	static Color MatchLineColor = 	Color.magenta;
	static Color LabelColor = 		Color.yellow;
	static Font LabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

	ImagePlus imp = null;

	@Override
	public int setup(String arg0, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G + PlugInFilter.STACK_REQUIRED + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (imp.getStackSize() < 2) {
			IJ.error("Stack with at least 2 images required!");
			return;
		}

		ImageStack stack = imp.getImageStack();
		final int w = stack.getWidth();
		final int h = stack.getHeight();

		FloatProcessor Ia = stack.getProcessor(1).convertToFloatProcessor();
		FloatProcessor Ib = stack.getProcessor(2).convertToFloatProcessor();

		SiftDetector.Parameters params = new SiftDetector.Parameters();
		// modify SIFT parameters here if needed

		SiftDetector sdA = new SiftDetector(Ia, params);
		SiftDetector sdB = new SiftDetector(Ib, params);

		List<SiftDescriptor> fsA = sdA.getSiftFeatures();
		List<SiftDescriptor> fsB = sdB.getSiftFeatures();

		IJ.log("SIFT features found in image 1: " + fsA.size());
		IJ.log("SIFT features found in image 2: " + fsB.size());

		// --------------------------------------------------

		IJ.log("matching ...");
		// create a matcher on the first set of features:
		SiftMatcher sm = new SiftMatcher(fsA);
		// match the second set of features:
		List<SiftMatch> matches = sm.matchDescriptors(fsB);

		// --------------------------------------------------

		ImageProcessor montage = new ByteProcessor(2 * w, h);
		montage.insert(stack.getProcessor(1), 0, 0);
		montage.insert(stack.getProcessor(2), w, 0);
		ImagePlus montageIm = new ImagePlus(imp.getShortTitle()+"-matches", montage);

		Overlay oly = new Overlay();
		oly.add(makeStraightLine(w, 0, w, h, Color.black));	// vertical separator
		int xoffset = w;

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
			montageIm.setOverlay(oly);
		}
		montageIm.show();
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

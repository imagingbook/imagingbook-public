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
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.sift.SiftColor;
import imagingbook.common.sift.SiftDescriptor;
import imagingbook.common.sift.SiftDetector;
import imagingbook.common.sift.SiftDetector.Parameters;
import imagingbook.sampleimages.SiftSampleImage;



/**
 * This plugin extracts multi-scale SIFT features from the current image and
 * displays them as M-shaped markers. The list of keypoints (if shown) is sorted
 * by descending magnitude. The input image is always converted to grayscale
 * before SIFT feature detection is performed.
 * 
 * @author WB
 * @version 2022/04/01
 * 
 * @see SiftDetector
 * @see SiftDescriptor
 */

public class Extract_Sift_Features implements PlugInFilter {
	
	private static Parameters params = new SiftDetector.Parameters();
	private static int MaxFeaturesToShow = 200;
	private static double FeatureScale = 1.0; // 1.5;
	private static double FeatureStrokewidth = 0.5;
	private static boolean ListSiftFeatures = false;
	private static Color[] ScaleLevelColors = getColors(SiftColor.class);
	
	private ImagePlus im;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Extract_Sift_Features() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(SiftSampleImage.Castle);
		}
	}
	
	// ---------------------------------------------------
	
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
		
		FloatProcessor fp = ip.convertToFloatProcessor();
		SiftDetector detector = new SiftDetector(fp, params);
		List<SiftDescriptor> features = detector.getSiftFeatures();
		
		ColoredStroke[] fStrokes = new ColoredStroke[ScaleLevelColors.length];
		for (int i = 0; i < ScaleLevelColors.length; i++) {
			fStrokes[i] = new ColoredStroke(FeatureStrokewidth, ScaleLevelColors[i]);
		}
		
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		
		int cnt = 1;
		for (SiftDescriptor sf : features) {	
			ColoredStroke stroke = fStrokes[sf.getScaleLevel() % fStrokes.length];
			ola.addShape(sf.getShape(FeatureScale), stroke);
			if(++cnt > MaxFeaturesToShow) break;
		}

		im.setOverlay(ola.getOverlay());
		
		if (ListSiftFeatures) {
			int n = 1;
			for (SiftDescriptor sf : features) {
				IJ.log(n + ": " + sf.toString());
				if(++n > MaxFeaturesToShow) break;
			}
		}
	}
	
	// ---------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addMessage("This plugin expects a single image.");
		DialogUtils.addToDialog(params, gd);
		gd.addNumericField("Max. number of features to show", MaxFeaturesToShow);
		gd.addCheckbox("List SIFT features)", ListSiftFeatures);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		DialogUtils.getFromDialog(params, gd);
		MaxFeaturesToShow = (int) gd.getNextNumber();
		ListSiftFeatures = gd.getNextBoolean();
		return true;
	}

}

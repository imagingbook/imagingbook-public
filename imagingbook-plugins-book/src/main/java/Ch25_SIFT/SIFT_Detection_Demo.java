/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */

package Ch25_SIFT;

import static imagingbook.common.color.sets.ColorEnumeration.getColors;

import java.awt.Color;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.sift.SiftColors;
import imagingbook.common.sift.SiftDescriptor;
import imagingbook.common.sift.SiftDetector;
import imagingbook.common.sift.SiftParameters;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * This plugin extracts multi-scale SIFT features [1] from the current image and
 * displays them as M-shaped markers. The list of keypoints (if shown) is sorted
 * by descending magnitude. The input image is always converted to grayscale
 * before SIFT feature detection is performed. See Ch. 25 of [2] for details. If
 * no image is currently open, the user is asked to load a predefined sample
 * image.
 * </p>
 * <p>
 * [1] D. G. Lowe. Distinctive image features from scale-invariant keypoints.
 * International Journal of Computer Vision 60, 91–110 (2004). <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/04/01
 * 
 * @see SiftDetector
 * @see SiftDescriptor
 */

public class SIFT_Detection_Demo implements PlugInFilter {
	
	private static ImageResource SampleImage = GeneralSampleImage.Castle;
	
	private static SiftParameters params = new SiftParameters();
	private static int MaxFeaturesToShow = 200;
	private static double FeatureScale = 1.0; // 1.5;
	private static double FeatureStrokewidth = 0.5;
	private static boolean ListSiftFeatures = false;
	private static Color[] ScaleLevelColors = getColors(SiftColors.class);
	
	private ImagePlus im;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public SIFT_Detection_Demo() {
		if (IjUtils.noCurrentImage()) {
			DialogUtils.askForSampleImage(SampleImage);
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

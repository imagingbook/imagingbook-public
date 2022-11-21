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
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.sift.SiftDescriptor;
import imagingbook.common.sift.SiftDetector;
import imagingbook.common.sift.SiftDetector.Parameters;
import imagingbook.sampleimages.SiftSampleImage;



/**
 * This plugin extracts multi-scale SIFT features from the current 
 * image and displays them as M-shaped markers.
 * List of keypoints (if selected) is sorted by descending magnitude.
 *  
 * @author WB
 * @version 2022/04/01
 * 
 * @see SiftDetector
 * @see SiftDescriptor
 */

public class Extract_Sift_Features implements PlugInFilter {
	
	private static Parameters params = new Parameters();
	private static double FeatureScale = 1.0; // 1.5;
	private static double FeatureStrokewidth = 0.5;
	private static boolean ListSiftFeatures = false;
	
	private static Color[] MarkerColors = {
			new Color(240,0,0), 	// red
			new Color(0,185,15), 	// green
			new Color(0,60,255), 	// blue
			new Color(255,0,200), 	// magenta
			new Color(255,155,0)	// yellow
		};

	ImagePlus imp;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Extract_Sift_Features() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(SiftSampleImage.Castle);
		}
	}
	

	@Override
	public int setup(String arg0, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		params =  new SiftDetector.Parameters();
						
		if (!runDialog()) {
			return;
		}
		
		FloatProcessor fp = ip.convertToFloatProcessor();
		SiftDetector sd = new SiftDetector(fp, params);
		List<SiftDescriptor> features = sd.getSiftFeatures();
		
		if (ListSiftFeatures) {
			int i = 0;
			for (SiftDescriptor sf : features) {
				IJ.log(i + ": " + sf.toString());
				i++;
			}
		}

		ImageProcessor ip2 = ip.duplicate();
		ImagePlus imp2 = new ImagePlus(imp.getShortTitle() + "-SIFT", ip2);
		
		Overlay oly = new Overlay();
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
		for (SiftDescriptor sf : features) {
			Color col = MarkerColors[sf.getScaleLevel() % MarkerColors.length];
			ColoredStroke stroke = new ColoredStroke(FeatureStrokewidth, col);
			ola.addShape(sf.getShape(FeatureScale), stroke);
		}

		imp2.setOverlay(oly);
		imp2.show();
	}
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		DialogUtils.addToDialog(params, gd);
		gd.addCheckbox("List all SIFT features (might be many!)", ListSiftFeatures);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		DialogUtils.getFromDialog(params, gd);
		ListSiftFeatures = gd.getNextBoolean();
		return true;
	}
	
//	private boolean showDialog() {
//		// TODO: use ParameterBundle methods
//		GenericDialog gd = new GenericDialog("Set SIFT parameters");
//		gd.addNumericField("tMag :", params.tMag, 3, 6, "");
//		gd.addNumericField("rMax :", params.rhoMax, 3, 6, "");
//		gd.addNumericField("orientation histogram smoothing :", params.nSmooth, 0, 6, "");
//		gd.addCheckbox("list all SIFT features (might be many!)", ListSiftFeatures);
//		
//		gd.showDialog();
//		if (gd.wasCanceled()) {
//			return false;
//		}
//		
//		params.tMag = gd.getNextNumber();
//		params.rhoMax = gd.getNextNumber();
//		params.nSmooth = (int) gd.getNextNumber();
//		ListSiftFeatures = gd.getNextBoolean();
//		if(gd.invalidNumber()) {
//			IJ.error("Input Error", "Invalid input number");
//			return false;
//		}	
//		return true;
//	}
	
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package CornerDetection;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;

import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.corners.Corner;
import imagingbook.common.corners.GradientCornerDetector;
import imagingbook.common.corners.GradientCornerDetector.Parameters;
import imagingbook.common.corners.HarrisCornerDetector;
import imagingbook.common.corners.MopsCornerDetector;
import imagingbook.common.corners.ShiTomasiCornerDetector;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;

/**
 * This plugin demonstrates the use of gradient corner detectors.
 * It calculates the corner positions and shows them as a vector overlay
 * on top of the source image.
 * 
 * @see HarrisCornerDetector
 * @see MopsCornerDetector
 * @see ShiTomasiCornerDetector
 * 
 * @author WB
 * @version 2022/03/30
 */
public class Find_Corners implements PlugInFilter {
	
	private enum DetectorType {
		Harris, MOPS, ShiThomasi;
	}
	
	private static DetectorType Algorithm = DetectorType.Harris;
	private static int MaxCornerCount = 0;				// number of corners to show (0 = all)
	private static Parameters Params = new Parameters();

	private static double CornerMarkSize = 1.0;
	private static double CornerMarkStrokeWidth = 0.25;
	private static BasicAwtColor CornerMarkColor = BasicAwtColor.Green;
	
	private ImagePlus im;
	
	@Override
    public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_ALL;
    }
    
	@Override
    public void run(ImageProcessor ip) {
    	
		if (!showDialog(Params)) {
			return;
		}
		
		GradientCornerDetector detector = null;	
		switch (Algorithm) {
		case Harris:
			detector = new HarrisCornerDetector(ip, Params);
			break;
		case MOPS:
			detector = new MopsCornerDetector(ip, Params);
			break;
		case ShiThomasi:
			detector = new ShiTomasiCornerDetector(ip, Params);
			break;
		}
		
		List<Corner> corners = detector.getCorners();
		
		// create a vector overlay to mark the resulting corners
		Overlay oly = new Overlay();
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
		ola.setStroke(new ColoredStroke(CornerMarkStrokeWidth, CornerMarkColor.getColor()));
		
		int cnt = 0;
		for (Corner c : corners) {
			ola.addShape(c.getShape(CornerMarkSize));
			//IJ.log(c.toString());
			cnt++;
			if (MaxCornerCount > 0 && cnt >= MaxCornerCount) break;
		}
		
		im.setOverlay(oly);
		
		// (new ImagePlus("Corner Score", detector.getQ())).show();
    }
    
	private boolean showDialog(Parameters params) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		
		gd.addEnumChoice("Detector type", Algorithm);
		addToDialog(params, gd);
		gd.addNumericField("Corners to show (0 = show all)", MaxCornerCount, 0);
		gd.addNumericField("Corners display size", CornerMarkSize, 1);
		gd.addEnumChoice("Corner color", CornerMarkColor);
		
		gd.showDialog();
		if(gd.wasCanceled())
			return false;
		
		Algorithm = gd.getNextEnumChoice(DetectorType.class);
		getFromDialog(params, gd);
		MaxCornerCount = (int) gd.getNextNumber();
		CornerMarkSize = gd.getNextNumber();
		CornerMarkColor = gd.getNextEnumChoice(BasicAwtColor.class);
		
		if(gd.invalidNumber() || !params.validate()) {
			IJ.error("Input Error", "Invalid input");
			return false;
		}	
		return true;
	}

}

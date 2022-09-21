/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch26_MserFeatures;

import java.awt.Color;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * Select a horizontal line!
 * TODO: add JavaDoc
 * @author WB (2021)
 *
 */
public class Show_Threshold_Projection implements PlugInFilter {
	
	static Color colorBelow = new Color(255, 252, 139);   //(200, 200, 200);
	static Color colorAbove = Color.white;
	static Color colorWaterDark = Color.blue;
	static Color colorWaterBright = new Color(200, 200, 255);
	
	static int threshold = 100;
	
	private ImagePlus img;
	
	@Override
	public int setup(String arg0, ImagePlus img) {
		this.img = img;
		return DOES_8G + ROI_REQUIRED; // | NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		
		Roi roi = img.getRoi();
		int y = roi.getBounds().y;
		
		int width = ip.getWidth();
		
		// selected pixel line
		int[] hline = new int[width];	
		for (int u = 0; u < width; u++) {
			hline[u] = ip.get(u, y);
		}
		
		ImageStack stack = new ImageStack(width, 256);
		
		for (int threshold = 0; threshold < 256; threshold++) {
			ColorProcessor cp = new ColorProcessor(width, 256);
			cp.setColor(colorAbove);
			cp.fill();
			
			// fill with water
			//cp.setColor(colorWaterDark);
			for (int v = 0; v <= threshold; v++) {
				cp.setColor((v % 2 == 0) ? colorWaterBright : colorWaterDark);
				for (int u = 0; u < width; u++) {
					cp.drawPixel(u, v);
				}
			}
			
			// fill underneath brightness surface
			for (int u = 0; u < width; u++) {
				for (int v = 0; v < hline[u]; v++) {
					cp.setColor(colorBelow);
					cp.drawPixel(u, v);
				}
			}
			
			// draw horizontal lines
			cp.setColor(Color.black);
			for (int u = 0; u < width; u++) {
				cp.drawPixel(u, hline[u]);
			}
			
			cp.setColor(Color.black);
			// draw vertical lines
			for (int u = 0; u < width - 1; u++) {
				if (hline[u] > hline[u + 1]) {
					for (int v = hline[u]; v >= hline[u + 1]; v--) {
						cp.drawPixel(u, v);
					}
				}
				if (hline[u] < hline[u + 1]) {
					for (int v = hline[u]; v <= hline[u + 1]; v++) {
						cp.drawPixel(u + 1, v);
					}
				}
			}
			
			cp.flipVertical();
			stack.addSlice("threshold="+threshold, cp);
			
			
		}

		new ImagePlus(img.getShortTitle()+ "-threshold-" + threshold, stack).show();

	}
	
}

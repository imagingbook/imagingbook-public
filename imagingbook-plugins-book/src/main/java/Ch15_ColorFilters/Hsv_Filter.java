/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch15_ColorFilters;


import ij.ImagePlus;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.colorspace.HsvColorSpace;
import imagingbook.common.math.Arithmetic;

/**
 * This ImageJ plugin implements a Gaussian blur filter to the periodic
 * hue component in HSV space.
 * @author WB
 *
 */

public class Hsv_Filter implements PlugInFilter {
	
	static double sigma = 3.0;
	static boolean FILTER_ALL_COMPONENTS = false;
	static boolean SHOW_ORIGINAL_HSV = true;

	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		ColorProcessor cp = (ColorProcessor) ip;
		final int w = ip.getWidth();
		final int h = ip.getHeight();
		
		FloatProcessor fH = new FloatProcessor(w, h);
		FloatProcessor fS = new FloatProcessor(w, h);
		FloatProcessor fV = new FloatProcessor(w, h);
		
		if (SHOW_ORIGINAL_HSV) {
			new ImagePlus("H", fH).show();
			new ImagePlus("S", fS).show();
			new ImagePlus("V", fV).show();
		}
		
		// Create Cos/Sin images from the hue angle:
		HsvColorSpace cc = HsvColorSpace.getInstance();
		final int[] RGB = new int[3];
		FloatProcessor fHcos = new FloatProcessor(w, h);
		FloatProcessor fHsin = new FloatProcessor(w, h);
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				cp.getPixel(u, v, RGB);
				float[] hsv = cc.fromRGB (RgbUtils.normalize(RGB)); 	// all HSV components are in [0,1]
				fH.setf(u, v, hsv[0]);
				fS.setf(u, v, hsv[1]);
				fV.setf(u, v, hsv[2]);
				double angle = 2 * Math.PI * hsv[0];
				fHcos.setf(u, v, (float) Math.cos(angle));
				fHsin.setf(u, v, (float) Math.sin(angle));
			}
		}

		new ImagePlus("Hcos", fHcos).show();
		new ImagePlus("Hsin", fHsin).show();
		
//		int[] hist = fH.getHistogram();
//		new HistogramPlot(hist, "Hue histogram").show();
		
		
		// Apply the Gaussian filter to Cos/Sin:
		GaussianBlur gb = new GaussianBlur();
		gb.blurFloat(fHcos, sigma, sigma, 0.002);
		gb.blurFloat(fHsin, sigma, sigma, 0.002);
		
		if (FILTER_ALL_COMPONENTS) {
			gb.blurFloat(fS, sigma, sigma, 0.002);
			gb.blurFloat(fV, sigma, sigma, 0.002);
		}
		
		// Calculate the filtered hue angle from Cos/Sin:
		FloatProcessor fHrec = combineCosSin(fHcos, fHsin);
		new ImagePlus("Hrec", fHrec).show();
		// Reconstruct and show the corresponding RGB image:
		ColorProcessor cp2 = makeRgb(fHrec, fS, fV);
		new ImagePlus("hue blurred cos/sin", cp2).show();
		
		// For testing, also show a direct blur of hue (disregarding periodicity):
		FloatProcessor fHblur = (FloatProcessor) fH.duplicate();
		gb.blurFloat(fHblur, sigma, sigma, 0.002);
		new ImagePlus("Hblur", fHblur).show();
		// Reconstruct and show the corresponding RGB image:
		ColorProcessor cp3 = makeRgb(fHblur, fS, fV);
		new ImagePlus("hue blurred direct", cp3).show();
	}
	
	
	FloatProcessor combineCosSin(FloatProcessor fHcos, FloatProcessor fHsin) {
		final int w = fHcos.getWidth();
		final int h = fHcos.getHeight();
		FloatProcessor fHrec = new FloatProcessor(w, h);
		final double twoPi = 2 * Math.PI;
		
		// rebuild the RGB image from blurred cos/sin
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				double Hcos = fHcos.getf(u, v);
				double Hsin = fHsin.getf(u, v);
				double Hpp = Math.atan2(Hsin, Hcos);    // Hpp in [-pi, .. pi]
				float H = (float) (Arithmetic.mod(Hpp, twoPi) / twoPi);  // H in [0, 1]
				fHrec.setf(u, v, H);
			}
		}
		return fHrec;
	}
	
	/**
	 * Combine 3 FloatProcessors representing a HSV image to a RGB image.
	 * @param fH hue component (values in [0,1])
	 * @param fS saturation component (values in [0,1])
	 * @param fV value component (values in [0,1])
	 * @return A new ColorProcessor.
	 */
	ColorProcessor makeRgb(FloatProcessor fH, FloatProcessor fS, FloatProcessor fV) {
		final int w = fH.getWidth();
		final int h = fH.getHeight();
		ColorProcessor cp = new ColorProcessor(w, h);
		HsvColorSpace cc = HsvColorSpace.getInstance();
		
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				float H = fH.getf(u, v);
				float S = fS.getf(u, v);
				float V = fV.getf(u, v);
				int[] RGB2 = RgbUtils.unnormalize(cc.toRGB(new float[] {H, S, V}));
				cp.putPixel(u, v, RGB2);
			}
		}
		return cp;
	}
}
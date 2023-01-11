/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch15_Color_Filters;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.colorspace.HsvColorSpace;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.math.Arithmetic;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin applies a Gaussian blur filter to selected components in HSV color space. Special attention must
 * be paid to the periodicity of the hue (H) component, which is an angular quantity. For this purpose, the H values are
 * split into cosine and sine parts, which are filtered separately and finally combined again to the filtered hue value.
 * See Sec. 15.1.3 if [1] for details. The components to be filtered can be selected by the user.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/10
 */
public class Gaussian_Filter_HSV implements PlugInFilter, JavaDocHelp {
	// TODO: convert to use imagingbook's GenericFilter methods!

	private static final String[] HsvLabels = {"H", "S", "V"};
	private static double Sigma = 3.0;
	private static boolean FilterH = true;
	private static boolean FilterS = false;
	private static boolean FilterV = false;
	private static boolean ShowOriginalHsv = false;
	private static boolean ShowFilteredHsv = false;
	private static boolean ShowOriginalCosSin = false;
	private static boolean ShowFilteredCosSin = false;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Gaussian_Filter_HSV() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.ColorTest3);
		}
	}

	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		if (!runDialog())
			return;

		ColorProcessor cp = (ColorProcessor) ip;
		final int w = ip.getWidth();
		final int h = ip.getHeight();
		
		FloatProcessor fH = new FloatProcessor(w, h);
		FloatProcessor fS = new FloatProcessor(w, h);
		FloatProcessor fV = new FloatProcessor(w, h);
		
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
				double theta = 2 * Math.PI * hsv[0];
				fHcos.setf(u, v, (float) Math.cos(theta));
				fHsin.setf(u, v, (float) Math.sin(theta));
			}
		}

		if (ShowOriginalHsv) {
			ImageStack stack = new ImageStack();
			stack.addSlice("H", fH.duplicate());
			stack.addSlice("S", fS.duplicate());
			stack.addSlice("V", fV.duplicate());
			new ImagePlus("HSV (original)", stack).show();
		}

		if (ShowOriginalCosSin) {
			ImageStack stack = new ImageStack();
			stack.addSlice("cos(H)", fHcos.duplicate());
			stack.addSlice("sin(H)", fHsin.duplicate());
			new ImagePlus("H cos/sin (original)", stack).show();
		}
		
//		int[] hist = fH.getHistogram();
//		new HistogramPlot(hist, "Hue histogram").show();

		// Apply the Gaussian filter to cos/sin components:
		GaussianBlur gb = new GaussianBlur();

		if (FilterH) {
			gb.blurFloat(fHcos, Sigma, Sigma, 0.002);
			gb.blurFloat(fHsin, Sigma, Sigma, 0.002);
		}

		if (ShowFilteredCosSin) {
			ImageStack stack = new ImageStack();
			stack.addSlice("cos(H)", fHcos.duplicate());
			stack.addSlice("sin(H)", fHsin.duplicate());
			new ImagePlus("H cos/sin (filtered)", stack).show();
		}

		if (FilterS) {
			gb.blurFloat(fS, Sigma, Sigma, 0.002);
		}
		if (FilterV) {
			gb.blurFloat(fV, Sigma, Sigma, 0.002);
		}
		
		// Calculate the filtered hue from cos/sin:
		FloatProcessor fHrec = combineCosSin(fHcos, fHsin);

		if (ShowFilteredHsv) {
			ImageStack stack = new ImageStack();
			stack.addSlice("H", fHrec.duplicate());
			stack.addSlice("S", fS.duplicate());
			stack.addSlice("V", fV.duplicate());
			new ImagePlus("HSV (filtered)", stack).show();
		}

		// new ImagePlus("H (filtered)", fHrec).show();
		// Reconstruct and show the corresponding RGB image:
		ColorProcessor cp2 = makeRgb(fHrec, fS, fV);
		new ImagePlus("RGB (filtered)", cp2).show();
		
		// For testing, also show a direct blur of hue (disregarding periodicity):
		// FloatProcessor fHblur = (FloatProcessor) fH.duplicate();
		// gb.blurFloat(fHblur, Sigma, Sigma, 0.002);
		// new ImagePlus("Hblur", fHblur).show();
		// // Reconstruct and show the corresponding RGB image:
		// ColorProcessor cp3 = makeRgb(fHblur, fS, fV);
		// new ImagePlus("hue blurred direct", cp3).show();
	}
	
	
	private FloatProcessor combineCosSin(FloatProcessor fHcos, FloatProcessor fHsin) {
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
	private ColorProcessor makeRgb(FloatProcessor fH, FloatProcessor fS, FloatProcessor fV) {
		final int w = fH.getWidth();
		final int h = fH.getHeight();
		ColorProcessor cp = new ColorProcessor(w, h);
		HsvColorSpace cc = HsvColorSpace.getInstance();
		
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				float H = fH.getf(u, v);
				float S = fS.getf(u, v);
				float V = fV.getf(u, v);
				int[] RGB2 = RgbUtils.denormalize(cc.toRGB(new float[] {H, S, V}));
				cp.putPixel(u, v, RGB2);
			}
		}
		return cp;
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addNumericField("Gaussian sigma", Sigma, 1);
		gd.setInsets(0, 0, 0);
		gd.addMessage("Components to filter:");
		gd.setInsets(0, 10, 0);
		gd.addCheckboxGroup(1, 3, HsvLabels, new boolean[] {FilterH, FilterS, FilterV});
		gd.addCheckbox("Show original HSV components", ShowOriginalHsv);
		gd.addCheckbox("Show filtered HSV components", ShowFilteredHsv);
		gd.addCheckbox("Show original H cos/sin", ShowOriginalCosSin);
		gd.addCheckbox("Show filtered H cos/sin", ShowFilteredCosSin);

		gd.showDialog();
		if(gd.wasCanceled())
			return false;

		Sigma = gd.getNextNumber();
		FilterH = gd.getNextBoolean();
		FilterS = gd.getNextBoolean();
		FilterV = gd.getNextBoolean();
		ShowOriginalHsv = gd.getNextBoolean();
		ShowFilteredHsv = gd.getNextBoolean();
		ShowOriginalCosSin = gd.getNextBoolean();
		ShowFilteredCosSin = gd.getNextBoolean();
		return true;
	}
}
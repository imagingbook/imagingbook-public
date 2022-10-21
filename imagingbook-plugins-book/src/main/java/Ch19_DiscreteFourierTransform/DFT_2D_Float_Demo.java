/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch19_DiscreteFourierTransform;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.spectral.dft.Dft2d;
import imagingbook.spectral.dft.ScalingMode;


/** 
 * This ImageJ plugin computes the 2-dimensional DFT (magnitude spectrum) from an image
 * of arbitrary size using {@code float} arrays.
 * Optionally, either a direct DFT or a fast FFT implementation is used.
 *
 * @author WB
 * @version 2022/04/01
 * 
 * @see Dft2d.Float
 * @see DFT_2D_Double_Demo
 */
public class DFT_2D_Float_Demo implements PlugInFilter {
	
	private static boolean ShowLogSpectrum = true;
	private static boolean UseFastMode = true;
	private static boolean CenterSpectrum = true;
	private static boolean ReconstructImage = true;
	
	private ImagePlus imp;
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) 
			return;
		
		FloatProcessor fp = ip.convertToFloatProcessor();
		String name = imp.getShortTitle();
		
		float[][] re = fp.getFloatArray();
		float[][] im = new float[fp.getWidth()][fp.getHeight()];
		
		Dft2d.Float dft2 = new Dft2d.Float();
		dft2.setScalingMode(ScalingMode.DEFAULT);
		dft2.setFastMode(UseFastMode);
		
		dft2.forward(re, im);
		
		float[][] mag = dft2.getMagnitude(re, im);
		FloatProcessor ms = new FloatProcessor(mag);
		if (ShowLogSpectrum) {
			ms.add(1.0);
			ms.log();
		}
		ms.resetMinAndMax();
		
		if (CenterSpectrum) {	// strange this only works here (not before) - bug in Blitter?
			swapQuadrants(ms);
		}
		
		String title = (ShowLogSpectrum) ?
				name + "-DFT (log. magnitude)" :
				name + "-DFT (magnitude)";
		new ImagePlus(title, ms).show();
		
		// ----------------------------------------------------
		
		if (ReconstructImage) {
			dft2.inverse(re, im);
			FloatProcessor reIp = new FloatProcessor(re);
			reIp.setMinAndMax(0, 255);
			new ImagePlus(name + "-reconstructed (real part)", reIp).show();
			
			FloatProcessor imIp = new FloatProcessor(im);
			imIp.setMinAndMax(0, 255);
			new ImagePlus(name + "-reconstructed (imag. part)", imIp).show();
		}
	}
	
	// -------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
		gd.addCheckbox("Use FFT", UseFastMode);
		gd.addCheckbox("Show logarithmic spectrum", ShowLogSpectrum);
		gd.addCheckbox("Center spectrum", CenterSpectrum);
		gd.addCheckbox("Reconstruct image", ReconstructImage);
		
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		
		UseFastMode = gd.getNextBoolean();
		ShowLogSpectrum = gd.getNextBoolean();
		CenterSpectrum = gd.getNextBoolean();
		ReconstructImage = gd.getNextBoolean();
		return true;
	}
	
	// -------------------------------------------------------------
	
	/**
	 * Centers a 2D DFT spectrum.
	 * Modifies the given image by moving the origin of the image to its center
	 * (circularly).
	 * TODO: Check for possible bug when applied to a {@link FloatProcessor}!
	 * 
	 * @param ip an {@link ImageProcessor} instance
	 */
	private void swapQuadrants(ImageProcessor ip) {
		// swap quadrants Q1 <-> Q3, Q2 <-> Q4
		// Q2 Q1
		// Q3 Q4
		ImageProcessor t1, t2;
		int w = ip.getWidth();
		int h = ip.getHeight();
		int wc = w / 2;
		int hc = h / 2;

		ip.setRoi(wc, 0, w - wc, hc); // Q1
		t1 = ip.crop();
		ip.setRoi(0, hc, wc, h - hc); // Q3
		t2 = ip.crop();

		ip.insert(t1, 0, hc); // swap Q1 <-> Q3
		ip.insert(t2, wc, 0);

		ip.setRoi(0, 0, wc, hc); // Q2
		t1 = ip.crop();
		ip.setRoi(wc, hc, w - wc, h - hc); // Q4
		t2 = ip.crop();

		ip.insert(t1, wc, hc);
		ip.insert(t2, 0, 0);
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package SpectralTechniques;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.spectral.dft.Dft2d;


/** 
 * This ImageJ plugin computes the 2-dimensional DFT (magnitude spectrum) from an image
 * of arbitrary size using float arrays.
 * Optionally, either a direct DFT or a fast FFT implementation is used.
 *
 * @author WB
 * @version 2022/04/01
 * 
 * @see Dft2d.Float
 */
public class DFT_2D_Float_Demo implements PlugInFilter {
	
	private static boolean ShowLogSpectrum = true;
	private static boolean UseFastMode = true;
	private static boolean ShowCenteredSpectrum = true;
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
		
		float[][] re = fp.getFloatArray();
		float[][] im = new float[fp.getWidth()][fp.getHeight()];
		
		Dft2d.Float dft2 = new Dft2d.Float();
		dft2.useFastMode(UseFastMode);
		
		dft2.forward(re, im);
		
		float[][] mag = dft2.getMagnitude(re, im);
		FloatProcessor ms = new FloatProcessor(mag);
		if (ShowLogSpectrum) {
			ms.add(1.0);
			ms.log();
		}
		ms.resetMinAndMax();
		
		if (ShowCenteredSpectrum) {	// strange this only works here (not before) - big in Blitter?
			Dft2d.swapQuadrants(ms);
		}
		
		String title = (ShowLogSpectrum) ?
				imp.getShortTitle() + "-DFT (log. magnitude)" :
				imp.getShortTitle() + "-DFT (magnitude)";
		new ImagePlus(title, ms).show();
		
		// ----------------------------------------------------
		
		if (ReconstructImage) {
			dft2.inverse(re, im);
			FloatProcessor reIp = new FloatProcessor(re);
			reIp.setMinAndMax(0, 255);
			new ImagePlus(imp.getShortTitle() + "-reconstructed (real part)", reIp).show();
			
			FloatProcessor imIp = new FloatProcessor(im);
			imIp.setMinAndMax(0, 255);
			new ImagePlus(imp.getShortTitle() + "-reconstructed (imag. part)",imIp).show();
		}
	}
	
	// -------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
		gd.addCheckbox("Use FFT", UseFastMode);
		gd.addCheckbox("Show logarithmic spectrum", ShowLogSpectrum);
		gd.addCheckbox("Show centered spectrum", ShowCenteredSpectrum);
		gd.addCheckbox("Reconstruct image", ReconstructImage);
		
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		
		UseFastMode = gd.getNextBoolean();
		ShowLogSpectrum = gd.getNextBoolean();
		ShowCenteredSpectrum = gd.getNextBoolean();
		ReconstructImage = gd.getNextBoolean();
		return true;
	}
}

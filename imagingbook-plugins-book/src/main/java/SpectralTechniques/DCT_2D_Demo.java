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
import imagingbook.spectral.dct.Dct2d;

/** 
 * Calculates and displays the 2-dimensional DCT after converting the input image to a float image.
 * of arbitrary size.
 * Optionally, either a direct DCT or a fast implementation is used.
 * 
 * @author WB
 * @version 2022/04/01
 * 
 * @see Dct2d
 */
public class DCT_2D_Demo implements PlugInFilter {
	
	private static boolean UseFastMode = true;
	private static boolean ShowLogSpectrum = true;
	private static boolean ReconstructImage = false;
	
	private ImagePlus imp;

	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G + DOES_32 + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) 
			return;

		FloatProcessor fp = ip.convertToFloatProcessor();
		float[][] g = fp.getFloatArray();

		// create a new DCT instance:
		Dct2d.Float dct = new Dct2d.Float();
		dct.useFastMode(UseFastMode);
		
		// calculate the forward DCT:
		dct.forward(g);

		FloatProcessor spectrum = new FloatProcessor(g);
		if (ShowLogSpectrum) {
			spectrum.abs();
			spectrum.add(1.0);
			spectrum.log();
		}
		spectrum.resetMinAndMax();
		new ImagePlus(imp.getShortTitle() + "-DCT (log. magnitude)", spectrum).show();
		
		// ----------------------------------------------------
		
		if (ReconstructImage) {
			dct.inverse(g);
			FloatProcessor reconstructed = new FloatProcessor(g);
			reconstructed.setMinAndMax(0, 255);
			new ImagePlus(imp.getShortTitle() + "-reconstructed", reconstructed).show();
		}

	}
	
	// ---------------------------------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
		gd.addCheckbox("Use fast transform", UseFastMode);
		gd.addCheckbox("Show absolute/log spectrum", ShowLogSpectrum);
		gd.addCheckbox("Reconstruct the input image", ReconstructImage);
		
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		
		UseFastMode = gd.getNextBoolean();
		ShowLogSpectrum = gd.getNextBoolean();
		ReconstructImage = gd.getNextBoolean();
		return true;
	}


}

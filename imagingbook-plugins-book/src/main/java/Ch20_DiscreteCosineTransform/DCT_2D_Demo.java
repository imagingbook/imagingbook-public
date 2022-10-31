/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch20_DiscreteCosineTransform;


import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.math.Matrix;
import imagingbook.spectral.dct.Dct2d;
import imagingbook.spectral.dct.Dct2dDirect;
import imagingbook.spectral.dct.Dct2dFast;

/**
 * <p>
 * Calculates and displays the 2-dimensional DCT after converting the input
 * image to a float image. of arbitrary size. Optionally, either a direct DCT or
 * a fast DCT implementation is used, using either {@code float} or
 * {@code double} data. See Ch. 20 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/04/01
 */
public class DCT_2D_Demo implements PlugInFilter {
	
	private static boolean UseFastMode = true;
	private static boolean UseDoublePrecision = false;
	private static boolean ShowLogSpectrum = true;
	private static boolean ReconstructImage = true;
	
	private ImagePlus im;
	private int w, h;
	private FloatProcessor reconstruction = null;

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + DOES_32 + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) 
			return;

		w = ip.getWidth();
		h = ip.getHeight();
		FloatProcessor fp = ip.convertToFloatProcessor();
		FloatProcessor spectrum = (UseDoublePrecision) ? runDouble(fp) : runFloat(fp);
			
		if (ShowLogSpectrum) {
			spectrum.abs();
			spectrum.add(1.0);
			spectrum.log();
		}
		
//		spectrum.resetMinAndMax();		// why doesn't this work?
		spectrum.setMinAndMax(0, 255);
		IJ.log("min = " + spectrum.getMin());
		IJ.log("max = " + spectrum.getMax());
		String name = im.getShortTitle();
		String title = (ShowLogSpectrum) ?
				name + "-DCT (log. spectrum)" : name + "-DCT (spectrum)";
		new ImagePlus(title, spectrum).show();
		
		// ----------------------------------------------------
		
		if (ReconstructImage) {
			reconstruction.setMinAndMax(0, 255);
			new ImagePlus(name + "-reconstructed", reconstruction).show();
		}

	}
	
	private FloatProcessor runFloat(FloatProcessor fp) {
		Dct2d.Float dct = (UseFastMode) ? new Dct2dFast.Float(w, h) : new Dct2dDirect.Float(w, h);
		float[][] g = fp.getFloatArray();

		// calculate the forward DCT:
		dct.forward(g);
		FloatProcessor spectrum = new FloatProcessor(g);
		
		if (ReconstructImage) {
			dct.inverse(g);
			this.reconstruction = new FloatProcessor(g);
		}
		
		return spectrum;
	}
	
	private FloatProcessor runDouble(FloatProcessor fp) {
		Dct2d.Double dct = (UseFastMode) ? new Dct2dFast.Double(w, h) : new Dct2dDirect.Double(w, h);
		double[][] g = Matrix.toDouble(fp.getFloatArray());

		// calculate the forward DCT:
		dct.forward(g);
		FloatProcessor spectrum = new FloatProcessor(Matrix.toFloat(g));

		if (ReconstructImage) {
			dct.inverse(g);
			this.reconstruction = new FloatProcessor(Matrix.toFloat(g));
		}

		return spectrum;
	}
	
	// ---------------------------------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
		gd.addCheckbox("Use fast transform", UseFastMode);
		gd.addCheckbox("Use double precision", UseDoublePrecision);
		gd.addCheckbox("Show absolute/log spectrum", ShowLogSpectrum);
		gd.addCheckbox("Reconstruct the input image", ReconstructImage);
		
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		
		UseFastMode = gd.getNextBoolean();
		UseDoublePrecision = gd.getNextBoolean();
		ShowLogSpectrum = gd.getNextBoolean();
		ReconstructImage = gd.getNextBoolean();
		return true;
	}


}

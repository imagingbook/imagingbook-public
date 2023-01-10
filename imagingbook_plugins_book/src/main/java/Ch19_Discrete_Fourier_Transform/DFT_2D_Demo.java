/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch19_Discrete_Fourier_Transform;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.math.Matrix;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.spectral.dft.Dft2d;
import imagingbook.spectral.dft.Dft2dDirect;
import imagingbook.spectral.dft.Dft2dFast;
import imagingbook.spectral.dft.ScalingMode;

import static imagingbook.common.ij.IjUtils.noCurrentImage;


/**
 * <p>
 * This ImageJ plugin computes the 2-dimensional DFT (magnitude spectrum) from an image of arbitrary size using
 * {@code float} or {@code double} data. Optionally, either a direct DFT or a fast FFT implementation is used. See
 * Chapters 18-19 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/10/23
 */
public class DFT_2D_Demo implements PlugInFilter, JavaDocHelp {
	
	private static boolean ShowLogSpectrum = true;
	private static boolean UseDoublePrecision = false;
	private static boolean UseFastMode = true;
	private static boolean CenterSpectrum = true;
	private static boolean ReconstructImage = true;
	
	private ImagePlus im;
	private int w, h;
	private FloatProcessor reconstructionRe = null;
	private FloatProcessor reconstructionIm = null;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public DFT_2D_Demo() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.IrishManor);
		}
	}
	
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
		FloatProcessor magSpectrum = (UseDoublePrecision) ? runDouble(fp) : runFloat(fp);
		
		if (ShowLogSpectrum) {
			magSpectrum.add(1.0);
			magSpectrum.log();
		}
		
		magSpectrum.resetMinAndMax();
		
		if (CenterSpectrum) {	// strange this only works here (not before) - bug in Blitter?
			swapQuadrants(magSpectrum);
		}
		
		String name = im.getShortTitle();
		String title = (ShowLogSpectrum) ?
				name + "-DFT (log. magnitude)" : name + "-DFT (magnitude)";
		new ImagePlus(title, magSpectrum).show();
		
		// show reconstructed image if available
		if (ReconstructImage) {
//			reIp.resetMinAndMax();
//			imIp.resetMinAndMax();
			new ImagePlus(name + "-reconstructed (real part)", reconstructionRe).show();
			new ImagePlus(name + "-reconstructed (imag. part = zero!)", reconstructionIm).show();
		}
		
	}
	
	private FloatProcessor runFloat(FloatProcessor fp) {
		float[][] re = fp.getFloatArray();
		float[][] im = new float[w][h];
		
		Dft2d.Float dft2 = (UseFastMode) ? 
				new Dft2dFast.Float(w, h, ScalingMode.DEFAULT) :
				new Dft2dDirect.Float(w, h, ScalingMode.DEFAULT);
		
		dft2.forward(re, im);
		
		float[][] mag = dft2.getMagnitude(re, im);
		FloatProcessor ms = new FloatProcessor(mag);
		
		if (ReconstructImage) {
			dft2.inverse(re, im);
			this.reconstructionRe = new FloatProcessor(re);
			this.reconstructionIm = new FloatProcessor(im);
		}
		
		return ms;
	}
	
	private FloatProcessor runDouble(FloatProcessor fp) {
		double[][] re = Matrix.toDouble(fp.getFloatArray());
		double[][] im = new double[w][h];
		
		Dft2d.Double dft2 = (UseFastMode) ? 
				new Dft2dFast.Double(w, h, ScalingMode.DEFAULT) :
				new Dft2dDirect.Double(w, h, ScalingMode.DEFAULT);
		
		dft2.forward(re, im);
		
		double[][] mag = dft2.getMagnitude(re, im);
		FloatProcessor ms = new FloatProcessor(Matrix.toFloat(mag));
		
		if (ReconstructImage) {
			dft2.inverse(re, im);
			this.reconstructionRe = new FloatProcessor(Matrix.toFloat(re));
			this.reconstructionIm = new FloatProcessor(Matrix.toFloat(im));
		}
		
		return ms;
	}
	// -------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addCheckbox("Use fast mode (FFT)", UseFastMode);
		gd.addCheckbox("Use double precision", UseDoublePrecision);
		gd.addCheckbox("Show logarithmic spectrum", ShowLogSpectrum);
		gd.addCheckbox("Show centered spectrum", CenterSpectrum);
		gd.addCheckbox("Reconstruct image (re/im)", ReconstructImage);
		
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return false;
		
		UseFastMode = gd.getNextBoolean();
		UseDoublePrecision = gd.getNextBoolean();
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

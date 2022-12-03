/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package More_;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.noise.hashing.Hash32Shift;
import imagingbook.common.noise.hashing.Hash32ShiftMult;
import imagingbook.common.noise.hashing.Hash32Ward;
import imagingbook.common.noise.hashing.HashFunction;
import imagingbook.common.noise.hashing.HashPermute;
import imagingbook.common.noise.perlin.PerlinNoiseGenerator2d;

/**
 * <p>
 * This ImageJ plugin creates a new noise image using a 2D gradient noise
 * generator [1]. Several parameters can be adjusted. See Ch. 8 of [2] for
 * details. The resulting noise image is of type {@link FloatProcessor}.
 * </p>
 * <p>
 * [1] K. Perlin. Improving noise. In "SIGGRAPH’02: Proceedings of the 29th
 * Annual Conference on Computer Graphics and Interactive Techniques", pp.
 * 681–682, San Antonio, Texas (2002).<br>
 * [2] W. Burger and M. J. Burge. "Principles of Digital Image Processing -
 * Advanced Methods" (Vol. 3). Undergraduate Topics in Computer Science.
 * Springer-Verlag, London (2013).
 * </p>
 * 
 * @author WB
 * @version 2022/11/24
 */

public class Perlin_Noise_2D implements PlugIn {
	
	private enum HasFunctionType {
		HashPermute, Hash32Ward, Hash32Shift, Hash32ShiftMult;
	}
	
	private static int W = 400;				// image width
	private static int H = 300;				// image height
	private static int K = 3;				// number of noise frequencies
	private static double fmin = 0.01; 	// min. frequency (in cycles per pixel unit) 
	private static double fmax = fmin * Math.pow(2, K);
	private static double persistence = 0.5; // persistence
	private static HasFunctionType HashT = HasFunctionType.HashPermute;
	private static int seed = 0;			// hash seed 

	@Override
	public void run(String arg0) {
		
		if (!runDialog()) {
			return;
		}
		
		HashFunction hf = getHashFun();
		
		// create the noise generator:
		PerlinNoiseGenerator2d ng = new PerlinNoiseGenerator2d(fmin, fmax, persistence, hf);
		
		// create a new image and fill with noise:
		ImageProcessor fp = new FloatProcessor(W, H);
		
		for (int v = 0; v < H; v++) {
			for (int u = 0; u < W; u++) {
				double val = ng.getNoiseValue(u, v);
				fp.setf(u, v, (float)val);
			}
		}
		
		// display the new image:
		(new ImagePlus("Perlin Noise Image", fp)).show();
	}
	
	private HashFunction getHashFun() {
		switch(HashT) {
		case Hash32Shift:
			return new Hash32Shift(seed); 
		case Hash32ShiftMult:
			return new Hash32ShiftMult(seed); 
		case Hash32Ward:
			return new Hash32Ward(seed); 
		case HashPermute:
			return new HashPermute(seed); 
		}
		return null;
	}
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		
		gd.addNumericField("Image width", W, 0);
		gd.addNumericField("Image height", H, 0);
		gd.addNumericField("Number of noise frequencies", K, 0);
		gd.addNumericField("Min. frequency (f_min)", fmin, 2);
		gd.addNumericField("Max. frequency (f_max)", fmax, 2);
		gd.addNumericField("Persistence", persistence, 2);
		gd.addNumericField("Seed (0 = random)", seed, 0);
		gd.addEnumChoice("Hash function type", HashT);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		W = (int) gd.getNextNumber();
		H = (int) gd.getNextNumber();
		K = (int) gd.getNextNumber();
		fmin = gd.getNextNumber();
		fmax = gd.getNextNumber();
		persistence = gd.getNextNumber();
		seed = (int) gd.getNextNumber();
		HashT = gd.getNextEnumChoice(HasFunctionType.class);
		return true;
	}
}

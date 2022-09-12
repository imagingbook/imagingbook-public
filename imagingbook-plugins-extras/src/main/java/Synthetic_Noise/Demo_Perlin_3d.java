/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Synthetic_Noise;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.PlugIn;
import ij.process.FloatProcessor;
import imagingbook.common.noise.hashing.HashFun;
import imagingbook.common.noise.perlin.PerlinNoiseGenNd;

/**
 * This ImageJ plugin creates a new noise image using a 3D gradient noise 
 * generator. Several parameters can be adjusted. For details see
 * Ch. 8 of W. Burger and M. J. Burge. "Principles of Digital Image Processing -
 * Advanced Methods" (Vol. 3). Undergraduate Topics in Computer Science.
 * Springer-Verlag, London (2013). https://www.imagingbook.com
 * 
 * TODO: NOT WORKING! Needs to be fixed, results are not plausible!
 * 
 * @author WB
 * @version 2013/05/28
 */

public class Demo_Perlin_3d  implements PlugIn {
	
	static int wx = 300;
	static int wy = 300;
	static int wz = 10;		int stepZ = 5;
	
	int dim = 3;
	
	static int octaves = 6;
	static int seed = 2;

	static double f_min = 0.01; // f_max / (1 << (octaves-1));
	static double f_max = f_min * (1 << (octaves-1));
	
	static double persistence = 0.5;

	String title = this.getClass().getSimpleName() + " Seed=" + seed;
	
	public void run(String arg0) {
		
		// choose hash function:
		HashFun hf = HashFun.create(seed);
//		HashFun hf = new Hash32Shift(seed);
//		HashFun hf = new Hash32ShiftMult(seed);
//		HashFun hf = new Hash32Ward(seed);
//		HashFun hf = new HashPermute(seed); 

		// create the noise generator:
		PerlinNoiseGenNd png = new PerlinNoiseGenNd(dim, f_min, f_max, persistence, hf);
		ImageStack stack = createNoiseImage(wx, wy, wz, png);
		ImagePlus im = new ImagePlus(this.getClass().getSimpleName(), stack);
		im.setDisplayRange(-0.6,0.6);
		im.show();
	}
	
	ImageStack createNoiseImage(int wx, int wy, int wz, PerlinNoiseGenNd ng) {
//		ImagePlus stackImg = 
//			NewImage.createFloatImage(title, wx, wy, wz, NewImage.FILL_BLACK);		
//		ImageStack stack = stackImg.getStack();
		
		ImageStack stack = new ImageStack(wx, wy);
		double[] X = new double[dim];
		IJ.showStatus("creating noise volume ...");
		for (int z = 1; z <= wz; z++) {
			IJ.showProgress(z, wz);
//			ImageProcessor fp = stack.getProcessor(z);
			FloatProcessor fp = new FloatProcessor(wx, wy);
			for (int v = 0; v < wy; v++) {
				for (int u = 0; u < wx; u++) {
					X[0] = u;
					X[1] = v;
					X[2] = z * stepZ;
					fp.setf(u, v, (float)ng.NOISE(X));
				}
			}
			stack.addSlice(fp);
			// fp.setMinAndMax(-0.6,0.6); //fp.resetMinAndMax();
		}
		IJ.showStatus("");
//		stackImg.setDisplayRange(-0.6,0.6);
//		return stackImg;
		return stack;
	}

}

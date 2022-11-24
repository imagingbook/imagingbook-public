/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Synthetic_Noise;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.noise.hashing.Hash32Shift;
import imagingbook.common.noise.hashing.HashFunction;
import imagingbook.common.noise.perlin.PerlinNoiseGeneratorNd;

/**
 * This ImageJ plugin creates a new noise image using a N-dim. gradient noise 
 * generator. Several parameters can be adjusted. For details see
 * Ch. 8 of W. Burger and M. J. Burge. "Principles of Digital Image Processing -
 * Advanced Methods" (Vol. 3). Undergraduate Topics in Computer Science.
 * Springer-Verlag, London (2013). https://imagingbook.com
 * 
 * TODO: NOT WORKING! Needs to be fixed, results are not plausible!
 * 
 * @author WB
 * @version 2013/05/28
 */

public class Demo_Perlin_Nd  implements PlugIn {
	
	static int w = 300;
	static int h = 300;
	static int octaves = 3;
	static int seed = 2;

	static double f_min = 0.01; // f_max / (1 << (octaves-1));
	static double f_max = f_min * (1 << (octaves-1));
	
	static double persistence = 0.5;

	String title = this.getClass().getSimpleName() + " Seed=" + seed;
	
	@Override
	public void run(String arg0) {
		int dim = 2;	// number of dimensions
		
		// choose hash function:
//		HashFun hf = HashFun.create(seed);
		HashFunction hf = new Hash32Shift(seed);
//		HashFun hf = new Hash32ShiftMult(seed);
//		HashFun hf = new Hash32Ward(seed);
//		HashFun hf = new HashPermute(seed); 

		// create the noise generator:
		PerlinNoiseGeneratorNd png = new PerlinNoiseGeneratorNd(dim, f_min, f_max, persistence, hf);
		createNoiseImage(w, h, png).show();
	}
	
	ImagePlus createNoiseImage(int w, int h, PerlinNoiseGeneratorNd ng) {
		ImageProcessor fp = new FloatProcessor(w,h);
		double[] X = new double[2];
		for (int v=0; v<h; v++){
			for (int u=0; u<w; u++){
				X[0] = u;
				X[1] = v;
				fp.setf(u, v, (float) ng.getNoiseValue(X));
			}
		}
		fp.setMinAndMax(-0.6,0.6); 	//fp.resetMinAndMax();
		ImagePlus nimg = new ImagePlus(title,fp);
		return nimg;
	}

}

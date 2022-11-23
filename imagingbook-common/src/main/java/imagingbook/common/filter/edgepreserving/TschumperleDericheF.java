/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.filter.edgepreserving;

import imagingbook.common.filter.linear.Kernel2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.util.ParameterBundle;


/**
 * Common interface for Tschumperle-Deriche filter implementations.
 * 
 * @author WB
 */
public interface TschumperleDericheF {
	
	/**
	 * Parameter bundle for Tschumperle-Deriche filters (implementations of {@link TschumperleDericheF}).
	 */
	public static class Parameters implements ParameterBundle<TschumperleDericheF> {
		
		@DialogUtils.DialogLabel("Number of iterations")
		public int iterations = 20;	
		
		@DialogUtils.DialogLabel("dt (Time step)")
		public double dt = 20.0;  		
		
		@DialogUtils.DialogLabel("Gradient smoothing (sigmaD)")
		public double sigmaD  = 0.5;
		
		@DialogUtils.DialogLabel("Structure tensor smoothing (sigmaM)")
		public double sigmaM  = 0.5;
		
		@DialogUtils.DialogLabel("Diffusion limiter along minimal variations (a0)")	//small value = strong smoothing
		public float a0 = 0.25f;  	
		
		@DialogUtils.DialogLabel("Diffusion limiter along maximal variations (a1)")	//small value = strong smoothing
		public float a1 = 0.90f;
		
		/** The alpha value applied in the first pass. */
		@DialogUtils.DialogHide
		public float alpha0 = 0.5f;
	}
	

	abstract class Constants {
		private static final float C1 = (float) (2 - Math.sqrt(2.0)) / 4;
		private static final float C2 = (float) (Math.sqrt(2.0) - 1) / 2;

		private static final float[][] Hdx = // gradient kernel X
			{{-C1, 0, C1},
			 {-C2, 0, C2},
			 {-C1, 0, C1}};
		
		private static final float[][] Hdy = // gradient kernel Y
			{{-C1, -C2, -C1},
			 {  0,   0,   0},
			 { C1,  C2,  C1}};
		
		protected static final Kernel2D kernelDx = new Kernel2D(Hdx, 1, 1, false);
		protected static final Kernel2D kernelDy = new Kernel2D(Hdy, 1, 1, false);
	}
	

	

}

/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.filter.edgepreserving;

import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.math.VectorNorm.NormType;
import imagingbook.common.util.ParameterBundle;

/**
 * Common interface for all Bilateral filter implementations.
 * 
 * @author WB
 *
 */
public interface BilateralF {
	
	/**
	 * Parameter bundle for bilateral filters (implementations of {@link BilateralF}).
	 */
	public static class Parameters implements ParameterBundle<BilateralF> {

		@DialogLabel("Sigma (domain)")
		public double sigmaD = 2; 		

		@DialogLabel("Sigma (range)")
		public double sigmaR = 50;
		
		@DialogLabel("Color distance norm")
		public NormType colorNormType = NormType.L2;

	}

}

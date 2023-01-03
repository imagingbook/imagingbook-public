/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral.dft;

/**
 * Abstract super class common to all 2D DFT implementations.
 * Holds information about data size and scaling mode.
 * 
 * @author WB
 *
 */
abstract class Dft2dImp implements Dft2d {
	
	final int M, N;		// width (M) and height (N) of the data array
	final ScalingMode sm;
	
	protected Dft2dImp(int width, int height, ScalingMode sm) {
		this.M = width;
		this.N = height;
		this.sm = sm;
	}
	
	// ----------------------------------------------------------
	
	@Override
	public int getWidth() {
		return this.M;
	}
	
	@Override
	public int getHeight() {
		return this.N;
	}
	
	@Override
	public ScalingMode getScalingMode() {
		return this.sm;
	}

}

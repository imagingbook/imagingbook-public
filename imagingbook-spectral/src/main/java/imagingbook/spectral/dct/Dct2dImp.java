/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral.dct;


/**
 * Abstract super class common to all 2D DCT implementations.
 * Holds information about data size.
 * 
 * @author WB
 *
 */
abstract class Dct2dImp implements Dct2d {
	
	final int M, N;		// width (M) and height (N) of the data array
	
	protected Dct2dImp(int width, int height) {
		this.M = width;
		this.N = height;
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

}

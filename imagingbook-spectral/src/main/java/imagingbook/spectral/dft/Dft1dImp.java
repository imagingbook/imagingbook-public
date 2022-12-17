/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral.dft;

/**
 * Abstract super class for all 1D DFT implementations.
 * Holds information about data size and scaling mode.
 * 
 * @author WB
 *
 */
abstract class Dft1dImp implements Dft1d {
	
	final int M;		// data size
	final ScalingMode sm;
	
	protected Dft1dImp(int size, ScalingMode sm) {
		this.M = size;
		this.sm = sm;
	}
	
	// --------------------------------------------------------------------
	
	@Override
	public int getSize() {
		return this.M;
	}
	
	@Override
	public ScalingMode getScalingMode() {
		return this.sm;
	}

}

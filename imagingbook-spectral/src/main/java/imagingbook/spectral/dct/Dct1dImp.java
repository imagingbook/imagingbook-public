/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral.dct;

abstract class Dct1dImp implements Dct1d {
	
	final int M;		// data size
	
	protected Dct1dImp(int size) {
		this.M = size;
	}
	
	// --------------------------------------------------------------------
	
	@Override
	public int getSize() {
		return this.M;
	}

}

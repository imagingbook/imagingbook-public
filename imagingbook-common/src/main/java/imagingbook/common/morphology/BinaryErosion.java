/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.morphology;

import ij.process.ByteProcessor;

public class BinaryErosion extends BinaryMorphologyFilter {
	
	public BinaryErosion() {
		super();
	}
	
	public BinaryErosion(byte[][] H) {
		super(H);
	}

	@Override
	public void applyTo(ByteProcessor ip) {
		// dilates the background
		ip.invert();
		new BinaryDilation(reflect(H)).applyTo(ip); //dilate(ip, reflect(H));
		ip.invert();
	}

}

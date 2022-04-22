/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.morphology;

import ij.IJ;
import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class BinaryDilation extends BinaryMorphologyFilter {
	
	public BinaryDilation() {
		super();
	}
			
	public BinaryDilation(byte[][] H) {
		super(H);
	}
	
	public void applyTo(ByteProcessor ip) {
		//assume that the hot spot of H is at its center (ic,jc)
		int xc = (H[0].length - 1) / 2;
		int yc = (H.length - 1) / 2;
		int N = H.length * H[0].length;
		
		ImageProcessor tmp = ip.createProcessor(ip.getWidth(), ip.getHeight());
		
		int k = 0;
		IJ.showProgress(k, N);
		for (int j = 0; j < H.length; j++) {
			for (int i = 0; i < H[j].length; i++) {
				if (H[j][i] > 0) { // this element is set
					// copy image into position (u-ch,v-cv)
					tmp.copyBits(ip, i - xc, j - yc, Blitter.MAX);
				}
				IJ.showProgress(k++, N);
			}
		}
		ip.copyBits(tmp, 0, 0, Blitter.COPY);
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.morphology;

import ij.IJ;
import ij.process.Blitter;
import ij.process.ByteProcessor;

/**
 * <p>
 * Implements a binary morphological dilation operation.
 * See Sec. 7.2.3 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/09/18
 */
public class BinaryDilation extends BinaryMorphologyFilter {
	
	/**
	 * Constructor, creates a {@link BinaryDilation} with a 3x3 box structuring element by default.
	 */
	public BinaryDilation() {
		super();
	}
	
	/**
	 * Constructor, creates a {@link BinaryDilation} with the specified structuring element.
	 * @param H the structuring element
	 */
	public BinaryDilation(byte[][] H) {
		super(H);
	}
	
	@Override
	public void applyTo(ByteProcessor bp) {
		//assume that the hot spot of H is at its center (ic,jc)
		int xc = (H[0].length - 1) / 2;
		int yc = (H.length - 1) / 2;
		int N = H.length * H[0].length;
		
		ByteProcessor tmp = (ByteProcessor) bp.createProcessor(bp.getWidth(), bp.getHeight());
		
		int k = 0;
		IJ.showProgress(k, N);
		for (int j = 0; j < H.length; j++) {
			for (int i = 0; i < H[j].length; i++) {
				if (H[j][i] > 0) { // this element is set
					// copy image into position (u-ch,v-cv)
					tmp.copyBits(bp, i - xc, j - yc, Blitter.MAX);
				}
				IJ.showProgress(k++, N);
			}
		}
		bp.copyBits(tmp, 0, 0, Blitter.COPY);
	}

}

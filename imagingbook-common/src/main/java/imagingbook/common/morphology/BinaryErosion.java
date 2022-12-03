/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.morphology;

import static imagingbook.common.morphology.StructuringElements.reflect;

import ij.process.ByteProcessor;

/**
 * <p>
 * Implements a binary morphological erosion operation. See Sec. 7.2.4 of [1]
 * for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/09/18
 */
public class BinaryErosion extends BinaryMorphologyFilter {
	
	/**
	 * Constructor, creates a {@link BinaryErosion} with a 3x3 box structuring
	 * element by default.
	 */
	public BinaryErosion() {
		super();
	}
	
	/**
	 * Constructor, creates a {@link BinaryErosion} with the specified structuring
	 * element.
	 * 
	 * @param H the structuring element
	 */
	public BinaryErosion(byte[][] H) {
		super(H);
	}

	@Override
	public void applyTo(ByteProcessor bp) {
		// dilates the background
		bp.invert();
		new BinaryDilation(reflect(H)).applyTo(bp); //dilate(ip, reflect(H));
		bp.invert();
	}

}

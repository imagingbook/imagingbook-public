/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.morphology;

import ij.process.ByteProcessor;

/**
 * Common interface for all binary morphological operators.
 * @author WB
 */
public interface BinaryMorphologyOperator {

	/**
	 * Applies this morphological operator to the specified image (destructively, that is, the image is modified).
	 *
	 * @param bp the image the operator is applied to
	 */
	public void applyTo(ByteProcessor bp);

}

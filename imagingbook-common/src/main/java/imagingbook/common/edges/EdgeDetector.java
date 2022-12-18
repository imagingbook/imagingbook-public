/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.edges;

import ij.process.FloatProcessor;

/**
 * This is the common interface for all color edge detectors.
 * 
 * @author WB
 * @version 2022/09/04
 */
public interface EdgeDetector {

	/**
	 * Returns the calculated edge magnitude for each pixel as a {@link FloatProcessor}.
	 *
	 * @return the edge magnitude map
	 */
	public FloatProcessor getEdgeMagnitude();

	/**
	 * Returns the calculated edge orientation for each pixel as a {@link FloatProcessor}.
	 *
	 * @return the edge orientation map
	 */
	public FloatProcessor getEdgeOrientation();
	
}

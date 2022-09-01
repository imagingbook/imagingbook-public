/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.color.edge;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;

/**
 * This is the common super-class for all color edge detectors.
 * @author W. Burger
 * @version 2013/05/30
 */
public abstract class ColorEdgeDetector {
	
	public abstract FloatProcessor getEdgeMagnitude();
	public abstract FloatProcessor getEdgeOrientation();
	
	FloatProcessor getRgbFloatChannel(ColorProcessor cp, int c) {	// c = 0,1,2
		int w = cp.getWidth();
		int h = cp.getHeight();
		ByteProcessor bp = new ByteProcessor(w, h, cp.getChannel(c + 1)); // numbered from 1,...,3!
		return bp.convertToFloatProcessor();
	}
}

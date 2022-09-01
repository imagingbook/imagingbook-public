/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Edge_Preserving_Smoothing;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.edgepreserving.PeronaMalikFilterScalar;
import imagingbook.common.filter.edgepreserving.PeronaMalikFilterVector;
import imagingbook.common.filter.edgepreserving.PeronaMalikF.ColorMode;
import imagingbook.common.filter.edgepreserving.PeronaMalikF.Parameters;
import imagingbook.common.filter.generic.GenericFilter;

/**
 * Minimal plugin to demonstrate the use of the PeronaMalikFilter class.
 * This plugin works for all types of images and stacks.
 * 
 * @author W. Burger
 * @version 2022/03/30
 * 
 * @see PeronaMalikFilterScalar
 * @see PeronaMalikFilterVector
 * @see GenericFilter
 */
public class Perona_Malik_Demo implements PlugInFilter {

	public int setup(String arg0, ImagePlus imp) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		
		// create a parameter object, modify settings if needed:
		Parameters params = new Parameters();
		params.iterations = 20;
		params.alpha = 0.15;
		params.kappa = 20.0;
		params.colorMode = ColorMode.ColorGradient;

		// create the actual filter:
		GenericFilter filter = (ip instanceof ColorProcessor) ?
			new PeronaMalikFilterVector(params) :
			new PeronaMalikFilterScalar(params);
		
		// apply the filter:
		filter.applyTo(ip);
	}
	
}




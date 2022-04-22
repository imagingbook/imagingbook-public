/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package MorphologicalFilters;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.morphology.BinaryDilation;
import imagingbook.common.morphology.BinaryMorphologyFilter;

/**
 * This plugin implements a binary dilation using a disk-shaped
 * structuring element with a fixed radius.
 * 
 * @author WB
 * @version 2022/01/24
 */
public class Dilate_Disk_Demo implements PlugInFilter {

	double radius = 6.5;
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		byte[][] H = BinaryMorphologyFilter.makeDiskKernel(radius);
		BinaryMorphologyFilter filter = new BinaryDilation(H);
		
		filter.applyTo((ByteProcessor) ip);
	}	
}

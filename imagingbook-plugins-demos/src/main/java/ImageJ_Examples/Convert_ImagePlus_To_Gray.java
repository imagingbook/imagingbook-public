/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Examples;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;

/**
 * This ImageJ plugin shows how to change the type of the current
 * image 'in place', i.e., without copying the image.
 * 
 * @author WB
 * @version 2020/12/17
 */
public class Convert_ImagePlus_To_Gray implements PlugIn {

	@Override
	public void run(String arg) {
		ImagePlus im = IJ.getImage();	// im can be of any type
		
		ImageConverter iConv = new ImageConverter(im);
		iConv.convertToGray8();
		
		ByteProcessor ip = (ByteProcessor) im.getProcessor();	// bp is of type ByteProcessor
		ip.sharpen(); // process the grayscale image ...
	}
}

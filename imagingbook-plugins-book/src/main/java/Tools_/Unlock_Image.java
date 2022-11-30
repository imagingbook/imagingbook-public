/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Tools_;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

/**
 * ImageJ plugin, unlocks the currently active image.
 * 
 * @author WB
 *
 */
public class Unlock_Image implements PlugIn {

	public void run(String arg0) {
		ImagePlus img = IJ.getImage();
		if (img != null && img.isLocked()) {
			img.unlock();
		}
	}

}

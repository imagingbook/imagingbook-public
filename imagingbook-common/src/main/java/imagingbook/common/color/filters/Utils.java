/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color.filters;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.ij.GuiTools;

public abstract class Utils {
	
	public static void showMask(CircularMask mask, String title) {
		ByteProcessor bp = new FloatProcessor(mask.getMask()).convertToByteProcessor(false);
		bp.threshold(0);
		ImagePlus im = new ImagePlus(title, bp);
		im.show();
		GuiTools.zoomExact(im, 32);
	}

}

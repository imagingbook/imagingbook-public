/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch12_RansacAndHough;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import imagingbook.common.ij.IjUtils;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * Hough line detection demo. Opens a local sample image and then runs
 * plugin {@link Hough_Lines_Detect}.
 * 
 * @author WB
 *
 */
public class Hough_Lines_Demo implements PlugIn {
	
	private static ImageResource resource = GeneralSampleImage.NoisyLines;

	@Override
	public void run(String arg) {
		
		ImagePlus im = resource.getImage();
		im.show();
		
		IjUtils.run(new Hough_Lines_Detect());
		im.close();
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ransac;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import imagingbook.common.ij.IjUtils;
import imagingbook.sampleimages.GeneralSampleImage;


/**
 * RANSAC ellipse detection demo. Opens a local sample image and then runs
 * plugin {@link Ransac_Ellipse_Detect}.
 * 
 * @author WB
 *
 */
public class Ransac_Ellipse_Demo implements PlugIn {
	
	private static GeneralSampleImage resource = GeneralSampleImage.NoisyEllipses;

	@Override
	public void run(String arg) {
		
		ImagePlus im = resource.getImage();
		im.show();
		
		IjUtils.run(new Ransac_Ellipse_Detect());
		im.close();
	}

}

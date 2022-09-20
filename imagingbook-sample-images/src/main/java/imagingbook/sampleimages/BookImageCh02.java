/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.sampleimages;

import imagingbook.core.resource.ImageResource;
import imagingbook.testutils.ImageResourceSelfTest;



/**
 * <p>
 * Enumeration defining a set of {@link ImageResource} objects for some general test images.
 * Usage example:
 * </p>
 * <pre>
 * import ij.process.ImageProcessor;
 * import imagingbook.core.resource.ImageResource;
 * import imagingbook.sampleimages.GeneralTestImage;
 * 
 * ImageResource ir = GeneralTestImage.Clown;
 * ImageProcessor ip = ir.getImage().getProcessor();
 * // process ip ...
 * </pre>
 * @see ImageResource
 *
 */
public enum BookImageCh02 implements ImageResource {

	Fig_02_01,
	Fig_02_02,
	
	Fig_02_06a,
	Fig_02_06b,
	Fig_02_06c,
	
	Fig_02_07a,
	Fig_02_07b,
	Fig_02_07c,
	
	Fig_02_08a,
	Fig_02_08b,
	Fig_02_08c,
	
	Fig_02_09a,
	Fig_02_09b,
	Fig_02_09c,
	
	Fig_02_10,
	
	Fig_02_11a,
	Fig_02_11b,
	
	Fig_02_12a,
	Fig_02_12c,
	Fig_02_12d,
	Fig_02_12e,
	
	Fig_02_13,
	;
	
	/**
//	 * This definition causes this ImageResource to be tested automatically.
//	 * The class must be public and static, the name is arbitrary.
//	 */
	public static class SelfTest extends ImageResourceSelfTest {}

}

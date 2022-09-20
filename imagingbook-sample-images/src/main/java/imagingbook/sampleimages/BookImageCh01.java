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
public enum BookImageCh01 implements ImageResource {
	Fig_01_01a,
	Fig_01_01b,
	Fig_01_01c,
	Fig_01_01d,
	Fig_01_01e,
	Fig_01_01f,
	Fig_01_01g,
	Fig_01_01h,
	Fig_01_01i,
	Fig_01_01j,
	Fig_01_01k,
	Fig_01_01l,
	
	Fig_01_05a,
	
	Fig_01_09a,
	Fig_01_09b,
	Fig_01_09c,
	Fig_01_09d,
	
	Fig_01_10,
	;
	
	/**
//	 * This definition causes this ImageResource to be tested automatically.
//	 * The class must be public and static, the name is arbitrary.
//	 */
	public static class SelfTest extends ImageResourceSelfTest {}

//	public static void main(String[] args) {
//		System.out.println(BookImageCh01.Fig_01_01a.autoName());
//		System.out.println(BookImageCh01.Fig_01_01a.getFileName());
//		System.out.println(BookImageCh01.Fig_01_01a.getRelativeDirectory());
//		System.out.println(BookImageCh01.Fig_01_01a.getRelativePath());
//		System.out.println(BookImageCh01.Fig_01_01a.getURL());
//		
//		System.out.println(BookImageCh01.Fig0101b.getURL());
//		System.out.println(GeneralSampleImage.Kepler.getURL());
//	}
}

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
public enum GeneralSampleImage implements ImageResource {
	
		/** Original: https://imagej.nih.gov/ij/images/blobs.gif */
		blobs,
		
		/** Original: https://imagej.nih.gov/ij/images/boats.gif */
		boats,
		
		/** A simple binary shape image with 0/255 values. */
		cat,
		
		/** Original: https://imagej.nih.gov/ij/images/clown.png */
		clown,
		
		/** Original: https://imagej.nih.gov/ij/images/Dot_Blot.jpg */
		DotBlot,
		
		/** Original gray scan from Kepler manuscript. */
		Kepler,
		
		/** A small grayscale image. */
		MonasterySmall,
	
		NoisyLines,
		NoisyCircles,
		NoisyEllipses,
		
		/** A fairly large binary image with 0/1 values. */
		RhinoBigCrop,
	;
	
	/**
//	 * This definition causes this ImageResource to be tested automatically.
//	 * The class must be public and static, the name is arbitrary.
//	 */
//	public static class SelfTest extends ImageResourceSelfTest {}

}

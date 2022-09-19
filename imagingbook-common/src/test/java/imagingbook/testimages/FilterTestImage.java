/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.testimages;

import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testutils.ImageResourceSelfTest;

/**
 * Enumeration defining a set of {@link ImageResource} objects for testing 
 * linear and nonlinear filters.
 * 
 * @see ImageResource
 * @see GeneralSampleImage
 */
public enum FilterTestImage implements ImageResource {
	MonasterySmall,
	Clown,
	
	MonasterySmallFilter3x3,
	ClownFilter3x3,
	
	MonasterySmallGauss3,
	ClownGauss3,
	
	ClownExampleFilter3x3Scalar,
	ClownExampleFilter3x3Vector,
	
	ClownMedianScalar3,
	ClownMedianVector3L1,
	ClownMedianVectorsharpen3L1,
	
	MonasterySmallBilateralNonsep,
	MonasterySmallBilateralSep,
	ClownBilateralNonsepScalar,
	ClownBilateralNonsepVector,
	ClownBilateralSepScalar,
	ClownBilateralSepVector,
	
	MonasterySmallKuwahara,
	ClownKuwaharaScalar,
	ClownKuwaharaVector,
	
	MonasterySmallNagaoMatsuyama,
	ClownNagaoMatsuyamaScalar,
	ClownNagaoMatsuyamaVector,
	
	MonasterySmallPeronaMalikG1,
	MonasterySmallPeronaMalikG2,
	MonasterySmallPeronaMalikG3,
	MonasterySmallPeronaMalikG4,
	
	ClownPeronaMalikSeparateChannels,
	ClownPeronaMalikBrightnessGradient,
	ClownPeronaMalikColorGradient,
	
	MonasterySmallTschumperleIter5,
	ClownSmallTschumperleIter5,
	;
	
	// ---------------------------------------------------
	
	private final String filename;
	
	FilterTestImage() {
		filename = null;
	}
	
	FilterTestImage(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String getFileName() {
		if (filename != null)
			return filename;
		else 
			return this.autoName();
	}
	
	// ---------------------------------------------------
		
	/**
	 * This definition causes this ImageResource to be tested automatically.
	 * The class must be public and static, the name is arbitrary.
	 */
	public static class SelfTest extends ImageResourceSelfTest {
	}
	
}

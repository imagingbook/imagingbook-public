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
	MonasterySmall("monastery-small.png"),
	Clown("clown.png"),
	
	BoatsFilter3x3("boats-filter3x3.png"),
	MonasterySmallFilter3x3("monastery-small-filter3x3.png"),
	ClownFilter3x3("clown-filter3x3.png"),
	
	MonasterySmallGauss3("monastery-small-gauss3.png"),
	ClownGauss3("clown-gauss3.png"),
	
	ClownExampleFilter3x3Scalar("clown-examplefilter3x3scalar.png"),
	ClownExampleFilter3x3Vector("clown-examplefilter3x3vector.png"),
	
	ClownMedianScalar3("clown-median-scalar-3.png"),
	ClownMedianVector3L1("clown-median-vector-3-L1.png"),
	ClownMedianVectorsharpen3L1("clown-median-vectorsharpen-3-L1.png"),
	
	MonasterySmallBilateralNonsep("monastery-small-bilateral-nonsep.png"),
	MonasterySmallBilateralSep("monastery-small-bilateral-sep.png"),
	ClownBilateralNonsepScalar("clown-bilateral-nonsep-scalar.png"),
	ClownBilateralNonsepVector("clown-bilateral-nonsep-vector.png"),
	ClownBilateralSepScalar("clown-bilateral-sep-scalar.png"),
	ClownBilateralSepVector("clown-bilateral-sep-vector.png"),
	
	MonasterySmallKuwahara("monastery-small-kuwahara.png"),
	ClownKuwaharaScalar("clown-kuwahara-scalar.png"),
	ClownKuwaharaVector("clown-kuwahara-vector.png"),
	
	MonasterySmallNagaoMatsuyama("monastery-nagaomatsuyama.png"),
	ClownNagaoMatsuyamaScalar("clown-nagaomatsuyama-scalar.png"),
	ClownNagaoMatsuyamaVector("clown-nagaomatsuyama-vector.png"),
	
	MonasterySmallPeronaMalikG1("monastery-small-peronamalik-g1.png"),
	MonasterySmallPeronaMalikG2("monastery-small-peronamalik-g2.png"),
	MonasterySmallPeronaMalikG3("monastery-small-peronamalik-g3.png"),
	MonasterySmallPeronaMalikG4("monastery-small-peronamalik-g4.png"),
	ClownPeronaMalikSeparateChannels("clown-peronamalik-separatechannels.png"),
	ClownPeronaMalikBrightnessGradient("clown-peronamalik-brightnessgradient.png"),
	ClownPeronaMalikColorGradient("clown-peronamalik-colorgradient.png"),
	
	MonasterySmallTschumperleIter5("monastery-small-tschumperle-iter5.png"),
	ClownSmallTschumperleIter5("clown-tschumperle-iter5.png"),
	;
	
	// ---------------------------------------------------
	
	private final String filename;
	
	FilterTestImage(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String getFileName() {
		return filename;
	}
	
	// ---------------------------------------------------
		
	/**
	 * This definition causes this ImageResource to be tested automatically.
	 * The class must be public and static, the name is arbitrary.
	 */
	public static class SelfTest extends ImageResourceSelfTest {
	}
	
}

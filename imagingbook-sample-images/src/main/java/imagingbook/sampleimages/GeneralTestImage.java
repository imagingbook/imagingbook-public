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
public enum GeneralTestImage implements ImageResource {	// TODO: make ImageResource abstract class?
		/** Original: https://imagej.nih.gov/ij/images/blobs.gif */
		Blobs("blobs.png"),
		/** Original: https://imagej.nih.gov/ij/images/boats.gif */
		Boats("boats.png"),
		/** Original: https://imagej.nih.gov/ij/images/clown.png */
		Clown("clown.png"),
		BoatsFilter3x3("boats-filter3x3.png"),
		/** Original: https://imagej.nih.gov/ij/images/Dot_Blot.jpg */
		DotBlot("Dot_Blot.png"),
		CatSkeleton("cat-skeleton.png"),
		Cat("cat.png"),
		ClownFilter3x3("clown-filter3x3.png"),
		ClownGauss3("clown-gauss3.png"),
		ClownMedianScalar3("clown-median-scalar-3.png"),
		ClownMedianVector3L1("clown-median-vector-3-L1.png"),
		ClownMedianVectorsharpen3L1("clown-median-vectorsharpen-3-L1.png"),
		MonasterySmallFilter3x3("monastery-small-filter3x3.png"),
		MonasterySmallGauss3("monastery-small-gauss3.png"),
		MonasterySmall("monastery-small.png"),
		RhinoBigCrop("rhino-big-crop.png"),
		SegmentationMed("segmentation-med.png"),
		SegmentationSmall("segmentation-small.png"),
		SimpleTestGridImg("simple-test-grid-img.png"),
		SimpleTestGridImg2("simple-test-grid-img2.png");

	private final String filename;
	
	GeneralTestImage(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String getFileName() {
		return filename;
	}
}

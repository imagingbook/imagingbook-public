/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.sampleimages;

import imagingbook.core.resource.ImageResource;

/**
 * <p>
 * Enumeration defining a set of {@link ImageResource} objects for selected sample images.
 * Usage example:
 * </p>
 * <pre>
 * import ij.process.ImageProcessor;
 * import imagingbook.core.resource.ImageResource;
 * import imagingbook.sampleimages.GeneralSampleImage;
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
		Blobs,
		
		/** Original: https://imagej.nih.gov/ij/images/boats.gif */
		Boats,
		
		/** A simple binary shape image with 0/255 values. */
		Cat,
		
		/** Original: https://imagej.nih.gov/ij/images/clown.png */
		Clown,
		
		/** Original: https://imagej.nih.gov/ij/images/Dot_Blot.jpg */
		DotBlot,
		
		/** Grayscale image used for SIFT demos. */
		IrishManor,
		
		/** Original gray scan from Kepler manuscript. */
		Kepler,
		
		/** Binary image with a single connected component. */
		MapleLeafSmall,
		
		/** Small grayscale image. */
		MonasterySmall,
	
		/** Binary image with straight lines embedded in noise. */
		NoisyLines,
		/** Binary image with circles embedded in noise. */
		NoisyCircles,
		/** Binary image with ellipses embedded in noise. */
		NoisyEllipses,

		
		/** TIFF image with attached ROI selection, used for trigonometric Fourier descriptors. */
		HouseRoi_tif,

		/** Fairly large binary image with 0/1 values. */
		RhinoBigCrop,

		/** A small binary image with 0/255 values (white background = 255). */
		RhinoSmall,

		/** A small binary image with 0/255 values and inverted LUT (white background = 0). */
		RhinoSmallInv,
		
		/** Binary image with star-shaped regions, used for SIFT demos. */
		Stars,
		
		/** Binary image with various tools, used for connected components segmentation. */
		ToolsSmall,
		
		/** Color image, used for piecewise image warping. */
		WartburgSmall_jpg,
		
		/** Color image used for projective rectification. */
		PostalPackageSmall_jpg,
		
		/** Color image used for various non-linear transformations. */
		Flower_jpg,
		
		/** Grayscale image used for MSER feature detection. */
		MortarSmall,
		
		/** Grayscale image used for SIFT feature detection. */
		Castle,
		/** Grayscale stereo image used for SIFT feature detection and matching. */
		RamsesSmall,
		/** A stack of 2 small grayscale stereo frames used for SIFT feature detection and matching. */
		RamsesSmallStack_tif,

		/** A stack of 2 small grayscale images used for linear blending. */
		ShipBeachSmallStack_tif,

		/** A stack of small grayscale images used for histogram specification/matching. */
		CityscapeSmallStack_tif
		;

}

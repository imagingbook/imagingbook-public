/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.image;

import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.mappings.Mapping2D;
import imagingbook.common.image.access.ImageAccessor;
import imagingbook.common.image.interpolation.InterpolationMethod;

/**
 * This class defines methods to perform arbitrary geometric transformations
 * on images. The geometric transformation (mapping) must be specified at
 * construction.
 * The specified geometric mapping is supposed to be INVERTED, i.e. transforming
 * <strong>target to source</strong> coordinates!
 * For reading pixel values (from the source image) the out-of-bounds strategy and
 * pixel interpolation method can be specified.
 * All methods work for both scalar-valued and color images.
 * 
 * @author WB
 * @version 2021/08/27
 * @version 2022/09/16 revised
 * 
 * @see ImageAccessor
 * @see Mapping2D
 */
public class ImageMapper {
	
	/** Default out-of-bounds strategy (see {@link OutOfBoundsStrategy}). */
	public static OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NearestBorder;	
	
	/** Default pixel interpolation method (see {@link InterpolationMethod}). */
	public static InterpolationMethod DefaultInterpolationMethod = InterpolationMethod.Bicubic;	
	
	private final OutOfBoundsStrategy obs;
	private final InterpolationMethod ipm;
	private final Mapping2D mapping;
	
	/**
	 * Constructor - creates a new {@link ImageMapper} with the specified geometric
	 * mapping. The default pixel interpolation method (see
	 * {@link #DefaultInterpolationMethod}) and out-of-bounds strategy (see
	 * {@link #DefaultOutOfBoundsStrategy}) are used.
	 * 
	 * @param targetToSourceMapping the geometric (target to source) transformation
	 */
	public ImageMapper(Mapping2D targetToSourceMapping) {
		this(targetToSourceMapping, DefaultOutOfBoundsStrategy, DefaultInterpolationMethod);
	}

	/**
	 * Constructor - creates a new {@link ImageMapper} with the specified geometric mapping,
	 * out-of-bounds strategy and pixel interpolation method.
	 * 
	 * @param targetToSourceMapping the geometric (target to source) transformation
	 * @param obs the out-of-bounds strategy (affects source image only)
	 * @param ipm the pixel interpolation method
	 */
	public ImageMapper(Mapping2D targetToSourceMapping, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		this.mapping = targetToSourceMapping;
		this.obs = obs;
		this.ipm = ipm;
	}
	
	// ---------------------------------------------------------------------
	
	/**
	 * Destructively transforms the passed image using this geometric
	 * mapping and the specified pixel interpolation method.
	 * 
	 * @param ip target image to which this mapping is applied
	 */
	public void map(ImageProcessor ip) {
		// make a temporary copy of the image:
		ImageProcessor source = ip.duplicate();
		ImageProcessor target = ip;
		map(source, target);
		source = null;
	}
	
	// ---------------------------------------------------------------------
	
	/**
	 * Transforms the source image to the target image using this geometric
	 * mapping and the specified pixel interpolation method. Note that source
	 * and target must be different images!
	 * The geometric mapping is supposed to be INVERTED, i.e. transforming
	 * target to source image coordinates!
	 * 
	 * @param source input image (not modified)
	 * @param target output image (modified)
	 */
	public void map(ImageProcessor source, ImageProcessor target) {
		if (target == source) {
			throw new IllegalArgumentException("source and target image must not be the same!");
		}
		ImageAccessor sourceAcc = ImageAccessor.create(source, obs, ipm);
		ImageAccessor targetAcc = ImageAccessor.create(target);
		map(sourceAcc, targetAcc);
	}

	// ---------------------------------------------------------------------

	/**
	 * Transforms the source image to the target image using this geometric
	 * mapping and the specified pixel interpolation method.
	 * The two images are passed as instances of {@link ImageAccessor}.
	 * Note that source and target must be different images!
	 * The geometric mapping is supposed to be INVERTED, i.e. transforming
	 * target to source image coordinates.
	 * Access to source pixels is controlled by the out-of-bounds strategy and pixel
	 * interpolation method settings of the source {@link ImageAccessor}
	 * (the corresponding settings of this {@link ImageAccessor} are ignored).
	 * 
	 * @param sourceAcc {@link ImageAccessor} for the source image
	 * @param targetAcc {@link ImageAccessor} for the target image
	 */
	public void map(ImageAccessor sourceAcc, ImageAccessor targetAcc) {
		if (targetAcc.getProcessor() == sourceAcc.getProcessor()) {
			throw new IllegalArgumentException("Source and target image must not be the same!");
		}
		ImageProcessor target = targetAcc.getProcessor();
		final int w = target.getWidth();
		final int h = target.getHeight();
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				Pnt2d sourcePt = this.mapping.applyTo(PntInt.from(u, v));
				float[] val = sourceAcc.getPix(sourcePt.getX(), sourcePt.getY());
				targetAcc.setPix(u, v, val);
			}
		}
	}

}

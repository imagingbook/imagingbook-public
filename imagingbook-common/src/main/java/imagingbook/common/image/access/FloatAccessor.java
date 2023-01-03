/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.image.access;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.interpolation.InterpolationMethod;

/**
 * Image accessor for scalar images with 32-bit (float) values.
 * 
 * @author WB
 * @version 2022/09/22
 */
public class FloatAccessor extends ScalarAccessor {
	private final float[] pixels;

	/**
	 * Constructor. See also the factory method
	 * {@link ScalarAccessor#create(ImageProcessor, OutOfBoundsStrategy, InterpolationMethod)}.
	 *
	 * @param ip an instance of {@link FloatProcessor}
	 * @param obs the out-of-bounds strategy to be used (use {@code null} for default settings)
	 * @param ipm the interpolation method to be used (use {@code null} for default settings)
	 */
	public FloatAccessor(FloatProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		super(ip, obs, ipm);
		this.pixels = (float[]) ip.getPixels();
	}
	
	public static FloatAccessor create(FloatProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		return new FloatAccessor(ip, obs, ipm);
	}

	@Override
	public float getVal(int u, int v) {
		int i = indexer.getIndex(u, v);
		if (i < 0)
			return this.defaultValue;
		else
			return pixels[i];
	}

	@Override
	public void setVal(int u, int v, float val) {
		if (u >= 0 && u < width && v >= 0 && v < height) {
			pixels[width * v + u] = val;
		}
	}
}
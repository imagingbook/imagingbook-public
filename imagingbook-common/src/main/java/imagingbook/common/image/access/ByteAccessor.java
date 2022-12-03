/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.image.access;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.interpolation.InterpolationMethod;

/**
 * Image accessor for scalar images with 8-bit (byte) values.
 * 
 * @author WB
 * @version 2022/09/22
 */
public class ByteAccessor extends ScalarAccessor {
	private final byte[] pixels;

	/**
	 * Constructor. See also the factory method
	 * {@link ScalarAccessor#create(ImageProcessor, OutOfBoundsStrategy, InterpolationMethod)}.
	 * 
	 * @param ip  an instance of {@link ByteProcessor}
	 * @param obs the out-of-bounds strategy to be used (use {@code null} for
	 *            default settings)
	 * @param ipm the interpolation method to be used (use {@code null} for default
	 *            settings)
	 */
	public ByteAccessor(ByteProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		super(ip, obs, ipm);
		this.pixels = (byte[]) this.ip.getPixels();
	}

	@Override
	public float getVal(int u, int v) {
		final int i = indexer.getIndex(u, v);
		if (i < 0)
			return this.defaultValue;
		else {
			return (0xff & pixels[i]);
		}
	}

	@Override
	public void setVal(int u, int v, float val) {
		int i = indexer.getIndex(u, v);
		if (i >= 0) {
			int vali = Math.round(val);
			if (vali < 0) vali = 0;
			if (vali > 255) vali = 255;
			pixels[i] = (byte) (0xFF & vali);
		}
	}
}
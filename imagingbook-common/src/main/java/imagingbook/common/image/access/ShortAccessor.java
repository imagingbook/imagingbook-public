/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.image.access;

import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.common.interpolation.InterpolationMethod;

/**
 * Image accessor for scalar images with 16-bit (short) values.
 */
public class ShortAccessor extends ScalarAccessor {
	private final short[] pixels;

	/**
	 * Constructor. Creates a new image accessor of type {@link ShortAccessor}. See also the
	 * generic factory method
	 * {@link ScalarAccessor#create(ImageProcessor, OutOfBoundsStrategy, InterpolationMethod)}.
	 * 
	 * @param ip  an instance of {@link ShortProcessor}
	 * @param obs the out-of-bounds strategy to be used (use {@code null} for
	 *            default settings)
	 * @param ipm the interpolation method to be used (use {@code null} for default
	 *            settings)
	 */
	public ShortAccessor(ShortProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		super(ip, obs, ipm);
		this.pixels = (short[]) this.ip.getPixels();
	}

	@Override
	public float getVal(int u, int v) {
		int i = indexer.getIndex(u, v);
		if (i < 0)
			return this.defaultValue;
		else
			return (0xFFFF & pixels[i]);
	}

	@Override
	public void setVal(int u, int v, float val) {
		int vali = Math.round(val);
		if (vali < 0)
			vali = 0;
		if (vali > 65535)
			vali = 65535;
		if (u >= 0 && u < width && v >= 0 && v < height) {
			pixels[width * v + u] = (short) (0xFFFF & vali);
		}
	}
}
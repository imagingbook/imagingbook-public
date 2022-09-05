/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.image;

import java.awt.color.ColorSpace;

import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import imagingbook.common.color.colorspace.sRgb65ColorSpace;

/**
 * This class defines a "color stack" as a subtype of {@link PixelPack}
 * with exactly 3 components (slices), representing a color image
 * in a specific color space (default is {@link sRgb65ColorSpace}).
 * It allows simple conversion to other color 
 * spaces (see {@link #convertFromSrgbTo(ColorSpace)}).
 * All conversions are 'destructive', i.e., the affected color stack
 * is modified.
 * Pixel values are typically in [0,1], depending on the associated
 * color space.
 * A {@link ColorStack} may be created from an existing
 * {@link ColorProcessor} whose pixels are assumed to be in sRGB
 * color space (see {@link #ColorStack(ColorProcessor)}).
 * To be converted back to a {@link ColorProcessor}, the {@link ColorStack}
 * must be in sRGB color space (see {@link #convertToSrgb()}).
 */
public class ColorStack extends PixelPack {
	
	// the current color space of this color stack
	private ColorSpace colorspace = null;
	
	// ---------------------------------------------------------------------
	
	/**
	 * Constructor.
	 * @param cp a color image assumed to be in sRGB
	 */
	public ColorStack(ColorProcessor cp) {
		super(cp, 1.0/255, null);
		setColorSpace(sRgb65ColorSpace.getInstance());
	}
	
	// -----------------------------------------------------------------
	
	/**
	 * Returns the 3 color components as an array of {@link FloatProcessor}.
	 * @return a {@link FloatProcessor} array
	 */
	public FloatProcessor[] getProcessors() {
		FloatProcessor[] processors = new FloatProcessor[3];
		for (int k = 0; k < 3; k++) {
			processors[k] = new FloatProcessor(width, height, data[k]);
		}
		return processors;
	}
	
	/**
	 * Returns the current color space instance of this {@link ColorStack}.
	 * @return the current color space
	 */
	public ColorSpace getColorspace() {
		return this.colorspace;
	}
	
	private void setColorSpace(ColorSpace colorspace) {
		this.colorspace = colorspace;
	}
	
	// -----------------------------------------------------------------
	
	/**
	 * Converts the pixel values of this {@link ColorStack} to the specified
	 * color space. The color stack must be in sRGB space.
	 * Exceptions are thrown if the color stack is not in sRGB color space
	 * or the target color space is sRGB.
	 * 
	 * @param targetColorspace the new color space
	 */
	public void convertFromSrgbTo(final ColorSpace targetColorspace) {
		if (!(colorspace instanceof sRgb65ColorSpace)) {
			throw new IllegalStateException("color stack must be in sRGB");
		}
		
		if (targetColorspace instanceof sRgb65ColorSpace) {
			throw new IllegalArgumentException("cannot convert color stack from sRGB to sRGB");
		}
		
		final float[] srgb = new float[3];
		
		for (int i = 0; i < length; i++) {
			getPix(i, srgb);
			clipTo01(srgb);
			float[] c = targetColorspace.fromRGB(srgb);
			setPix(i, c);
		}
		
		setColorSpace(targetColorspace);
	}
	
	// -----------------------------------------------------------------

	/**
	 * Converts this {@link ColorStack} to sRGB space.
	 * An exceptions is thrown if the color stack is in sRGB color space
	 * already.
	 */
	public void convertToSrgb() {
		if (colorspace instanceof sRgb65ColorSpace) {
			throw new IllegalStateException("color stack is in sRGB already");
		}
		
		final float[] c = new float[3];
		
		for (int i = 0; i < length; i++) {
			getPix(i, c);
			float[] srgb = colorspace.toRGB(c);
			setPix(i, srgb);
		}

		setColorSpace(sRgb65ColorSpace.getInstance());
	}
	
	// ---------------------------------------------------------------
	
	@Override
	public ColorProcessor toColorProcessor() {
		if (!(colorspace instanceof sRgb65ColorSpace)) {
			throw new IllegalStateException("color stack must be in sRGB to convert to ColorProcessor");
		}
		return super.toColorProcessor(255);
	}
	
	@Override	// arbitrary scaling is inhibited
	public ColorProcessor toColorProcessor(double scale) {
		throw new UnsupportedOperationException();
	}
	
	// ------------------------------------------------------------------
	
	private float clipTo01(float val) {
		if (val < 0) return 0f;
		if (val > 1) return 1f;
		return val;
	}
	
	private void clipTo01(float[] vals) {
		for (int i = 0; i < vals.length; i++) {
			vals[i] = clipTo01(vals[i]);
		}
	}
    
}

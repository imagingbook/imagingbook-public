/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.quantize;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import imagingbook.common.color.RgbUtils;

import java.awt.image.IndexColorModel;

import static imagingbook.common.math.Arithmetic.sqr;

public interface ColorQuantizer {
	
	public static final int MAX_RGB = 255;

	/**
	 * Retrieves the color map produced by this color quantizer. The returned array is in the format float[idx][rgb],
	 * where rgb = 0 (red), 1 (green), 2 (blue) and  0 &le; idx &lt; nColors.
	 *
	 * @return The table of reference (quantization) colors.
	 */
	public float[][] getColorMap();
	
	// ---------------------------------------------------------------
	
	/**
	 * Returns the number of quantized colors.
	 * 
	 * @return the number of quantized colors
	 */
	public default int getColorCount() {
		return getColorMap().length;
	}


	/**
	 * Performs color quantization on the given full-color RGB image and creates an indexed color image.
	 *
	 * @param cp The original full-color RGB image.
	 * @return The quantized (indexed color) image.
	 */
	public default ByteProcessor quantize(ColorProcessor cp) {
		float[][] colormap = this.getColorMap();
		if (colormap.length > 256) 
			throw new RuntimeException("cannot index to more than 256 colors");
		
		int w = cp.getWidth();
		int h = cp.getHeight();
		
		int[]  rgbPixels = (int[]) cp.getPixels();
		byte[] idxPixels = new byte[rgbPixels.length];

		for (int i = 0; i < rgbPixels.length; i++) {
			idxPixels[i] = (byte) findColorIndex(rgbPixels[i], colormap);
		}

		IndexColorModel idxCm = makeIndexColorModel(colormap);
		return new ByteProcessor(w, h, idxPixels, idxCm);
	}
	
	static IndexColorModel makeIndexColorModel(float[][] colormap) {
		final int nColors = colormap.length;
		byte[] rMap = new byte[nColors];
		byte[] gMap = new byte[nColors];
		byte[] bMap = new byte[nColors];
		for (int i = 0; i < nColors; i++) {
			rMap[i] = floatToUnsignedByte(colormap[i][0]);
			gMap[i] = floatToUnsignedByte(colormap[i][1]);
			bMap[i] = floatToUnsignedByte(colormap[i][2]);
		}
		return new IndexColorModel(8, nColors, rMap, gMap, bMap);
	}
	
	static byte floatToUnsignedByte(float x) {
		int xi = Math.round(x);
		if (xi < 0) {
			xi = 0;
		}
		else if (xi > MAX_RGB) {
			xi = MAX_RGB;
		}
		return (byte) (0xFF & xi);
	}

	/**
	 * Performs color quantization on the given array of ARGB-encoded color values and returns a new sequence of
	 * quantized colors.
	 *
	 * @param origPixels The original ARGB-encoded color values.
	 * @return The quantized ARGB-encoded color values.
	 */
	public default int[] quantize(int[] origPixels) {
		int[] qantPixels = new int[origPixels.length];
		for (int i = 0; i < origPixels.length; i++) {
			qantPixels[i] = quantize(origPixels[i]);
		}
		return qantPixels;
	}

	/**
	 * Performs color quantization on the given single ARGB-encoded color value and returns the associated quantized
	 * color.
	 *
	 * @param rgb The original ARGB-encoded color value.
	 * @return The quantized ARGB-encoded color value.
	 */
	public default int quantize(int rgb) {
		final float[][] colormap = getColorMap();
		int idx = findColorIndex(rgb, colormap);
		int red = (int) (colormap[idx][0] + 0.5f);
		int grn = (int) (colormap[idx][1] + 0.5f);
		int blu = (int) (colormap[idx][2] + 0.5f);
		return RgbUtils.encodeRgbToInt(red, grn, blu);
	}

	/**
	 * Finds the color table index of the color that is "closest" to the supplied RGB color (minimum Euclidean distance
	 * in color space). This method may be overridden by inheriting classes, for example, to use quick indexing in the
	 * octree method.
	 *
	 * @param p Original color, encoded as an ARGB integer.
	 * @param colormap a color map (float)
	 * @return The associated color table index.
	 */
	default int findColorIndex(int p, float[][] colormap) {
		int[] rgb = RgbUtils.intToRgb(p);
		int n = colormap.length;
		float minD2 = Float.POSITIVE_INFINITY;
		int minIdx = -1;
		for (int i = 0; i < n; i++) {
			final float red = colormap[i][0];
			final float grn = colormap[i][1];
			final float blu = colormap[i][2];
			float d2 = sqr(red - rgb[0]) + sqr(grn - rgb[1]) + sqr(blu - rgb[2]);	// dist^2
			if (d2 < minD2) {
				minD2 = d2;
				minIdx = i;
			}
		}
		return minIdx;
	}

}

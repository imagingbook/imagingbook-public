/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.colorspace;

import java.awt.color.ColorSpace;

import static imagingbook.common.math.Arithmetic.max;
import static imagingbook.common.math.Arithmetic.min;


/**
 * <p>
 * Implementation of HLS (hue/lightness/saturation) color space. See Sec. 13.2.3 of [1] for additional details. All
 * component values are assumed to be in [0,1]. Note that sRGB components passed to method {@link #fromRGB(float[])}
 * and returned by method {@link #toRGB(float[])} are NOT converted to linear RGB.
 * This is a singleton class with no public constructors, use {@link #getInstance()} to obtain the single instance.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/09/04
 */
@SuppressWarnings("serial")
public class HlsColorSpace extends ColorSpace {
	
	private static final HlsColorSpace instance = new HlsColorSpace();
	
	public static HlsColorSpace getInstance() {
		return instance;
	}
	
	private HlsColorSpace() {
		super(ColorSpace.TYPE_HLS, 3);
	}

	@Override
	public float[] fromRGB(float[] rgb) {
		final float r = rgb[0], g = rgb[1], b = rgb[2]; // r, g, b assumed to be in [0,1]
		final float cHi = max(r, g, b);		// max. component value
		final float cLo = min(r, g, b);		// min. component value
		final float cRng = cHi - cLo;			// component range
		
		// Calculate lightness L
		float L = (cHi + cLo) / 2;
		
		// Calculate saturation S		
		float S = 0;
		if (0 < L && L < 1) {
			float d = (L <= 0.5f) ? L : (1 - L);
			S = 0.5f * cRng / d;
		}
		
		// Calculate hue H (same as in HSV)
		float H = 0;	
		if (cHi > 0 && cRng > 0) {        // a color pixel
			float dr = (cHi - r) / cRng;
			float dg = (cHi - g) / cRng;
			float db = (cHi - b) / cRng;
			float h;
			if (r == cHi)                      // R is largest component
				h = db - dg;
			else if (g == cHi)                 // G is largest component
				h = dr - db + 2.0f;
			else                               // B is largest component
				h = dg - dr + 4.0f;
			if (h < 0)
			  h = h + 6;
			H = h / 6;
		}
		return new float[] {H, L, S};
	}
	
	@Override
	public float[] toRGB(float[] hls) {
		final float H = hls[0], L = hls[1], S = hls[2]; // H,L,S in [0,1]
		float r = 0, g = 0, b = 0; 	
		if (L <= 0)				// black
			r = g = b = 0;
		else if (L >= 1)		// white
			r = g = b = 1;
		else {
			float hh = (6 * H) % 6;
			int   c1 = (int) hh;
			float c2 = hh - c1;
			float d = (L <= 0.5f) ? (S * L) : (S * (1 - L));
			float w = L + d;
			float x = L - d;
			float y = w - (w - x) * c2;
			float z = x + (w - x) * c2;
			switch (c1) {
				case 0: r = w; g = z; b = x; break;
				case 1: r = y; g = w; b = x; break;
				case 2: r = x; g = w; b = z; break;
				case 3: r = x; g = y; b = w; break;
				case 4: r = z; g = x; b = w; break;
				case 5: r = w; g = x; b = y; break;
				default: // this should not happen
			}			
		}	// r, g, b in [0,1]
		return new float[] {r, g, b};
	}

	@Override
	public float[] toCIEXYZ(float[] colorvalue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float[] fromCIEXYZ(float[] colorvalue) {
		throw new UnsupportedOperationException();
	}
	
	private static final String[] ComponentNames = {"H", "L", "S"};
	
	@Override
	public String getName (int idx) {
		return ComponentNames[idx];
	}

}

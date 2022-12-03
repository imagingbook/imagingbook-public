/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.color.colorspace;

import static imagingbook.common.math.Arithmetic.max;
import static imagingbook.common.math.Arithmetic.min;

import java.awt.color.ColorSpace;


/**
 * <p>
 * Implementation of HSV (hue/saturation/value) color space.
 * See Sec. 13.2.3 of [1] for additional details.
 * All component values are assumed to be in [0,1].
 * This is a singleton class with no public constructors,
 * use {@link #getInstance()} to obtain the single instance.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/09/04
 */
@SuppressWarnings("serial")
public class HsvColorSpace extends ColorSpace {
	
	private static final HsvColorSpace instance = new HsvColorSpace();
	
	public static HsvColorSpace getInstance() {
		return instance;
	}
	
	private HsvColorSpace() {
		super(ColorSpace.TYPE_HSV, 3);
	}

	@Override
	public float[] fromRGB(float[] rgb) {
		final float R = rgb[0], G = rgb[1], B = rgb[2]; // R,G,B in [0,1]		
		final float cHi = max(R, G, B);					// max. component value
		final float cLo = min(R, G, B);					// min. component value
		final float cRng = cHi - cLo;				    // component range
		
		// compute value V
		float V = cHi;
		float H = 0;
		float S = 0;
		
		// compute saturation S
		if (cHi > 0)
			S = cRng / cHi;

		// compute hue H
		if (cRng > 0) {	// hue is defined only for color pixels
			float rr = (cHi - R) / cRng;
			float gg = (cHi - G) / cRng;
			float bb = (cHi - B) / cRng;
			float hh;
			if (R == cHi)                   // R is largest component value
				hh = bb - gg;
			else if (G == cHi)              // G is largest component value
				hh = rr - bb + 2;
			else                            // B is largest component value
				hh = gg - rr + 4;
			if (hh < 0)
				hh = hh + 6;
			H = hh / 6;
		}
		return new float[] {H, S, V};
	}
	
	@Override
	public float[] toRGB(float[] hsv) {
		final float H = hsv[0], S = hsv[1], V = hsv[2];  // H,S,V in [0,1]
		final float hh = (6 * H) % 6;                 
		final int   c1 = (int) hh;                     
		final float c2 = hh - c1;
		final float x = (1 - S) * V;
		final float y = (1 - (S * c2)) * V;
		final float z = (1 - (S * (1 - c2))) * V;
		float r = 0, g = 0, b = 0;
		switch (c1) {
			case 0: r = V; g = z; b = x; break;
			case 1: r = y; g = V; b = x; break;
			case 2: r = x; g = V; b = z; break;
			case 3: r = x; g = y; b = V; break;
			case 4: r = z; g = x; b = V; break;
			case 5: r = V; g = x; b = y; break;
			default: // this should not happen
		}
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
	
	private static final String[] ComponentNames = {"H", "S", "V"};
	
	@Override
	public String getName (int idx) {
		return ComponentNames[idx];
	}

}

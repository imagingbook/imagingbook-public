/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.color.colorspace;

import static imagingbook.common.math.Arithmetic.max;
import static imagingbook.common.math.Arithmetic.min;

/**
 * Methods for converting between RGB and HLS color spaces.
 * This is a singleton class with no public constructors,
 * use {@link #getInstance()} to obtain the single instance.
 * <p>
 * Note: This class is obsolete and has been replaced by {@link HsvColorSpace}.
 * </p>
 * 
 * @author WB
 * @version 2022/09/03
*/
@Deprecated
public class HsvConverter {
	
	private static final HsvConverter instance = new HsvConverter();
	
	public static HsvConverter getInstance() {
		return instance;
	}
	
	private HsvConverter() {	
	}

	public float[] fromRGB (int[] RGB) {
		final int R = RGB[0], G = RGB[1], B = RGB[2]; // R,G,B in [0,255]		
		final int cHi = max(R, G, B);					// max. component value
		final int cLo = min(R, G, B);					// min. component value
		final int cRng = cHi - cLo;				    // component range
		//final float cMax = 255.0f;
		
		// compute value V
		float V = cHi / 255.0f;
		float H = 0, S = 0;
		
		// compute saturation S
		if (cHi > 0)
			S = (float) cRng / cHi;

		// compute hue H
		if (cRng > 0) {	// hue is defined only for color pixels
			float rr = (float)(cHi - R) / cRng;
			float gg = (float)(cHi - G) / cRng;
			float bb = (float)(cHi - B) / cRng;
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
	
	public int[] toRGB (float[] HSV) {
		final float H = HSV[0], S = HSV[1], V = HSV[2];  // H,S,V in [0,1]
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
		int R = min(Math.round(r * 255), 255);
		int G = min(Math.round(g * 255), 255);
		int B = min(Math.round(b * 255), 255);
		return new int[] {R, G, B};
	}

}

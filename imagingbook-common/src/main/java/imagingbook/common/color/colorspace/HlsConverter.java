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
 * @author W. Burger
 * @version 2022/09/01
*/
public class HlsConverter {
	
	public HlsConverter() {	
	}
	
	public float[] fromRGB (int[] RGB) {
		final int R = RGB[0], G = RGB[1], B = RGB[2]; // R, G, B assumed to be in [0,255]
		final int cHi = max(R, G, B);		// max. component value
		final int cLo = min(R, G, B);		// min. component value
		final int cRng = cHi - cLo;			// component range
		
		// Calculate lightness L
		float L = ((cHi + cLo) / 255f) / 2;
		
		// Calculate saturation S		
		float S = 0;
		if (0 < L && L < 1) {
			float d = (L <= 0.5f) ? L : (1 - L);
			S = 0.5f * (cRng / 255f) / d;
		}
		
		// Calculate hue H (same as in HSV)
		float H = 0;	
		if (cHi > 0 && cRng > 0) {        // a color pixel
			float r = (float)(cHi - R) / cRng;
			float g = (float)(cHi - G) / cRng;
			float b = (float)(cHi - B) / cRng;
			float h;
			if (R == cHi)                      // R is largest component
				h = b - g;
			else if (G == cHi)                 // G is largest component
				h = r - b + 2.0f;
			else                               // B is largest component
				h = g - r + 4.0f;
			if (h < 0)
			  h = h + 6;
			H = h / 6;
		}
		return new float[] {H, L, S};
	}
	
	public int[] toRGB (float[] HLS) {
		final float H = HLS[0], L = HLS[1], S = HLS[2]; // H,L,S in [0,1]
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
		int R = min(Math.round(r * 255), 255);
		int G = min(Math.round(g * 255), 255);
		int B = min(Math.round(b * 255), 255);
		return new int[] {R, G, B};
	}

}

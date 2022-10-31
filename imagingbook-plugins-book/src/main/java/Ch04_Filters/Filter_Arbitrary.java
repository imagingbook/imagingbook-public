/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch04_Filters;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * <p>
 * ImageJ plugin for a linear filter with a kernel of arbitrary size and integer
 * coefficients (naive implementation).
 * The filter kernel is a 2D {@code int} array of size
 * (2K+1)×(2L+1) with the origin at the center. The summation variable
 * {@code sum} is also defined as an integer (int), which is scaled by a constant factor
 * {@code s} and rounded to the new pixel value {@code q}. 
 * Note that the border pixels are not modified. 
 * See Sec. 4.2 (Prog. 4.3) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>DDigital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 */
public class Filter_Arbitrary implements PlugInFilter {

    public int setup(String arg, ImagePlus im) {
        return DOES_8G;
    }

    public void run(ImageProcessor ip) {
        int M = ip.getWidth();
        int N = ip.getHeight();
        
        // arbitrary filter kernel of size (2K + 1) x (2L + 1):
        int[][] H = {
            {0,0,1,1,1,0,0},
            {0,1,1,1,1,1,0},
            {1,1,1,1,1,1,1},
            {0,1,1,1,1,1,0},
            {0,0,1,1,1,0,0}};
        
        double s = 1.0 / 23;	// normalization factor (sum of filter coefficients is 23)
        
        // H[L][K] is the center element of H:
        int K = (H[0].length - 1) / 2;	// K = 3 (x-center of kernel H)
        int L = (H.length - 1) / 2;		// L = 2 (y-center of kernel H)
        
        ImageProcessor copy = ip.duplicate();

        for (int u = K; u <= M - K - 1; u++) {
        	for (int v = L; v <= N - L - 1; v++) {
                // compute filter result for position (u, v):
                int sum = 0;
                for (int i = -K; i <= K; i++) {
                	for (int j = -L; j <= L; j++) {
						int p = copy.getPixel(u + i, v + j);
						int c = H[j + L][i + K];
                        sum = sum + c * p;
                    }
                }
                int q = (int) Math.round(s * sum);
                // clamp result:
                if (q < 0)   q = 0;
                if (q > 255) q = 255;
                ip.putPixel(u, v, q);
            }
        }
    }

}

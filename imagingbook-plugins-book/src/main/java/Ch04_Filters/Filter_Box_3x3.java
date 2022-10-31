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
 * ImageJ plugin for a linear 3×3 "box" filter. 
 * First a duplicate (copy) of the original image (orig) is created,
 * which is used as the source image in the subsequent filter computation. 
 * The resulting value q is placed in the original image. Notice that the
 * border pixels remain unchanged because they are not reached by the iteration
 * over (u,v).
 * See Sec. 4.2 (Prog. 4.1) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 */
public class Filter_Box_3x3 implements PlugInFilter {

    public int setup(String arg, ImagePlus imp) {
        return DOES_8G;
    }

    public void run(ImageProcessor orig) {
        final int M = orig.getWidth();
        final int N = orig.getHeight();
        final double W = 9;
        ImageProcessor copy = orig.duplicate();

		for (int u = 1; u <= M - 2; u++) {
			for (int v = 1; v <= N - 2; v++) {
                //compute filter result for position (u, v):
                int sum = 0;
                for (int i = -1; i <= 1; i++) {
                	for (int j = -1; j <= 1; j++) {
						int p = copy.getPixel(u + i, v + j);
                        sum = sum + p;
                    }
                }
                int q = (int) (sum / W);
				orig.putPixel(u, v, q);
            }
        }
    }

}

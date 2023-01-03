/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch04_Filters;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin for a simple 3×3 linear smoothing filter. The filter kernel is defined as a 2D array of type
 * {@code double}. The coordinate origin of the filter is assumed at the center of the matrix (i.e., at array position
 * [1, 1]), which is accounted for by an offset of 1 for the i, j coordinates in inner filter loop. The results are
 * rounded and stored in the original image (ip). Note that the border pixels are not modified. See Sec. 4.2 (Prog. 4.2)
 * of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class Filter_Smooth_3x3 implements PlugInFilter {

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Filter_Smooth_3x3() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.IrishManor);
		}
	}

    public int setup(String arg, ImagePlus imp) {
        return DOES_8G;
    }

    public void run(ImageProcessor ip) {
    	int M = ip.getWidth();
    	int N = ip.getHeight();

    	// 3x3 filter kernel (sums to 1)
    	double[][] H = {
    			{0.075, 0.125, 0.075},
    			{0.125, 0.200, 0.125},
    			{0.075, 0.125, 0.075}};

    	ImageProcessor copy = ip.duplicate();

    	for (int u = 1; u <= M - 2; u++) {
    		for (int v = 1; v <= N - 2; v++) {
    			// compute filter result for position (u,v):
    			double sum = 0;
    			for (int i = -1; i <= 1; i++) {
    				for (int j = -1; j <= 1; j++) {
    					int p = copy.get(u + i, v + j);
    					// get the corresponding filter coefficient:
    					double c = H[j + 1][i + 1];
    					sum = sum + c * p;
    				}
    			}
    			int q = (int) Math.round(sum);
    			ip.set(u, v, q);
    		}
    	}
    }

}

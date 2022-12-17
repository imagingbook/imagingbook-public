/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch03_Point_Operations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin performs linear histogram equalization on the selected grayscale image, which is modified. See
 * Sec. 3.5 (Prog. 3.2) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class Equalize_Histogram implements PlugInFilter {

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Equalize_Histogram() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.IrishManor);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		return DOES_8G;
	}
    
	@Override
	public void run(ImageProcessor ip) {
		int M = ip.getWidth();
		int N = ip.getHeight();
		int K = 256; // number of intensity values

		int[] H = ip.getHistogram();
		// compute the cumulative histogram H (recursively and in-place):
		for (int j = 1; j < H.length; j++) {
			H[j] = H[j - 1] + H[j];
		}

		// equalize the image:
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				int a = ip.get(u, v);
				int b = H[a] * (K - 1) / (M * N);
				ip.set(u, v, b);
			}
		}
	}
	
}

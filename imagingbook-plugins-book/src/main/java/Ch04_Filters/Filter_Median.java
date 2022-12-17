/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch04_Filters;

import java.util.Arrays;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin for a median filter of arbitrary size. An array {@code A} of type {@code int} is defined to hold the
 * region’s pixel values for each filter position (u, v). This array is sorted by using the standard Java method
 * {@link Arrays#sort(int[])}. The center element of the sorted vector ({@code A[n]}) is taken as the median value and
 * stored in the original image {@code ip}. See Sec. 4.1.2 (Prog. 4.5) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class Filter_Median implements PlugInFilter {

	/** Filter radius. */
	public static int R = 3; // the size of the filter is (2R+1)x(2R+1)

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Filter_Median() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.IrishManor);
		}
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}

		final int M = ip.getWidth();
		final int N = ip.getHeight();
		final ImageProcessor copy = ip.duplicate();

		// vector to hold pixels from (2R+1)x(2R+1) neighborhood:
		int[] A = new int[(2 * R + 1) * (2 * R + 1)];
		
		// index of center vector element n = 2(R^2 + R):
		int n = 2 * (R * R + R);
		
		for (int u = R; u <= M - R - 2; u++) {
			for (int v = R; v <= N - R - 2; v++) {
				// fill the pixel vector A for filter position (u,v):
				int k = 0;
				for (int i = -R; i <= R; i++) {
					for (int j = -R; j <= R; j++) {
						A[k] = copy.get(u + i, v + j);
						k++;
					}
				}
				// sort vector A and take the center element A[n]:
				Arrays.sort(A);
				ip.set(u, v, A[n]);
			}
		}
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addMessage("Filter kernel size is (2r+1) x (2r+1):");
		gd.addNumericField("Kernel radius (r > 0)", R, 0);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		R = (int) gd.getNextNumber();
		return true;
	}

}

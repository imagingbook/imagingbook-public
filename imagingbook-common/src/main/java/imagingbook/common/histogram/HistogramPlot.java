/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */

package imagingbook.common.histogram;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

/**
 * Defines a simple image window to display histograms. This is a sub-class of
 * {@link ImagePlus} (similar to {@code ij.gui.HistogramPlot}).
 * 
 * @author WB
 * @version 2022/09/22 revised to extend ImagePlus
 */
public class HistogramPlot extends ImagePlus {
	
	static final int BACKGROUND = NewImage.FILL_WHITE;

    private final int width =  256;
    private final int height = 128;
    private final int base = height - 1;
    private final int paintValue = 0;
	private final int[] H = new int[256];
	
	/**
	 * Constructor for a normalized discrete distribution.
	 * @param nH a normalized discrete distribution with values in [0,1]
	 * @param title the window title to be displayed 
	 */
	public HistogramPlot(double[] nH, String title) {
		setImage(NewImage.createByteImage(title, width, height, 1, BACKGROUND));
		// nH must be a normalized histogram of length 256
		for (int i = 0; i < nH.length; i++) {
			H[i] = (int) Math.round(height * nH[i]);
		}
		drawHist();
	}
	
	/**
	 * Constructor for a discrete distribution.
	 * @param h a discrete distribution (histogram) with arbitrary values
	 * @param title the window title to be displayed 
	 */
	public HistogramPlot(int[] h, String title) {
		this(HistogramUtils.normalizeMax(h), title);
	}
	
	/**
	 * Constructor for a piecewise linear cumulative distribution.
	 * @param cdf a piecewise linear cumulative distribution function
	 * @param title the window title to be displayed 
	 */
	public HistogramPlot(PiecewiseLinearCdf cdf, String title) {
		this(cdf.getCdf(), title);
	}
	
	private void drawHist() {
		ImageProcessor ip = this.getProcessor();
		ip.setValue(0);
		ip.drawLine(0, base, width - 1, base);
		ip.setValue(paintValue);
		int u = 0;
		for (int i = 0; i < H.length; i++) {
			int k = H[i];
			if (k > 0) {
				ip.drawLine(u, base - 1, u, base - k);
			}
			u = u + 1;
		}
	}
	
}

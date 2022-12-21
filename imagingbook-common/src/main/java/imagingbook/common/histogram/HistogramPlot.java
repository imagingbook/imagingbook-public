/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.histogram;

import ij.ImagePlus;
import ij.process.ByteProcessor;

/**
 * Defines a simple image window to display histograms. This is a sub-class of {@link ImagePlus} (similar to
 * {@code ij.gui.HistogramPlot}).
 *
 * @author WB
 * @version 2022/12/07
 */
public class HistogramPlot extends ImagePlus {
	
	private static final int Background = 255;
	private static final int Foreground = 0;
    private static final int width =  256;
    private static final int height = 128;

	/**
	 * Constructor for a normalized discrete distribution.
	 *
	 * @param nH a normalized discrete distribution with values in [0,1]
	 * @param title the window title to be displayed (may be null)
	 */
	public HistogramPlot(double[] nH, String title) {
		super(title, drawHistogram(nH));
	}

	/**
	 * Constructor for a discrete distribution.
	 *
	 * @param h a discrete distribution (histogram) with arbitrary values
	 * @param title the window title to be displayed (may be null)
	 */
	public HistogramPlot(int[] h, String title) {
		this(HistogramUtils.normalizeMax(h), title);
	}

	private static ByteProcessor drawHistogram(double[] nH) {
		ByteProcessor ip = new ByteProcessor(width, height);
		int base = height - 1;
		ip.setValue(Background);
		ip.fill();
		ip.setValue(Foreground);
		ip.drawLine(0, base, width - 1, base);
		int u = 0;
		for (int i = 0; i < nH.length; i++) {
			int k = (int) Math.round(height * nH[i]);
			if (k > 0) {
				ip.drawLine(u, base - 1, u, base - k);
			}
			u = u + 1;
		}
		return ip;
	}
	
}

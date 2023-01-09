/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch02_Histograms_Statistics;

import Ch03_Point_Operations.Equalize_Histogram;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.histogram.HistogramPlot;
import imagingbook.common.histogram.HistogramUtils;
import imagingbook.common.ij.DialogUtils;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin which calculates and displays (lists) the histogram of a 8-bit grayscale image. See Sec. 2.3 of [1] for
 * additional details. Alternatively the histogram could be obtained with ImageJs built-in method (see also
 * {@link Equalize_Histogram}):
 * </p>
 * <pre>
 * int[] h = ip.getHistogram();</pre>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @see HistogramPlot
 * @see HistogramUtils
 * @see Show_Histogram
 */
public class Compute_Histogram implements PlugInFilter {

	private static boolean ShowHistogramPlot = true;
	private static boolean ListHistogramEntries = false;
	private static int HISTOGRAM_HEIGHT = 128;

	private ImagePlus im;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Compute_Histogram() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.IrishManor);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + NO_CHANGES;
	}
    
	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}

		int[] h = new int[256]; 	// histogram array (initialized to zero values)
		final int M  = ip.getWidth();
		final int N = ip.getHeight();

		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				int i = ip.getPixel(u, v);
				h[i] = h[i] + 1;
			}
		}
		
		// alternative to the above:
		// int[] h = ip.getHistogram();

		// ... histogram h[] may now be used

		if (ShowHistogramPlot) {
			showHistogram(h, "Histogram of " + im.getShortTitle());
		}
		
		// listing histogram values:
		if (ListHistogramEntries) {
			IJ.log("  i       h[i]");
			for (int i = 0; i < h.length; i++) {
				IJ.log(String.format("%3d: %8d", i, h[i]));
			}
		}
	}

	void showHistogram(int[] h, String title) {
		int height = HISTOGRAM_HEIGHT;
		int hmax = 0;
		for (int i = 0; i < h.length; i++) {
			hmax = Math.max(hmax,  h[i]);
		}
		ByteProcessor hIp = new ByteProcessor(h.length, height);	// create a new image
		hIp.setColor(255);
		hIp.fill();
		hIp.setColor(0);
		for (int i = 0; i < h.length; i++) {
			int len = (int) Math.round(height * h[i] / hmax);	// scale the max value to window height
			hIp.drawLine(i, height, i, height - len);
		}
		new ImagePlus(title, hIp).show();	// show the new image on screen
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());

		gd.addCheckbox("Show histogram plot", ShowHistogramPlot);
		gd.addCheckbox("List histogram entries", ListHistogramEntries);

		gd.showDialog();
		if(gd.wasCanceled())
			return false;

		ShowHistogramPlot = gd.getNextBoolean();
		ListHistogramEntries = gd.getNextBoolean();
		return true;
	}

}

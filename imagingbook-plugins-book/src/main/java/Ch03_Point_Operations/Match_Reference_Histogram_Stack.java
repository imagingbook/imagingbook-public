/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package Ch03_Point_Operations;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.histogram.HistogramPlot;
import imagingbook.common.histogram.HistogramUtils;
import imagingbook.common.ij.DialogUtils;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin, matches the histograms of the supplied images which are supplied as a {@link ImageStack} with 2 ore
 * more frames. The first image of this stack is used as the reference image, i.e., its cumulative histogram is used to
 * calculate the intensity transformation for the remaining images. See Sec. 3.6.4 (Fig. 3.17) of [1] for additional
 * details. The plugin displays the modified images as a new image stack. Optionally, the original histograms, the
 * associated modified histograms and cumulative histograms are shown (again as image stacks). Note that all modified
 * cumulative histograms should be very similar.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @see HistogramPlot
 * @see HistogramUtils
 */
public class Match_Reference_Histogram_Stack implements PlugInFilter {

	private static boolean ShowOriginalHistograms = true;
	private static boolean ShowModifiedHistograms = true;
	private static boolean ShowModifiedCumulativeHistograms = false;

	private ImagePlus im;	// reference image (selected interactively)

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Match_Reference_Histogram_Stack() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.CityscapeSmallStack);
		}
	}

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + STACK_REQUIRED + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		ImageStack origImageStack = im.getStack();
		int n = origImageStack.getSize();
		if (n < 2) {
			IJ.error("Plugin requires stack with at least 2 slices!");
			return;
		}

		if (!runDialog()) // select the reference image
			return;

		// get all histograms:
		int[][] histograms = new int[n][];
		for (int i = 0; i < n; i++) {
			ImageProcessor ipA = origImageStack.getProcessor(i + 1);
			histograms[i] = ipA.getHistogram();
		}
		// first image specifies the reference histogram:
		int[] hRef = histograms[0];

		// adapt remaining images to the reference histogram:
		ImageStack modImageStack = new ImageStack();
		modImageStack.addSlice(origImageStack.getProcessor(1).duplicate());
		for (int i = 1; i < n; i++) {
			int[] hA = histograms[i];
			int[] f = HistogramUtils.matchHistograms(hA, hRef);
			ImageProcessor ipA = origImageStack.getProcessor(i + 1).duplicate();
			ipA.applyTable(f);
			modImageStack.addSlice(ipA);
		}
		new ImagePlus("Modified images", modImageStack).show();

		if (ShowOriginalHistograms) {
			ImageStack origHistStack = new ImageStack();
			for (int i = 0; i < n; i++) {
				HistogramPlot plot = new HistogramPlot(histograms[i], null);
				origHistStack.addSlice(plot.getProcessor());
			}
			new ImagePlus("Original histograms", origHistStack).show();
		}

		if (ShowModifiedHistograms) {
			ImageStack modHistStack = new ImageStack();
			for (int i = 0; i < n; i++) {
				ImageProcessor ipAm = modImageStack.getProcessor(i + 1);
				int[] h = ipAm.getHistogram();
				modHistStack.addSlice(new HistogramPlot(h, null).getProcessor());
			}
			new ImagePlus("Modified histograms", modHistStack).show();
		}

		if (ShowModifiedCumulativeHistograms) {
			ImageStack modCumHistStack = new ImageStack();
			for (int i = 0; i < n; i++) {
				ImageProcessor ipA = modImageStack.getProcessor(i + 1);
				int[] h = ipA.getHistogram();
				modCumHistStack.addSlice(new HistogramPlot(HistogramUtils.cdf(h), null).getProcessor());
			}
			new ImagePlus("Modified cumulative histograms", modCumHistStack).show();
		}
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addCheckbox("Show original histograms", ShowOriginalHistograms);

		gd.addCheckbox("Show modified histograms", ShowModifiedHistograms);
		gd.addCheckbox("Show modified cum. histograms", ShowModifiedCumulativeHistograms);

		gd.showDialog();
		if(gd.wasCanceled())
			return false;

		ShowOriginalHistograms = gd.getNextBoolean();
		ShowModifiedHistograms = gd.getNextBoolean();
		ShowModifiedCumulativeHistograms = gd.getNextBoolean();
		return true;
	}

}


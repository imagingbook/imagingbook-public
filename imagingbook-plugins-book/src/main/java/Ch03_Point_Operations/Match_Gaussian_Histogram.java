/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch03_Point_Operations;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.histogram.HistogramPlot;
import imagingbook.common.histogram.HistogramUtils;
import imagingbook.common.ij.DialogUtils;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;
import static imagingbook.common.math.Arithmetic.sqr;

/**
 * <p>
 * ImageJ plugin, adapts image intensities to match a Gaussian distribution with specified parameters &mu;, &sigma;
 * ({@link #Mean}, {@link #StdDev}). The current active image is modified, the histogram and cumulative histogram of the
 * resulting image are displayed. See Sec. 3.6.4 (Fig. 3.15) of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @see HistogramUtils
 * @see HistogramPlot
 */
public class Match_Gaussian_Histogram implements PlugInFilter {

	// parameters of Gaussian target distribution:
	private static double Mean = 128;
	private static double StdDev = 90;

	private static boolean ShowOriginalHistograms = true;
	private static boolean ShowCumulativeHistograms = true;
	private static boolean ShowFinalHistogram = true;
	private static boolean ListMappingFunction = false;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Match_Gaussian_Histogram() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.IrishManor);
		}
	}
	
	@Override
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		if (!runDialog()) {
			return;
		}
		
		// get histograms
		int[] hi = ip.getHistogram();
		int[] hG = makeGaussianHistogram(Mean, StdDev);

		if (ShowOriginalHistograms) {
			new HistogramPlot(hi, "Original Histogram").show();
			new HistogramPlot(hG, "Gaussian Histogram").show();
		}
		
		double[] chG = HistogramUtils.cdf(hG);
    	int[] F = HistogramUtils.matchHistograms(hi, hG);
		ip.applyTable(F);
		int[] hAm = ip.getHistogram();

		if (ShowFinalHistogram) {
			new HistogramPlot(hAm, "Final Histogram").show();
		}

		if (ShowCumulativeHistograms) {
			new HistogramPlot(chG, "Gaussian Cumulative Histogram").show();
			new HistogramPlot(HistogramUtils.cdf(hAm), "Final Cumulative Histogram").show();
		}

		if (ListMappingFunction) {
			for (int i = 0; i < F.length; i++) {
				IJ.log(i + " -> " + F[i]);
			}
		}
	}
	
	private int[] makeGaussianHistogram (double mean, double sigma) {
		int[] h = new int[256];
		double sigma2 = 2 * sqr(sigma);
		for (int i = 0; i < h.length; i++) {
			double x = mean - i;
			double g = Math.exp(-sqr(x) / sigma2) / sigma;
			h[i] = (int) Math.round(10000 * g);
		}
		return h;
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());

		gd.addNumericField("Mean (μ)", Mean, 2);
		gd.addNumericField("Std deviation (σ)", StdDev, 2);

		gd.addCheckbox("Show original histograms", ShowOriginalHistograms);
		gd.addCheckbox("Show cumulative histograms", ShowCumulativeHistograms);
		gd.addCheckbox("Show final histogram", ShowFinalHistogram);
		gd.addCheckbox("List mapping function", ListMappingFunction);

		gd.showDialog();
		if(gd.wasCanceled())
			return false;

		Mean = gd.getNextNumber();
		StdDev = gd.getNextNumber();

		ShowOriginalHistograms = gd.getNextBoolean();
		ShowCumulativeHistograms = gd.getNextBoolean();
		ShowFinalHistogram = gd.getNextBoolean();
		ListMappingFunction = gd.getNextBoolean();
		return true;
	}

}


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
import imagingbook.common.histogram.PiecewiseLinearCdf;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * Adapts image intensities to match a reference histogram that is piecewise-linear. See Sec. 3.6.4 (Fig. 3.14) of [1]
 * for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @see HistogramUtils
 * @see HistogramPlot
 * @see PiecewiseLinearCdf
 */
public class Match_Piecewise_Linear_Histogram implements PlugInFilter, JavaDocHelp {

	private static int[]    a = {28, 75, 150, 210};			// a_k (brightness values)
	private static double[] P = {.05, .25, .75, .95};		// P_k (cum. probabilities)

	private static boolean ShowOriginalHistograms = true;
	private static boolean ShowCumulativeHistograms = true;
	private static boolean ShowFinalHistogram = true;
	private static boolean ListMappingFunction = false;

	/** Constructor, asks to open a predefined sample image if no other image is currently open. */
	public Match_Piecewise_Linear_Histogram() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.IrishManor);
		}
	}

	@Override
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G;
	}
	
	@Override
	public void run(ImageProcessor ipA) {
		if (!runDialog()) {
			return;
		}

		// get histogram of original image
		int[] hA = ipA.getHistogram();

		// -------------------------
		PiecewiseLinearCdf plCdf = new PiecewiseLinearCdf(256, a, P);
		// -------------------------

		double[] nhB = plCdf.getPdf();
		nhB = HistogramUtils.normalizeMax(nhB);

		if (ShowOriginalHistograms) {
			new HistogramPlot(hA, "Original Histogram").show();
			new HistogramPlot(nhB, "Reference Histogram").show();
		}
		if (ShowCumulativeHistograms) {
			new HistogramPlot(HistogramUtils.cdf(hA), "Original Cumulative Histogram").show();
			new HistogramPlot(plCdf.getCdf(), "Reference Cumulative Histogram").show();
		}

		int[] F = HistogramUtils.matchHistograms(hA, plCdf);

		ipA.applyTable(F);
		
		int[] hAm = ipA.getHistogram();

		if (ShowFinalHistogram) {
			new HistogramPlot(hAm, "Final Histogram").show();
			new HistogramPlot(HistogramUtils.cdf(hAm), "Final Cumulative Histogram").show();
		}

		if (ListMappingFunction) {
			for (int i = 0; i < F.length; i++) {
				IJ.log(i + " -> " + F[i]);
			}
		}
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addCheckbox("Show original histograms", ShowOriginalHistograms);
		gd.addCheckbox("Show cumulative histograms", ShowCumulativeHistograms);
		gd.addCheckbox("Show final histogram", ShowFinalHistogram);
		gd.addCheckbox("List mapping function", ListMappingFunction);

		gd.showDialog();
		if(gd.wasCanceled())
			return false;

		ShowOriginalHistograms = gd.getNextBoolean();
		ShowCumulativeHistograms = gd.getNextBoolean();
		ShowFinalHistogram = gd.getNextBoolean();
		ListMappingFunction = gd.getNextBoolean();
		return true;
	}
}


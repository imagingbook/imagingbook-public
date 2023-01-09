/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package Ch13_Color_Images;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.quantize.ColorQuantizer;
import imagingbook.common.color.quantize.KMeansClusteringQuantizer;
import imagingbook.common.color.quantize.MedianCutQuantizer;
import imagingbook.common.color.quantize.OctreeQuantizer;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import java.util.Locale;

import static imagingbook.common.color.quantize.KMeansClusteringQuantizer.DefaultIterations;
import static imagingbook.common.color.quantize.KMeansClusteringQuantizer.InitialClusterMethod.MostFrequent;
import static imagingbook.common.color.quantize.KMeansClusteringQuantizer.InitialClusterMethod.Random;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin, performs Median Cut color quantization using {@link MedianCutQuantizer}. See Sec. 13.4.2 (Algs.
 * 13.1-3) of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/05
 */
public class Quantize_Color_Image implements PlugInFilter, JavaDocHelp {
	
	enum QuantizeMethod {
		MedianCut,
		Octree,
		OctreeQuick,
		KMeansRandom,
		KMeansMostFrequent
	}
	
	private static QuantizeMethod METHOD = QuantizeMethod.MedianCut;
	private static int NCOLORS = 16;
	private static boolean CREATE_INDEXED_IMAGE = true; 
	private static boolean CREATE_RGB_IMAGE = false;
	private static boolean LIST_COLOR_MAP = false;
	
	private ImagePlus im;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Quantize_Color_Image() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.Flower);
		}
	}
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_RGB + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		
		if (!runDialog())
			return;
		
		ColorProcessor cp = (ColorProcessor) ip;
		ColorQuantizer quantizer = makeQuantizer((int[])cp.getPixels());
		
		int nCols = quantizer.getColorMap().length;
		
		String title = im.getShortTitle() + "-" + METHOD.toString();
		
		if (CREATE_INDEXED_IMAGE) {
			// quantize to an indexed color image
			ByteProcessor qip = quantizer.quantize(cp);
			ImagePlus imp = new ImagePlus(title + "-IDX-" + nCols, qip);
			imp.setTypeToColor256();
			imp.show();
		}
		
		if (CREATE_RGB_IMAGE) {
			// quantize to a full-color RGB image
			int[] rgbPix = quantizer.quantize((int[])cp.getPixels());
			ColorProcessor rgbIp = new ColorProcessor(cp.getWidth(), cp.getHeight(), rgbPix);
			ImagePlus imp = new ImagePlus(title + "-RGB-" + nCols , rgbIp);
			imp.show();
		}
		
		if (LIST_COLOR_MAP) {
			listColorMap(quantizer);
		}
	}
	
	private ColorQuantizer makeQuantizer(int[] pixels) {
		switch (METHOD) {
		case MedianCut: 
			return new MedianCutQuantizer(pixels, NCOLORS);
		case Octree: 
			return new OctreeQuantizer(pixels, NCOLORS, false);
		case OctreeQuick: 
			return new OctreeQuantizer(pixels, NCOLORS, true);
		case KMeansRandom: 
			return new KMeansClusteringQuantizer(pixels, NCOLORS, Random, DefaultIterations);
		case KMeansMostFrequent: 
			return new KMeansClusteringQuantizer(pixels, NCOLORS, MostFrequent, DefaultIterations);
		}
		return null;
	}

	public void listColorMap(ColorQuantizer quantizer) {
		float[][] colormap = quantizer.getColorMap();
		IJ.log("Quantized colors (r, g, b)");
		int n = colormap.length;
		for (int i = 0; i < n; i++) {
			float red = colormap[i][0];
			float grn = colormap[i][1];
			float blu = colormap[i][2];
			IJ.log(String.format(Locale.US, "  %3d: (%5.1f, %5.1f, %5.1f)", i, red, grn, blu));
		}
	}
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addEnumChoice("Quantization method", METHOD);
		gd.addNumericField("No. of colors (1,..,256)", NCOLORS, 0);
		gd.addCheckbox("Create indexed color image", CREATE_INDEXED_IMAGE);
		gd.addCheckbox("Create quantized RGB image", CREATE_RGB_IMAGE);
		gd.addCheckbox("List quantized colors", LIST_COLOR_MAP);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		METHOD = gd.getNextEnumChoice(QuantizeMethod.class);
		int nc = (int) gd.getNextNumber();
		nc = Math.min(nc, 256);
		nc = Math.max(1, nc);		
		NCOLORS = nc;
		CREATE_INDEXED_IMAGE = gd.getNextBoolean();
		CREATE_RGB_IMAGE = gd.getNextBoolean();
		LIST_COLOR_MAP = gd.getNextBoolean();
		return true;
	}
}

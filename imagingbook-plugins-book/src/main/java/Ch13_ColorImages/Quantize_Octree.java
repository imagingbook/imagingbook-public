/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package Ch13_ColorImages;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.quantize.OctreeQuantizer;

/**
 * <p>
 * ImageJ plugin demonstrating the use of the {@link OctreeQuantizer} class.
 * See Sec. 13.4 for more details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/05
 */
public class Quantize_Octree implements PlugInFilter {
	
	private static int NCOLORS = 16;
	private static boolean QUICK = false;	
	private static boolean CREATE_INDEXED_IMAGE = true; 
	private static boolean CREATE_RGB_IMAGE = false;
	private static boolean LIST_COLOR_MAP = false;
	
	private ImagePlus im;
	
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
		int[] pixels = (int[]) cp.getPixels();

		OctreeQuantizer quantizer = new OctreeQuantizer(pixels, NCOLORS);
		quantizer.setQuickQuantization(QUICK);
		int nCols = quantizer.getColorMap().length;
		
		String title = im.getShortTitle() + "-Octree";
		
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
			new ImagePlus(title + "-RGB-" + nCols , rgbIp).show();
		}
		
		if (LIST_COLOR_MAP) {
			quantizer.listColorMap();
		}
	}
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("No. of colors (1,..,256)", NCOLORS, 0);
		gd.addCheckbox("Use quick quantization", QUICK);
		gd.addCheckbox("Create indexed color image", CREATE_INDEXED_IMAGE);
		gd.addCheckbox("Create quantized RGB image", CREATE_RGB_IMAGE);
		gd.addCheckbox("List quantized colors", LIST_COLOR_MAP);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		int nc = (int) gd.getNextNumber();
		nc = Math.min(nc, 255);
		nc = Math.max(2, nc);
		NCOLORS = nc;
		QUICK = gd.getNextBoolean();
		CREATE_INDEXED_IMAGE = gd.getNextBoolean();
		CREATE_RGB_IMAGE = gd.getNextBoolean();
		LIST_COLOR_MAP = gd.getNextBoolean();
		return true;
	}

//	private void shuffleArray(int[] ar) {
//		Random rnd = ThreadLocalRandom.current();
//		for (int i = ar.length - 1; i > 0; i--) {
//			int index = rnd.nextInt(i + 1);
//			// Simple swap
//			int a = ar[index];
//			ar[index] = ar[i];
//			ar[i] = a;
//		}
//	}

}

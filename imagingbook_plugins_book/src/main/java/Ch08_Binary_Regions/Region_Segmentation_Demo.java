/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch08_Binary_Regions;


import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.common.color.iterate.RandomHueGenerator;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.BinaryRegionSegmentation;
import imagingbook.common.regions.BreadthFirstSegmentation;
import imagingbook.common.regions.DepthFirstSegmentation;
import imagingbook.common.regions.RecursiveSegmentation;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.common.regions.SequentialSegmentation;
import imagingbook.core.plugin.IjPluginName;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import java.util.List;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of various region labeling techniques
 * provided by the imagingbook "regions" package:
 * </p>
 * <ul>
 * <li>{@link BreadthFirstSegmentation},</li>
 * <li>{@link DepthFirstSegmentation},</li>
 * <li>{@link RecursiveSegmentation},</li>
 * <li>{@link RegionContourSegmentation},</li>
 * <li>{@link SequentialSegmentation}.</li>
 * </ul>
 * <p>
 * See Sec. 8.1 of [1] for additional details. One of four labeling types can be
 * selected (see the {@link #run(ImageProcessor)} method). All methods should
 * produce the same results (except {@link RegionContourSegmentation}, which may
 * run out of memory easily). Requires a binary image. Zero-value pixels are
 * considered background, all other pixels are foreground. Display lookup tables
 * (LUTs) are not considered. The plugin creates a new image with connected
 * components either randomly-colored or region labels shown as gray values. The
 * original image is not modified.
 * If no image is currently open, the plugin optionally loads a suitable
 * sample image.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2020/12/20
 * @version 2022/09/28 revised
 */
@IjPluginName("Region Segmentation Demo")
public class Region_Segmentation_Demo implements PlugInFilter, JavaDocHelp {
	
	/** Enum type for various region labeling methods. */
	public enum SegmentationMethod {
		Recursive,
		DepthFirst, 
		BreadthFirst,
		Sequential,
		RegionAndContours,
	}

	/** The region labeling method to be used. */
	public static SegmentationMethod Method = SegmentationMethod.BreadthFirst;
	/** Neighborhood type used for region segmentation (4- or 8-neighborhood). */
	public static NeighborhoodType2D Neighborhood = NeighborhoodType2D.N8;
	
	/** Set true to randomly color segmented regions. */
	public static boolean ColorComponents = true;
	/** Set true to to list segmented regions to the console. */
	public static boolean ListRegions = false;

	public static int RandomSeed = 0;
	
	private ImagePlus im;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Region_Segmentation_Demo() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.ToolsSmall);
		}
	}
	
    @Override
	public int setup(String arg, ImagePlus im) {
    	this.im = im;
		return DOES_8G + NO_CHANGES;
    }
	
    @Override
	public void run(ImageProcessor ip) {
    	
    	if (!runDialog())
    		return;
    	
    	if (Method == SegmentationMethod.Recursive && 
    			!IJ.showMessageWithCancel("Recursive labeling", 
    					"This may run out of stack memory!\n" + "Continue?")) {
			return;
    	}

    	// Copy the original to a new byte image:
    	ByteProcessor bp = ip.convertToByteProcessor(false);
    	
		BinaryRegionSegmentation segmentation = null;
		switch(Method) {
		case BreadthFirst : 	
			segmentation = new BreadthFirstSegmentation(bp, Neighborhood); break;
		case DepthFirst : 		
			segmentation = new DepthFirstSegmentation(bp, Neighborhood); break;
		case Sequential : 		
			segmentation = new SequentialSegmentation(bp, Neighborhood); break;
		case RegionAndContours : 
			segmentation = new RegionContourSegmentation(bp, Neighborhood); break;
		case Recursive : 
			segmentation = new RecursiveSegmentation(bp, Neighborhood); break;
		}
		
		if (!segmentation.isSegmented()) {
			IJ.showMessage("Something went wrong, segmentation failed!");
			return;
		}

		// Retrieve a list of detected regions (sorted by size):
		List<BinaryRegion> regions = segmentation.getRegions(true);
		
		if (regions.isEmpty()) {
			IJ.showMessage("No regions detected!");
			return;
		}
		
		// Show the resulting labeling as a color or gray image:
		String title = im.getShortTitle() + "-" + Method.name();
		ImageProcessor labelIp = ColorComponents ? //Display.makeLabelImage(segmenter, ColorRegions);
				makeLabelImageColor(segmentation) : makeLabelImageGray(segmentation);
		(new ImagePlus(title, labelIp)).show();
		
		if (ListRegions) {
			IJ.log("Regions sorted by size: " + regions.size());
			for (BinaryRegion r: regions) {
				IJ.log(r.toString());
			}
		}
		
    }

    // ---------------------------------------------------------------------
    
    /**
     * Returns a color image of the specified segmentation with randomly
     * colored components.
     * 
     * @param segmenter a binary region segmentation
     * @return a {@link ColorProcessor} with randomly colored regions 
     */
	private ColorProcessor makeLabelImageColor(BinaryRegionSegmentation segmenter) {
		int minLabel = segmenter.getMinLabel();
		int maxLabel = segmenter.getMaxLabel();
		
		// set up a table of random colors, one for each label:
		RandomHueGenerator rcg = new RandomHueGenerator(RandomSeed);
		int[] labelColor = new int[maxLabel + 1];
		for (int i = minLabel; i <= maxLabel; i++) {
			labelColor[i] = rcg.next().getRGB();
		}
		
		final int width = segmenter.getWidth();
		final int height = segmenter.getHeight();
		
		ColorProcessor cp = new ColorProcessor(width, height);
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int lb = segmenter.getLabel(u, v);
				if (lb >= 0 && lb < labelColor.length) {
					cp.set(u, v, labelColor[lb]);
				}
			}
		}
		return cp;
	}
	
	/**
     * Returns a 16-bit unsigned gray image of the specified segmentation with component
     * pixels set to the associated segmentation labels.
     * 
     * @param segmenter a binary region segmentation
     * @return a 16-bit {@link ShortProcessor} with the original region labels 
     */
	private ShortProcessor makeLabelImageGray(BinaryRegionSegmentation segmenter) {
		int width = segmenter.getWidth();
		int height = segmenter.getHeight();
		ShortProcessor sp = new ShortProcessor(width, height);
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int lb = segmenter.getLabel(u, v);
				sp.set(u, v, (lb >= 0) ? lb : 0);
			}
		}
		sp.resetMinAndMax();
		return sp;
	}

	// ---------------------------------------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(Region_Segmentation_Demo.class.getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addEnumChoice("Segmentation method", Method);
		gd.addEnumChoice("Neighborhood type", Neighborhood);
		gd.addCheckbox("Color components", ColorComponents);
		gd.addCheckbox("List regions", ListRegions);
		gd.addNumericField("Random seed (0 = none)", RandomSeed, 0);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}

		Method = gd.getNextEnumChoice(SegmentationMethod.class);
		Neighborhood = gd.getNextEnumChoice(NeighborhoodType2D.class);
		ColorComponents = gd.getNextBoolean();
		ListRegions = gd.getNextBoolean();
		RandomSeed = (int) gd.getNextNumber();
		return true;
	}


}




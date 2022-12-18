/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package Ch08_Binary_Regions;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.hulls.ConvexHull;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.core.plugin.IjPluginName;
import imagingbook.sampleimages.GeneralSampleImage;

import java.util.List;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of the {@link ConvexHull} class. See
 * Sec. 8.4.2 of [1] for additional details. It performs region segmentation,
 * calculates the convex hull for each region found and then displays the result
 * in a new image. Requires a binary image. Zero-value pixels are considered
 * background, all other pixels are foreground. Display lookup tables (LUTs) are
 * not considered. The resulting convex hull is shown as a vector overlay on top
 * of a new image, the original image is not modified.
 * If no image is currently open, the plugin optionally loads a suitable
 * sample image.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * 
 * @author WB
 * @version 2022/06/24
 * 
 */
@IjPluginName("Convex Hull Demo")
public class Convex_Hull_Demo implements PlugInFilter {
	
	/** Color of the convex hull outline. */
	public static BasicAwtColor DrawingColor = BasicAwtColor.Blue;
	public static double StrokeWidth = 0.5;
	
	private ImagePlus im = null;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Convex_Hull_Demo() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.ToolsSmall);
		}
	}
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G; 
	}
	
	@Override
	public void run(ImageProcessor ip) {
		if (!IjUtils.isBinary(ip)) {
			IJ.showMessage("Plugin requires a binary image!");
			return;
		}

		if (!runDialog())
			return;

		RegionContourSegmentation segmenter = new RegionContourSegmentation((ByteProcessor) ip);
		
		List<BinaryRegion> regions = segmenter.getRegions();
		if (regions.isEmpty()) {
			IJ.error("No regions detected!");
			return;
		}
		
		ImageProcessor ip2 = ip.duplicate();
		ip2.add(128);
		
		// draw convex hulls as vector overlay
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		ola.setStroke(new ColoredStroke(StrokeWidth, DrawingColor.getColor()));
		
		for (BinaryRegion r: regions) {
			//ConvexHull hull = new ConvexHull(r);					// takes all region points
			ConvexHull hull = new ConvexHull(r.getOuterContour());	// takes only outer contour points
			ola.addShapes(hull.getShapes());
		}

		im.setOverlay(ola.getOverlay());
	}


	// --------------------------------------------------------------------------

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(Region_Contours_Demo.class.getSimpleName());
		gd.addEnumChoice("Drawing color", DrawingColor);
		gd.addNumericField("Stroke width", StrokeWidth, 1);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}

		DrawingColor = gd.getNextEnumChoice(BasicAwtColor.class);
		StrokeWidth = gd.getNextNumber();
		return true;
	}
}

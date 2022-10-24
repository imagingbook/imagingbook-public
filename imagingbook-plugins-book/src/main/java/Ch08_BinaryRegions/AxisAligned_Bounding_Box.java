/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package Ch08_BinaryRegions;

import java.awt.Color;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.hulls.AxisAlignedBoundingBox;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.core.plugin.IjPluginName;

/**
 * <p>
 * This ImageJ plugin creates a binary region segmentation, calculates the
 * center and major axis and subsequently the major axis-aligned bounding box
 * for each binary region (connected component). See Sec. 8.6.4 of [1] for
 * additional details. Requires a binary image. Zero-value pixels are considered
 * background, all other pixels are foreground. Display lookup tables (LUTs) are
 * not considered. The resulting bounding box is shown as a vector overlay on
 * top of a new image, the original image is not modified.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/06/23
 */
@IjPluginName("Axis-Aligned Bounding Box")
public class AxisAligned_Bounding_Box implements PlugInFilter {
	
	/** Color of the bounding-box center. */
	public static Color CenterColor = Color.magenta;
	/** Color of the bounding-box outline. */
	public static Color BoundingBoxColor = Color.blue;

	private ImagePlus im;
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + NO_CHANGES; 
	}
	
	@Override
	public void run(ImageProcessor ip) {
		
		if (!IjUtils.isBinary(ip)) {
			IJ.showMessage("Plugin requires a binary image!");
			return;
		}
		
		RegionContourSegmentation segmenter = new RegionContourSegmentation((ByteProcessor) ip);
		List<BinaryRegion> regions = segmenter.getRegions();
		if (regions.isEmpty()) {
			IJ.error("No regions detected!");
			return;
		}
		
		ImageProcessor ip2 = ip.duplicate();
		ip2.add(128);	// brighten
		
		// draw bounding boxes as vector overlay
		Overlay oly = new Overlay();
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
		ola.setStroke(new ColoredStroke(0.5, BoundingBoxColor));
		
		for (BinaryRegion r: regions) {
			AxisAlignedBoundingBox box = new AxisAlignedBoundingBox(r);
			ola.addShapes(box.getShapes());
		}
		
		ImagePlus im2 = new ImagePlus(im.getShortTitle() + "-aligned-bb", ip2);
		im2.setOverlay(oly);
		im2.show();
	}
	
}

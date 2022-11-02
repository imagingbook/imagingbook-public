/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch08_BinaryRegions;

import static imagingbook.common.ij.IjUtils.noCurrentImage;
import static imagingbook.common.ij.IjUtils.askForSampleImage;

import java.awt.Color;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.Contour;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.core.plugin.IjPluginName;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of the class
 * {@link RegionContourSegmentation} to perform both region labeling and contour
 * tracing simultaneously. See Sec. 8.2.2 of [1] for additional details.
 * Requires a binary image. Zero-value pixels are considered background, all
 * other pixels are foreground. Display lookup tables (LUTs) are not considered.
 * The resulting contours are displayed as a non-destructive vector overlay on
 * the original image. Outer contours of single-pixel regions are marked by an
 * "X".
 * </p>
 * <p>
 * This plugin also demonstrates the use of the {@link ShapeOverlayAdapter}
 * (provided by the imagingbook library) which handles 0.5 pixel offsets for
 * vector graphics transparently.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2020/12/20
 * @version 2022/09/27 revised overlay generation
 * 
 * @see RegionContourSegmentation
 */
@IjPluginName("Region Contours Demo")
public class Region_Contours_Demo implements PlugInFilter {
	
	/** Neighborhood type used for region segmentation (4- or 8-neighborhood). */
	public static NeighborhoodType2D Neighborhood = NeighborhoodType2D.N8;
	
	/** Stroke width used for drawing contours. */
	public static double ContourStrokeWidth = 0.25;
	/** Color used for drawing outer contours. */
	public static Color OuterContourColor = Color.red;
	/** Color used for drawing inner contours. */
	public static Color InnerContourColor = Color.green;
	
	/** Set true to list detected regions to the text console. */
	public static boolean ListRegions = false;
	
	private ImagePlus im = null;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Region_Contours_Demo() {
		if (noCurrentImage()) {
			askForSampleImage(GeneralSampleImage.ToolsSmall);
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
	   	
	   	// Make sure we have a proper byte image:
	   	ByteProcessor bp = ip.convertToByteProcessor();
	   	
	   	// Create the region segmenter / contour tracer:
		RegionContourSegmentation seg = new RegionContourSegmentation(bp, Neighborhood);
		
		// Get a list of detected regions (sorted by size):
		List<BinaryRegion> regions = seg.getRegions(true);
		if (regions.isEmpty()) {
			IJ.showMessage("No regions detected!");
			return;
		}

		// Draw outer and inner contours for each detected region:
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		ColoredStroke outerStroke = new ColoredStroke(ContourStrokeWidth, OuterContourColor);
		ColoredStroke innerStroke = new ColoredStroke(ContourStrokeWidth, InnerContourColor);
		
		for (BinaryRegion r : seg.getRegions()) {
			Contour oc = r.getOuterContour();
			ola.addShape(oc.getPolygonPath(), outerStroke);
			for (Contour ic : r.getInnerContours()) {
				ola.addShape(ic.getPolygonPath(), innerStroke);
			}
		}
		
		im.setOverlay(ola.getOverlay());
		
		// Optionally list regions to console:
		if (ListRegions) {
			IJ.log("\nDetected regions: " + regions.size());
			for (BinaryRegion R : regions) {
				IJ.log(R.toString());
			}
		}
		
	}
	
	// --------------------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(Region_Contours_Demo.class.getSimpleName());
		gd.addEnumChoice("Neighborhood type", Neighborhood);
		gd.addCheckbox("List regions", ListRegions);
		
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		
		Neighborhood = gd.getNextEnumChoice(NeighborhoodType2D.class);
		ListRegions  = gd.getNextBoolean();
		return true;
	}
}

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
import ij.process.ImageProcessor;
import imagingbook.common.color.sets.BasicAwtColor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.common.util.GenericProperties.PropertyKey;
import imagingbook.core.plugin.IjPluginName;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.geom.Line2D;
import java.util.List;

import static imagingbook.common.ij.IjUtils.noCurrentImage;
import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

/**
 * <p>
 * ImageJ plugin, shows each region's major axis as a vector scaled by the region's eccentricity. See Sec. 8.6 of [1]
 * for additional details. Also demonstrates the use of the region property scheme, i.e., how to assign numeric
 * properties to regions and retrieve them afterwards. Requires a binary image. Zero-value pixels are considered
 * background, all other pixels are foreground. Display lookup tables (LUTs) are not considered. Results are drawn into
 * a new image (pixel graphics), the original image is not modified. If no image is currently open, the plugin
 * optionally loads a suitable sample image.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2020/12/17
 */
@IjPluginName("Major Axis Demo")
public class Major_Axis_Demo implements PlugInFilter {	// TODO: convert to overlay display


	static final PropertyKey<Double> dxKey = new PropertyKey<>("dx");
	static final PropertyKey<Double> dyKey = new PropertyKey<>("dy");
	static final PropertyKey<Double> ecc1Key = new PropertyKey<>("ecc1");
	static final PropertyKey<Double> ecc2Key = new PropertyKey<>("ecc2");

	
	/** Scale of the axis length. */
	public static double DrawingScale = 50;
	/** Color for drawing overlay graphics. */
	public static BasicAwtColor DrawingColor = BasicAwtColor.Blue;
	/** Stroke width for drawing overlay graphics. */
	public static double StrokeWidth = 0.5;
	
	private ImagePlus im;
	
	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Major_Axis_Demo() {
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


		// perform region segmentation:
		RegionContourSegmentation segmenter = new RegionContourSegmentation((ByteProcessor) ip);
		List<BinaryRegion> regions = segmenter.getRegions(true);

		// calculate and register certain region properties:
		for (BinaryRegion r : regions) {
			calculateRegionProperties(r);
		}
		
		// draw major axis vectors (scaled by eccentricity) as vector overlays
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		ola.setStroke(new ColoredStroke(StrokeWidth, DrawingColor.getColor()));
		for (BinaryRegion r : regions) {
			if (r.getSize() > 10) {
				Pnt2d xc = r.getCenter();
				double x0 = xc.getX();
				double y0 = xc.getY();
				double x1 = x0 + DrawingScale * r.getProperty(dxKey);
				double y1 = y0 + DrawingScale * r.getProperty(dyKey);
				ola.addShape(xc.getShape(0.05 * DrawingScale));
				ola.addShape(new Line2D.Double(x0, y0, x1, y1));
			}
		}
		im.setOverlay(ola.getOverlay());
	}
	
	private void calculateRegionProperties(BinaryRegion r) {
		// calculate central moment mu11, mu20, mu02:
		Pnt2d xctr = r.getCenter();
		double xc = xctr.getX();
		double yc = xctr.getY();
		double mu11 = 0;
		double mu20 = 0;
		double mu02 = 0;
		for (Pnt2d p : r) {
			double dx = (p.getX() - xc);
			double dy = (p.getY() - yc);
			mu11 = mu11 + dx * dy;
			mu20 = mu20 + dx * dx;
			mu02 = mu02 + dy * dy;
		}
		
		double A = 2 * mu11;
		double B = mu20 - mu02;
		
		double normAB = Math.sqrt(sqr(A) + sqr(B));
		double scale = sqrt(2 * (sqr(A) + sqr(B) + B * sqrt(sqr(A) + sqr(B))));
		
		double dx = B + normAB;
		double dy = A;
		
		r.setProperty(dxKey, dx / scale);
		r.setProperty(dyKey, dy / scale);
		
		// calculate 2 versions of eccentricity:
		double a = mu20 + mu02;
		double b = Math.sqrt(Math.pow(mu20 - mu02, 2) + 4 * mu11 * mu11);
		r.setProperty(ecc1Key, (a + b) / (a - b));
		r.setProperty(ecc2Key, (Math.pow(mu20 - mu02,  2) + 4 * mu11 * mu11) / Math.pow(mu20 + mu02, 2));
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
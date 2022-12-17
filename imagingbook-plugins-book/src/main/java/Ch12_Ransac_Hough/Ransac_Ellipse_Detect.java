/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch12_Ransac_Hough;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

import java.util.ArrayList;
import java.util.List;

import Ch12_Ransac_Hough.settings.RansacDrawSettings;
import ij.IJ;
//import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.ImagesToStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.ransac.RansacCircleDetector;
import imagingbook.common.ransac.RansacEllipseDetector;
import imagingbook.common.ransac.RansacResult;
import imagingbook.sampleimages.GeneralSampleImage;

/**
 * <p>
 * RANSAC ellipse detection implemented with imagingbook library class
 * {@link RansacCircleDetector} (see Sec. 12.1.5 of [1] for details). If no
 * image is currently open, the plugin optionally loads a suitable sample image.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/10/03
 */
public class Ransac_Ellipse_Detect implements PlugInFilter, RansacDrawSettings {

	private static RansacEllipseDetector.Parameters params = new RansacEllipseDetector.Parameters();
	static {
		params.randomSeed = 17;
	}
	
	private static int MaxEllipseCount = 3;
	
	private int W, H;
	private ImagePlus im;
	private String title;

	
	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Ransac_Ellipse_Detect() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.NoisyEllipses);
		}
	}
	
	// -------------------------------------------------------------------------

	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		
		title = "Ellipses from " + im.getTitle();
		W = ip.getWidth();
		H = ip.getHeight();	
		
		if (!runDialog()) {
			return;
		}
	
		Pnt2d[] points = IjUtils.collectNonzeroPoints(ip);
		List<RansacResult<GeometricEllipse>> ellipses = new ArrayList<>();

		// ---------------------------------------------------------------------
		RansacEllipseDetector detector = new RansacEllipseDetector(params);
		// ---------------------------------------------------------------------
		
		List<ImagePlus> resultImages = new ArrayList<>();
		int cnt = 0;

		RansacResult<GeometricEllipse> sol = detector.detectNext(points);
		while (sol != null && cnt < MaxEllipseCount) {
			ellipses.add(sol);
			cnt = cnt + 1;
			
			ImagePlus imSnap = new ImagePlus("circle-"+cnt, showPointSet(points));
			Overlay oly = new Overlay();
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);
			imSnap.setOverlay(oly);
			
			{	// draw inliers (points)
				ColoredStroke stroke = new ColoredStroke(LineStrokeWidth, InlierColor, 0);
				stroke.setFillColor(InlierColor);
				for (Pnt2d p : sol.getInliers()) {
					ola.addShape(p.getShape(InlierRadius), stroke);
				}
			}
	
			{ 	// draw initial circle
				GeometricEllipse ellipse = sol.getPrimitiveInit();
				ColoredStroke stroke = new ColoredStroke(LineStrokeWidth, InitialFitColor, 0);
				ola.addShape(ellipse.getShape(), stroke);
			}
	
			{ 	// draw final circle
				GeometricEllipse ellipse = sol.getPrimitiveFinal();
				ColoredStroke stroke = new ColoredStroke(LineStrokeWidth, FinalFitColor, 0);
				ola.addShape(ellipse.getShape(), stroke);
			}
	
			{	// draw the 5 random points used
				ColoredStroke stroke = new ColoredStroke(LineStrokeWidth, RandomDrawDotColor, 0);
				stroke.setFillColor(RandomDrawDotColor);
				for (Pnt2d p : sol.getDraw()) {
					ola.addShape(p.getShape(RandoDrawDotRadius), stroke);
				}
			}
			
			resultImages.add(imSnap);
			sol = detector.detectNext(points);
		}

		// combine all result images to a stack:
		if (resultImages.isEmpty()) {
			IJ.error("No items detected!");
		}
		else {
			ImagePlus stack = ImagesToStack.run(resultImages.toArray(new ImagePlus[0]));
			stack.setTitle(title);
			stack.show();
		}
	}

	// ------------------------------------------------------
	
	private ByteProcessor showPointSet(Pnt2d[] points) {
		ByteProcessor bp = new ByteProcessor(W, H);
		IjUtils.drawPoints(bp, points, 255);
		bp.invertLut();
		return bp;
	}
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		addToDialog(params, gd);
		gd.addNumericField("Max. number of ellipses", MaxEllipseCount);
		
		gd.addStringField("Output title", title, 16);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		getFromDialog(params, gd);
		MaxEllipseCount = (int) gd.getNextNumber();
		title = gd.getNextString();
		
		return params.validate();
	}
}

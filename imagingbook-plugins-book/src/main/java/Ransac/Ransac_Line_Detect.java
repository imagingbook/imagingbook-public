/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ransac;

import java.util.ArrayList;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.ImagesToStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.ransac.RansacDetectorLine;
import imagingbook.common.ransac.RansacCurveResult;

/**
 * RANSAC line detection using imagingbook library class {@link RansacDetectorLine}.
 * 
 * @author WB
 *
 */
public class Ransac_Line_Detect implements PlugInFilter, Settings {
	
	private static RansacDetectorLine.Parameters params = new RansacDetectorLine.Parameters();
	private static int MaxLineCount = 6;
	private static int RandomSeed = 17;
	
	// --------------------
	
	private int W, H;
	private ImagePlus im;
	private String title;

	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {
		title = "Lines from " + im.getTitle();
		W = ip.getWidth();
		H = ip.getHeight();
		
		if (!runDialog()) {
			return;
		}
		
		Pnt2d[] points = IjUtils.collectNonzeroPoints(ip);
		List<RansacCurveResult<AlgebraicLine>> lines = new ArrayList<>();

		// ---------------------------------------------------------------
		RansacDetectorLine detector = new RansacDetectorLine();
		// ---------------------------------------------------------------

		if (RandomSeed > 0) {
			detector.getRandom().setSeed(RandomSeed);
		}
		
		List<ImagePlus> resultImages = new ArrayList<>();
		int cnt = 0;
		
		RansacCurveResult<AlgebraicLine> sol = detector.findNext(points);
		while (sol != null && cnt < MaxLineCount) {
			lines.add(sol);
			cnt = cnt + 1;
			
			ImagePlus imSnap = new ImagePlus("line-"+cnt, showPointSet(points));
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
	
			{ 	// draw initial line
				AlgebraicLine line = sol.getPrimitiveInit();
				ColoredStroke stroke = new ColoredStroke(LineStrokeWidth, InitialFitColor, 0);
				ola.addShape(line.getShape(W, H), stroke);
			}
	
			{	// draw final line
				AlgebraicLine line = sol.getPrimitiveFinal();
				ColoredStroke stroke = new ColoredStroke(LineStrokeWidth, FinalFitColor, 0);
				ola.addShape(line.getShape(W, H), stroke);
			}
	
			{	// draw the 2 random points used
				ColoredStroke pointStroke = new ColoredStroke(LineStrokeWidth, RandomDrawDotColor, 0);
				pointStroke.setFillColor(RandomDrawDotColor);
				for (Pnt2d p : sol.getDraw()) {
					ola.addShape(p.getShape(RandoDrawDotRadius), pointStroke);
				}
			}
			
			resultImages.add(imSnap);
			sol = detector.findNext(points);
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

	
	private ByteProcessor showPointSet(Pnt2d[] points) {
		ByteProcessor bp = new ByteProcessor(W, H);
		IjUtils.drawPoints(bp, points, 255);
		bp.invertLut();
		return bp;
	}
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		params.addToDialog(gd);
		gd.addNumericField("Max. number of lines", MaxLineCount);
		gd.addNumericField("Random seed", RandomSeed);
		
		gd.addStringField("Output title", title, 16);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		params.getFromDialog(gd);
		MaxLineCount = (int) gd.getNextNumber();
		RandomSeed = (int) gd.getNextNumber();
		title = gd.getNextString();
		
		return params.validate();
	}

}

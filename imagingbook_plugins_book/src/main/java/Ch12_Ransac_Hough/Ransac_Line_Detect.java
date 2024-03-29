/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch12_Ransac_Hough;

import Ch12_Ransac_Hough.settings.RansacDrawSettings;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.ImagesToStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.IjUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.ransac.RansacLineDetector;
import imagingbook.common.ransac.RansacResult;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import java.util.ArrayList;
import java.util.List;

import static imagingbook.common.ij.DialogUtils.addToDialog;
import static imagingbook.common.ij.DialogUtils.getFromDialog;
import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * RANSAC line detection using imagingbook library class {@link RansacLineDetector} (see Sec. 12.1.2 of [1] for
 * details). If no image is currently open, the plugin optionally loads a suitable sample image.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/10/03
 */
public class Ransac_Line_Detect implements PlugInFilter, RansacDrawSettings, JavaDocHelp {
	
	private static RansacLineDetector.Parameters params = new RansacLineDetector.Parameters();
	static {
		params.randomSeed = 17;
	}
	
	private static int MaxLineCount = 6;
	
	// --------------------
	
	private int W, H;
	private ImagePlus im;
	private String title;

	/**
	 * Constructor, asks to open a predefined sample image if no other image
	 * is currently open.
	 */
	public Ransac_Line_Detect() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(GeneralSampleImage.NoisyLines);
		}
	}
	
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
		List<RansacResult<AlgebraicLine>> lines = new ArrayList<>();

		// ---------------------------------------------------------------
		RansacLineDetector detector = new RansacLineDetector();
		// ---------------------------------------------------------------
		
		List<ImagePlus> resultImages = new ArrayList<>();
		int cnt = 0;
		
		RansacResult<AlgebraicLine> sol = detector.detectNext(points);
		while (sol != null && cnt < MaxLineCount) {
			lines.add(sol);
			cnt = cnt + 1;
			
			ImagePlus imSnap = new ImagePlus("line-"+cnt, showPointSet(points));
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter();

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

			imSnap.setOverlay(ola.getOverlay());
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

	private ByteProcessor showPointSet(Pnt2d[] points) {
		ByteProcessor bp = new ByteProcessor(W, H);
		IjUtils.drawPoints(bp, points, 255);
		bp.invertLut();
		return bp;
	}
	
	// ------------------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		addToDialog(params, gd);
		gd.addNumericField("Max. number of lines", MaxLineCount);
		
		gd.addStringField("Output title", title, 16);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		getFromDialog(params, gd);
		MaxLineCount = (int) gd.getNextNumber();
		title = gd.getNextString();
		
		return params.validate();
	}

}

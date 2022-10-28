/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

package More_;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Locale;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.iterate.CssColorSequencer;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.fd.FourierDescriptor;
import imagingbook.common.geometry.fd.FourierDescriptorUniform;
import imagingbook.common.geometry.fd.Utils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.math.Complex;
import imagingbook.common.regions.Contour;
import imagingbook.common.regions.ContourTracer;
import imagingbook.common.regions.RegionContourSegmentation;

/**
 * <p>
 * This ImageJ plugin visualizes the composition of 2D shapes by superposition
 * of nested ellipses, corresponding to complex coefficient pairs of elliptic
 * Fourier descriptors.
 * </p>
 * <p>
 * The plugin assumes that the input image is binary (of type
 * {@link ByteProcessor}). It is segmented and the outer contour of the largest
 * connected component is used to calculate a Fourier descriptor (of type
 * {@link FourierDescriptorUniform}) with a user-defined number of coefficient
 * pairs. The plugin then displays a sequence of frames illustrating the
 * reconstruction of the shape by superposition of nested ellipses as the path
 * parameter (t) runs from 0 to 1. See Sec. 26.3.6 (esp. Fig. 26.12) of [1] for
 * details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction Using Java</em>, 2nd ed, Springer (2016).
 * </p>
 * @author WB
 * @version 2022/10/28
 */
public class FD_Ellipse_Superposition_Demo implements PlugInFilter {
	
	private static int FourierCoefficientPairs = 3;
	
	// visualization-related settings
	private static int StepsPerFullRevolution = 500;
	private static int StepsPerSecond = 10;
	
	private static boolean ShowOriginalContour = true;
	private static boolean ShowFullReconstruction = true;
	private static boolean ShowEllipseTree = true;
	private static boolean ShowPathParameter = true;
	
	private static int ReconstructionPoints = 100;
	
	private static Color ContourColor = new Color(0, 60, 255);
	private static double ContourStrokeWidth = 0.25;
	private static double ReconstructionMarkerRadius = 3.0;
	private static Color ReconstructionColor = new Color(0, 185, 15);
	private static double ReconstructionStrokeWidth = 0.5;
	
	private static Font PathParameterFont = new Font(Font.MONOSPACED, Font.PLAIN, 10);

	
	ImagePlus im = null;
//	String origTitle = null;
	static boolean verbose = true;
	
	@Override
	public int setup(String arg, ImagePlus im) { 
    	this.im = im;
		return DOES_8G + NO_CHANGES; 
	}
	
	@Override
	public void run(ImageProcessor ip) {
		
		if (!runDialog()) {
			return;
		}
		
		ByteProcessor bp = (ByteProcessor) ip;
		Pnt2d[] contr = getLargestRegionContour(bp);
		FourierDescriptor fd = FourierDescriptorUniform.from(Utils.toComplexArray(contr), FourierCoefficientPairs);
		Complex ctr = fd.getCoefficient(0);
		
		ImagePlus imA = makeBackgroundImage();	
		imA.show();
		
		long frameTimeMs = Math.round(1000.0/StepsPerSecond);
		boolean done = false;

		while (!done) {		
			for (int k = 0; k < StepsPerFullRevolution; k++) {		
				if (IJ.escapePressed() || imA.getWindow() == null || imA.getWindow().isClosed()) {
					done = true; break;
				}
				
				double t = (double) k / StepsPerFullRevolution;	// t in [0,1]
				long startTime = System.currentTimeMillis();
				
				ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
				
				if (ShowPathParameter) {
					ola.addText(5, 0, String.format(Locale.US, "t=%.4f", t), PathParameterFont, Color.black);
				}

				if (ShowOriginalContour) {
					ola.addShape(toClosedPath(contr), new ColoredStroke(ContourStrokeWidth, ContourColor));
					ola.addShape(Pnt2d.from(ctr.re, ctr.im).getShape());
				}

				if (ShowFullReconstruction) { // draw the shape reconstructed from all FD-pairs
					Path2D rec = Utils.toPath(fd.getShapeFull(ReconstructionPoints));
					ola.addShape(rec, new ColoredStroke(ReconstructionStrokeWidth, ReconstructionColor));
				}

				if (ShowEllipseTree) {
					Complex cc = ctr;	// current ellipse center
					CssColorSequencer csq = new CssColorSequencer();
					csq.setRandomSeed(17);
					for (int m = 1; m <= FourierCoefficientPairs; m++) {
						Color ellcol = csq.next();
						
						// draw the ellipse for FD pair m:
						Shape ellipse = fd.getEllipse(m);			
						AffineTransform trans = AffineTransform.getTranslateInstance(cc.re, cc.im);
						Shape shape = trans.createTransformedShape(ellipse);
						ola.addShape(shape, new ColoredStroke(ReconstructionStrokeWidth/2, ellcol));

						// show marker for current path position for t
						Complex cNext = cc.add(fd.getShapePointPair(m, t));
						ColoredStroke stk = new ColoredStroke(ReconstructionStrokeWidth/2, ellcol);
						stk.setFillColor(ellcol);
						ola.addShape(Pnt2d.from(cNext.toArray()).getShape(ReconstructionMarkerRadius), stk);
						
						// make current point the center of the next ellipse
						cc = cc.add(cNext);
						cc = cNext;
					}
				}

				imA.setOverlay(ola.getOverlay());
				imA.updateAndDraw();
				
				// check and wait of needed:
				long elapsedTime = System.currentTimeMillis() - startTime;
				int remainingTime = (int) (frameTimeMs - elapsedTime);
				if (remainingTime > 0) {
					IJ.wait(remainingTime);
				}
			}
		}
	}

	private void brighten(ByteProcessor ip, int minGray) {
		 float scale = (255 - minGray) / 255f;
		 int[] table = new int[256];
		 for (int i=0; i<256; i++) {
			 table[i] = Math.round(minGray + scale * i);
			 
		 }
		 ip.applyTable(table);
	}

	// -------------------------------------------------------------
	
	private Pnt2d[]  getLargestRegionContour(ByteProcessor ip) {
		//  label regions and trace contours
		ContourTracer tracer = new RegionContourSegmentation(ip);
		List<? extends Contour> outerContours = tracer.getOuterContours(true);
		
		if (outerContours.isEmpty()) {
			IJ.error("no regions found");
			return null;
		}
		
		return outerContours.get(0).getPointArray();	// contour of largest region
	}
	
	private ImagePlus makeBackgroundImage() {
		ByteProcessor bp = im.getProcessor().convertToByteProcessor();
		String title = String.format("%s-partial-%03d", im.getShortTitle(), FourierCoefficientPairs);
		
		if (bp.isInvertedLut()) {
			bp.invert();
			bp.invertLut();
		}
		brighten(bp, 220);	
		return new ImagePlus(title, bp);
	}
	
	private Path2D toClosedPath(Pnt2d[] pnts) {
		Path2D path = new Path2D.Float();
		path.moveTo(pnts[0].getX(), pnts[0].getY());
		for (int i = 1; i < pnts.length; i++) {
			path.lineTo(pnts[i].getX(), pnts[i].getY());
		}
		path.closePath();
		return path;
	}
	
	// -------------------------------------------------------------
	
	private boolean runDialog() {
		GenericDialog dlg = new GenericDialog(this.getClass().getSimpleName());
		dlg.addMessage("Fourier decomposition parameters:");
		dlg.addNumericField("Number of FD pairs (min. 1)", FourierCoefficientPairs, 0);
		dlg.addNumericField("Reconstruction Points", ReconstructionPoints, 0);
		
		dlg.addMessage("Animation parameters:");
		dlg.addNumericField("Steps per full revolution", StepsPerFullRevolution, 0);
		dlg.addNumericField("Steps per second", StepsPerSecond, 0);
		
		dlg.addCheckbox("Show Original Contour", ShowOriginalContour);
		dlg.addCheckbox("Show Full Reconstruction", ShowFullReconstruction);
		dlg.addCheckbox("Show Ellipse Tree", ShowEllipseTree);
		dlg.addCheckbox("Show Path Parameter (t)", ShowPathParameter);
		
		dlg.showDialog();
		if(dlg.wasCanceled()) 
			return false;
		
		FourierCoefficientPairs = Math.max(1, (int) dlg.getNextNumber());
		ReconstructionPoints = (int) dlg.getNextNumber();
		
		StepsPerFullRevolution = (int) dlg.getNextNumber();
		StepsPerSecond = (int) dlg.getNextNumber();
		
		ShowOriginalContour = dlg.getNextBoolean();
		ShowFullReconstruction = dlg.getNextBoolean();
		ShowEllipseTree = dlg.getNextBoolean();
		ShowPathParameter = dlg.getNextBoolean();
		
		dlg.addMessage("Press ESC or close window to stop animation.");
		return true;
	}
	
}
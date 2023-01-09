/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package More_;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.corners.Corner;
import imagingbook.common.corners.GradientCornerDetector.Parameters;
import imagingbook.common.corners.HarrisCornerDetector;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.delaunay.DelaunayTriangulation;
import imagingbook.common.geometry.delaunay.Triangle;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.List;

import static imagingbook.common.ij.IjUtils.noCurrentImage;


/**
 * This ImageJ plugin performs corner detection on the active image, applies Delaunay triangulation to the N
 * strongest corners and displays the result as a vector overlay on top of the same image. If no image is currently
 * open, the user is asked to load a predefined sample image.
 *
 * @author WB
 * @version 2022/06/25
 * @see HarrisCornerDetector
 * @see DelaunayTriangulation
 */
public class Delaunay_Demo implements PlugInFilter, JavaDocHelp {
	
	private static ImageResource SampleImage = GeneralSampleImage.MortarSmall;
	
	private static int CornerCount = 0;					// number of corners to show (0 = show all)
	private static boolean ShuffleCorners = true;	
	private static Color DelaunayColor = Color.green;	// color for graph edges
	private static Color PointColor = Color.red;		// color for point markers
	private static double StrokeWidth = 0.25;
	private static double PointRadius = 1.0;
	
	private static Parameters params = new Parameters();
	
	private ImagePlus im = null;

	/**
	 * Constructor, asks to open a predefined sample image if no other image is currently open.
	 */
	public Delaunay_Demo() {
		if (noCurrentImage()) {
			DialogUtils.askForSampleImage(SampleImage);
		}
	}
	
    @Override
	public int setup(String arg, ImagePlus im) {
    	this.im = im;
        return DOES_ALL;
    }
    
    @Override
	public void run(ImageProcessor ip) {
    	
		if (!showDialog()) {
			return;
		}
		
		HarrisCornerDetector cd = new HarrisCornerDetector(ip, params);
		List<Corner> corners = cd.getCorners();

		DelaunayTriangulation dt = DelaunayTriangulation.from(corners, ShuffleCorners);
		im.setOverlay(makeOverlay(dt));
    }
    
	// ---------------------------------------------------------------------------
	
	private Overlay makeOverlay(DelaunayTriangulation dt) {
		List<Triangle> triangles = dt.getTriangles();
		List<Pnt2d> allPoints = dt.getPoints();

		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();

		// draw the triangles:
		ColoredStroke triangleStroke = new ColoredStroke(StrokeWidth, DelaunayColor);
		triangleStroke.setEndCap(BasicStroke.CAP_ROUND);
		triangleStroke.setLineJoin(BasicStroke.JOIN_ROUND);
		
		ola.setStroke(triangleStroke);
		for (Triangle trgl : triangles) {
			 ola.addShape(trgl.getShape());
		}
		
		// draw the vertices of the triangulation:
		ColoredStroke pointStroke = new ColoredStroke(StrokeWidth, PointColor);
		ola.setStroke(pointStroke);
		for (Pnt2d p : allPoints) {
			// get original Pnt2d shape (not Corner shape)
			ola.addShape(Pnt2d.from(p).getShape(PointRadius));
		}

		return ola.getOverlay();
	}
	
	// ---------------------------------------------------------------------------
	
	private boolean showDialog() {
		GenericDialog dlg = new GenericDialog(this.getClass().getSimpleName());
		dlg.addNumericField("Corner score threshold", params.scoreThreshold, 0);
		dlg.addNumericField("Border width", params.border, 0);
		dlg.addCheckbox("Clean up corners", params.doCleanUp);
		dlg.addNumericField("Corners to show (0 = show all)", CornerCount, 0);
		dlg.addCheckbox("Shuffle corners", ShuffleCorners);
		
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;	
		
		params.scoreThreshold = (int) dlg.getNextNumber();
		params.border = (int) dlg.getNextNumber();
		params.doCleanUp = dlg.getNextBoolean();
		CornerCount = (int) dlg.getNextNumber();
		ShuffleCorners = dlg.getNextBoolean();
		
		if(dlg.invalidNumber() || !params.validate()) {
			IJ.error("Input Error", "Invalid input");
			return false;
		}	
		return true;
	}
}

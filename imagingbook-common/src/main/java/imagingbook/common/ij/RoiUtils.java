/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.ij;

import java.awt.Polygon;

import ij.gui.EllipseRoi;
import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.RotatedRectRoi;
import ij.process.FloatPolygon;
import imagingbook.common.geometry.basic.Pnt2d;

/**
 * This class defines static ROI-related utility methods
 * to interface with ImageJ.
 * 
 * @author WB
 *
 */
public class RoiUtils {
	
	private RoiUtils() {}
	
	/**
	 * Retrieves the outline of the specified ROI as an array of {@link Pnt2d}
	 * points with {@code int} coordinates. Note that unless the ROI is of type
	 * {@link PolygonRoi} or {@link PointRoi} only the corner points of the bounding
	 * box are returned. Interpolated contour points are returned for a instance of
	 * {@link OvalRoi}.
	 * 
	 * @param roi the ROI
	 * @return the ROI's polygon coordinates
	 */
	public static Pnt2d[] getOutlinePointsInt(Roi roi) {
		Polygon pgn = roi.getPolygon();
		Pnt2d[] pts = new Pnt2d[pgn.npoints];
		for (int i = 0; i < pgn.npoints; i++) {
			pts[i] = Pnt2d.PntInt.from(pgn.xpoints[i], pgn.ypoints[i]);
		}
		return pts;
	}
	
	/**
	 * Retrieves the outline of the specified ROI as an array of {@link Pnt2d}
	 * points with {@code double} coordinates. Returned coordinates are measured
	 * relative to the pixel center, e.g., (5.0, 3.0) is assumed to be in the middle
	 * of pixel (5, 3). This method retrieves ROI points with
	 * {@link Roi#getFloatPolygon()} but applies type-dependent correction for
	 * consistent point rendering.
	 * 
	 * @param roi the {@link Roi}
	 * @return the ROI's outline coordinates
	 */
	public static Pnt2d[] getOutlinePointsFloat(Roi roi) {
		final double offset = (needsOffset(roi)) ? -0.5 : 0.0;
		FloatPolygon pgn = roi.getFloatPolygon();
		Pnt2d[] pts = new Pnt2d[pgn.npoints];
		for (int i = 0; i < pgn.npoints; i++) {
			pts[i] = Pnt2d.PntDouble.from(pgn.xpoints[i] + offset, pgn.ypoints[i] + offset);
		}
		return pts;
	}
	
	/**
	 * Returns true if the given {@link Roi} instance requires a half-pixel
	 * offset applied to the points returned {@code Roi#getFloatPolygon()}
	 * for proper rendering. This fixes a problem (bug) in ImageJ to make
	 * sure that all ROI types render consistently. This is the situation
	 * (as of IJ 1.53u):
	 * - Line, FreehandLine, SegmentLine, Point, MultiPoint: no offset needed (OK),
	 * - Elliptic, Oval, Polygon, Rectangle, RotRectangle: 0.5 pix offset needed.
	 * 
	 * @param roi a ROI instance
	 * @return true if offset needed
	 */
	private static boolean needsOffset(Roi roi) {
		int type = roi.getType();
		if (roi instanceof EllipseRoi) return true;
		if (roi instanceof OvalRoi) return true;
		if (roi instanceof PolygonRoi && type == Roi.POLYGON) return true;
		if (roi instanceof Roi && type == Roi.RECTANGLE) return true;
		if (roi instanceof RotatedRectRoi) return true;
		return false;
	}
	
	/**
	 * Converts an array of 2D points (of type {@link Pnt2d}) to
	 * a {@link PointRoi} instance.
	 * 
	 * @param points v
	 * @return a {@link PointRoi} instance
	 */
	public static PointRoi toPointRoi(Pnt2d[] points) {
		PointRoi roi = new PointRoi();
		for (Pnt2d p : points) {
			roi.addPoint(p.getX(), p.getY());
		}
		return roi;
	}
	
}

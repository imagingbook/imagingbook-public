/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.ij;

import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.FloatPolygon;
import imagingbook.common.geometry.basic.Pnt2d;

import java.awt.Polygon;

/**
 * This class defines static ROI-related utility methods to interface with ImageJ.
 *
 * @author WB
 * @version 2022/09/22
 */
public class RoiUtils {
	
	private RoiUtils() {}

	/**
	 * Retrieves the outline of the specified ROI as an array of {@link Pnt2d} points with {@code int} coordinates. Note
	 * that unless the ROI is of type {@link PolygonRoi} or {@link PointRoi} only the corner points of the bounding box
	 * are returned. Interpolated contour points are returned for a instance of {@link OvalRoi}.
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
	 * Retrieves the outline of the specified ROI as an array of {@link Pnt2d} points with {@code double} coordinates.
	 * Returned coordinates are measured relative to the pixel center, e.g., (5.0, 3.0) is assumed to be in the middle
	 * of pixel (5, 3). This method retrieves ROI points with {@link Roi#getFloatPolygon()} but applies type-dependent
	 * correction for consistent point rendering.
	 *
	 * @param roi the {@link Roi}
	 * @return the ROI's outline coordinates
	 */
	public static Pnt2d[] getOutlinePointsFloat(Roi roi) {
//		final double offset = (isAreaRoi(roi)) ? -0.5 : 0.0; // replaced by Roi#isLineOrPoint() with IJ 1.53v
		final double offset = (roi.isLineOrPoint()) ? 0.0 : -0.5; // shift area rois to pixel centers
		FloatPolygon poly = roi.getFloatPolygon();
		return getPoints(poly, offset);
	}

	/**
	 * Extracts the points of an ImageJ {@link FloatPolygon}.
	 *
	 * @param poly the original polygon
	 * @param offset x/y offset to add to coordinates (typically -0.5 for "area ROIs")
	 * @return the polygon's vertex points
	 */
	public static Pnt2d[] getPoints(FloatPolygon poly, double offset) {
		Pnt2d[] pts = new Pnt2d[poly.npoints];
		for (int i = 0; i < poly.npoints; i++) {
			pts[i] = Pnt2d.from(poly.xpoints[i] + offset, poly.ypoints[i] + offset);
		}
		return pts;
	}

	/**
	 * Extracts the points of an ImageJ {@link FloatPolygon}. Calls {@link #getPoints(FloatPolygon, double)} with zero
	 * offset.
	 *
	 * @param poly the original polygon
	 * @return the polygon's vertex points
	 */
	public static Pnt2d[] getPoints(FloatPolygon poly) {
		return getPoints(poly, 0.0);
	}

	/**
	 * Converts an array of 2D points (of type {@link Pnt2d}) to a {@link PointRoi} instance.
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

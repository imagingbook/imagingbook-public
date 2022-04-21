package imagingbook.common.ij;

import java.awt.Polygon;

import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
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
	
	/**
	 * Retrieves the outline of the specified ROI as an
	 * array of {@link Pnt2d} points with {@code int}
	 * coordinates. Note that unless the ROI is of type
	 * {@link PolygonRoi} or {@link PointRoi} only the corner points of the
	 * bounding box are returned.
	 * Interpolated contour points are returned for a instance of {@link OvalRoi}.
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
	 * Retrieves the outline of the specified ROI as an
	 * array of {@link Pnt2d} points with {@code int}
	 * coordinates. Note that unless the ROI is of type
	 * {@link PolygonRoi} or {@link PointRoi} only the corner points of the
	 * bounding box are returned.
	 * Interpolated contour points are returned for a instance of {@link OvalRoi}.
	 * 
	 * @param roi the ROI
	 * @return the ROI's polygon coordinates
	 */
	public static Pnt2d[] getOutlinePointsFloat(Roi roi) {
		FloatPolygon pgn = roi.getFloatPolygon();
		Pnt2d[] pts = new Pnt2d[pgn.npoints];
		for (int i = 0; i < pgn.npoints; i++) {
			pts[i] = Pnt2d.PntDouble.from(pgn.xpoints[i], pgn.ypoints[i]);
		}
		return pts;
	}
	
	public static PointRoi toPointRoi(Pnt2d[] points) {
		PointRoi roi = new PointRoi();
		for (Pnt2d p : points) {
			roi.addPoint(p.getX(), p.getY());
		}
		return roi;
	}
	
}

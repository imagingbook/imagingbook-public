package imagingbook.common.ij;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.RotatedRectRoi;
import imagingbook.common.geometry.basic.Pnt2d;

public class RoiUtilsTest {

	@Test
	public void test1() {
		double TOL = 1e-6;
		double x1 = 4.0, y1 = 5.2, x2 = 17.9, y2 = -3.5;
		
		Roi roi = new Line(x1, y1, x2, y2);
		Pnt2d[] pts = RoiUtils.getOutlinePointsFloat(roi);
//		System.out.println("V=" + Arrays.toString(pts));
		
		assertTrue(pts[0].equals(Pnt2d.from(x1, y1), TOL));
		assertTrue(pts[1].equals(Pnt2d.from(x2, y2), TOL));
	}
	
	@Test
	public void testAreaRois() {
		Roi[] rois = {
			new EllipseRoi(3, 5, 7, 2, 1),
			new OvalRoi(3, 5, 7, 2),
			new PolygonRoi((int[]) null, (int[]) null, 0, Roi.POLYGON),
			new Roi(2, 5, 1, 7),
			new RotatedRectRoi(3, 5, 7, 2, 1),
		};
		
		for (Roi roi : rois) {
			assertTrue(roi.toString(), isAreaRoi(roi));
			assertFalse(roi.toString(), roi.isLineOrPoint());
		}
	}
	
	@Test
	public void testLinePointRois() {
		Roi[] rois = {
			new PolygonRoi((int[]) null, (int[]) null, 0, Roi.POLYLINE),
			new PolygonRoi((int[]) null, (int[]) null, 0, Roi.POLYLINE),
			new PolygonRoi((int[]) null, (int[]) null, 0, Roi.FREELINE),
//			new PolygonRoi((int[]) null, (int[]) null, 0, Roi.FREEROI),	// not sure if line or area?
		};
		
		for (Roi roi : rois) {
			assertFalse(roi.toString(), isAreaRoi(roi));
			assertTrue(roi.toString(), roi.isLineOrPoint());
		}
	}

	
	//------------------------------------------------------------------
	
	/**
	 * <p>
	 * Moved from RoiUtils:
	 * Returns true if the given {@link Roi} is an "area ROI", that is, coordinates
	 * returned by returned {@code Roi#getFloatPolygon()} are referenced to the
	 * top-left corner of pixels. In contrast, integer coordinates of "line ROIs"
	 * are positioned at pixel centers.
	 * </p>
	 * <p>
	 * Area selections: EllipseRoi, OvalRoi, polygon
	 * (PolygonRoi with type = Roi.POLYGON), rectangle (Roi with
	 * type = Roi.RECTANGLE), RotRectangle.<br>
	 * Line selections: Line, FreehandLine, SegmentLine, Point, MultiPoint.
	 * </p>
	 * 
	 * @param roi a ROI instance
	 * @return true if the ROI is an area selection
	 */
	public static boolean isAreaRoi(Roi roi) {	// TODO: replace by roi.isLineOrPoint() when available
		int type = roi.getType();
		if (roi instanceof EllipseRoi) return true;
		if (roi instanceof OvalRoi) return true;
		if (roi instanceof PolygonRoi && type == Roi.POLYGON) return true;
		if (roi instanceof Roi && type == Roi.RECTANGLE) return true;
		if (roi instanceof RotatedRectRoi) return true;
		return false;
	}
}

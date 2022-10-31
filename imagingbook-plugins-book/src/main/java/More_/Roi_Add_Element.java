package More_;

import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.ij.RoiUtils;


/**
 * This is for testing placement or ROI points.
 * 
 * - Line, FreehandLine, SegmentLine, Point, MultiPoint: no offset (OK)
 * - Elliptic, Oval, Polygon, Rectangle, RotRectangle: 0.5 pix offset too much
 * 
 * Damn - this is confusing: Polygons and FreehandRois behave differently
 * depending on which menu is used to draw them!!
 * 
 * @author WB
 *
 */
public class Roi_Add_Element implements PlugInFilter {

	ImagePlus im;
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		
//		Roi roi = new EllipseRoi(1.5, 5.2, 7.9, 5.2, 0.75);
		EllipseRoi roi = new EllipseRoi(1.5, 5.2, 7.9, 3.2, 0.75);

		im.setRoi(roi, true);
		
//		Roi roi = im.getRoi();
//		IJ.log("roi = " + roi.getClass() + " type = " + roi.getType() + " = " + roi.getTypeAsString());
//		
//		Pnt2d[] V = getOutlinePointsFloat(roi);
//		IJ.log("V=" + Arrays.toString(V));
//		
//		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
//		ColoredStroke stroke = new ColoredStroke(0.05, Color.red);
//		for (Pnt2d p : V) {
//			ola.addShape(p.getShape(0.25), stroke);
//		}
//		
//		im.setOverlay(ola.getOverlay());
	}

	
	public static Pnt2d[] getOutlinePointsFloat(Roi roi) {
		return RoiUtils.getOutlinePointsFloat(roi);
	}
	
//	/**
//	 * - Line, FreehandLine, SegmentLine, Point, MultiPoint: no offset needed (OK)
//	 * - Elliptic, Oval, Polygon, Rectangle, RotRectangle: 0.5 pix offset too much
//	 * @param roi
//	 * @return
//	 */
//	private static boolean needsOffset(Roi roi) {
//		int type = roi.getType();
//		if (roi instanceof EllipseRoi) return true;
//		if (roi instanceof OvalRoi) return true;
//		if (roi instanceof PolygonRoi && type == Roi.POLYGON) return true;
//		if (roi instanceof Roi && type == Roi.RECTANGLE) return true;
//		if (roi instanceof RotatedRectRoi) return true;
//		return false;
//	}
}

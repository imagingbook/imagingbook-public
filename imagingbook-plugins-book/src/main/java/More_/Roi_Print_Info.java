package More_;

import java.awt.Color;
import java.util.Arrays;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.ij.RoiUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;


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
public class Roi_Print_Info implements PlugInFilter {

	ImagePlus im;
	
	@Override
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		Roi roi = im.getRoi();
		IJ.log("roi = " + roi.getClass() + " type = " + roi.getType() + " = " + roi.getTypeAsString());
		
		Pnt2d[] V = RoiUtils.getOutlinePointsFloat(roi);
		IJ.log("V=" + Arrays.toString(V));
		
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		ColoredStroke stroke = new ColoredStroke(0.05, Color.red);
		for (Pnt2d p : V) {
			ola.addShape(p.getShape(0.25), stroke);
		}
		
		im.setOverlay(ola.getOverlay());
	}

}

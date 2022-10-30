package imagingbook.common.ij;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.gui.Line;
import ij.gui.Roi;
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

}

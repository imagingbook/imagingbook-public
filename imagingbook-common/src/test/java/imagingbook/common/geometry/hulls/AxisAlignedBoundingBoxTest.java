package imagingbook.common.geometry.hulls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;

public class AxisAlignedBoundingBoxTest {

	@Test
	public void test1() {
		List<Pnt2d> points = Arrays.asList(
				Pnt2d.from(2, 5),
				Pnt2d.from(5, 4),
				Pnt2d.from(5, 7),
				Pnt2d.from(7, 6),
				Pnt2d.from(9, 9),
				Pnt2d.from(6, 2)
				);
		
		runPointTest(points);
	}
	
	private void runPointTest(Iterable<Pnt2d> points) {
		AxisAlignedBoundingBox box = new AxisAlignedBoundingBox(points);
		
		Pnt2d[] corners = box.getCornerPoints();
		assertNotNull(corners);
		assertEquals(4, corners.length);
//		System.out.println(Arrays.toString(corners));
		
		Path2D poly = box.getShape(1);
		
		// check if all sample points are inside the bounding box
		for (Pnt2d p : points) {
			assertTrue(poly.contains(p.getX(), p.getY()));
		}
	}

}

package imagingbook.common.geometry.hulls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;

public class AxisAlignedBoundingBoxTest {

	@Test
	public void test1() {
		Pnt2d[] points = {
				Pnt2d.from(2, 5),
				Pnt2d.from(5, 4),
				Pnt2d.from(5, 7),
				Pnt2d.from(7, 6),
				Pnt2d.from(9, 9),
				Pnt2d.from(6, 2)
		};
		
		runPointTest(points);
	}
	
	@Test
	public void test2() {
		int N = 100; 	// number of random points
		int K = 100;	// number of tries
		Random rg = new Random(17);
		Pnt2d[] pointArray = new Pnt2d[N];
		
		for (int k = 0; k < K; k++) {
			for (int i = 0; i < N; i++) {
				pointArray[i] = Pnt2d.from(100 * rg.nextDouble(), 100 * rg.nextDouble());
			}
			runPointTest(pointArray);
		}
	}
	
	private static void runPointTest(Pnt2d[] points) {
		AxisAlignedBoundingBox box = new AxisAlignedBoundingBox(points);
		
		Pnt2d[] corners = box.getCornerPoints();
		assertNotNull(corners);
		assertEquals(4, corners.length);
		
		// check if all sample points are inside the bounding box
		for (Pnt2d p : points) {
			assertTrue("point not contained in bounding box: " + p, box.contains(p));
		}
	}

}

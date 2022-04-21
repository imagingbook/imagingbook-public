package imagingbook.common.geometry.line;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

public class HessianLineTest {
	
	static Pnt2d p1 = PntInt.from(30, 10);
	static Pnt2d p2 = PntInt.from(200, 100);
	static Pnt2d p3 = PntInt.from(90, 40);

	@Test
	public void test1() {
		// example from CV lecture notes
		HessianLine h12 = HessianLine.fromPoints(p1, p2);
		
		Assert.assertEquals(0.0, h12.getDistance(p1), 1E-6);						// x1 is actually ON the line
		Assert.assertEquals(0.0, h12.getDistance(p2), 1E-6);						// x1 is actually ON the line
		
		Assert.assertEquals(-0.4678877204190327, h12.getA(), 1E-6);
		Assert.assertEquals(0.8837879163470618, h12.getB(), 1E-6);
		Assert.assertEquals(5.198752449100363, h12.getC(), 1E-6);
		
		Assert.assertEquals(2.0576955586061656, h12.getAngle(), 1E-6);
		Assert.assertEquals(-5.198752449100363, h12.getRadius(), 1E-6);
		
//		System.out.println("h12 = " + h12.toString());
//		System.out.println("a = " + h12.getA());
//		System.out.println("b = " + h12.getB());
//		System.out.println("c = " + h12.getC());
//		System.out.println("alpha = " + h12.getAngle());
//		System.out.println("rad = " + h12.getRadius());
	}
	
	@Test
	public void test2() {
		double angle = 0.2;
		double radius = 80;
		HessianLine hl1 = new HessianLine(angle, radius);
		HessianLine hl2 = new HessianLine(hl1);
		Assert.assertEquals(angle, hl2.getAngle(), 1E-6);
		Assert.assertEquals(radius, hl2.getRadius(), 1E-6);
	}
	
	@Test
	public void test3() {
		HessianLine l12 = HessianLine.fromPoints(p1, p2);
		Pnt2d x0 = l12.getClosestLinePoint(p3);
		Assert.assertEquals(0.0, l12.getDistance(x0), 1E-6);						// x0 is actually ON the line
		Assert.assertEquals(p3.distance(x0), Math.abs(l12.getDistance(p3)), 1E-6);	// distance (p3,x0) is shortest 
	}

}

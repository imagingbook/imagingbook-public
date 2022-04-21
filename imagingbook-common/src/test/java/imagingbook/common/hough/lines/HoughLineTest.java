package imagingbook.common.hough.lines;


import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.line.AlgebraicLine;

public class HoughLineTest {
	
	static Pnt2d p1 = PntInt.from(30, 10);
	static Pnt2d p2 = PntInt.from(200, 100);
	static Pnt2d p3 = PntInt.from(90, 40);
	
	static Pnt2d pRef = PntInt.from(70, 50);
	
	@Test
	public void test0() {
		// example from CV lecture notes
		Pnt2d xRef = PntInt.from(90, 60);
		AlgebraicLine h12 = AlgebraicLine.from(p1, p2);
	
		HoughLine L12 = new HoughLine(h12, xRef.getX(), xRef.getY(), 0);
		Assert.assertEquals(0.0, L12.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L12.getDistance(p2), 1E-6);
	}

	@Test
	public void test1() {
		HoughLine L12 = HoughLine.fromPoints(p1, p2, pRef, 2);
		HoughLine L21 = HoughLine.fromPoints(p2, p1, pRef, 2);
		
		Assert.assertEquals(0.0, L12.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L12.getDistance(p2), 1E-6);
		
		Assert.assertEquals(0.0, L21.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L21.getDistance(p2), 1E-6);
	}
	
	@Test
	public void test2() {
		HoughLine l12 = HoughLine.fromPoints(p1, p2, pRef, 2);
		Pnt2d x0 = l12.getClosestLinePoint(p3);
		Assert.assertEquals(0.0, l12.getDistance(x0), 1E-6);						// x0 is actually ON the line
		Assert.assertEquals(p3.distance(x0), Math.abs(l12.getDistance(p3)), 1E-6);	// distance (p3,x0) is shortest 
	}
	
	@Test
	public void test3() {
		// lA, lH1 lH2 must be the same lines:
		AlgebraicLine LA = AlgebraicLine.from(p1, p2);
		HoughLine L1 = HoughLine.fromPoints(p1, p2, pRef, 2);
		HoughLine L2 = new HoughLine(LA, pRef.getX(), pRef.getY(), 2);
		
		Assert.assertEquals(0.0, LA.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, LA.getDistance(p2), 1E-6);
		
		Assert.assertEquals(0.0, L1.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L1.getDistance(p2), 1E-6);
		
		Assert.assertEquals(0.0, L2.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L2.getDistance(p2), 1E-6);
	}
	
	@Test
	public void test4() {
		// create a HoughLine from two points
		HoughLine L1 = HoughLine.fromPoints(p1, p2, pRef, 2);
		// check if both points are on the line
		Assert.assertEquals(0.0, L1.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L1.getDistance(p2), 1E-6);
		
		// create a duplicate HoughLine from L1's angle and radius
		HoughLine L2 = new HoughLine(L1.getAngle(), L1.getRadius(), pRef.getX(), pRef.getY(), 2);
		// check if the two points are on this line too
		Assert.assertEquals(0.0, L2.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L2.getDistance(p2), 1E-6);
		
		// create a duplicate HoughLine directly from L1 (using the same reference point)
		HoughLine L3 = new HoughLine(L1, pRef.getX(), pRef.getY(), 2);
		// check if the two points are on this line too
		Assert.assertEquals(0.0, L3.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L3.getDistance(p2), 1E-6);
		
		// create a duplicate HoughLine directly from L1 (using a different reference point)
		HoughLine L4 = new HoughLine(L1, pRef.getX() - 10, pRef.getY() + 15, 2);
		// check if the two points are on this line too
		Assert.assertEquals(0.0, L4.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, L4.getDistance(p2), 1E-6);
	}
	
	@Test
	public void test5() { // Example is used in CV lecture notes
		// create L1 (zero-referenced):
		double a1 = -90, b1 = 170, c1 = 1000;
		AlgebraicLine L1 = new AlgebraicLine(a1, b1, c1);
		
		// create L2 (with specific reference point):
		double xR = 10, yR = 50;
		double a2 = a1, b2 = b1, c2 = c1 + a1 * xR + b1 * yR;
		HoughLine L2 = new HoughLine(a2, b2, c2, xR, yR, 0);
		
		// make sure both are "equivalent" (contain the same points (x,y))
		Assert.assertTrue(L1.equals(L2));
		Assert.assertTrue(L2.equals(L1));
	}

}

package imagingbook.common.geometry.line;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;


public class AlgebraicLineTest {
	
	static Pnt2d p1 = PntInt.from(30, 10);
	static Pnt2d p2 = PntInt.from(200, 100);
	static Pnt2d p3 = PntInt.from(90, 40);

	@Test
	public void test1() {
		AlgebraicLine l12 = AlgebraicLine.from(p1, p2);		
		AlgebraicLine l21 = AlgebraicLine.from(p2, p1);
		
		Assert.assertEquals(0.0, l12.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, l12.getDistance(p2), 1E-6);
		
		Assert.assertEquals(0.0, l21.getDistance(p1), 1E-6);
		Assert.assertEquals(0.0, l21.getDistance(p2), 1E-6);
	}
	
	@Test
	public void test2() {
		AlgebraicLine l12 = AlgebraicLine.from(p1, p2);
		Pnt2d x0 = l12.getClosestLinePoint(p3);
		Assert.assertEquals(0.0, l12.getDistance(x0), 1E-6);						// x0 is actually ON the line
		Assert.assertEquals(p3.distance(x0), Math.abs(l12.getDistance(p3)), 1E-6);	// distance (p3,x0) is shortest 
	}

	@Test
	public void test3() {
		AlgebraicLine L1 = new AlgebraicLine(10, 15, -2);
		AlgebraicLine L2 = new AlgebraicLine(L1);
		Assert.assertTrue(L1.equals(L2));
		Assert.assertTrue(L2.equals(L1));
	}
	
	@Test
	public void test4() {
		AlgebraicLine L1 = new AlgebraicLine(10, 0, -2);
		AlgebraicLine L2 = new AlgebraicLine(-10, 0, 2);
		Assert.assertTrue(L1.equals(L2));
		Assert.assertTrue(L2.equals(L1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test5() {
		new AlgebraicLine(0, 0, -2);
	}
}

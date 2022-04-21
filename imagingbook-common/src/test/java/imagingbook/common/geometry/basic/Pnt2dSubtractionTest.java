package imagingbook.common.geometry.basic;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

// Subtract points from points.
public class Pnt2dSubtractionTest {

	static double DELTA = 1E-6;

	@Test
	public void testSubtractIntInt() {
		// int + int points must create another int point
		Pnt2d p1 = PntInt.from( 3, 8);
		Pnt2d p2 = PntInt.from(-2, 7);
		Pnt2d p3 = p1.minus(p2);
		Assert.assertTrue(p3 instanceof PntInt);
		Assert.assertEquals(5, ((PntInt)p3).x);
		Assert.assertEquals(1, ((PntInt)p3).y);
	}

	@Test
	public void testSubtractDoubleDouble() {
		// double + double points must create another double point
		Pnt2d p1 = PntDouble.from( 3, 8);
		Pnt2d p2 = PntDouble.from(-2, 7);
		Pnt2d p3 = p1.minus(p2);
		Assert.assertTrue(p3 instanceof PntDouble);
		Assert.assertEquals(5, p3.getX(), DELTA);
		Assert.assertEquals(1, p3.getY(), DELTA);
	}
	
	@Test
	public void testSUbtractDoubleInt() {
		// adding double + int point a double point
		Pnt2d p1 = PntDouble.from( 3, 8);
		Pnt2d p2 = PntInt.from(-2, 7);
		Pnt2d p3 = p1.minus(p2);
		Assert.assertTrue(p3 instanceof PntDouble);
		Assert.assertEquals(5, p3.getX(), DELTA);
		Assert.assertEquals(1, p3.getY(), DELTA);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testSubtractIntDouble() {
		// adding int + double throws an exception
		Pnt2d p1 = PntInt.from( 3, 8);
		Pnt2d p2 = PntDouble.from(-2, 7);
		@SuppressWarnings("unused")
		Pnt2d p3 = p1.minus(p2);
	}
}

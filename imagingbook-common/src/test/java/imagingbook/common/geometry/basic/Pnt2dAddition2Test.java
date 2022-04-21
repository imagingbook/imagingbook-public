package imagingbook.common.geometry.basic;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

// Adding discrete coordinates to points.
public class Pnt2dAddition2Test {

	static double DELTA = 1E-6;

	@Test
	public void testAdditionIntInt() {
		// int + int points must create another int point
		Pnt2d p1 = PntInt.from( 3, 8);
		Pnt2d p3 = p1.plus(-2, 7);
		Assert.assertTrue(p3 instanceof PntInt);
		Assert.assertEquals(1, ((PntInt)p3).x);
		Assert.assertEquals(15, ((PntInt)p3).y);
	}

	@Test
	public void testAdditionDoubleDouble() {
		// double + double points must create another double point
		Pnt2d p1 = PntDouble.from( 3, 8);
		Pnt2d p3 = p1.plus(-2.0, 7.0);
		Assert.assertTrue(p3 instanceof PntDouble);
		Assert.assertEquals(1, p3.getX(), DELTA);
		Assert.assertEquals(15, p3.getY(), DELTA);
	}
	
	@Test
	public void testAdditionDoubleInt() {
		// adding double + int point a double point
		Pnt2d p1 = PntDouble.from( 3, 8);
		Pnt2d p3 = p1.plus(-2, 7);
		Assert.assertTrue(p3 instanceof PntDouble);
		Assert.assertEquals(1, p3.getX(), DELTA);
		Assert.assertEquals(15, p3.getY(), DELTA);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testAdditionIntDouble() {
		// adding int + double throws an exception
		Pnt2d p1 = PntInt.from( 3, 8);
		@SuppressWarnings("unused")
		Pnt2d p3 = p1.plus(-2.0, 7.0);
	}
}

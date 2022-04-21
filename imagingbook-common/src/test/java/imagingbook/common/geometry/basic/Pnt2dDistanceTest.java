package imagingbook.common.geometry.basic;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

// Testing distance calculations
public class Pnt2dDistanceTest {

	static double DELTA = 1E-6;

	@Test
	public void testDistanceIntInt() {
		// int + int points
		Pnt2d p1 = PntInt.from( 3, 8);
		Pnt2d p2 = PntInt.from(-2, 7);
		double dist = 5.0990195;
		Assert.assertEquals(dist, p1.distance(p2), DELTA);
		Assert.assertEquals(dist, p2.distance(p1), DELTA);
	}

	@Test
	public void testDistanceDoubleDouble() {
		// double + double points 
		Pnt2d p1 = PntDouble.from( 3, 8);
		Pnt2d p2 = PntDouble.from(-2, 7);
		double dist = 5.0990195;
		Assert.assertEquals(dist, p1.distance(p2), DELTA);
		Assert.assertEquals(dist, p2.distance(p1), DELTA);
	}
	
	@Test
	public void testDistanceDoubleInt() {
		// adding double + int point a double point
		Pnt2d p1 = PntDouble.from( 3, 8);
		Pnt2d p2 = PntInt.from(-2, 7);
		double dist = 5.0990195;
		Assert.assertEquals(dist, p1.distance(p2), DELTA);
		Assert.assertEquals(dist, p2.distance(p1), DELTA);
	}
	
	
	@Test
	public void testL1DistanceDouble() {
		Pnt2d p1 = PntDouble.from( 3, 8);
		Pnt2d p2 = PntDouble.from(-2, 7);
		double dist = 6;
		Assert.assertEquals(dist, p1.distL1(p2), DELTA);
		Assert.assertEquals(dist, p2.distL1(p1), DELTA);
	}
	
	
	@Test
	public void testL1DistanceInt() {
		PntInt p1 = PntInt.from( 3, 8);
		PntInt p2 = PntInt.from(-2, 7);
		int dist = 6;
		Assert.assertEquals(dist, p1.distL1(p2));
		Assert.assertEquals(dist, p2.distL1(p1));
	}

}

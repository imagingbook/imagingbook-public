package imagingbook.common.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import imagingbook.common.math.VectorNorm.NormType;

public class VectorNormTest {
	
	private static final double TOL = 1e-6;
	
	private static final double[] x1d = { 3, -5, 0, 17 };
	private static final float[]  x1f = { 3, -5, 0, 17 };
	private static final int[]    x1i = { 3, -5, 0, 17 };
	
	private static final double[] x2d = { -1, 2, 3, 11 };
	private static final float[]  x2f = { -1, 2, 3, 11 };
	private static final int[]    x2i = { -1, 2, 3, 11 };

	@Test
	public void testMagL1() {
		VectorNorm norm = NormType.L1.create();
		double mag = 25;
		assertEquals(mag, norm.magnitude(x1d), TOL);
		assertEquals(mag, norm.magnitude(x1f), TOL);
		assertEquals(mag, norm.magnitude(x1i), TOL);
	}
	
	@Test
	public void testMagL2() {
		VectorNorm norm = NormType.L2.create();
		double mag = 17.97220075561143;
		assertEquals(mag, norm.magnitude(x1d), TOL);
		assertEquals(mag, norm.magnitude(x1f), TOL);
		assertEquals(mag, norm.magnitude(x1i), TOL);
	}
	
	@Test
	public void testMagLinf() {
		VectorNorm norm = NormType.Linf.create();
		double mag = 17;
		assertEquals(mag, norm.magnitude(x1d), TOL);
		assertEquals(mag, norm.magnitude(x1f), TOL);
		assertEquals(mag, norm.magnitude(x1i), TOL);
	}
	
	// -------------------------------------------------

	@Test
	public void testDistL1() {
		VectorNorm norm = NormType.L1.create();
		double dist = 20;
		assertEquals(dist, norm.distance(x1d, x2d), TOL);
		assertEquals(dist, norm.distance(x1f, x2f), TOL);
		assertEquals(dist, norm.distance(x1i, x2i), TOL);
	}
	
	@Test
	public void testDistL2() {
		VectorNorm norm = NormType.L2.create();
		double dist = 10.488088481701515;
		assertEquals(dist, norm.distance(x1d, x2d), TOL);
		assertEquals(dist, norm.distance(x1f, x2f), TOL);
		assertEquals(dist, norm.distance(x1i, x2i), TOL);
	}
	
	@Test
	public void testDistLinf() {
		VectorNorm norm = NormType.Linf.create();
		double dist = 7;
		assertEquals(dist, norm.distance(x1d, x2d), TOL);
		assertEquals(dist, norm.distance(x1f, x2f), TOL);
		assertEquals(dist, norm.distance(x1i, x2i), TOL);
	}
	
	// -------------------------------------------------

	@Test
	public void testDist2L1() {
		VectorNorm norm = NormType.L1.create();
		double dist = 400;
		assertEquals(dist, norm.distance2(x1d, x2d), TOL);
		assertEquals(dist, norm.distance2(x1f, x2f), TOL);
		assertEquals(dist, norm.distance2(x1i, x2i), TOL);
	}
	
	@Test
	public void testDist2L2() {
		VectorNorm norm = NormType.L2.create();
		double dist = 110;
		assertEquals(dist, norm.distance2(x1d, x2d), TOL);
		assertEquals(dist, norm.distance2(x1f, x2f), TOL);
		assertEquals(dist, norm.distance2(x1i, x2i), TOL);
	}
	
	@Test
	public void testDist2Linf() {
		VectorNorm norm = NormType.Linf.create();
		double dist = 49;
		assertEquals(dist, norm.distance2(x1d, x2d), TOL);
		assertEquals(dist, norm.distance2(x1f, x2f), TOL);
		assertEquals(dist, norm.distance2(x1i, x2i), TOL);
	}
}




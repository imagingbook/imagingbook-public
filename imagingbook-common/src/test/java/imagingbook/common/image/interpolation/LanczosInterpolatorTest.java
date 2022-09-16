package imagingbook.common.image.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LanczosInterpolatorTest {
	
	static double TOL = 1e-6;

	@Test
	public void testN2() {
		LanczosInterpolator interp = new LanczosInterpolator(2);
		assertEquals(1.0, interp.getWeight(0.0), TOL);
		assertEquals(0.573159168250756, interp.getWeight(0.5), TOL);
		assertEquals(0.0, interp.getWeight(1.0), TOL);
		assertEquals(-0.06368435202786181, interp.getWeight(1.5), TOL);
		assertEquals(0.0, interp.getWeight(2.0), TOL);
		assertEquals(0.0, interp.getWeight(2.5), TOL);
	}
	
	@Test
	public void testN3() {
		LanczosInterpolator interp = new LanczosInterpolator(3);
		assertEquals(1.0, interp.getWeight(0.0), TOL);
		assertEquals(0.6079271018540265, interp.getWeight(0.5), TOL);
		assertEquals(0.0, interp.getWeight(1.0), TOL);
		assertEquals(-0.13509491152311703, interp.getWeight(1.5), TOL);
		assertEquals(0.0, interp.getWeight(2.0), TOL);
		assertEquals(0.024317084074161062, interp.getWeight(2.5), TOL);
		assertEquals(0.0, interp.getWeight(3.0), TOL);
		assertEquals(0.0, interp.getWeight(3.5), TOL);
	}
	
	@Test
	public void testN4() {
		LanczosInterpolator interp = new LanczosInterpolator(4);
		assertEquals(1.0, interp.getWeight(0.0), TOL);
		assertEquals(0.6203830132406946, interp.getWeight(0.5), TOL);
		assertEquals(0.0, interp.getWeight(1.0), TOL);
		assertEquals(-0.16641523160350802, interp.getWeight(1.5), TOL);
		assertEquals(0.0, interp.getWeight(2.0), TOL);
		assertEquals(0.05990948337726289, interp.getWeight(2.5), TOL);
		assertEquals(0.0, interp.getWeight(3.0), TOL);
		assertEquals(-0.012660877821238668, interp.getWeight(3.5), TOL);
		assertEquals(0.0, interp.getWeight(4.0), TOL);
		assertEquals(0.0, interp.getWeight(4.5), TOL);
	}

}




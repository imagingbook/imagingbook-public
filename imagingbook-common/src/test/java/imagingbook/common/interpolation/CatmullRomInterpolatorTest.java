package imagingbook.common.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CatmullRomInterpolatorTest {
	
	static double TOL = 1e-6;

	@Test
	public void test1() {
		CatmullRomInterpolator interp = new CatmullRomInterpolator();
		assertEquals(1.0, interp.getWeight(0.0), TOL);
		assertEquals(0.5625, interp.getWeight(0.5), TOL);
		assertEquals(0.0, interp.getWeight(1.0), TOL);
		assertEquals(-0.0625, interp.getWeight(1.5), TOL);
		assertEquals(0.0, interp.getWeight(2.0), TOL);
		assertEquals(0.0, interp.getWeight(2.5), TOL);
	}

}

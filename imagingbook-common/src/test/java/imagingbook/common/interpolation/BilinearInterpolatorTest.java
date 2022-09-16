package imagingbook.common.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BilinearInterpolatorTest {
	
	static double TOL = 1e-6;

	@Test
	public void test1() {
		BilinearInterpolator interp = new BilinearInterpolator();
		assertEquals(1.0, interp.getWeight(0.0), TOL);
		assertEquals(0.5, interp.getWeight(0.5), TOL);
		assertEquals(0.0, interp.getWeight(1.0), TOL);
		assertEquals(0.0, interp.getWeight(1.5), TOL);
		assertEquals(0.0, interp.getWeight(2.0), TOL);
		assertEquals(0.0, interp.getWeight(2.5), TOL);
	}

}

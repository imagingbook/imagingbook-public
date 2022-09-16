package imagingbook.common.image.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MitchellNetravaliInterpolatorTest {
	
	static double TOL = 1e-6;

	@Test
	public void test1() {
		MitchellNetravaliInterpolator interp = new MitchellNetravaliInterpolator();
		assertEquals(0.8888888888888888, interp.getWeight(0.0), TOL);
		assertEquals(0.5347222222222222, interp.getWeight(0.5), TOL);
		assertEquals(0.055555555555555435, interp.getWeight(1.0), TOL);
		assertEquals(-0.034722222222222245, interp.getWeight(1.5), TOL);
		assertEquals(0.0, interp.getWeight(2.0), TOL);
		assertEquals(0.0, interp.getWeight(2.5), TOL);
	}

}

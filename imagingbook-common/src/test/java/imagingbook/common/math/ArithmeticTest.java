package imagingbook.common.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ArithmeticTest {

	@Test
	public void testModDoubleDouble() {
		double D = 1E-9;
		assertEquals( 1.4, Arithmetic.mod( 3.5,  2.1), D);
		assertEquals( 0.7, Arithmetic.mod(-3.5,  2.1), D);
		assertEquals(-0.7, Arithmetic.mod( 3.5, -2.1), D);
		assertEquals(-1.4, Arithmetic.mod(-3.5, -2.1), D);
	}

	@Test
	public void testEqualsDoubleDouble() {
		assertFalse(Arithmetic.equals(-0.7, 0.8));
		assertTrue(Arithmetic.equals(-0.7, -0.7));
		assertTrue(Arithmetic.equals(1.0, 1.0 + Arithmetic.EPSILON_DOUBLE / 2));
		assertTrue(Arithmetic.equals(1.0, 1.0 - Arithmetic.EPSILON_DOUBLE / 2));
	}

	@Test
	public void testEqualsFloatFloat() {
		assertFalse(Arithmetic.equals(-0.7f, 0.8f));
		assertTrue(Arithmetic.equals(-0.7f, -0.7f));
		assertTrue(Arithmetic.equals(1.0f, 1.0f + Arithmetic.EPSILON_FLOAT / 2));
		assertTrue(Arithmetic.equals(1.0f, 1.0f - Arithmetic.EPSILON_FLOAT / 2));
	}
	
//	@Test
//	public void modVsRemainder() {
//		double a = 3.5, b = 2.1;
//		doIt(a,b);
//		doIt(a,-b);
//		doIt(-a,b);
//		doIt(-a,-b);
//	}
//	
//	private void doIt(double a, double b) {
//		double r1 = Arithmetic.mod( a,  b);
//		double r2 = a %  b;
//		System.out.format("(%.1f, %.1f): %.1f  vs %.1f\n", a, b, r1, r2);
//		//System.out.format("(%.1f mod %.1f) -> %.3f\n", a, b, r1);
//	}

}

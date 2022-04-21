package imagingbook.common.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ComplexTest {
	
	Complex z1 = new Complex(0.3, 0.6);
	Complex z2 = new Complex(-1, 0.2);
	
	@Test	// z1 + z2 = (-0.700000000, 0.800000000)
	public void testComplexAdd() {
		Complex z12 = z1.add(z2);
//		assertEquals(-0.7, z12.re, 1E-6);
//		assertEquals( 0.8, z12.im, 1E-6);
		assertTrue(z12.equals(new Complex(-0.7, 0.8)));
		assertTrue(z12.equals(-0.7, 0.8));
		
		Complex z21 = z2.add(z1);
//		assertEquals(-0.7, z21.re, 1E-6);
//		assertEquals( 0.8, z21.im, 1E-6);
		assertTrue(z21.equals(new Complex(-0.7, 0.8)));
		assertTrue(z21.equals(-0.7, 0.8));
	}

	@Test // z1 * z2 = (-0.420000000, -0.540000000)
	public void testComplexMultiply() {
		// test multiplication (commutative)
		Complex z12 = z1.multiply(z2);
//		assertEquals(-0.42, z12.re, 1E-6);
//		assertEquals(-0.54, z12.im, 1E-6);
		assertTrue(z12.equals(new Complex(-0.42, -0.54)));
		assertTrue(z12.equals(-0.42, -0.54));
		
		Complex z21 = z2.multiply(z1);
//		assertEquals(-0.42, z21.re, 1E-6);
//		assertEquals(-0.54, z21.im, 1E-6);
		assertTrue(z21.equals(new Complex(-0.42, -0.54)));
		assertTrue(z21.equals(-0.42, -0.54));
	}
	
	@Test // z1.pow(5) = (0.099630000, -0.092340000)
	public void testComplexPow() {
		// test multiplication (commutative)
		Complex z12 = z1.pow(5);
		assertEquals( 0.09963, z12.re, 1E-6);
		assertEquals(-0.09234, z12.im, 1E-6);
	}

	@Test // z1.rotate(0.1) = (0.238601200, 0.626952524)
	public void testComplexRotate() {
		// test multiplication (commutative)
		Complex z12 = z1.rotate(0.1);
		assertEquals( 0.238601200, z12.re, 1E-6);
		assertEquals( 0.626952524, z12.im, 1E-6);
	}
	
}

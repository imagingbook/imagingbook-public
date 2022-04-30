package imagingbook.common.geometry.circle;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import imagingbook.common.math.Matrix;

public class AlgebraicCircleTest {

	@Test		// test constructors
	public void test1() {
		GeometricCircle gc1 = new GeometricCircle(200, -300, 777);
		AlgebraicCircle ac1 = new AlgebraicCircle(gc1);
		
		GeometricCircle gc2 = new GeometricCircle(ac1);
		AlgebraicCircle ac2 = new AlgebraicCircle(gc2);

		assertTrue(gc1.equals(gc2, 1e-9));
		assertTrue(gc2.equals(gc1, 1e-9));
		
		assertTrue(ac1.equals(ac2, 1e-9));
		assertTrue(ac2.equals(ac1, 1e-9));
	}

	@Test		// test duplication
	public void test2() {
		AlgebraicCircle ac1 = new AlgebraicCircle(17, -23, 7, 6);
		AlgebraicCircle ac2 = ac1.duplicate();
		assertTrue(ac1.equals(ac2, 1e-6));
	}
	
	@Test		// test normalization
	public void test3() {
		double[] p1 = {17, 23, 7, 6};
		double[] p2 = Matrix.multiply(-1, p1);
		double[] p3 = Matrix.multiply(1e9, p1);
		
		AlgebraicCircle ac1 = new AlgebraicCircle(p1);
		AlgebraicCircle ac2 = new AlgebraicCircle(p2);
		AlgebraicCircle ac3 = new AlgebraicCircle(p3);
		
		assertTrue(ac1.equals(ac2, 1e-6));
		assertTrue(ac1.equals(ac3, 1e-6));
	}
}

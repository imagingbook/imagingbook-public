package imagingbook.common.geometry.fitting.circle.algebraic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.AlgebraicCircle;

public class CircleFit3PointsTest {

	/**
	 * Fits a circle to 3 given points and checks the outcome.
	 */
	@Test
	public void test1() {
		// 3 points for the circle to pass through
		Pnt2d p0 = Pnt2d.from(6, 7);
		Pnt2d p1 = Pnt2d.from(1, 9);
		Pnt2d p2 = Pnt2d.from(-3, 5);
		
		AlgebraicCircle acExp = new AlgebraicCircle(28.0, -108.0, -228.0, -136.0);	// expected circle (normalized)
		
		doCheck(new CircleFit3Points(p0, p1, p2), acExp);
		doCheck(new CircleFit3Points(p2, p0, p1), acExp);
		doCheck(new CircleFit3Points(p1, p2, p0), acExp);
	}
	
	private void doCheck(CircleFit3Points fit, AlgebraicCircle acExp) {
		double[] p = fit.getParameters();
		assertNotNull(p);
		AlgebraicCircle ac = fit.getAlgebraicCircle();		// fitted circle
		assertNotNull(ac);	
		assertTrue(acExp.equals(ac, 1e-6));					// compare ac to expected circle
	}
	
	
	@Test
	public void test2() {
		// fit to 3 collinear points  (getParameters() must return null)
		Pnt2d p0 = Pnt2d.from(-1, -1);
		Pnt2d p1 = Pnt2d.from(4, 4);
		Pnt2d p2 = Pnt2d.from(99, 99);
		
		CircleFitAlgebraic fit = new CircleFit3Points(p0, p1, p2);
		assertNull(fit.getParameters());

	}

}

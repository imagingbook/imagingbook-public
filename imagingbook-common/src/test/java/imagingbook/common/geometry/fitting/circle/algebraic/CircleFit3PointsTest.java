package imagingbook.common.geometry.fitting.circle.algebraic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.AlgebraicCircle;

public class CircleFit3PointsTest {
	
	// 3 points for the circle to pass through
	static Pnt2d p0 = Pnt2d.from(6, 7);
	static Pnt2d p1 = Pnt2d.from(1, 9);
	static Pnt2d p2 = Pnt2d.from(-3, 5);
	static Pnt2d[] pnts = {p0, p1, p2};
	
	static AlgebraicCircle acExp = new AlgebraicCircle(28.0, -108.0, -228.0, -136.0);	// expected circle (normalized)

	/**
	 * Fits a circle to 3 given points and checks the outcome.
	 */
	@Test
	public void test1() {
		doCheck(new CircleFit3Points(p0, p1, p2), acExp);
		doCheck(new CircleFit3Points(p2, p0, p1), acExp);
		doCheck(new CircleFit3Points(p1, p2, p0), acExp);
	}
	
	@Test
	public void test2() {
		doCheck(new CircleFitHyper(pnts), acExp);
		doCheck(new CircleFitKasaOrig(pnts), acExp);
		doCheck(new CircleFitKasaA(pnts), acExp);
		doCheck(new CircleFitKasaB(pnts), acExp);
		doCheck(new CircleFitPratt(pnts), acExp);
		doCheck(new CircleFitTaubin(pnts), acExp);
	}
	
	private void doCheck(CircleFitAlgebraic fit, AlgebraicCircle acExp) {
		double[] p = fit.getParameters();
		assertNotNull(p);
		AlgebraicCircle ac = fit.getAlgebraicCircle();		// fitted circle
		assertNotNull(ac);	
		assertTrue(acExp.equals(ac, 1e-6));					// compare ac to expected circle
	}
	
	
	@Test
	public void test4() {
		// fit to 3 collinear points  (getParameters() must return null)
		Pnt2d x0 = Pnt2d.from(-1, -1);
		Pnt2d x1 = Pnt2d.from(4, 4);
		Pnt2d x2 = Pnt2d.from(99, 99);
		
		CircleFitAlgebraic fit = new CircleFit3Points(x0, x1, x2);
		assertNull(fit.getParameters());

	}
	


}

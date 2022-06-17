package imagingbook.common.geometry.fitting.ellipse.algebraic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;


import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.AlgebraicEllipse;

public class EllipseFitFitzgibbonOriginalTest {

	@Test(expected = IllegalArgumentException.class)
	public void test1() {
		Pnt2d[] points = TestPointSet5.points;	// 5 points are not enough!
		new EllipseFitFitzgibbonOriginal(points);
	}
	
	@Test
	public void test2() {
		Pnt2d[] points = TestPointSet6.points;
		double[] q_exp = TestPointSet6.qExpected;
		
		EllipseFitAlgebraic fit = new EllipseFitFitzgibbonOriginal(points);
		double[] q = fit.getParameters();
		assertNotNull("ellipse parameters are null", q);
		
		AlgebraicEllipse ell = fit.getEllipse();
		assertNotNull("ellipse is null", ell);
		
		double[] qn = ell.getParameters(); 	// normalized ellipse parameters
		assertArrayEquals("ellipse parameters do not match", q_exp, qn, 1e-6);
	}

}

package imagingbook.common.geometry.fitting.ellipse.algebraic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.AlgebraicEllipse;
import imagingbook.common.geometry.fitting.ellipse.algebraic.data.TestPointSet5;

public class EllipseFit5PointsTest {

	@Test
	public void test1() {
		Pnt2d[] points = TestPointSet5.points;
		double[] q_exp = TestPointSet5.qExpected;
		
		EllipseFitAlgebraic fit = new EllipseFit5Points(points);
		double[] q = fit.getParameters();
		assertNotNull("ellipse parameters are null", q);
		
		AlgebraicEllipse ell = fit.getEllipse();
		assertNotNull("ellipse is null", ell);
		
		double[] qn = ell.getParameters(); 	// normalized ellipse parameters
		assertArrayEquals("ellipse parameters do not match", q_exp, qn, 1e-6);
	}

}

package imagingbook.common.geometry.fitting.line;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.line.AlgebraicLine;

public class OrthogonalLineFitEigenTest {

	static double TOL = 1e-6;

	@Test
	public void test1() {
		double[][] X = {{1, 8}, {4, 5}, {4, 7}, {6, 4}, {9, 4}}; // book example
		AlgebraicLine refLine = new AlgebraicLine(-0.4968900437902618, -0.8678135078357052, 7.244827854073206);
		
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		LineFit fit = new OrthogonalLineFitEigen(pts);
		AlgebraicLine line = fit.getLine();
		assertNotNull(line);
		assertTrue(refLine.equals(line, TOL));	
		assertEquals(2.6645834350486606, fit.getSquaredOrthogonalError(pts), TOL);
	}
	
	@Test
	public void test2() {
		double[][] X = {{1, 8}, {4, 5}}; // 2 points only
		AlgebraicLine refLine = new AlgebraicLine(-0.7071067811865476, -0.7071067811865476, 6.3639610306789285);
		
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		LineFit fit = new OrthogonalLineFitEigen(pts);
		AlgebraicLine line = fit.getLine();
		assertNotNull(line);
		assertTrue(refLine.equals(line, TOL));	
		assertEquals(0.0, fit.getSquaredOrthogonalError(pts), TOL);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test3() {
		double[][] X = {{1, 8}};
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		@SuppressWarnings("unused")
		LineFit fit = new OrthogonalLineFitEigen(pts);
	}

}

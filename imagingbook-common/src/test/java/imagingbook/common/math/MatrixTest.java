package imagingbook.common.math;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.math.Arithmetic.DivideByZeroException;
import imagingbook.common.math.Matrix.IncompatibleDimensionsException;
import imagingbook.common.math.Matrix.NonsquareMatrixException;
import imagingbook.testutils.ArrayTests;


public class MatrixTest {
	
	static float[][] A = {
			{ -1, 2, 3 }, 
			{  4, 5, 6 }, 
			{  7, 8, 9 }};
	
	static double[][] B = {
			{ -1, 2, 3 }, 
			{ 4, 5, 6 }};

	@Test
	public void testMatrixRowsAndColumns() {
		Assert.assertEquals(3, Matrix.getNumberOfRows(A));
		Assert.assertEquals(3, Matrix.getNumberOfColumns(A));
		Assert.assertEquals(6.0, A[1][2], 1E-6);
		
		Assert.assertEquals(2, Matrix.getNumberOfRows(B));
		Assert.assertEquals(3, Matrix.getNumberOfColumns(B));
		Assert.assertEquals(2.0, A[0][1], ArrayTests.TOLERANCE);
	}
	
	@Test
	public void testMatrixDeterminant() {
		Assert.assertEquals(6.0, Matrix.determinant3x3(A), ArrayTests.TOLERANCE);
	}
	
	@Test(expected = IncompatibleDimensionsException.class)
	public void testMatrixDeterminantFail() {
		Matrix.determinant3x3(B);
	}

	@Test
	public void testMatrixRowColumnSums() {
		Assert.assertArrayEquals(new float[] {10, 15, 18}, Matrix.sumColumns(A), ArrayTests.TOLERANCE);
		Assert.assertArrayEquals(new float[] {4, 15, 24}, Matrix.sumRows(A), ArrayTests.TOLERANCE);
		
		Assert.assertArrayEquals(new double[] {3, 7, 9}, Matrix.sumColumns(B), ArrayTests.TOLERANCE);
		Assert.assertArrayEquals(new double[] {4, 15}, Matrix.sumRows(B), ArrayTests.TOLERANCE);
	}
	
	@Test
	public void testMatrixTranspose() {
		Assert.assertArrayEquals(A, Matrix.transpose(Matrix.transpose(A)));
		Assert.assertArrayEquals(B, Matrix.transpose(Matrix.transpose(B)));
	}
	
	@Test
	public void testMatrixTrace() {
		Assert.assertEquals(13.0, Matrix.trace(Matrix.toDouble(A)), ArrayTests.TOLERANCE);
	}
	
	@Test
	public void testMatrixInverseFloat() {
		float[][] Ai = Matrix.inverse(A);
		ArrayTests.assertArrayEquals(A, Matrix.inverse(Ai), ArrayTests.TOLERANCE * 10);
	}
	
	@Test
	public void testMatrixInverseDouble() {
		double[][] AA = Matrix.toDouble(A);
		double[][] AAi = Matrix.inverse(AA);
		ArrayTests.assertArrayEquals(AA, Matrix.inverse(AAi), ArrayTests.TOLERANCE);
	}
	
	@Test(expected = NonsquareMatrixException.class)
	public void testMatrixInverseNFail() {
		Matrix.inverse(B);
	}
	
	@Test
	public void testMatrixRealMatrix() {
		RealMatrix Br = MatrixUtils.createRealMatrix(B);
		Assert.assertArrayEquals(B, Br.getData());
	}
	
	@Test
	public void testMatrixDeterminants() {
		Assert.assertEquals((double) Matrix.determinant3x3(A), Matrix.determinant(Matrix.toDouble(A)), ArrayTests.TOLERANCE);
	}
	
	// --------------------------------------------------------------------

	@Test
	public void testMatrixJoin() {
		float[] v1 = {1,2,3};
		float[] v2 = {4,5,6,7};
		float[] v3 = {};
		float[] v4 = {8};		
		Assert.assertArrayEquals(new float[] {1, 2, 3, 4, 5, 6, 7, 8}, Matrix.join(v1, v2, v3, v4), ArrayTests.TOLERANCE);
	}
	
	@Test
	public void testMatrixMinMax() {
		float[] x = {-20,30,60,-40, 0};
		double[] y = {-20,30,60,-40, 0};
		Assert.assertEquals(Matrix.min(x), -40, ArrayTests.TOLERANCE);
		Assert.assertEquals(Matrix.min(y), -40, ArrayTests.TOLERANCE);
		Assert.assertEquals(Matrix.max(x), 60, ArrayTests.TOLERANCE);
		Assert.assertEquals(Matrix.max(y), 60, ArrayTests.TOLERANCE);
	}
	
	// --------------------------------------------------------------------
	
	@Test
	public void testMatrixHomogeneous() {
		double[] x = {2, -7, 3};
		double[] xh = {2, -7, 3, 1};
		Assert.assertArrayEquals(xh, Matrix.toHomogeneous(x), ArrayTests.TOLERANCE);
		Assert.assertArrayEquals(x, Matrix.toCartesian(xh), ArrayTests.TOLERANCE);
		Assert.assertArrayEquals(x, Matrix.toCartesian(Matrix.multiply(-5, xh)), ArrayTests.TOLERANCE);
	}
	
	@Test (expected = DivideByZeroException.class)
	public void testMatrixHomogeneousFail() {
		double[] xh = {2, -7, 3, 0};
		Matrix.toCartesian(xh);
	}

}

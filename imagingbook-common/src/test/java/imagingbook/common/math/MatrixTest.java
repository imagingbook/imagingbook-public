/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.math;

import static imagingbook.testutils.NumericTestUtils.TOLERANCE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.math.Matrix.IncompatibleDimensionsException;
import imagingbook.common.math.Matrix.NonsquareMatrixException;
import imagingbook.common.math.Matrix.ZeroLengthVectorException;
import imagingbook.common.math.exception.DivideByZeroException;
import imagingbook.testutils.NumericTestUtils;


public class MatrixTest {
	
	private static final double[][] Ad = {
			{ -1, 2, 3 }, 
			{  4, 5, 6 }, 
			{  7, 8, 9 }};
	
	private static final float[][] Af = Matrix.toFloat(Ad);
	
	private static final double[][] Bd = {
			{ -1, 2, 3 }, 
			{ 4, 5, 6 }};
	
	private static final float[][] Bf = Matrix.toFloat(Bd);

	@Test
	public void testMatrixRowsAndColumns() {
		assertEquals(3, Matrix.getNumberOfRows(Af));
		assertEquals(3, Matrix.getNumberOfColumns(Af));
		assertEquals(6.0, Af[1][2], 1E-6);
		
		assertEquals(2, Matrix.getNumberOfRows(Bd));
		assertEquals(3, Matrix.getNumberOfColumns(Bd));
		assertEquals(6.0, Bd[1][2], TOLERANCE);
		
		assertEquals(2.0, Af[0][1], TOLERANCE);
	}
	
	@Test
	public void testMatrixMakeMatrix() {		
		NumericTestUtils.assertArrayEquals(Bd, Matrix.makeDoubleMatrix(2, 3, -1, 2, 3, 4, 5, 6), TOLERANCE);
		NumericTestUtils.assertArrayEquals(Bd, Matrix.makeDoubleMatrix(2, 3, new double[] {-1, 2, 3, 4, 5, 6}), TOLERANCE);
		
		NumericTestUtils.assertArrayEquals(Bf, Matrix.makeFloatMatrix(2, 3, -1, 2, 3, 4, 5, 6), TOLERANCE);
		NumericTestUtils.assertArrayEquals(Bf, Matrix.makeFloatMatrix(2, 3, new float[] {-1, 2, 3, 4, 5, 6}), TOLERANCE);
		
		NumericTestUtils.assertArrayEquals(Bd, Matrix.makeRealMatrix(2, 3, -1, 2, 3, 4, 5, 6).getData(), TOLERANCE);
	}
	
	@Test
	public void testMatrixFlatten() {
		NumericTestUtils.assertArrayEquals(Bd, Matrix.makeDoubleMatrix(2, 3, Matrix.flatten(Bd)), TOLERANCE);
		NumericTestUtils.assertArrayEquals(Bf, Matrix.makeFloatMatrix(2, 3, Matrix.flatten(Bf)), TOLERANCE);
		
		assertArrayEquals(new double[] {7, 8, 9}, Matrix.flatten(Matrix.makeDoubleMatrix(1, 3, 7, 8, 9)), TOLERANCE);
		assertArrayEquals(new double[] {7, 8, 9}, Matrix.flatten(Matrix.makeDoubleMatrix(3, 1, 7, 8, 9)), TOLERANCE);
		
		assertArrayEquals(new float[] {7, 8, 9}, Matrix.flatten(Matrix.makeFloatMatrix(1, 3, 7, 8, 9)), TOLERANCE);
		assertArrayEquals(new float[] {7, 8, 9}, Matrix.flatten(Matrix.makeFloatMatrix(3, 1, 7, 8, 9)), TOLERANCE);
		
	}
	
	@Test
	public void testMatrixSameSize() {
		assertTrue(Matrix.sameSize(Ad, Ad));
		assertTrue(Matrix.sameSize(Af, Af));
		
		assertFalse(Matrix.sameSize(Ad, Bd));
		assertFalse(Matrix.sameSize(Af, Bf));
	}
	
	// --------------------------------------------------------------------

	@Test
	public void testMatrixRowColumnSums() {
		assertArrayEquals(new float[] {10, 15, 18}, Matrix.sumColumns(Af), TOLERANCE);
		Assert.assertArrayEquals(new float[] {4, 15, 24}, Matrix.sumRows(Af), TOLERANCE);
		
		assertArrayEquals(new double[] {3, 7, 9}, Matrix.sumColumns(Bd), TOLERANCE);
		assertArrayEquals(new double[] {4, 15}, Matrix.sumRows(Bd), TOLERANCE);
	}
	
	// --------------------------------------------------------------------
	
	@Test
	public void testMatrixTranspose() {
		assertArrayEquals(Af, Matrix.transpose(Matrix.transpose(Af)));
		assertArrayEquals(Bd, Matrix.transpose(Matrix.transpose(Bd)));
	}
	
	@Test
	public void testMatrixTraceDouble() {
		assertEquals(13.0, Matrix.trace(Ad), TOLERANCE);
	}
	
	@Test
	public void testMatrixTraceFloat() {
		assertEquals(13.0, (double) Matrix.trace(Af), TOLERANCE);
	}
	
	// --------------------------------------------------------------------
	
	@Test
	public void testMatrixGetDiagonal() {
		double[] AdDiag = {-1, 5, 9};
		float[] AfDiag = {-1, 5, 9};
		assertArrayEquals(AdDiag, Matrix.getDiagonal(Ad), TOLERANCE);
		assertArrayEquals(AfDiag, Matrix.getDiagonal(Af), TOLERANCE);
		assertArrayEquals(AdDiag, Matrix.getDiagonal(MatrixUtils.createRealMatrix(Ad)).toArray(), TOLERANCE);
	}
	
	@Test(expected = NonsquareMatrixException.class)
	public void testMatrixGetDiagonalDoubleFail() {
		Matrix.getDiagonal(Bd);
	}
	
	@Test(expected = NonsquareMatrixException.class)
	public void testMatrixGetDiagonalFloatFail() {
		Matrix.getDiagonal(Bf);
	}
	
	// --------------------------------------------------------------------
	
	@Test
	public void testMatrixInverseFloat() {
		float[][] Ai = Matrix.inverse(Af);
		NumericTestUtils.assertArrayEquals(Af, Matrix.inverse(Ai), TOLERANCE * 10);
	}
	
	@Test
	public void testMatrixInverseDouble() {
		double[][] AA = Matrix.toDouble(Af);
		double[][] AAi = Matrix.inverse(AA);
		NumericTestUtils.assertArrayEquals(AA, Matrix.inverse(AAi), TOLERANCE);
	}
	
	@Test(expected = NonsquareMatrixException.class)
	public void testMatrixInverseNFail() {
		Matrix.inverse(Bd);
	}
	
	@Test
	public void testMatrixRealMatrix() {
		RealMatrix Br = MatrixUtils.createRealMatrix(Bd);
		assertArrayEquals(Bd, Br.getData());
	}
	
	// --------------------------------------------------------------------
	
	@Test  
	public void testMatrixDeterminant() {
		assertEquals(6.0, Matrix.determinant3x3(Af), TOLERANCE);
	}
	
	@Test (expected = NonsquareMatrixException.class)
	public void testMatrixDeterminantFail1() {
		Matrix.determinant(Bd);
	}
	
	@Test (expected = NonsquareMatrixException.class)
	public void testMatrixDeterminantFail2() {
		Matrix.determinant(MatrixUtils.createRealMatrix(Bd));
	}
	
	@Test
	public void testMatrixDeterminant3x3() {
		assertEquals((double) Matrix.determinant3x3(Af), Matrix.determinant(Ad), TOLERANCE);
	}
	
	@Test(expected = IncompatibleDimensionsException.class)
	public void testMatrixDeterminant3x3Fail() {
		Matrix.determinant3x3(Bd);
	}
	
	@Test
	public void testMatrixDeterminant2x2() {
		double[][] Md = {{2, 7}, {-1, 5}};
		float[][] Mf = Matrix.toFloat(Md);
		assertEquals((double) Matrix.determinant2x2(Md), Matrix.determinant(Md), TOLERANCE);
		assertEquals((double) Matrix.determinant2x2(Mf), Matrix.determinant(Md), TOLERANCE);
	}
	
	
	@Test (expected = IncompatibleDimensionsException.class)
	public void testMatrixDeterminant2x2FloatFail() {
		Matrix.determinant2x2(Af);
	}
	
	@Test (expected = IncompatibleDimensionsException.class)
	public void testMatrixDeterminant2x2DoubleFail() {
		Matrix.determinant2x2(Ad);
	}
	
	// --------------------------------------------------------------------

	@Test
	public void testMatrixJoin() {
		float[] v1 = {1,2,3};
		float[] v2 = {4,5,6,7};
		float[] v3 = {};
		float[] v4 = {8};		
		assertArrayEquals(new float[] {1, 2, 3, 4, 5, 6, 7, 8}, Matrix.join(v1, v2, v3, v4), TOLERANCE);
	}
	
	@Test
	public void testMatrixMinMax() {
		float[]  x = {-20,30,60,-40, 0};
		double[] y = {-20,30,60,-40, 0};
		assertEquals(Matrix.min(x), -40, TOLERANCE);
		assertEquals(Matrix.min(y), -40, TOLERANCE);
		assertEquals(Matrix.max(x), 60, TOLERANCE);
		assertEquals(Matrix.max(y), 60, TOLERANCE);
	}
	
	@Test (expected = ZeroLengthVectorException.class)
	public void testMatrixMinFailDouble() {
		Matrix.min(new double[0]);
	}
	
	@Test (expected = ZeroLengthVectorException.class)
	public void testMatrixMinFailFloat() {
		Matrix.min(new float[0]);
	}
	
	@Test (expected = ZeroLengthVectorException.class)
	public void testMatrixMaxFailDouble() {
		Matrix.max(new double[0]);
	}
	
	@Test (expected = ZeroLengthVectorException.class)
	public void testMatrixMaxFailFloat() {
		Matrix.max(new float[0]);
	}
	
	
	@Test
	public void testMatrixIdxMax() {
		float[]  x = {-20,30,60,-40, 0};
		double[] y = {-20,30,60,-40, 0};
		assertEquals(Matrix.idxMax(x), 2);
		assertEquals(Matrix.idxMax(y), 2);
		
	}
	
	@Test
	public void testMatrixIdxMin() {
		float[]  x = {-20,30,60,-40, 0};
		double[] y = {-20,30,60,-40, 0};
		assertEquals(Matrix.idxMin(x), 3);
		assertEquals(Matrix.idxMin(y), 3);
		
	}
	
	@Test (expected = ZeroLengthVectorException.class)
	public void testMatrixIdxMinFailDouble() {
		Matrix.idxMin(new double[0]);
	}
	
	@Test (expected = ZeroLengthVectorException.class)
	public void testMatrixIdxMinFailFloat() {
		Matrix.idxMin(new float[0]);
	}
	
	@Test (expected = ZeroLengthVectorException.class)
	public void testMatrixIdxMaxFailDouble() {
		Matrix.idxMax(new double[0]);
	}
	
	@Test (expected = ZeroLengthVectorException.class)
	public void testMatrixIdxMaxFailFloat() {
		Matrix.idxMax(new float[0]);
	}
	
	// --------------------------------------------------------------------
	
	@Test
	public void testMatrixJoinDouble() {
		double[] a = {-20, 30, 60, -40, 0};
		double[] b = {19};
		double[] c = {8, 2, 11};
		double[] abc = {-20, 30, 60, -40, 0, 19, 8, 2, 11};
		
		assertArrayEquals(a, Matrix.join(a), TOLERANCE);
		assertArrayEquals(b, Matrix.join(b), TOLERANCE);
		assertArrayEquals(c, Matrix.join(c), TOLERANCE);
		
		assertArrayEquals(a, Matrix.join(a, new double[0]), TOLERANCE);
		assertArrayEquals(a, Matrix.join(new double[0], a), TOLERANCE);
		
		assertArrayEquals(abc, Matrix.join(a, b, c), TOLERANCE);	
	}
	
	@Test
	public void testMatrixJoinFloat() {
		float[] a = {-20, 30, 60, -40, 0};
		float[] b = {19};
		float[] c = {8, 2, 11};
		float[] abc = {-20, 30, 60, -40, 0, 19, 8, 2, 11};

		assertArrayEquals(a, Matrix.join(a), TOLERANCE);
		assertArrayEquals(b, Matrix.join(b), TOLERANCE);
		assertArrayEquals(c, Matrix.join(c), TOLERANCE);
		
		assertArrayEquals(a, Matrix.join(a, new float[0]), TOLERANCE);
		assertArrayEquals(a, Matrix.join(new float[0], a), TOLERANCE);
		
		assertArrayEquals(abc, Matrix.join(a, b, c), TOLERANCE);	
	}
	
	// --------------------------------------------------------------------
	
	@Test
	public void testMatrixLerpDouble() {
		double[] a = {8,  2, 11};
		double[] b = {1, -3, 5};
		double[] exptd = {3.100, -1.500, 6.800};
		double t = 0.7f;
		assertArrayEquals(exptd,  Matrix.lerp(a, b, t), TOLERANCE);
	}
	
	@Test
	public void testMatrixLerpFloat() {
		float[] a = {8,  2, 11};
		float[] b = {1, -3, 5};
		float[] exptd = {3.100f, -1.500f, 6.800f};
		float t = 0.7f;
		assertArrayEquals(exptd,  Matrix.lerp(a, b, t), TOLERANCE);
	}
	
	// --------------------------------------------------------------------
	
	@Test
	public void testMatrixHomogeneous() {
		double[] x = {2, -7, 3};
		double[] xh = {2, -7, 3, 1};
		assertArrayEquals(xh, Matrix.toHomogeneous(x), TOLERANCE);
		assertArrayEquals(x, Matrix.toCartesian(xh), TOLERANCE);
		assertArrayEquals(x, Matrix.toCartesian(Matrix.multiply(-5, xh)), TOLERANCE);
	}
	
	@Test (expected = DivideByZeroException.class)
	public void testMatrixHomogeneousFail() {
		double[] xh = {2, -7, 3, 0};
		Matrix.toCartesian(xh);
	}
	
	// --------------------------------------------------------------------
	
	

}

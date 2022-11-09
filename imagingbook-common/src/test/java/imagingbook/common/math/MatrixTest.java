/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.math;

import static imagingbook.common.math.Arithmetic.sqr;
import static imagingbook.testutils.NumericTestUtils.TOLERANCE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonSymmetricMatrixException;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.math.Matrix.IncompatibleDimensionsException;
import imagingbook.common.math.Matrix.NonsquareMatrixException;
import imagingbook.common.math.Matrix.ZeroLengthVectorException;
import imagingbook.common.math.exception.DivideByZeroException;
import imagingbook.testutils.NumericTestUtils;
import imagingbook.testutils.RandomMatrixGenerator;


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
	public void testMatrixNumberOfRowsAndColumns() {
		assertEquals(3, Matrix.getNumberOfRows(Af));
		assertEquals(3, Matrix.getNumberOfColumns(Af));
		assertEquals(6.0, Af[1][2], TOLERANCE);
		assertEquals(2.0, Af[0][1], TOLERANCE);
		
		assertEquals(2, Matrix.getNumberOfRows(Bd));
		assertEquals(3, Matrix.getNumberOfColumns(Bd));
		assertEquals(6.0, Bd[1][2], TOLERANCE);
		assertEquals(2.0, Bd[0][1], TOLERANCE);
		
		RealMatrix Br = MatrixUtils.createRealMatrix(Bd);
		assertEquals(2, Matrix.getNumberOfRows(Br));
		assertEquals(3, Matrix.getNumberOfColumns(Br));
		assertEquals(6.0, Br.getEntry(1, 2), TOLERANCE);
		assertEquals(2.0, Br.getEntry(0, 1), TOLERANCE);
	}
	
	@Test
	public void testMatrixMakeMatrix() {		
		NumericTestUtils.assert2dArrayEquals(Bd, Matrix.makeDoubleMatrix(2, 3, -1, 2, 3, 4, 5, 6), TOLERANCE);
		NumericTestUtils.assert2dArrayEquals(Bd, Matrix.makeDoubleMatrix(2, 3, new double[] {-1, 2, 3, 4, 5, 6}), TOLERANCE);
		
		NumericTestUtils.assert2dArrayEquals(Bf, Matrix.makeFloatMatrix(2, 3, -1, 2, 3, 4, 5, 6), TOLERANCE);
		NumericTestUtils.assert2dArrayEquals(Bf, Matrix.makeFloatMatrix(2, 3, new float[] {-1, 2, 3, 4, 5, 6}), TOLERANCE);
		
		NumericTestUtils.assert2dArrayEquals(Bd, Matrix.makeRealMatrix(2, 3, -1, 2, 3, 4, 5, 6).getData(), TOLERANCE);
		NumericTestUtils.assert2dArrayEquals(Bd, Matrix.makeRealMatrix(2, 3, new double[] {-1, 2, 3, 4, 5, 6}).getData(), TOLERANCE);
	}
	
	@Test
	public void testMatrixFlatten() {
		NumericTestUtils.assert2dArrayEquals(Bd, Matrix.makeRealMatrix(2, 3, Matrix.flatten(MatrixUtils.createRealMatrix(Bd))).getData(), TOLERANCE);
		NumericTestUtils.assert2dArrayEquals(Bd, Matrix.makeDoubleMatrix(2, 3, Matrix.flatten(Bd)), TOLERANCE);
		NumericTestUtils.assert2dArrayEquals(Bf, Matrix.makeFloatMatrix(2, 3, Matrix.flatten(Bf)), TOLERANCE);
		
		assertArrayEquals(new double[] {7, 8, 9}, Matrix.flatten(Matrix.makeDoubleMatrix(1, 3, 7, 8, 9)), TOLERANCE);
		assertArrayEquals(new double[] {7, 8, 9}, Matrix.flatten(Matrix.makeDoubleMatrix(3, 1, 7, 8, 9)), TOLERANCE);
		
		assertArrayEquals(new float[] {7, 8, 9}, Matrix.flatten(Matrix.makeFloatMatrix(1, 3, 7, 8, 9)), TOLERANCE);
		assertArrayEquals(new float[] {7, 8, 9}, Matrix.flatten(Matrix.makeFloatMatrix(3, 1, 7, 8, 9)), TOLERANCE);
		
		assertArrayEquals(new double[] {7, 8, 9}, Matrix.flatten(Matrix.makeRealMatrix(1, 3, 7, 8, 9)), TOLERANCE);
		assertArrayEquals(new double[] {7, 8, 9}, Matrix.flatten(Matrix.makeRealMatrix(3, 1, 7, 8, 9)), TOLERANCE);
	}
	
	@Test
	public void testMatrixMakeDoubleVector() {
		double[] x = {1, 3, 7, 8, 9};
		assertArrayEquals(x, Matrix.makeDoubleVector(1, 3, 7, 8, 9), TOLERANCE);
		assertArrayEquals(x, Matrix.makeDoubleVector(x), TOLERANCE);
		assertArrayEquals(new double[0], Matrix.makeDoubleVector(), TOLERANCE);
	}
	
	@Test
	public void testMatrixMakeFloatVector() {
		float[] x = {1, 3, 7, 8, 9};
		assertArrayEquals(x, Matrix.makeFloatVector(1, 3, 7, 8, 9), TOLERANCE);
		assertArrayEquals(x, Matrix.makeFloatVector(x), TOLERANCE);
		assertArrayEquals(new float[0], Matrix.makeFloatVector(), TOLERANCE);
	}
	
	@Test
	public void testMatrixMakeRealVector() {
		double[] x = {1, 3, 7, 8, 9};
		assertArrayEquals(x, Matrix.makeRealVector(1, 3, 7, 8, 9).toArray(), TOLERANCE);
		assertArrayEquals(x, Matrix.makeRealVector(x).toArray(), TOLERANCE);
		assertArrayEquals(new double[0], Matrix.makeRealVector().toArray(), TOLERANCE);
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
	public void testMatrixIsRectangularDouble() {
		double[][] X =
			{{1, 2, 3},
			 {4},
			 {5, 6, 7, 8}};
		assertTrue(Matrix.isRectangular(Ad));
		assertFalse(Matrix.isRectangular(X));
	}
	
	@Test
	public void testMatrixIsRectangularFloat() {
		float[][] X =
			{{1, 2, 3},
			 {4},
			 {5, 6, 7, 8}};
		assertTrue(Matrix.isRectangular(Af));
		assertFalse(Matrix.isRectangular(X));
	}
	
	@Test
	public void testMatrixIsSquareDouble() {
		assertTrue(Matrix.isSquare(Ad));
		assertFalse(Matrix.isSquare(Bd));
	}
	
	@Test
	public void testMatrixIsSquareFloat() {
		assertTrue(Matrix.isSquare(Af));
		assertFalse(Matrix.isSquare(Bf));
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
		assertEquals(13.0, Matrix.trace(Af), TOLERANCE);
	}
	
	// --------------------------------------------------------------------
	
	@Test
	public void testMatrixMultiplyValVec() {
		double[] x = {7, -5, 2};
		double s = Math.PI; 
		
		assertArrayEquals(Matrix.multiply(s, x), MatrixUtils.createRealVector(x).mapMultiply(s).toArray(), TOLERANCE);
	}
	
	@Test
	public void testMatrixMultiplyValMat() {
		double s = Math.PI; 
		RealMatrix Ar = MatrixUtils.createRealMatrix(Ad);
		RealMatrix Br = MatrixUtils.createRealMatrix(Bd);
		
		assertArrayEquals(Matrix.multiply(s, Ad), Ar.scalarMultiply(s).getData());
		assertArrayEquals(Matrix.multiply(s, Bd), Br.scalarMultiply(s).getData());
	}
	
	@Test
	public void testMatrixMultiplyMatMat() {
		double[][] Id2 = Matrix.idMatrix(2);
		double[][] Id3 = Matrix.idMatrix(3);
		
		RealMatrix Ar = MatrixUtils.createRealMatrix(Ad);
		RealMatrix Br = MatrixUtils.createRealMatrix(Bd);
		
		assertArrayEquals(Matrix.multiply(Ad, Id3), Ad);
		assertArrayEquals(Matrix.multiply(Id3, Ad), Ad);
		
		assertArrayEquals(Matrix.multiply(Bd, Id3), Bd);
		assertArrayEquals(Matrix.multiply(Id2, Bd), Bd);
		
		assertArrayEquals(Matrix.multiply(Ad, Ad), Ar.multiply(Ar).getData());
		
		assertArrayEquals(Matrix.multiply(Bd, Matrix.transpose(Bd)), Br.multiply(Br.transpose()).getData());
		assertArrayEquals(Matrix.multiply(Matrix.transpose(Bd), Bd), Br.transpose().multiply(Br).getData());
		
		assertArrayEquals(Matrix.multiply(Bd, Ad), Br.multiply(Ar).getData());
		assertArrayEquals(Matrix.multiply(Ad, Matrix.transpose(Bd)), Ar.multiply(Br.transpose()).getData());
	}
	
	@Test
	public void testMatrixMultiplyMatVec() {
		double[] x = {7, -5, 2};
		RealMatrix Ar = MatrixUtils.createRealMatrix(Ad);
		RealMatrix Br = MatrixUtils.createRealMatrix(Bd);
		
		assertArrayEquals(Matrix.multiply(Matrix.idMatrix(3), x), x, TOLERANCE);
		
		assertArrayEquals(Matrix.multiply(Ad, x), Ar.operate(x), TOLERANCE);
		assertArrayEquals(Matrix.multiply(Bd, x), Br.operate(x), TOLERANCE);
		assertArrayEquals(Matrix.multiply(Matrix.transpose(Ad), x), Ar.transpose().operate(x), TOLERANCE);
	}
	
	@Test
	public void testMatrixMultiplyVecMat() {
		double[] x = {7, -5, 2};
		RealMatrix Ar = MatrixUtils.createRealMatrix(Ad);
		RealMatrix Br = MatrixUtils.createRealMatrix(Bd);
		
		assertArrayEquals(Matrix.multiply(x, Matrix.idMatrix(3)), x, TOLERANCE);
		
		assertArrayEquals(Matrix.multiply(x, Ad), Ar.preMultiply(x), TOLERANCE);
		assertArrayEquals(Matrix.multiply(x, Matrix.transpose(Bd)), Br.transpose().preMultiply(x), TOLERANCE);
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
		NumericTestUtils.assert2dArrayEquals(Af, Matrix.inverse(Ai), TOLERANCE * 10);
	}
	
	@Test
	public void testMatrixInverseDouble() {
		double[][] AA = Matrix.toDouble(Af);
		double[][] AAi = Matrix.inverse(AA);
		NumericTestUtils.assert2dArrayEquals(AA, Matrix.inverse(AAi), TOLERANCE);
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
		assertEquals(Matrix.determinant3x3(Af), Matrix.determinant(Ad), TOLERANCE);
	}
	
	@Test(expected = IncompatibleDimensionsException.class)
	public void testMatrixDeterminant3x3Fail() {
		Matrix.determinant3x3(Bd);
	}
	
	@Test
	public void testMatrixDeterminant2x2() {
		double[][] Md = {{2, 7}, {-1, 5}};
		float[][] Mf = Matrix.toFloat(Md);
		assertEquals(Matrix.determinant2x2(Md), Matrix.determinant(Md), TOLERANCE);
		assertEquals(Matrix.determinant2x2(Mf), Matrix.determinant(Md), TOLERANCE);
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
	
	@Test
	public void testMatrixIsSymmetric() {
		double[][] X =
			{{1, 2, 3},
			 {2, 0, 6},
			 {3, 6, 9}};
		assertTrue(Matrix.isSymmetric(X));
		assertTrue(Matrix.isSymmetric(new double[][] {{1}}));
		assertFalse(Matrix.isSymmetric(Ad));
	}
	
	// --------------------------------------------------------------------
	
	@Test
	public void testMatrixIsSingular() {
		double[][] X =
			{{1, 2, 3},
			 {4, 5, 6},
			 {7, 8, 9}};
		double[][] Y =
			{{3, 6},
			 {2, 4}};
		double[][] Z =
			{{1, 2, 2},
			 {1, 2, 2},
			 {3, 2, -1}};
		assertTrue(Matrix.isSingular(X));
		assertTrue(Matrix.isSingular(Y));
		assertTrue(Matrix.isSingular(Z));
		
		assertFalse(Matrix.isSingular(Ad));
		assertFalse(Matrix.isSingular(new double[][] {{3, 2}, {1, -2}}));	
	}
	
	@Test(expected = NonsquareMatrixException.class)
	public void testMatrixIsSingularFail() {
		Matrix.isSingular(Bd);
	}
	
	// --------------------------------------------------------------------

	@Test
	public void testMatrixIsPositiveDefinite() {
		double[][] X =
			{{2, -1, 0},
			 {-1, 2, -1},
			 {0, -1, 2}};
		double[][] Y =
			{{-3, 2, 0},
			 {2, -3, 0},
			 {0, 0, 5}};
		double[][] Z1 =
			{{7, 2},
			 {2, 1}};
		double[][] Z2 =
			{{7, 2},
			 {2, -1}};
		assertTrue(Matrix.isPositiveDefinite(MatrixUtils.createRealMatrix(X)));
		assertFalse(Matrix.isPositiveDefinite(MatrixUtils.createRealMatrix(Y)));
		assertTrue(Matrix.isPositiveDefinite(MatrixUtils.createRealMatrix(Z1)));
		assertFalse(Matrix.isPositiveDefinite(MatrixUtils.createRealMatrix(Z2)));
	}
	
	@Test(expected = NonSymmetricMatrixException.class)
	public void testMatrixIsPositiveDefiniteFail() {
		assertFalse(Matrix.isPositiveDefinite(MatrixUtils.createRealMatrix(Ad)));
	}
	
	// --------------------------------------------------------------------
	
	@Test
	public void testMatrixDistL2Double() {
		double[] x = {5, 3, -1, 7};
		double[] y = {-1, 3, 2, 4};
		assertEquals(7.3484692283495345, Matrix.distL2(x, y), TOLERANCE);
		assertEquals(Matrix.distL2(y, x), Matrix.distL2(x, y), TOLERANCE);
		assertEquals(sqr(Matrix.distL2(x, y)), Matrix.distL2squared(x, y), TOLERANCE);
	}
	
	// --------------------------------------------------------------------
	
	@Test
	public void testMatrixDistL2Float() {
		float[] x = {5, 3, -1, 7};
		float[] y = {-1, 3, 2, 4};
		assertEquals(7.3484692283495345, Matrix.distL2(x, y), TOLERANCE);
		assertEquals(Matrix.distL2(y, x), Matrix.distL2(x, y), TOLERANCE);
		assertEquals(sqr(Matrix.distL2(x, y)), Matrix.distL2squared(x, y), TOLERANCE);
	}
	
	@Test
	public void testMatrixNormL1Double() {
		double[] x = {5, 3, -1, 7};
		double[] y = {6};
		double[] z = {0};
		assertEquals(16.0, Matrix.normL1(x), TOLERANCE);
		assertEquals(6.0, Matrix.normL1(y), TOLERANCE);
		assertEquals(0.0, Matrix.normL1(z), TOLERANCE);
	}
	
	@Test
	public void testMatrixNormL1Float() {
		float[] x = {5, 3, -1, 7};
		float[] y = {6};
		float[] z = {0};
		assertEquals(16.0, Matrix.normL1(x), TOLERANCE);
		assertEquals(6.0, Matrix.normL1(y), TOLERANCE);
		assertEquals(0.0, Matrix.normL1(z), TOLERANCE);
	}
	
	@Test
	public void testMatrixNormL2Double() {
		double[] x = {5, 3, -1, 7};
		double[] y = {6};
		double[] z = {0};
		assertEquals(9.16515138991168, Matrix.normL2(x), TOLERANCE);
		assertEquals(6.0, Matrix.normL2(y), TOLERANCE);
		assertEquals(0.0, Matrix.normL2(z), TOLERANCE);
		
		assertEquals(sqr(Matrix.normL2(x)), Matrix.normL2squared(x), TOLERANCE);
		assertEquals(sqr(Matrix.normL2(y)), Matrix.normL2squared(y), TOLERANCE);
	}
	
	@Test
	public void testMatrixNormL2Float() {
		float[] x = {5, 3, -1, 7};
		float[] y = {6};
		float[] z = {0};
		assertEquals(9.16515138991168, Matrix.normL2(x), TOLERANCE);
		assertEquals(6.0, Matrix.normL2(y), TOLERANCE);
		assertEquals(0.0, Matrix.normL2(z), TOLERANCE);
		
		assertEquals(sqr(Matrix.normL2(x)), Matrix.normL2squared(x), TOLERANCE);
		assertEquals(sqr(Matrix.normL2(y)), Matrix.normL2squared(y), TOLERANCE);
	}
	
	@Test
	public void testMatrixNorm() {
		assertEquals(16.881943016134134, Matrix.norm(Ad), TOLERANCE);
		assertEquals(9.539392014169456, Matrix.norm(Bd), TOLERANCE);
		assertEquals(6.0, Matrix.norm(new double[][] {{6}}), TOLERANCE);
		assertEquals(1.0, Matrix.norm(new double[][] {{-1}}), TOLERANCE);
	}
	
	// ---------------------------------------------------------
	
	@Test
	public void testMatrixNormalize() {
		double[] xd = {5, 3, -1, 7};
		double[] yd = {6};
		assertEquals(1.0, Matrix.normL2(Matrix.normalize(xd)), TOLERANCE);
		assertEquals(1.0, Matrix.normL2(Matrix.normalize(yd)), TOLERANCE);
		
		float[] xf = {5, 3, -1, 7};
		float[] yf = {6};
		assertEquals(1.0, Matrix.normL2(Matrix.normalize(xf)), TOLERANCE);
		assertEquals(1.0, Matrix.normL2(Matrix.normalize(yf)), TOLERANCE);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMatrixNormalizeFailDouble() {
		double[] x = {0, 0, 0, 0};
		assertEquals(1.0, Matrix.normL2(Matrix.normalize(x)), TOLERANCE);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMatrixNormalizeFailFloat() {
		float[] x = {0, 0, 0, 0};
		assertEquals(1.0, Matrix.normL2(Matrix.normalize(x)), TOLERANCE);
	}
	
	// ---------------------------------------------------------
	
	@Test
	public void testMatrixSumDouble() {
		double[] x = {5, 3, -1, 7};
		double[] y = {6};
		double[] z = {0};
		assertEquals(14.0, Matrix.sum(x), TOLERANCE);
		assertEquals(6.0, Matrix.sum(y), TOLERANCE);
		assertEquals(0.0, Matrix.sum(z), TOLERANCE);
		
		assertEquals(43.0, Matrix.sum(Ad), TOLERANCE);
		assertEquals(19.0, Matrix.sum(Bd), TOLERANCE);
	}
	
	@Test
	public void testMatrixSumFloat() {
		float[] x = {5, 3, -1, 7};
		float[] y = {6};
		float[] z = {0};
		assertEquals(14.0, Matrix.sum(x), TOLERANCE);
		assertEquals(6.0, Matrix.sum(y), TOLERANCE);
		assertEquals(0.0, Matrix.sum(z), TOLERANCE);
		
		assertEquals(43.0, Matrix.sum(Af), TOLERANCE);
		assertEquals(19.0, Matrix.sum(Bf), TOLERANCE);	
	}
	
	// ---------------------------------------------------------
	
	@Test
	public void testMatrixSumRowDouble() {
		assertEquals( 4.0, Matrix.sumRow(Ad, 0), TOLERANCE);
		assertEquals(15.0, Matrix.sumRow(Ad, 1), TOLERANCE);
		assertEquals(24.0, Matrix.sumRow(Ad, 2), TOLERANCE);
	}
	
	@Test
	public void testMatrixSumRowFloat() {
		assertEquals( 4.0, Matrix.sumRow(Af, 0), TOLERANCE);
		assertEquals(15.0, Matrix.sumRow(Af, 1), TOLERANCE);
		assertEquals(24.0, Matrix.sumRow(Af, 2), TOLERANCE);
	}

	@Test
	public void testMatrixSumColumnDouble() {
		assertEquals(10.0, Matrix.sumColumn(Ad, 0), TOLERANCE);
		assertEquals(15.0, Matrix.sumColumn(Ad, 1), TOLERANCE);
		assertEquals(18.0, Matrix.sumColumn(Ad, 2), TOLERANCE);
	}
	
	@Test
	public void testMatrixSumColumnFloat() {
		assertEquals(10.0, Matrix.sumColumn(Af, 0), TOLERANCE);
		assertEquals(15.0, Matrix.sumColumn(Af, 1), TOLERANCE);
		assertEquals(18.0, Matrix.sumColumn(Af, 2), TOLERANCE);
	}
	
	@Test
	public void testMatrixSumRowsDouble() {
		double[] sums = Matrix.sumRows(Ad);
		for (int r = 0; r < sums.length; r++) {
			assertEquals(sums[r], Matrix.sumRow(Ad, r), TOLERANCE);
		}
	}
	
	@Test
	public void testMatrixSumRowsFloat() {
		float[] sums = Matrix.sumRows(Af);
		for (int r = 0; r < sums.length; r++) {
			assertEquals(sums[r], Matrix.sumRow(Af, r), TOLERANCE);
		}
	}
	
	@Test
	public void testMatrixSumColumnsDouble() {
		double[] sums = Matrix.sumColumns(Ad);
		for (int c = 0; c < sums.length; c++) {
			assertEquals(sums[c], Matrix.sumColumn(Ad, c), TOLERANCE);
		}
	}
	
	@Test
	public void testMatrixSumColumnsFloat() {
		float[] sums = Matrix.sumColumns(Af);
		for (int c = 0; c < sums.length; c++) {
			assertEquals(sums[c], Matrix.sumColumn(Af, c), TOLERANCE);
		}
	}
	
	@Test
	public void testDoubleLongConversion() {
		RandomMatrixGenerator rg = new RandomMatrixGenerator(17);
		double[][] M = rg.makeRandomMatrix(3, 4);
		long[][] ML = Matrix.toLongBits(M);
		assertNotNull(ML);
		double[][] M2 = Matrix.fromLongBits(ML);
		NumericTestUtils.assert2dArrayEquals(M, M2, 0.0);
	}
	
	
	// ---------------------------------------------------------
	
	@Test	// book example (see Appendix B.8.1)
	public void testMatrixSolve() {
		double[][] A = {
				{2, 3, -2},
				{-1, 7, 6},
				{4, -3, -5}};
		double[] b = {1, -2, 1};
		double[] x = {-0.3698, 0.1780, -0.6027};
		assertArrayEquals(x, Matrix.solve(A, b), 1e-4);
		
		assertArrayEquals(new double[] {-3, 14, -10}, Matrix.solve(Ad, b), 1e-4);
		
		double[][] As = {	// singular matrix has no solution
				{1, 2, 3},
				{1, 2, 3},
				{4, -3, -5}};	
		Assert.assertNull(Matrix.solve(As, b));
	}
	
	@Test(expected = NonsquareMatrixException.class)
	public void testMatrixSolveFail1() {
		double[] b = {1, -2, 1};
		Matrix.solve(Bd, b);
	}
	
	@Test(expected = IncompatibleDimensionsException.class)
	public void testMatrixSolveFail2() {
		double[] b = {1, -2, 1, 5};
		Matrix.solve(Ad, b);
	}
}

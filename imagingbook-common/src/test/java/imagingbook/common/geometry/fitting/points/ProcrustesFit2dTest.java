/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fitting.points;

import static imagingbook.testutils.NumericTestUtils.assert2dArrayEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.math4.legacy.linear.MatrixUtils;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.math.PrintPrecision;

public class ProcrustesFit2dTest {

	@Test
	public void test1() {
		PrintPrecision.set(6);
		int NDIGITS = 1;
		
		boolean allowTranslation = true;
		boolean allowScaling = true;
		boolean forceRotation = true;
		
		double alpha = 0.6;
		double[][] R0data =
			{{ Math.cos(alpha), -Math.sin(alpha) },
			 { Math.sin(alpha),  Math.cos(alpha) }};
		
		RealMatrix R0 = MatrixUtils.createRealMatrix(R0data);
		double[] t0 = {4, -3};
		double s0 = 3.5;
		
//		System.out.format("original alpha: a = %.6f\n", alpha);
//		System.out.println("original rotation: R = \n" + Matrix.toString(R0.getData()));
//		System.out.println("original translation: t = " + Matrix.toString(t0));
//		System.out.format("original scale: s = %.6f\n", s0);
		
		Pnt2d[] P = {
				PntInt.from(2, 5),
				PntInt.from(7, 3),
				PntInt.from(0, 9),
				PntInt.from(5, 4)
		};
		
		double Eexp = 0.048079;
		
		Pnt2d[] Q = new Pnt2d[P.length];
		
		for (int i = 0; i < P.length; i++) {
			Pnt2d q = PntDouble.from(R0.operate(P[i].toDoubleArray()));
			// add noise
			double qx = roundToDigits(s0 * q.getX() + t0[0], NDIGITS);
			double qy = roundToDigits(s0 * q.getY() + t0[1], NDIGITS);
			Q[i] = Pnt2d.PntDouble.from(qx, qy);
		}
		
		//P[0] = Point.create(2, 0);	// to provoke a large error
		
		ProcrustesFit2d pf = new ProcrustesFit2d(P, Q, allowTranslation, allowScaling, forceRotation);

		double[][] R = pf.getRotation();
		double[] T = pf.getTranslation();
		double S = pf.getScale();
		double E = pf.getError();
//		double[][] A = pf.getTransformationMatrix();
		
//		System.out.format("estimated alpha: a = %.6f\n", Math.acos(R[0][0]));
//		System.out.println("estimated rotation: R = \n" + Matrix.toString(R));
//		System.out.println("estimated translation: t = " + Matrix.toString(T));
//		System.out.format("estimated scale: s = %.6f\n", S);
//		System.out.format("fitting error = %.6f\n", pf.getError());
//		System.out.println("transformation matrix: A = \n" + Matrix.toString(A));
		
		assertEquals(alpha, Math.acos(R[0][0]), 1e-3);
		assert2dArrayEquals(R0data, R, 1e-3);
		assertArrayEquals(t0, T, 1e-1);
		assertEquals(s0, S, 1e-3);
		assertEquals(Eexp, E, 1e-3);
	}
	
	
	private static double roundToDigits(double x, int ndigits) {
		int d = (int) Math.pow(10, ndigits);
		return Math.rint(x * d) / d;
	}

}

package imagingbook.common.math;

import static imagingbook.common.math.Arithmetic.isZero;
import static imagingbook.common.math.Arithmetic.sqr;

/**
 * This code is volatile, don't use yet!
 * 
 * @author WB
 * @version 2021/08/26
 */
public class ChiSquaredDistance {
	
	public static double distance(double[] P, double[] Q) {
		if (P.length != Q.length) {
			throw new IllegalArgumentException("vectors P, Q have different lengths");
		}	
		double sum = 0;
		for (int i = 0; i < P.length; i++) {
			double denom = P[i] + Q[i];
			if (!isZero(denom)) {
				sum = sum + sqr(P[i] - Q[i]) / denom;
			}
		}
		return 0.5 * sum;
	}
	
	// ------------------------------------------------------------------
	
	private static void testDistance(double[] P, double[] Q) {
		System.out.println();
		System.out.println("P = " + Matrix.toString(P));
		System.out.println("Q = " + Matrix.toString(Q));
		System.out.format("D(P,Q) = %.6f\n", distance(P, Q));
		System.out.format("D(Q,P) = %.6f\n", distance(Q, P));
		
		System.out.format("D(P,P) = %.6f\n", distance(P, P));
		System.out.format("D(Q,Q) = %.6f\n", distance(Q, Q));
	}

	public static void main(String[] args) {
		{
			double[] P = {1, 2, 13, 5, 45, 23};
			double[] Q = {67, 90, 18, 79, 24, 98};
			testDistance(P, Q);
		}	
		{
			double[] P = {91, 900, 78, 30, 602, 813};
			double[] Q = {57, 49, 36, 759, 234, 928};
			testDistance(P, Q);
		}
		
	}
}

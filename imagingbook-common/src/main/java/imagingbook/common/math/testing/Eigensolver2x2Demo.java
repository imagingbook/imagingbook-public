package imagingbook.common.math.testing;

import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.math.eigen.Eigensolver2x2;

/**
 * This is a small demo program to show the use of the
 * {@link Eigensolver2x2} class.
 * It takes a 2x2 real-valued matrix and calculates its two
 * eigenvalues and eigenvectors.
 * Results are printed and checked.
 * 
 * @author WB
 *@version 2020/03/25
 */
@Deprecated 			// move to tests!
public class Eigensolver2x2Demo {
	
	public static void main(String[] args) {
		
		PrintPrecision.set(6);
		
		// specify a 2x2 matrix
		double[][] M = {
				{3, -2},
				{-4, 1}};
		
		System.out.format("M =\n%s\n\n", Matrix.toString(M));
		
		Eigensolver2x2 solver = new Eigensolver2x2(M);
		double[]   eigenvalues  = solver.getEigenvalues();
		//double[][] eigenvectors = solver.getEigenvectors();
		
		System.out.println("eigenvalues/eigenvectors: check if M*x = \u03BB*x \n");
		
		for (int i = 1; i <= 2; i++) {
			// get the eigen-pair <lambda_i, x_i>
			double lambda = eigenvalues[i-1];
			double[] x = solver.getEigenvector(i-1);
			
			System.out.format("\u03BB_%d  = %.6f\n", i, lambda);
			System.out.format("x_%d = %s\n", i, Matrix.toString(x));
			
			System.out.format("Check: \u03BB_%d * x_%d = %s\n", i, i, Matrix.toString(Matrix.multiply(lambda, x)));
			System.out.format("Check:   M * x_%d = %s\n\n", i, Matrix.toString(Matrix.multiply(M, x)));
		}

	}
}

/*
M =
{{3.000000, -2.000000}, 
{-4.000000, 1.000000}}

eigenvalues/eigenvectors: check if M*x = λ*x 

λ_1  = 5.000000
x_1 = {4.000000, -4.000000}
Check: λ_1 * x_1 = {20.000000, -20.000000}
Check:   M * x_1 = {20.000000, -20.000000}

λ_2  = -1.000000
x_2 = {-2.000000, -4.000000}
Check: λ_2 * x_2 = {2.000000, 4.000000}
Check:   M * x_2 = {2.000000, 4.000000}
*/
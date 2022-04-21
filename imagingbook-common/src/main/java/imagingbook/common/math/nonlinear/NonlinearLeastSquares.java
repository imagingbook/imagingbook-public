package imagingbook.common.math.nonlinear;

import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.sin;
import static org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory.model;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;

import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

/**
 * Static utility methods for simplified access to nonlinear least-squares
 * solvers in Apache Commons Math.
 * Hides much of the available configuration options.
 * If other than default settings are needed, the original API should be used.
 * 
 * TODO: Needs cleanup and tests.
 * 
 * @author WB
 *
 */
public abstract class NonlinearLeastSquares {
	
	public static int MaxEvaluations = 1000;
	public static int MaxIterations = 1000;
	public static double Tolerance = 1e-6;
	
	/**
	 * Solves the nonlinear least-squares problem defined by the arguments.
	 * Delegates to the Levenberg-Marquardt method.
	 * 
	 * @param V the "value" function, V(p) must return a vector for the current parameters p
	 * @param J the "Jacobian" function, J(p) must return a matrix for the current parameters p
	 * @param z the vector of observed ("target") values
	 * @param p0 initial parameter vector
	 * @return the vector of optimal parameters
	 */
	public static RealVector solveNLS(MultivariateVectorFunction V, MultivariateMatrixFunction J, 
			RealVector z, RealVector p0) {
		return solveLevenvergMarquardt(V, J, z, p0);
	}
	
	/**
	 * Solves the nonlinear least-squares problem defined by the arguments
	 * using Levenberg-Marquardt optimization.
	 * 
	 * @param V the "value" function, V(p) must return a vector for the current parameters p
	 * @param J the "Jacobian" function, J(p) must return a matrix for the current parameters p
	 * @param z the vector of observed ("target") values
	 * @param p0 initial parameter vector
	 * @return the vector of optimal parameters
	 */
	public static RealVector solveLevenvergMarquardt(MultivariateVectorFunction V, MultivariateMatrixFunction J, 
						RealVector z, RealVector p0) {
		LeastSquaresProblem problem = makeProblem(V, J, z, p0);
		LeastSquaresOptimizer optimizer = new LevenbergMarquardtOptimizer();
		Optimum solution = optimizer.optimize(problem);
		//System.out.println("iterations = " + solution.getIterations());
		return solution.getPoint();
	}
	
	/**
	* Solves the nonlinear least-squares problem defined by the arguments
	 * using Gauss-Newton optimization.
	 * 
	 * @param V the "value" function, V(p) must return a vector for the current parameters p
	 * @param J the "Jacobian" function, J(p) must return a matrix for the current parameters p
	 * @param z the vector of observed ("target") values
	 * @param p0 initial parameter vector
	 * @return the vector of optimal parameters
	 */
	public static RealVector solveGaussNewton(MultivariateVectorFunction V, MultivariateMatrixFunction J, 
						RealVector z, RealVector p0) {
		LeastSquaresProblem problem = makeProblem(V, J, z, p0);
		LeastSquaresOptimizer optimizer = new GaussNewtonOptimizer();
		Optimum solution = optimizer.optimize(problem);
		//System.out.println("iterations = " + solution.getIterations());
		return solution.getPoint();
	}
	
	// --------------------------------------------------------------------
	
	private static LeastSquaresProblem makeProblem(MultivariateVectorFunction V, MultivariateMatrixFunction J, 
					RealVector observed, RealVector start) {	
		return LeastSquaresFactory.create(model(V, J), observed, start,
			LeastSquaresFactory.evaluationChecker(new SimpleVectorValueChecker(Tolerance, Tolerance)),
			MaxEvaluations,	MaxIterations);
	}
	
	// --------------------------------------------------------------------
	// book version
	// --------------------------------------------------------------------
	
	public static RealVector solveNLS2(
			MultivariateVectorFunction V, 
			MultivariateMatrixFunction J, 
			RealVector z, RealVector p0) 
	{
		LeastSquaresProblem problem = 
				LeastSquaresFactory.create(model(V, J), z, p0, null,
						MaxEvaluations,	MaxIterations);
		LeastSquaresOptimizer optimizer = new LevenbergMarquardtOptimizer();
		Optimum solution = optimizer.optimize(problem);
//		System.out.println("iterations = " + solution.getIterations());
		if (solution.getIterations() > MaxIterations)
			return null;
		else 
			return solution.getPoint();
	}
	
	// ---------------------------------------------------------------------

	private static RealVector makeTargetVector(double[][] data) {
		int n = data.length;
		RealVector target = new ArrayRealVector(n);
		for (int i = 0; i < n; i++) {
			target.setEntry(i, data[i][1]);
		}
		return target;
	}

	public static void main(String[] args) {
		PrintPrecision.set(6);
		
		// 1D fitting example in book appendix: f(x) = exp(-a * x) * sin(b * x) + c
		double[][] data = {{3, 2.5}, {6, 1.7}, {8, 2.5}, {8, 2.3}, {15, 2.1}}; // (xi, yi)
		
		final int n = data.length;
		final int k = 3; 				// number of parameters
		
		MultivariateVectorFunction V = new MultivariateVectorFunction() {
			@Override
			public double[] value(double[] p) {
				double[] V = new double[n];
				double a = p[0];
				double b = p[1];
				double c = p[2];
				for (int i = 0; i < n; i++) {
					double x = data[i][0];
					V[i] = exp(-a * x) * sin(b * x) + c;
				}
				return V;
			}
		};
		
		MultivariateMatrixFunction J = new MultivariateMatrixFunction() {
			@Override
			public double[][] value(double[] p) {
				double[][] J = new double[n][k];
				double a = p[0];
				double b = p[1];
//				double c = p[2];
				for (int i = 0; i < n; i++) {			
					double x = data[i][0];
					J[i][0] = -exp(-a * x) * x * sin(b * x); // df(x)/da
					J[i][1] =  exp(-a * x) * x * cos(b * x); // df(x)/db
					J[i][2] = 1; 							 //	df(x)/dc
				}
				return J;
			}
		};
		
		RealVector z = makeTargetVector(data);
		RealVector p0 = new ArrayRealVector(new double[] {0,1,2});
		{
			RealVector popt = solveLevenvergMarquardt(V, J, z, p0);
			System.out.println("Levenberg:    popt = " + Matrix.toString(popt.toArray()));
		}
		{
			RealVector popt = solveGaussNewton(V, J, z, p0);
			System.out.println("Gauss-Newton:    popt = " + Matrix.toString(popt.toArray()));
		}
		{
			MaxIterations = 10;
			RealVector popt = solveNLS2(V, J, z, p0);
			System.out.println("solveNLS2 (LM): popt = " + Matrix.toString(popt.toArray()));
		}
	}
}

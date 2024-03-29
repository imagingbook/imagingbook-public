/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math.nonlinear;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;

import static org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory.model;

/**
 * <p>
 * This class defines static methods for simplified access to nonlinear least-squares solvers in Apache Commons Math,
 * hiding much of the available configuration options. If other than default settings are needed, the original (Apache
 * Commons Math) API should be used. See the Appendix C of [1] for additional details and examples.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public abstract class NonlinearLeastSquares {
	
	private NonlinearLeastSquares() {}
	
	public static int MaxEvaluations = 1000;
	public static int MaxIterations = 1000;
	public static double Tolerance = 1e-6;

	/**
	 * Solves the nonlinear least-squares problem defined by the arguments. Delegates to the Levenberg-Marquardt
	 * method.
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
	 * Solves the nonlinear least-squares problem defined by the arguments using Levenberg-Marquardt optimization.
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
	 * Solves the nonlinear least-squares problem defined by the arguments using Gauss-Newton optimization.
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

}

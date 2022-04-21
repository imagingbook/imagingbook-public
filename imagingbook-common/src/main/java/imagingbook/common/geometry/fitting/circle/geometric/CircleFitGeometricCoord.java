package imagingbook.common.geometry.fitting.circle.geometric;

import static imagingbook.common.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;
import static org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory.evaluationChecker;
import static org.apache.commons.math3.linear.MatrixUtils.createRealVector;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.apache.commons.math3.util.Pair;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitPratt;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

/**
 * "Coordinate-based" geometric circle fit using iterative minimization using
 * a Levenberg-Marquart optimizer.
 * 
 * @author WB
 *
 */
public class CircleFitGeometricCoord extends CircleFitGeometric {
	
	private final Pnt2d[] pts;
	private final double[] V;
	private final double[][] J;
	
	private final Optimum solution;	
	private final List<double[]> history = new LinkedList<>();
	
	public CircleFitGeometricCoord(Pnt2d[] pts, GeometricCircle initCircle) {
		this(pts, initCircle, DefaultMaxIterations, DefaultMaxIterations, DefaultTolerance);
	}
	
	public CircleFitGeometricCoord(Pnt2d[] pts, GeometricCircle initCircle, int maxEvaluations, int maxIterations, double tolerance) {
		this.pts = pts;
		this.V = new double[2 * pts.length];
		this.J = new double[2 * pts.length][3];

		LeastSquaresProblem problem = 
				LeastSquaresFactory.create(
					new AnalyticModel(), // model(V, J), 
					makeTargetVector(pts),
					createRealVector(initCircle.getParameters()),
					evaluationChecker(new SimpleVectorValueChecker(tolerance, tolerance)),
					maxEvaluations,	maxIterations);
		
		LeastSquaresOptimizer optimizer = new LevenbergMarquardtOptimizer();	// new GaussNewtonOptimizer();	
		this.solution = optimizer.optimize(problem);
	}
	
	// --------------------------------------------------------------------------
	
	@Override
	public double[] getParameters() {
		return solution.getPoint().toArray();
	}
	
	@Override
	public int getIterations() {
		return solution.getIterations();
	}
	
	@Override
	public List<double[]> getHistory() {
		return history;
	}
	
	
	private RealVector makeTargetVector(final Pnt2d[] X) {
		final int n = X.length;
		final double[] target = new double[2*n];
		for (int i = 0; i < n; i++) {
			target[2*i]   = X[i].getX();		// x_i
			target[2*i+1] = X[i].getY();		// y_i
		}
		return MatrixUtils.createRealVector(target);
	}
	
	// --------------------------------------------------------------------------
    
    /**
     * Defines function {@link #value(RealVector)} which returns
     * the vector of model values and the associated Jacobian matrix
     * for a given parameter point.
     */
    class AnalyticModel implements MultivariateJacobianFunction {
    	int k = 0;

		@Override
		public Pair<RealVector, RealMatrix> value(RealVector point) {
			k++;
			final double[] p = point.toArray();
			if (RecordHistory) {
				history.add(p.clone());
			}
			final double xc = p[0], yc = p[1], r  = p[2];
			for (int i = 0; i < pts.length; i++) {
				final double dx = pts[i].getX() - xc;
				final double dy = pts[i].getY() - yc;
				final double ri2 = sqr(dx) + sqr(dy);	// r_i^2
				final double ri =  sqrt(ri2);			// r_i
				final double ri3 = ri2 * ri;			// r_i^3
				
				// values: // TODO: check for ri == 0
				V[2*i]   = xc + dx * r / ri;
				V[2*i+1] = yc + dy * r / ri;
				
				// Jacobian:			
				J[2*i][0]   = 1 + (r / ri) * (sqr(dx) / ri2 - 1); // 1 + (r * sqr(dx) / ri3) - (r / ri); 
				J[2*i][1]   = r * dx * dy / ri3;
				J[2*i][2]   = dx / ri;
				
				J[2*i+1][0] = r * dx * dy / ri3;
				J[2*i+1][1] = 1 + (r / ri) * (sqr(dy) / ri2 - 1); // 1 + (r * sqr(dy) / ri3) - (r / ri);
				J[2*i+1][2] = dy / ri;
			}
			if (k == 1) {
				System.out.println("V = " + Matrix.toString(V));
				System.out.println("J = \n" + Matrix.toString(J));
			}
			RealVector VV = new ArrayRealVector(V, false);
			RealMatrix JJ = new Array2DRowRealMatrix(J, false);
			return new Pair<>(VV, JJ);
		}
    }
    
    // -------------------------------------------------------------------
    
    public static void main(String[] args) {
    	PrintPrecision.set(3);
    	CircleFitGeometric.RecordHistory = true;
    	
    	Pnt2d[] points = {
				Pnt2d.from(15,9),
				Pnt2d.from(68,33),
				Pnt2d.from(35,69),
				Pnt2d.from(17,51),
				Pnt2d.from(90,54)
		};
    	
		GeometricCircle estimA = new CircleFitPratt(points).getGeometricCircle();
		System.out.println("estimA: " + estimA.toString());
		System.out.println("estimA error = " + estimA.getMeanSquareError(points));
		
		GeometricCircle init = new GeometricCircle(45, 40, 30);		// Example (a)
//		GeometricCircle init = new GeometricCircle(75, 75, 12);		// Example (b)
		//GeometricCircle init = estimA;
//		GeometricCircle init = estimA.disturb(0, 0, 0);
		System.out.println(" init: " + init.toString());
		System.out.println(" init error = " + init.getMeanSquareError(points));
		
		CircleFitGeometricCoord geomfitter = new CircleFitGeometricCoord(points, init);
		GeometricCircle circleG = geomfitter.getCircle();
//		
//		//Circle2D refined = Doube.levenMarqFull(points, init);
//		
		System.out.println("circleG: " + circleG.toString());
		System.out.println("iterations: " + geomfitter.getIterations());
		System.out.println("final error = " + circleG.getMeanSquareError(points));
		for (double[] p : geomfitter.getHistory()) {
			System.out.println(Matrix.toString(p));
		}
    }

}

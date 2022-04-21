package imagingbook.common.geometry.fitting.circle.geometric;

import static imagingbook.common.math.Arithmetic.isZero;
import static java.lang.Math.hypot;
import static org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory.evaluationChecker;

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
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.apache.commons.math3.util.Pair;

import ij.IJ;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.circle.GeometricCircle;
import imagingbook.common.geometry.fitting.circle.algebraic.CircleFitPratt;
import imagingbook.common.math.PrintPrecision;

/**
 * "Distance-based" geometric circle fitter using a
 * Levenberg-Marquart optimizer.
 * 
 * @author WB
 *
 */
public class CircleFitGeometricDist extends CircleFitGeometric {
	
	private final Pnt2d[] pts;
	private final double[] V;
	private final double[][] J;
	private final Optimum solution;
	private final List<double[]> history = new LinkedList<>();
	
	public CircleFitGeometricDist(Pnt2d[] pts, GeometricCircle initCircle) {
		this(pts, initCircle, DefaultMaxIterations, DefaultMaxIterations, DefaultTolerance);
	}
	
	public CircleFitGeometricDist(Pnt2d[] pts, GeometricCircle initCircle, int maxEvaluations, int maxIterations, double tolerance) {
		this.pts = pts;
		this.V = new double[pts.length];
		this.J = new double[pts.length][3];
		
		LeastSquaresProblem problem = 
				LeastSquaresFactory.create(
					new AnalyticModel(), // model(V, J), 
					new ArrayRealVector(pts.length),		// zero vector
					new ArrayRealVector(initCircle.getParameters()),
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
	
	// --------------------------------------------------------------------------
    
    /**
     * Defines function {@link #value(RealVector)} which returns
     * the vector of model values and the associated Jacobian matrix
     * for a given parameter point.
     */
    class AnalyticModel implements MultivariateJacobianFunction {

		@Override
		public Pair<RealVector, RealMatrix> value(RealVector point) {
			final double[] p = point.toArray();
			if (RecordHistory) {
				history.add(p.clone());
			}
			
			final double xc = p[0], yc = p[1], r  = p[2];
			for (int i = 0; i < pts.length; i++) {
				double dx = xc - pts[i].getX();
				double dy = yc - pts[i].getY();
				double ri = hypot(dx, dy);
				
				// values:
				V[i] = ri - r;
				
				// Jacobian:
				if (isZero(ri)) { 	// (xi,yi) is at the circle center
					J[i][0] = 0; 	// (xc - xi) = 0 too
					J[i][1] = 0; 	// (yc - yi) = 0 too
				} else {
					J[i][0] = dx / ri;
					J[i][1] = dy / ri;
				}
				J[i][2] = -1;
			}
			RealVector VV = new ArrayRealVector(V, false);
			RealMatrix JJ = new Array2DRowRealMatrix(J, false);
			return new Pair<>(VV, JJ);
		}
    	
    }
    
    // -------------------------------------------------------------------
    
    public static void main(String[] args) {
    	PrintPrecision.set(9);
    	//Pnt2d[] points = CircleMaker.makeTestCircle(XC, YC, R, 100, Angle0, Angle1, SigmaNoise);
    	//Pnt2d[] points = CircleMaker.makeTestCircle(XC, YC, R, 4, Angle0, Angle1, 0.1);
    	//Pnt2d[] points = CircleMaker.makeTestGander(30);
    	//Pnt2d[] points = CircleSampler.make3Points(30);
    	
    	Pnt2d[] points = {
				Pnt2d.from(15,9),
				Pnt2d.from(68,33),
				Pnt2d.from(35,69),
				Pnt2d.from(17,51),
				Pnt2d.from(90,54)
		};
    		
//    	Circle2D real = new Circle2D(XC, YC, R);
//    	IJ.log(" real: " + real.toString());
    	
//    	Pnt2d[] points = makeTestPoints();
		
		GeometricCircle estimA = new CircleFitPratt(points).getGeometricCircle();
		IJ.log("estimA: " + estimA.toString());
		IJ.log("estimA error = " + estimA.getMeanSquareError(points));
		
		//Circle2D init = new Circle2D(XC, YC, R);
		//Circle2D init = estimA;
		GeometricCircle init = estimA.disturb(-1.5, 0.5, 10);
		IJ.log(" init: " + init.toString());
		IJ.log(" init error = " + init.getMeanSquareError(points));
		
		CircleFitGeometricDist geomfitter = new CircleFitGeometricDist(points, init);
		GeometricCircle refined = geomfitter.getCircle();
		
		//Circle2D refined = Doube.levenMarqFull(points, init);
		
		IJ.log("refin: " + refined.toString());
		IJ.log("iterations: " + geomfitter.getIterations());
		
		IJ.log("final error = " + refined.getMeanSquareError(points));
    }

}

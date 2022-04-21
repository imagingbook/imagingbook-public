package imagingbook.common.geometry.fitting.ellipse.geometric;

import static imagingbook.common.math.Arithmetic.sqr;
import static imagingbook.common.math.Matrix.makeRealMatrix;
import static imagingbook.common.math.Matrix.subtract;
import static org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory.evaluationChecker;
import static org.apache.commons.math3.linear.MatrixUtils.createRealVector;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory;
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
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.geometry.fitting.ellipse.EllipseSampler;
import imagingbook.common.geometry.fitting.ellipse.algebraic.EllipseFitAlgebraic;
import imagingbook.common.geometry.fitting.ellipse.algebraic.EllipseFitFitzgibbonStable;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;
import imagingbook.common.util.ParameterBundle;

/**
 * "Coordinate-based" geometric ellipse fitter using iterative minimization with
 * the Levenberg-Marquart method.
 * 
 * @author WB
 *
 */
public class EllipseGeometricFitCoord extends EllipseFitGeometric {
	
	private final Pnt2d[] pts;
	private final double tolerance;
	private final int maxEvaluations;
	private final int maxIterations;
	
	private final double[] V;		// arrays for function values/Jacobian
	private final double[][] J;
	
	private final MultivariateJacobianFunction model;
	private final Optimum solution;
	private final List<double[]> history = new ArrayList<>();
	
	
	public EllipseGeometricFitCoord(Pnt2d[] pts, GeometricEllipse initEllipse, int maxEvaluations, 
			int maxIterations, double tolerance, boolean syntheticDeriv) {
		this.pts = pts;
		int n = pts.length;
		this.V = new double[2*n];
		this.J = new double[2*n][5];
		this.maxEvaluations = maxEvaluations;
		this.maxIterations = maxIterations;
		this.tolerance = tolerance;
		this.model = (syntheticDeriv) ? new SyntheticModel() : new AnalyticModel();
		this.solution = solveLM(initEllipse.getParameters());
	}
	
	public EllipseGeometricFitCoord(Pnt2d[] pts, GeometricEllipse initEllipse) {
		this(pts, initEllipse, DefaultMaxIterations, DefaultMaxIterations, DefaultTolerance, false);
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

	private Optimum solveLM(double[] p0) {
		LeastSquaresProblem problem = 
			LeastSquaresFactory.create(
				model, 
				makeTargetVector(pts),
				createRealVector(p0),
				evaluationChecker(new SimpleVectorValueChecker(tolerance, tolerance)),
				maxEvaluations,	maxIterations);

		LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
		Optimum solution = optimizer.optimize(problem);
		
		return solution;
	}
	
	/**
	 * Returns a vector of alternating x/y coordinates of the target points:
	 * {@code (x_0, y_0, x_1, y_1, ..., x_{n-1}, y_{n-1})}.
	 * 
	 * @param pts target points
	 * @return vector of length 2n
	 */
	private RealVector makeTargetVector(Pnt2d[] pts) {
		int n = pts.length;
		final double[] target = new double[2*n];
		for (int i = 0; i < n; i++) {
			Pnt2d p = pts[i];
			target[2*i]     = p.getX();
			target[2*i + 1] = p.getY();
		}
		return MatrixUtils.createRealVector(target);
	}
	
	// --------------------------------------------------------------------------
	
	class AnalyticModel implements MultivariateJacobianFunction {
		
		@Override
		public Pair<RealVector, RealMatrix> value(RealVector point) {
			final double[] p = point.toArray();
			if (RecordHistory) {
				history.add(p.clone());
			}
			final GeometricEllipse ellipse = new GeometricEllipse(p);
			final double ra = ellipse.ra;
			final double rb = ellipse.rb;
			final double xc = ellipse.xc;
			final double yc = ellipse.yc;
			final double theta = ellipse.theta;
			final double ra2 = sqr(ra);
			final double rb2 = sqr(rb);
			final double C = Math.cos(theta);
			final double S = Math.sin(theta);
			final double[] xyc = new double[] {xc, yc};
			RealMatrix R = makeRealMatrix(2, 2,
					C, -S, 
					S,  C);
			RealMatrix Rt = R.transpose();
			
			// calculate values and Jacobian
			for (int i = 0; i < pts.length; i++) {
				final int i2 = 2*i;
				Pnt2d XYi = pts[i];
				Pnt2d XYp = ellipse.getClosestPoint(XYi);
				
				// values
				V[i2]     = XYp.getX();
				V[i2 + 1] = XYp.getY();
				
				// Jacobian
				double[] xyi = XYi.toDoubleArray();
				double[] uvi = Rt.operate(subtract(xyi, xyc));					// target point in canon. coordinates
				double ui = uvi[0],  vi = uvi[1];								// = (u_i, v_i)

				double[] xyp = ellipse.getClosestPoint(xyi);					// ellipse point closest to Xi
				double xp = xyp[0], yp = xyp[1];								// = (\breve{x}_i, \breve{y}_i)

				double[] uvp = Rt.operate(subtract(xyp, xyc));					// closest point in canon. coordinates
				double up = uvp[0], vp = uvp[1];								// = (\breve{u}_i, \breve{u}_i)				

				RealMatrix Q1 = makeRealMatrix(2, 2,
						0, 0 ,
						vi - vp, up - ui );	
				RealMatrix Q2 = makeRealMatrix(2, 2,
						1/ra2, 0,
						0,   1/rb2 );
				RealMatrix Q3 = makeRealMatrix(2, 2,
						up/ra2,  vp/rb2,
						vp/rb2, -up/ra2 );
				RealMatrix Q = Q1.multiply(Q2).add(Q3);
				RealMatrix Qi = MatrixUtils.inverse(Q);

				RealMatrix T = makeRealMatrix(2, 2,
						0, 0,
						-vp/rb2, up/ra2 );

				RealMatrix U = makeRealMatrix(2, 5,
						0, 0,  -C, -S,  vi,
						0, 0,   S, -C, -ui );

				RealMatrix V1 = makeRealMatrix(2, 3,
						1, 0, 0,
						0, vi - vp, up - ui );
				RealMatrix V2 = makeRealMatrix(3, 5,
						-sqr(up)/(ra2*ra), -sqr(vp)/(rb2*rb), 0, 0, 0 ,
						-2*up/(ra2*ra), 0,                    0, 0, 0 ,
						0, -2*vp/(rb2*rb),                    0, 0, 0 );
				RealMatrix V = V1.multiply(V2);

				RealMatrix W = makeRealMatrix(2, 5,
						0, 0, 1, 0, yc - yp,
						0, 0, 0, 1, xp - xc );

				RealMatrix Ji = // J = -R * Q^-1 * (T * U + V) + W ..... 2x5 matrix
						R.scalarMultiply(-1).multiply(Qi).multiply(T.multiply(U).add(V)).add(W);
				
				for (int j = 0; j < 5; j++) {
					J[i2][j]     = Ji.getEntry(0, j);
					J[i2 + 1][j] = Ji.getEntry(1, j);
				}
			}
			
			RealVector VV = new ArrayRealVector(V, false);
			RealMatrix JJ = new Array2DRowRealMatrix(J, false);
			return new Pair<>(VV, JJ);
		}
		
	}
	
	// ---------------------------------------------------------------------------
	
	class SyntheticModel implements MultivariateJacobianFunction {
		double delta = 0.00001;

		@Override
		public Pair<RealVector, RealMatrix> value(RealVector point) {
			double[] p = point.toArray();
			if (RecordHistory) {
				history.add(p.clone());
			}
			GeometricEllipse ellipse = new GeometricEllipse(p);		// ellipse for current parameter point
			GeometricEllipse[] dEllipses = new GeometricEllipse[5];	// differentially modified ellipses
			for (int j = 0; j < 5; j++) {
				double[] pd = p.clone();	
				pd[j] = p[j] + delta;				// vary parameter j
				dEllipses[j] = new GeometricEllipse(pd);
			}
			
			// calculate values and Jacobian
			for (int i = 0; i < pts.length; i++) {
				final int i2 = 2*i;
				Pnt2d XYi = pts[i];
				Pnt2d XYp = ellipse.getClosestPoint(XYi);
				double xp = XYp.getX();
				double yp = XYp.getY();
				V[i2]     = xp;								// values = closest point coord.
				V[i2 + 1] = yp;	
				
				for (int j = 0; j < 5; j++) {
					Pnt2d XYpj = dEllipses[j].getClosestPoint(XYi);
					J[i2][j]     = (XYpj.getX() - xp) / delta;	// estimated partial derivative
					J[i2 + 1][j] = (XYpj.getY() - yp) / delta;
				}
			}
			
			RealVector VV = new ArrayRealVector(V, false);
			RealMatrix JJ = new Array2DRowRealMatrix(J, false);
			return new Pair<>(VV, JJ);
		}
		
	}
	
	// -------------------------------------------------------------------
	// -------------------------------------------------------------------
	
	public static class Parameters implements ParameterBundle {
		
		@DialogLabel("number of points")
		public int n = 20;
		
		@DialogLabel("ellipse center (xc)")
		public double xc = 200;
		
		@DialogLabel("ellipsecenter (yc)")
		public double yc = 190;
		
		@DialogLabel("major axis radius (ra)")
		public double ra = 170;
		
		@DialogLabel("minor axis radius (rb)")
		public double rb = 120;
		
		@DialogLabel("start angle (deg)")
		public double angle0 = 0;
		
		@DialogLabel("stop angle (deg)")
		public double angle1 = 180; // was Math.PI/4;
		
		@DialogLabel("ellipse orientation (deg)")
		public double theta = 45;
		
		@DialogLabel("x/y noise (sigma)")
		public double sigma = 5.0; //2.0;
	};
	
	private static Parameters params = new Parameters();
	
      
    public static void main(String[] args) {
    	
    	System.out.println("*** Testing "  + EllipseGeometricFitCoord.class.getSimpleName() + " ***");
    	
    	PrintPrecision.set(9);
    	
    	GeometricEllipse realEllipse = new GeometricEllipse(params.ra, params.rb, params.xc, params.yc, 
				Math.toRadians(params.theta));
    	
    	EllipseSampler sampler = new EllipseSampler(realEllipse, 17);
    	
    	Pnt2d[] points = sampler.getPoints(params.n, 
				Math.toRadians(params.angle0), Math.toRadians(params.angle1), params.sigma);
    	
//    	Pnt2d[] points = null;
    	//Pnt2d[] points = CircleMaker.makeTestCircle(XC, YC, R, 100, Angle0, Angle1, SigmaNoise);
    	//Pnt2d[] points = CircleMaker.makeTestCircle(XC, YC, R, 4, Angle0, Angle1, 0.1);
    	//Pnt2d[] points = CircleSampler.makeTestGander(30);
    	//Pnt2d[] points = CircleSampler.make3Points(30);
    		
    	EllipseFitAlgebraic fitA = new EllipseFitFitzgibbonStable(points);
    	
    	GeometricEllipse ellipseA = new GeometricEllipse(fitA.getEllipse());
    	System.out.println("ellipseA = " + ellipseA);
    	System.out.println("errorA = " + ellipseA.getMeanSquareError(points));
		
    	EllipseGeometricFitCoord fitG = new EllipseGeometricFitCoord(points, ellipseA);
    	GeometricEllipse ellipseG = fitG.getEllipse();
    	System.out.println("ellipseG = " + ellipseG);
    	System.out.println("errorG = " + ellipseG.getMeanSquareError(points));
    	System.out.println("iterations = " + fitG.getIterations());
    	
    	for (double[] p : fitG.getHistory()) {
    		System.out.println("   " + Matrix.toString(p));
    	}
    	
    	// check analytic/synthetic Jacobians:
    	GeometricEllipse eTest = new GeometricEllipse (100, 120, 50, -30, 0.25);
    	Pnt2d xi = Pnt2d.from(110, -70);
    	Pnt2d[] pts = {xi};
    	
    	{
    		EllipseGeometricFitCoord fit = new EllipseGeometricFitCoord(pts, ellipseA);
	    	MultivariateJacobianFunction model = fit.new AnalyticModel();
	    	Pair<RealVector, RealMatrix> result = model.value(Matrix.makeRealVector(eTest.getParameters()));
	    	RealMatrix J = result.getSecond();
	    	System.out.println("J analytic = \n" + Matrix.toString(J.getData()));
    	}
    	
    	{
    		EllipseGeometricFitCoord fit = new EllipseGeometricFitCoord(pts, ellipseA);
    		MultivariateJacobianFunction model = fit.new SyntheticModel();
	    	Pair<RealVector, RealMatrix> result = model.value(Matrix.makeRealVector(eTest.getParameters()));
	    	RealMatrix J = result.getSecond();
	    	System.out.println("J synthetic = \n" + Matrix.toString(J.getData()));
    	}
    }

}

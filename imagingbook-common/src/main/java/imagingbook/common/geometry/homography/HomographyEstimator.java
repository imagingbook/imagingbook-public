package imagingbook.common.geometry.homography;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntDouble;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.geometry.mappings.linear.ProjectiveMapping2D;
import imagingbook.common.math.Matrix;

/**
 * This class defines methods for estimating the homography (projective)
 * transformation between pairs of 2D point sets.
 * It works in two steps:
 * First, an initial estimate of the homography is obtained with the
 * "direct linear transform" method.
 * Second, iterative, non-linear Levenberg-Marquart (LM) optimization is applied
 * to find the final fit.
 *
 * @author WB
 * @version 2020/05/19
 */
public class HomographyEstimator {

	private static int MaxLmEvaluations = 1000;
	private static int MaxLmIterations = 1000;

	private final boolean normalizePointCoordinates;
	private final boolean doNonlinearRefinement;

	// ------------------------------------------------------------

	public HomographyEstimator() {
		this(true, true);
	}

	public HomographyEstimator(boolean normalizePointCoordinates, boolean doNonlinearRefinement) {
		this.normalizePointCoordinates = normalizePointCoordinates;
		this.doNonlinearRefinement = doNonlinearRefinement;
	}

	// ------------------------------------------------------------

	/**
	 * Estimates the homography (projective transformation) from two given 2D point sets
	 * and returns a {@linkplain ProjectiveMapping2D} instance.
	 * The correspondence between the points is assumed to be known.
	 * @param P the 1st sequence of 2D points 
	 * @param Q the 2nd sequence of 2D points
	 * @return the estimated homography (instance of type {@linkplain ProjectiveMapping2D})
	 */
	public ProjectiveMapping2D getHomographyMapping(Pnt2d[] P, Pnt2d[] Q) {
		RealMatrix H = getHomography(P, Q);
		return new ProjectiveMapping2D(H.getData());
	}

	/**
	 * Estimates the homography (projective transformation) from two given 2D point sets.
	 * The correspondence between the points is assumed to be known.
	 * @param P the 1st sequence of 2D points 
	 * @param Q the 2nd sequence of 2D points
	 * @return the estimated homography (3 x 3 matrix)
	 */
	public RealMatrix getHomography(Pnt2d[] P, Pnt2d[] Q) {
		if (P.length != Q.length) {
			throw new IllegalArgumentException("getHomography(): P and Q must be of same length");
		}
		int n = P.length;
		
		// input point sets are normalized (to zero mean, unit variance) if required:
		RealMatrix Id = MatrixUtils.createRealIdentityMatrix(3);
		RealMatrix Na = (normalizePointCoordinates) ? MathUtil.getNormalisationMatrix(P) : Id;
		RealMatrix Nb = (normalizePointCoordinates) ? MathUtil.getNormalisationMatrix(Q) : Id;	
		RealMatrix M = MatrixUtils.createRealMatrix(n * 2, 9);

		// find an initial solution using the DLT
		for (int j = 0, r = 0; j < P.length; j++) {
			final double[] pA = MathUtil.transform(P[j].toDoubleArray(), Na);
			final double[] pB = MathUtil.transform(Q[j].toDoubleArray(), Nb);
			final double xA = pA[0];
			final double yA = pA[1];
			final double xB = pB[0];
			final double yB = pB[1];			
			M.setRow(r + 0, new double[] {xA, yA, 1, 0, 0, 0, -(xA * xB), -(yA * xB), -(xB)});
			M.setRow(r + 1, new double[] {0, 0, 0, xA, yA, 1, -(xA * yB), -(yA * yB), -(yB)});
			r = r + 2;
		}

		// find h, such that M . h = 0:
		double[] h = MathUtil.solveHomogeneousSystem(M).toArray();

		// assemble homography matrix H from h:
		RealMatrix H = MatrixUtils.createRealMatrix(new double[][] 
				{{h[0], h[1], h[2]},
				 {h[3], h[4], h[5]},
				 {h[6], h[7], h[8]}} );

		// de-normalize the homography
		if (normalizePointCoordinates) {
			H = MatrixUtils.inverse(Nb).multiply(H).multiply(Na);
		}

		// rescale M such that H[2][2] = 1 (unless H[2][2] close to 0)
		if (Math.abs(H.getEntry(2, 2)) > 10e-8) {
			H = H.scalarMultiply(1.0 / H.getEntry(2, 2));
		}
		else {
			throw new RuntimeException("estimateHomography(): H could not be normalized");
		}

		// refine the solution using non-linear optimization if required
		return doNonlinearRefinement ? refineHomography(H, P, Q) : H;
	}

	/**
	 * Estimates the homographies between a fixed set of 2D model points and
	 * multiple observations (image point sets).
	 * The correspondence between the points is assumed to be known.
	 * TODO: This should not be here.
	 * @param modelPts a sequence of 2D points on the model (calibration target)
	 * @param obsPoints a sequence 2D image point sets (one set per view).
	 * @return the sequence of estimated homographies (3 x 3 matrices), one for each view
	 * @deprecated
	 */
	public RealMatrix[] getHomographies(Pnt2d[] modelPts, Pnt2d[][] obsPoints) {
		final int M = obsPoints.length;
		RealMatrix[] homographies = new RealMatrix[M];
		for (int i = 0; i < M; i++) {
			homographies[i] = getHomography(modelPts, obsPoints[i]);
		}
		return homographies;
	}

	/**
	 * Refines the initial homography by non-linear (Levenberg-Marquart) optimization.
	 * @param Hinit the initial (estimated) homography matrix
	 * @param pntsA the 1st sequence of 2D points 
	 * @param pntsB the 2nd sequence of 2D points
	 * @return the refined homography matrix
	 */
	private static RealMatrix refineHomography(RealMatrix Hinit, Pnt2d[] pntsA, Pnt2d[] pntsB) {
		final int M = pntsA.length;		
		double[] observed = new double[2 * M];
		for (int i = 0; i < M; i++) {
			observed[i * 2 + 0] = pntsB[i].getX();
			observed[i * 2 + 1] = pntsB[i].getY();
		}			
		MultivariateVectorFunction value = getValueFunction(pntsA);
		MultivariateMatrixFunction jacobian = getJacobianFunction(pntsA);

		LeastSquaresProblem problem = LeastSquaresFactory.create(
				LeastSquaresFactory.model(value, jacobian),
				MatrixUtils.createRealVector(observed), 
				MathUtil.getRowPackedVector(Hinit), 
				// TODO: add weight matrix (diagonal)?
				null,  // ConvergenceChecker -- do we need one?
				MaxLmEvaluations, 
				MaxLmIterations);

		LevenbergMarquardtOptimizer lm = new LevenbergMarquardtOptimizer();
		Optimum result = lm.optimize(problem);

		RealVector optimum = result.getPoint();
		RealMatrix Hopt = MathUtil.fromRowPackedVector(optimum, 3, 3);
		int iterations = result.getIterations();
		if (iterations >= MaxLmIterations) {
			throw new RuntimeException("refineHomography(): max. number of iterations exceeded");
		}
		//System.out.println("LM optimizer iterations " + iterations);
		return Hopt.scalarMultiply(1.0 / Hopt.getEntry(2, 2));
	}


	private static MultivariateVectorFunction getValueFunction(Pnt2d[] X) {
		//System.out.println("MultivariateVectorFunction getValueFunction");
		return new MultivariateVectorFunction() {
			
			@Override
			public double[] value(double[] h) {			
				final double[] Y = new double[X.length * 2];
				for (int j = 0; j < X.length; j++) {
					final double x = X[j].getX();
					final double y = X[j].getY();
					final double w = h[6] * x + h[7] * y + h[8];
					Y[j * 2 + 0] = (h[0] * x + h[1] * y + h[2]) / w;
					Y[j * 2 + 1] = (h[3] * x + h[4] * y + h[5]) / w;
				}
				return Y;
			}
		};
	}

	private static MultivariateMatrixFunction getJacobianFunction(Pnt2d[] X) {
		return new MultivariateMatrixFunction() {
			
			@Override
			public double[][] value(double[] h) {
				final double[][] J = new double[2 * X.length][];
				for (int i = 0; i < X.length; i++) {
					final double x = X[i].getX();
					final double y = X[i].getY();

					final double w  = h[6] * x + h[7] * y + h[8];
					final double w2 = w * w;

					final double sx = h[0] * x + h[1] * y + h[2];		
					J[2 * i + 0] = new double[] {x/w, y/w, 1/w, 0, 0, 0, -sx*x/w2, -sx*y/w2, -sx/w2};

					final double sy = h[3] * x + h[4] * y + h[5];
					J[2 * i + 1] = new double[] {0, 0, 0, x/w, y/w, 1/w, -sy*x/w2, -sy*y/w2, -sy/w2};
				}
				return J;
			}
		};
	}

	/*
	protected MultivariateMatrixFunction getJacobianFunction(final Point[] X) {
		return new MultivariateMatrixFunction() {
			@Override
			public double[][] value(double[] h) {
				final double[][] J = new double[2 * X.length][9];
				for (int i = 0; i < X.length; i++) {
					final double x = X[i].getX();
					final double y = X[i].getY();

					final double w  = h[6] * x + h[7] * y + h[8];
					final double w2 = w * w;

					final double sx = h[0] * x + h[1] * y + h[2];		
					J[2 * i + 0][0] = x / w;
					J[2 * i + 0][1] = y / w;
					J[2 * i + 0][2] = 1.0 / w;
					J[2 * i + 0][3] = 0;
					J[2 * i + 0][4] = 0;
					J[2 * i + 0][5] = 0;
					J[2 * i + 0][6] = -sx * x / w2;
					J[2 * i + 0][7] = -sx * y / w2;
					J[2 * i + 0][8] = -sx / w2;

					final double sy = h[3] * x + h[4] * y + h[5];
					J[2 * i + 1][0] = 0;
					J[2 * i + 1][1] = 0;
					J[2 * i + 1][2] = 0;
					J[2 * i + 1][3] = x / w;
					J[2 * i + 1][4] = y / w;
					J[2 * i + 1][5] = 1.0 / w;
					J[2 * i + 1][6] = -sy * x / w2;
					J[2 * i + 1][7] = -sy * y / w2;
					J[2 * i + 1][8] = -sy / w2;
				}
				return J;
			}
		};
	}
	 */

	/*
 	protected MultivariateMatrixFunction getJacobianFunction(final Point[] X) {
		return new MultivariateMatrixFunction() {
			// See Multi-View Geometry in Computer Vision, eq 4.21, p129
			@Override
			public double[][] value(double[] h) {
				final double[][] J = new double[2 * X.length][9];
				for (int i = 0; i < X.length; i++) {
					final double x = X[i].getX();
					final double y = X[i].getY();

					final double t2 = x * h[6];
					final double t3 = y * h[7];
					final double t4 = h[8] + t2 + t3;
					final double t5 = 1.0 / t4;
					final double t6 = x * h[0];
					final double t7 = y * h[1];
					final double t8 = h[2] + t6 + t7;
					final double t9 = 1.0 / (t4 * t4);
					final double t10 = x * t5;
					final double t11 = y * t5;
					final double t12 = x * h[3];
					final double t13 = y * h[4];
					final double t14 = h[5] + t12 + t13;

					J[2 * i + 0][0] = t10;
					J[2 * i + 0][1] = t11;
					J[2 * i + 0][2] = t5;
//					J[2 * i + 0][3] = 0;
//					J[2 * i + 0][4] = 0;
//					J[2 * i + 0][5] = 0;
					J[2 * i + 0][6] = -x * t8 * t9;
					J[2 * i + 0][7] = -y * t8 * t9;
					J[2 * i + 0][8] = -t8 * t9;

//					J[2 * i + 1][0] = 0;
//					J[2 * i + 1][1] = 0;
//					J[2 * i + 1][2] = 0;
					J[2 * i + 1][3] = t10;
					J[2 * i + 1][4] = t11;
					J[2 * i + 1][5] = t5;
					J[2 * i + 1][6] = -x * t9 * t14;
					J[2 * i + 1][7] = -y * t9 * t14;
					J[2 * i + 1][8] = -t9 * t14;
				}
				return J;
			}
		};
	}
	 */


	// TESTING --------------------------------------------------------

	static double NOISE = 0.1;

	private static Pnt2d mapPoint(RealMatrix H, Pnt2d p) {
		double[] xa = p.toDoubleArray(); // {p.getX(), p.getY()};
		double[] xb = MathUtil.transform(xa, H);
		return PntDouble.from(xb);
	}

	private static Random rand = new Random();

	private static Pnt2d mapPointWithNoise(RealMatrix H, Pnt2d p, double noise) {
		double[] xa = {p.getX(), p.getY()};
		double[] xb = MathUtil.transform(xa, H);
		double xn = noise * rand.nextGaussian();
		double yn = noise * rand.nextGaussian();
		return PntDouble.from(xb[0] + xn, xb[1] + yn);
	}

	/**
	 * Used for testing only.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		RealMatrix Hreal = MatrixUtils.createRealMatrix(new double[][]
				{{3, 2, -1},
			{5, 0, 2},
			{4, 4, 9}});

		System.out.println("H (real) = ");
		System.out.println(Matrix.toString(Hreal.scalarMultiply(1/Hreal.getEntry(2, 2)).getData()));
		//		System.out.println(Matrix.toString(Hreal.getData()));

		List<Pnt2d> pntlistA = new ArrayList<Pnt2d>();
		pntlistA.add(PntInt.from(10, 7));
		pntlistA.add(PntInt.from(3, -1));
		pntlistA.add(PntInt.from(5, 5));
		pntlistA.add(PntInt.from(-6, 13));
		pntlistA.add(PntInt.from(0, 1));
		pntlistA.add(PntInt.from(2, 3));

		List<Pnt2d> pntlistB = new ArrayList<Pnt2d>();
		for (Pnt2d a : pntlistA) {
			pntlistB.add(mapPointWithNoise(Hreal, a, NOISE));
		}

		Pnt2d[] pntsA = pntlistA.toArray(new Pnt2d[0]);
		Pnt2d[] pntsB = pntlistB.toArray(new Pnt2d[0]);


		System.out.println("\nPoint correspondences:");
		for (int i = 0; i < pntsA.length; i++) {
			Pnt2d a = pntsA[i];
			Pnt2d b = pntsB[i];
			System.out.format("(%.3f, %.3f) -> (%.3f, %.3f)\n", a.getX(), a.getY(), b.getX(), b.getY());
		}
		System.out.println();

		System.out.println("\n*************** WITHOUT NONLINEAR REFINEMENT *****************");
		runTestDLT(new HomographyEstimator(true, false), pntsA, pntsB);

		System.out.println("\n*************** WITH NONLINEAR REFINEMENT *****************");
		runTestDLT(new HomographyEstimator(true, true), pntsA, pntsB);

		System.out.println("\n*************** USING LEAST-SQUARES MAPPING *****************");
		runTestLeastSquares(pntsA, pntsB);

		System.out.println("\n*************** GENERATE A MAPPING *****************");
		ProjectiveMapping2D pm = new HomographyEstimator().getHomographyMapping(pntsA, pntsB);
		System.out.println("projective mapping = \n" + pm.toString());

	}

	private static void runTestDLT(HomographyEstimator he, Pnt2d[] pntsA, Pnt2d[] pntsB) {
		RealMatrix Hest = he.getHomography(pntsA, pntsB);

		System.out.println("H (estim.) = "); 
		System.out.println(Matrix.toString(Hest.getData()));

		Pnt2d[] pntsC = new Pnt2d[pntsA.length];
		for (int i = 0; i < pntsA.length; i++) {	
			pntsC[i] = mapPoint(Hest, pntsA[i]);
		}

		System.out.println("\nPoints mapped:");
		double sumDist2 = 0;
		double maxDist2 = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < pntsA.length; i++) {
			Pnt2d a = pntsA[i];
			Pnt2d b = pntsB[i];
			Pnt2d c = pntsC[i];
			double dist2 = b.distanceSq(c);
			sumDist2 += dist2;
			maxDist2 = Math.max(maxDist2, dist2);
			System.out.format("(%.3f, %.3f) -> (%.3f, %.3f) d=%.4f\n", a.getX(), a.getY(), c.getX(), c.getY(), dist2);
		}
		System.out.format("\nTotal error = %.2f\n", Math.sqrt(sumDist2));
		System.out.format("Max. dist = %.2f\n", Math.sqrt(maxDist2));	
	}

	private static void runTestLeastSquares(Pnt2d[] pntsA, Pnt2d[] pntsB) {
		ProjectiveMapping2D mapping = ProjectiveMapping2D.fromPoints(pntsA, pntsB);
		double[][] Hmap = mapping.getTransformationMatrix();
		System.out.println("H (mapping) = "); 
		System.out.println(Matrix.toString(Hmap));
		{
			Pnt2d[] pntsC = new Pnt2d[pntsA.length];
			for (int i = 0; i < pntsA.length; i++) {	
				pntsC[i] = //(Pnt2d) 
					mapping.applyTo(PntDouble.from(pntsA[i]));
//					mapping.applyTo(Pnt2d.PntDouble.from(pntsA[i].getX(), pntsA[i].getY()));
			}

			System.out.println("\nPoints mapped:");
			double sumDist2 = 0;
			double maxDist2 = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < pntsA.length; i++) {
				Pnt2d a = pntsA[i];
				Pnt2d b = pntsB[i];
				Pnt2d c = pntsC[i];
				double dist2 = b.distanceSq(c);
				sumDist2 += dist2;
				maxDist2 = Math.max(maxDist2, dist2);
				System.out.format("(%.3f, %.3f) -> (%.3f, %.3f) d=%.4f\n", a.getX(), a.getY(), c.getX(), c.getY(), dist2);
			}
			System.out.format("\nTotal error = %.2f\n", Math.sqrt(sumDist2));
			System.out.format("Max. dist = %.2f\n", Math.sqrt(maxDist2));
		}
		{
			System.out.println("   +++ running refinement ++++");
			RealMatrix Hinit = MatrixUtils.createRealMatrix(Hmap);
			RealMatrix Hest = refineHomography(Hinit, pntsA, pntsB);
			System.out.println("H (estim.) = "); 
			System.out.println(Matrix.toString(Hest.getData()));

			Pnt2d[] pntsC = new Pnt2d[pntsA.length];
			for (int i = 0; i < pntsA.length; i++) {	
				pntsC[i] = mapPoint(Hest, pntsA[i]);
			}

			//			System.out.println("\nPoints mapped:");
			double sumDist2 = 0;
			double maxDist2 = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < pntsA.length; i++) {
				@SuppressWarnings("unused")
				Pnt2d a = pntsA[i];
				Pnt2d b = pntsB[i];
				Pnt2d c = pntsC[i];
				double dist2 = b.distanceSq(c);
				sumDist2 += dist2;
				maxDist2 = Math.max(maxDist2, dist2);
				//				System.out.format("(%.3f, %.3f) -> (%.3f, %.3f) d=%.4f\n", a.getX(), a.getY(), c.getX(), c.getY(), dist2);
			}
			System.out.format("\nTotal error = %.2f\n", Math.sqrt(sumDist2));
			System.out.format("Max. dist = %.2f\n", Math.sqrt(maxDist2));	
		}
	}

}

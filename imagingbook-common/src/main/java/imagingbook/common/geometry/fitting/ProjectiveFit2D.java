package imagingbook.common.geometry.fitting;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;

public class ProjectiveFit2D implements LinearFit2D {
	
	private final RealMatrix A;		// the calculated transformation matrix
	private final double err;		// the calculated error
	
	/**
	 * Constructor.
	 * Fits two sequences of 2D points using a projective transformation model.
	 * At least 4 point pairs are required. For 4 point pairs, the solution
	 * is an exact fit, otherwise a least-squares fit is found.
	 * @param P the source points
	 * @param Q the target points
	 */
	public ProjectiveFit2D(Pnt2d[] P, Pnt2d[] Q) {
		checkSize(P, Q);
		final int n = P.length;
		
		double[] ba = new double[2 * n];
		double[][] Ma = new double[2 * n][];
		for (int i = 0; i < n; i++) {
			double px = P[i].getX();
			double py = P[i].getY();
			double qx = Q[i].getX();
			double qy = Q[i].getY();
			ba[2 * i + 0] = qx;
			ba[2 * i + 1] = qy;
			Ma[2 * i + 0] = new double[] { px, py, 1, 0, 0, 0, -qx * px, -qx * py };
			Ma[2 * i + 1] = new double[] { 0, 0, 0, px, py, 1, -qy * px, -qy * py };
		}
		
		RealMatrix M = MatrixUtils.createRealMatrix(Ma);
		RealVector b = MatrixUtils.createRealVector(ba);
		DecompositionSolver solver = new SingularValueDecomposition(M).getSolver();
		RealVector h = solver.solve(b);
		A = MatrixUtils.createRealMatrix(3, 3);
		A.setEntry(0, 0, h.getEntry(0));
		A.setEntry(0, 1, h.getEntry(1));
		A.setEntry(0, 2, h.getEntry(2));
		A.setEntry(1, 0, h.getEntry(3));
		A.setEntry(1, 1, h.getEntry(4));
		A.setEntry(1, 2, h.getEntry(5));
		A.setEntry(2, 0, h.getEntry(6));
		A.setEntry(2, 1, h.getEntry(7));
		A.setEntry(2, 2, 1.0);
		
		err = this.calculateError(P, Q, A);
	}

	@Override
	public double[][] getTransformationMatrix() {
		return A.getData();
	}

	@Override
	public double getError() {
		return err;
	}
	
	// ------------------------------------------------------------------------------
	
	protected static void checkSize(Pnt2d[] P, Pnt2d[] Q) {
		if (P.length < 4 || Q.length < 4) {
			throw new IllegalArgumentException("At least 4 point pairs are required to calculate this fit");
		}
	}

}

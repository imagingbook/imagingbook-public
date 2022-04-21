package imagingbook.common.geometry.fitting;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import imagingbook.common.geometry.basic.Pnt2d;

public class AffineFit2D implements LinearFit2D {
	
	private final RealMatrix A;		// the calculated transformation matrix
	private final double err;		// the calculated error
	
	/**
	 * Constructor.
	 * Fits two sequences of 2D points using an affine transformation model.
	 * At least 3 point pairs are required. For 3 point pairs, the solution
	 * is an exact fit, otherwise a least-squares fit is found.
	 * @param P the source points
	 * @param Q the target points
	 */
	public AffineFit2D(Pnt2d[] P, Pnt2d[] Q) {	// 
		checkSize(P, Q);
		int m = P.length;
		
		RealMatrix M = MatrixUtils.createRealMatrix(2 * m, 6);
		RealVector b = new ArrayRealVector(2 * m);
		
		// mount the matrix M
		int row = 0;
		for (Pnt2d p : P) {
			M.setEntry(row, 0, p.getX());
			M.setEntry(row, 1, p.getY());
			M.setEntry(row, 2, 1);
			row++;
			M.setEntry(row, 3, p.getX());
			M.setEntry(row, 4, p.getY());
			M.setEntry(row, 5, 1);
			row++;
		}
		
		// mount vector b
		row = 0;
		for (Pnt2d q : Q) {
			b.setEntry(row, q.getX());
			row++;
			b.setEntry(row, q.getY());
			row++;
		}
		
		// solve M * a = b (for the unknown parameter vector a):
		DecompositionSolver solver = new SingularValueDecomposition(M).getSolver();
		RealVector a = solver.solve(b);
		A = makeTransformationMatrix(a);
		err = calculateError(P, Q, A);
	}

	// creates a (2 x 3) transformation matrix from the elements of a
	private RealMatrix makeTransformationMatrix(RealVector a) {
		RealMatrix A = MatrixUtils.createRealMatrix(2, 3);
		int i = 0;
		for (int row = 0; row < 2; row++) {
			// get (n+1) elements from a and place in row
			for (int j = 0; j < 3; j++) {
				A.setEntry(row, j, a.getEntry(i));
				i++;
			}
		}
		return A;
	}
	
	// --------------------------------------------------------
	
	private void checkSize(Pnt2d[] P, Pnt2d[] Q) {
		if (P.length < 3 || Q.length < 3) {
			throw new IllegalArgumentException("At least 3 point pairs are required to calculate this fit");
		}
	}
	
	// --------------------------------------------------------

	@Override
	public double[][] getTransformationMatrix() {
		return A.getData();
//		return A;
	}

	@Override
	public double getError() {
		return err;
	}

}

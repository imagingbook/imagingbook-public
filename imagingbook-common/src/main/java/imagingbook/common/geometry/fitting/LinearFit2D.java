package imagingbook.common.geometry.fitting;

import org.apache.commons.math3.linear.RealMatrix;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.linear.LinearMapping2D;

/**
 * Describes a fitter based on a linear transformation model.
 * @author WB
 */
public interface LinearFit2D {
	
	/**
	 * Returns the (3,3) or (2,3) transformation matrix A for this fit, such that
	 * {@code y_i ~ A * x_i} (with {@code x_i} in homogeneous coordinates).
	 * 
	 * @return the transformation matrix for this fit
	 */
	double[][] getTransformationMatrix();
	
	/**
	 * Retrieves the total (squared) error for the estimated fit.
	 * @return the fitting error
	 */
	double getError();
	
	
	default public double calculateError(Pnt2d[] P, Pnt2d[] Q, RealMatrix A) {
		final int m = Math.min(P.length,  Q.length);
		LinearMapping2D map = new LinearMapping2D(A.getData());
		double errSum = 0;
		for (int i = 0; i < m; i++) {
			Pnt2d p = P[i];
			Pnt2d q = Q[i];
			Pnt2d pp = map.applyTo(p);
			double e = q.distance(pp);
			errSum = errSum + e * e;
		}
		return Math.sqrt(errSum);
	}
	
}
package imagingbook.common.math.eigen;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Common interface for eigendecompositions capable of
 * delivering solutions when all eigenvalues are real.
 *  
 * @author WB
 * @version 2022/07/05
 */
public interface RealEigenDecomposition {
	
	public boolean hasComplexEigenvalues();
	public double getRealEigenvalue(int k);
	public double[] getRealEigenvalues();
	public RealVector getEigenvector(int k);
	public RealMatrix getV();
	
	public default RealMatrix getD() {
		return MatrixUtils.createRealDiagonalMatrix(getRealEigenvalues());
	}
	

}

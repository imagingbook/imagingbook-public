package imagingbook.common.math.eigen;

/**
 * Interface for classes performing eigenvalue/eigenvector calculations.
 *
 */
public interface RealEigensolver {
	
	/**
	 * Returns the size of the system (N = number of rows and columns).
	 * @return size of the system (N)
	 */
	public int getSize();
	
	/**
	 * Returns {@code true} iff all eigenvalues of the associated matrix are real.
	 * @return as described
	 */
	public boolean isReal();
	
	/**
	 * Returns a vector of real eigenvalues in no specific order.
	 * {@code NaN} is inserted for complex eigenvalues.
	 * @return an array of eigenvalues
	 */
	public double[] getEigenvalues();
	
	/**
	 * Returns the k-th eigenvalue (&lambda;_k, for k = 0,...,N-1).
	 * {@code NaN} is returned if the associated eigenvalue
	 * is complex-valued (non-real).
	 * @param k index 
	 * @return the k-th eigenvalue (&lambda;<sub>k</sub>)
	 */
	public double getEigenvalue(int k);
	
	/**
	 * Returns a matrix whose columns are the eigenvectors of the
	 * solution, arranged in the same order as the eigenvalues
	 * returned by {@link #getEigenvalues()}. 
	 * 
	 * @return a matrix of eigenvectors (column vectors)
	 */
	public double[][] getEigenvectors();
	
	/**
	 * Returns the k-th eigenvector (x_k, for k = 0,...,N-1).
	 * The ordering of the returned eigenvectors is the same as for the
	 * eigenvalues returned by {@link #getEigenvalues()}.
	 * @param k index
	 * @return the k-th eigenvector (x<sub>k</sub>)
	 */
	public double[] getEigenvector(int k);


}

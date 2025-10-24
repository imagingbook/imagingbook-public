/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math.eigen;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * An implementation of {@link RealEigenDecomposition}, which merely wraps an instance of {@link EigenDecomposition} as
 * provided by the Apache Commons Math library. Note this works only for square matrices and symmetry and zero
 * tolerances cannot be specified, default values are always used. The Jama version ({@link EigenDecompositionJama})
 * generally appears to be more flexible and robust.
 *
 * @author WB
 * @version 2022/11/24
 * @see EigenDecompositionJama
 * @see Eigensolver2x2
 */
public class EigenDecompositionApache implements RealEigenDecomposition {
	
	// instance of org.apache.commons.math3.linear.EigenDecomposition
	private final EigenDecomposition decomp;

	/**
	 * Constructor, calculates the eigen decomposition of the specified matrix, which must be square but does not need
	 * to be symmetric.
	 *
	 * @param M the matrix to decompose
	 */
	public EigenDecompositionApache(RealMatrix M) {
		this.decomp = new EigenDecomposition(M);
	}

	@Override
	public boolean hasComplexEigenvalues() {
		return decomp.hasComplexEigenvalues();
	}

	@Override
	public double getRealEigenvalue(int k) {
		return decomp.getRealEigenvalue(k);
	}

	@Override
	public double[] getRealEigenvalues() {
		return decomp.getRealEigenvalues();
	}

	@Override
	public RealVector getEigenvector(int k) {
		return decomp.getEigenvector(k);
	}

	@Override
	public RealMatrix getV() {
		return decomp.getV();
	}

	/**
	 * Returns the underlying Apache eigendecomposition instance, mainly for debugging.
	 *
	 * @return the Apache eigendecomposition instance
	 */
	public EigenDecomposition getInternalDecomposition() {
		return decomp;
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.geometry.mappings.linear;

import imagingbook.common.math.Matrix;

/**
 * <p>
 * This class represents a pure 2D rotation about the coordinate origin (as a
 * special case of affine mapping). It can be assumed that every instance of
 * this class is indeed a rotation. See Secs. 21.1.3 and 21.3.1 of [1] for
 * details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 *  @author WB
 */
public class Rotation2D extends AffineMapping2D {
	
	/**
	 * Constructor. Creates a 2D rotation by a given
	 * angle about the origin.
	 * @param alpha rotation angle (in radians)
	 */
	public Rotation2D(double alpha) {
		super(
			 Math.cos(alpha), -Math.sin(alpha), 0,
			 Math.sin(alpha),  Math.cos(alpha), 0);
	}
	
	/**
	 * Auxiliary constructor used internally for duplicating instances.
	 * @param a00 matrix element A_00
	 * @param a01 matrix element A_01
	 * @param a10 matrix element A_10
	 * @param a11 matrix element A_11
	 */
	protected Rotation2D(double a00, double a01, double a10, double a11) {
		super(a00, a01, 0, a10, a11, 0);
	}
	
	/**
	 * Constructor. 
	 * Creates a new {@link Rotation2D} object from an existing instance.
	 * @param m a {@link Rotation2D} instance
	 */
	public Rotation2D(Rotation2D m) {
		this(m.a00, m.a01, m.a10, m.a11);
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Concatenates this rotation (A) with another rotation (B) and returns
	 * a new rotation (C), such that C(x) = B(A(x)).
	 * @param B the second rotation
	 * @return the concatenated rotations
	 */
	public Rotation2D concat(Rotation2D B) {
		double[][] C = Matrix.multiply(B.getTransformationMatrix(), this.getTransformationMatrix());
		return new Rotation2D(C[0][0], C[0][1], C[1][0], C[1][1]);
	}
	
	@Override
	public Rotation2D duplicate() {
		return new Rotation2D(this);
	}
	
	@Override
	public Rotation2D getInverse() {
		return new Rotation2D(a00, -a01, -a10, a11);
	}

}




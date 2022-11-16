package imagingbook.common.geometry.mappings;

import imagingbook.common.geometry.basic.Pnt2d;

/**
 * Implementing classes provide the Jacobian matrix for the associated mapping.
 * 
 * @author WB
 *
 */
public interface Jacobian {
	
	/**
	 * <p>
	 * Returns the Jacobian matrix for this mapping, evaluated at the given 2D
	 * point. See Appendix Sec. D.1.1 of [1] for more details.
	 * </p>
	 * <p>
	 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
	 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
	 * </p>
	 * 
	 * @param pnt the 2D position to calculate the Jacobian for
	 * @return the Jacobian matrix
	 */
	public double[][] getJacobian(Pnt2d pnt);

}

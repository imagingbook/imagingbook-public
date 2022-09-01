/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.math.eigen.eispack;

abstract class QZHES {
	
	// EISPACK Routines, see http://www.netlib.org/eispack/
	
	/**
	 * <p>
	 * This subroutine is the first step of the qz algorithm for solving generalized
	 * matrix eigenvalue problems, SIAM J. Numer. Anal. 10, 241-256 (1973) by Moler
	 * and Stewart. This description has been adapted from the original version
	 * (dated August 1983).
	 * </p>
	 * <p>
	 * This subroutine accepts a pair of real general matrices and reduces one of
	 * them to upper Hessenberg form and the other to upper triangular form using
	 * orthogonal transformations. It is usually followed by qzit, qzval and,
	 * possibly, qzvec.
	 * </p>
	 * <p>
	 * On output:
	 * </p>
	 * <ul>
	 * <li><strong>a</strong> has been reduced to upper hessenberg form. The
	 * elements below the first subdiagonal have been set to zero.</li>
	 * 
	 * <li><strong>b</strong> has been reduced to upper triangular form. The
	 * elements below the main diagonal have been set to zero.</li>
	 * 
	 * <li><strong>z</strong> contains the product of the right hand transformations
	 * if matz has been set to true. Otherwise, <strong>z</strong> is not
	 * referenced.</li>
	 * </ul>
	 * 
	 * 
	 * @param a    contains a real general matrix.
	 * @param b    contains a real general matrix.
	 * @param matz should be set to true if the right hand transformations are to be
	 *             accumulated for later use in computing eigenvectors, and to false
	 *             otherwise.
	 * @param z    on output, contains the product of the right hand transformations if matz
	 *             has been set to true. Otherwise, z is not referenced.
	 */
	public static void qzhes(double[][] a, double[][] b, boolean matz, double[][] z) {
		
		final int n = a.length;
		
		int i, j, k, l, nm1, nm2;
		double r, s, t;
		int l1;
		double u1, u2, v1, v2;
		int lb, nk1;
		double rho;
	
		// initialize z (with identity matrix)
		if (matz) {
			for (j = 0; j < n; j++) {
				for (i = 0; i < n; i++) {
					z[i][j] = 0.0;
				}	//	L2
				z[j][j] = 1.0;
			}	// L3
		}
	
		// L10: reduce b to upper triangular form
		if (n <= 1) {
			return;
		}
		
		nm1 = n - 1;	// TODO: check!!
		
		for (l = 0; l < nm1; l++) {
			l1 = l + 1;
			s = 0.0;
	
			for (i = l1; i < n; i++) {
				s = s + Math.abs(b[i][l]);
			}	// L20
	
			if (s != 0.0) {		
				s = s + Math.abs(b[l][l]);
				r = 0.0;
				for (i = l; i < n; i++) {
					b[i][l] = b[i][l] / s;
					r = r + b[i][l] * b[i][l];
				}	// L25
		
				r = Math.copySign(Math.sqrt(r), b[l][l]);
				b[l][l] = b[l][l] + r;
				rho = r * b[l][l];
		
				for (j = l1; j < n; j++) {
					t = 0.0;	// dot product
					for (i = l; i < n; i++) {
						t = t + b[i][l] * b[i][j];
					}	//L30
					t = -t / rho;
					
					for (i = l; i < n; i++) {
						b[i][j] = b[i][j] + t * b[i][l];
					}	// L40
					
				}	// L50
		
				for (j = 0; j < n; j++) {
					t = 0.0;	// dot product
					for (i = l; i < n; i++) {
						t = t + b[i][l] * a[i][j];
					}	// L60
					
					t = -t / rho;
					
					for (i = l; i < n; i++) {
						a[i][j] = a[i][j] + t * b[i][l];
					}	// L70
					
				}	// L80
		
				b[l][l] = -s * r;
				for (i = l1; i < n; i++) {
					b[i][l] = 0.0;
				}	// L90
			
			}	// end if		
		}	// L100
	
		// reduce a to upper Hessenberg form, while keeping b triangular
		if (n == 2) {
			return;
		}
		nm2 = n - 2;
		
		for (k = 0; k < nm2; k++) {
			nk1 = nm1 - k - 1; // = n - 2 - k;
	
			// for l=n-1 step -1 until k+1 do
			for (lb = 0; lb < nk1; lb++) {
				l = n - lb - 2;					// TODO: check!!
				l1 = l + 1;
	
				// zero a(l+1,k)
				s = Math.abs(a[l][k]) + Math.abs(a[l1][k]);
				if (s == 0.0) {
					continue;	// goto 150
				}
				u1 = a[l][k] / s;
				u2 = a[l1][k] / s;
				r = Math.copySign(Math.hypot(u1, u2), u1); 
				v1 = -(u1 + r) / r;
				v2 = -u2 / r;
				u2 = v2 / v1;
	
				for (j = k; j < n; j++) {
					t = a[l][j] + u2 * a[l1][j];
					a[l][j] = a[l][j] + t * v1;
					a[l1][j] = a[l1][j] + t * v2;
				}	// L110
	
				a[l1][k] = 0.0;
	
				for (j = l; j < n; j++) {
					t = b[l][j] + u2 * b[l1][j];
					b[l][j] = b[l][j] + t * v1;
					b[l1][j] = b[l1][j] + t * v2;
				}	// L120
	
				// zero b(l+1,l)
				s = Math.abs(b[l1][l1]) + Math.abs(b[l1][l]);
				if (s == 0.0) {
					continue;	// go to 150
				}
				u1 = b[l1][l1] / s;
				u2 = b[l1][l] / s;
				r = Math.copySign(Math.hypot(u1, u2), u1);
				v1 = -(u1 + r) / r;
				v2 = -u2 / r;
				u2 = v2 / v1;
	
				for (i = 0; i <= l1; i++) {
					t = b[i][l1] + u2 * b[i][l];
					b[i][l1] = b[i][l1] + t * v1;
					b[i][l] = b[i][l] + t * v2;
				}	// L130
	
				b[l1][l] = 0.0;
	
				for (i = 0; i < n; i++) {
					t = a[i][l1] + u2 * a[i][l];
					a[i][l1] = a[i][l1] + t * v1;
					a[i][l] = a[i][l] + t * v2;
				}	// L140
	
				if (matz) {
					for (i = 0; i < n; i++) {
						t = z[i][l1] + u2 * z[i][l];
						z[i][l1] = z[i][l1] + t * v1;
						z[i][l] = z[i][l] + t * v2;
					}	// L145
				}
			}	// L150
		}	// L160
	
	} // end of qzhes

}

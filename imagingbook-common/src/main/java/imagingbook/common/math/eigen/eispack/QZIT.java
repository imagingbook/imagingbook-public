/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math.eigen.eispack;

public abstract class QZIT {
	
	/*
	EISPACK Routines, see http://www.netlib.org/eispack/ for the original FORTRAN code.
	Untangled to goto-free Java by W. Burger using a sequential state machine concept, inspired by D. E. Knuth,
	"Structured Programming with Goto Statements", Computing Surveys, Vol. 6, No. 4 (1974).
	 */

	private QZIT() {}

	/**
	 * <p>
	 * This subroutine is the second step of the qz algorithm for solving generalized matrix eigenvalue problems, Siam
	 * J. Numer. anal. 10, 241-256(1973) by Moler and Stewart, as modified in technical note NASA TN D-7305(1973) by
	 * Ward. This description has been adapted from the original version (dated August 1983).
	 * </p>
	 * <p>
	 * This subroutine accepts a pair of real matrices, one of them in upper Hessenberg form and the other in upper
	 * triangular form. It reduces the Hessenberg matrix to quasi-triangular form using orthogonal transformations while
	 * maintaining the triangular form of the other matrix. It is usually preceded by qzhes and followed by qzval and,
	 * possibly, qzvec.
	 * </p>
	 * <p>
	 * On output:
	 * </p>
	 * <ul>
	 * <li><strong>a</strong> has been reduced to quasi-triangular form. The elements below the first subdiagonal are
	 * still zero and no two consecutive subdiagonal elements are nonzero.</li>
	 * <li><strong>b</strong> is still in upper triangular form, although its elements have been altered. The location
	 * b(n,1) is used to store eps1 times the norm of b for later use by qzval and qzvec.
	 * <li><strong>z</strong> contains the product of the right hand transformations (for both steps) if matz has been
	 * set to true.</li>
	 * </ul>
	 *
	 * @param a contains a real upper hessenberg matrix.
	 * @param b contains a real upper triangular matrix.
	 * @param eps1 is a tolerance used to determine negligible elements. eps1 = 0.0 (or negative) may be input, in which
	 * case an element will be neglected only if it is less than roundoff error times the norm of its matrix. If the
	 * input eps1 is positive, then an element will be considered negligible if it is less than eps1 times the norm of
	 * its matrix. A positive value of eps1 may result in faster execution, but less accurate results.
	 * @param matz should be set to true if the right hand transformations are to be accumulated for later use in
	 * computing eigenvectors, and to false otherwise.
	 * @param z contains, if matz has been set to true, the transformation matrix produced in the reduction by qzhes, if
	 * performed, or else the identity matrix. If matz has been set to false, z is not referenced.
	 * @return -1 for normal return, j if the limit of 30*n iterations is exhausted while the j-th eigenvalue is being
	 * is being sought.
	 */
	public static int qzit(double[][] a, double[][] b, double eps1, boolean matz, double[][] z) {

		final int n = a.length;
		
		int i, j, k, l = 0;
		double r, s, t, a1 = 0, a2 = 0, a3 = 0;
		int k1, k2, l1 = 0, ll;
		double u1, u2, u3;
		double v1, v2, v3;
		double a11 = 0, a12, a21 = 0, a22, a33 = 0, a34 = 0, a43 = 0, a44 = 0;
		double b11 = 0, b12, b22 = 0, b33, b34 = 0, b44;
		int na = 0, en, ld = 0;
		double ep;
		double sh = 0;
		int km1, lm1 = 0;
		double ani, bni;
		int ish = 0, itn, its = 0, enm2 = 0, lor1;
		double epsa, epsb, anorm, bnorm;
		int enorn;
		boolean notlas;
	
		int ierr = -1;		// no error 
	
		// compute epsa and epsb
		anorm = 0.0;
		bnorm = 0.0;
			      
		for (i = 0; i < n; i++) {
			ani = 0.0;
			if (i != 0) {
				ani = (Math.abs(a[i][(i - 1)]));
			}
			bni = 0.0;
			
			for (j = i; j < n; j++) {
				ani += Math.abs(a[i][j]);
				bni += Math.abs(b[i][j]);
			}
	
			if (ani > anorm) {
				anorm = ani;
			}
			if (bni > bnorm) {
				bnorm = bni;
			}
		}
	
		if (anorm == 0.0) {
			anorm = 1.0;
		}
		if (bnorm == 0.0) {
			bnorm = 1.0;
		}
	
//		ep = eps1;
//		if (ep == 0.0) {
//			// use round-off level if eps1 is zero
//			ep = epsilon(1.0);
//		}
		ep = (eps1 > 0) ? eps1 : epslon(1.0);
		epsa = ep * anorm;
		epsb = ep * bnorm;
	
		// rReduce a to quasi-triangular form, while keeping b triangular
		lor1 = 0;
		enorn = n;
		en = n - 1;
		itn = n * 30;
		
		// ------------------------------------------
	
		State state = State.L60;
		StateLoop: while (state != State.Final) {
			switch (state) {
	
			case L60: 
				// Begin QZ step
				if (en <= 1) {
					state = State.L1001;
					break;
				}
				if (!matz) {
					enorn = en + 1;
				}
				its = 0;
				na = en - 1;
				enm2 = na;
				state = State.L70;
				break;
	
			case L70:
				ish = 2;
				// check for convergence or reducibility.
				for (ll = 0; ll <= en; ++ll) {
					lm1 = en - ll - 1;
					l = lm1 + 1;
					if (l == 0) {
						state = State.L95;
						continue StateLoop;		// check again!!
					}
					if (Math.abs(a[l][lm1]) <= epsa) {
						state = State.L90;
						break;
					}
				}
				state = State.L90;
				break;
	
			case L90:
				a[l][lm1] = 0.0;
				if (l < na) {
					state = State.L95;
					break;
				}
				// 1-by-1 or 2-by-2 block isolated
				en = lm1;
				state = State.L60;
				break;
	
			case L95: 
				// check for small top of b
				ld = l;
				state = State.L100;
				break;
	
			case L100:
				l1 = l + 1;
				b11 = b[l][l];
	
				if (Math.abs(b11) > epsb) {
					state = State.L120;
					break;
				}
	
				b[l][l] = 0.0;
				s = Math.abs(a[l][l]) + Math.abs(a[l1][l]);
				u1 = a[l][l] / s;
				u2 = a[l1][l] / s;
//				r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1);
				r = Math.copySign(Math.hypot(u1, u2), u1);
				v1 = -(u1 + r) / r;
				v2 = -u2 / r;
				u2 = v2 / v1;
	
				for (j = l; j < enorn; j++) {
					t = a[l][j] + u2 * a[l1][j];
					a[l][j] = a[l][j] + t * v1;
					a[l1][j] = a[l1][j] + t * v2;
	
					t = b[l][j] + u2 * b[l1][j];
					b[l][j] = b[l][j] + t * v1;
					b[l1][j] = b[l1][j] + t * v2;
				}
	
				if (l != 0) {
					a[l][lm1] = -a[l][lm1];
				}
				lm1 = l;
				l = l1;
				state = State.L90;
				break;
	
			case L120:
				a11 = a[l][l] / b11;
				a21 = a[l1][l] / b11;
				if (ish == 1) {
					state = State.L140;
					break;
				}
				// iteration strategy
				if (itn == 0) {
					state = State.L1000;
					break;
				}
				if (its == 10) {
					state = State.L155;
					break;
				}
	
				// determine type of shift
				b22 = b[l1][l1];
				if (Math.abs(b22) < epsb) {
					b22 = epsb;
				}
				b33 = b[na][na];
				if (Math.abs(b33) < epsb) {
					b33 = epsb;
				}
				b44 = b[en][en];
				if (Math.abs(b44) < epsb)
					b44 = epsb;
				a33 = a[na][na] / b33;
				a34 = a[na][en] / b44;
				a43 = a[en][na] / b33;
				a44 = a[en][en] / b44;
				b34 = b[na][en] / b44;
				t = 0.5 * (a43 * b34 - a33 - a44);
				r = t * t + a34 * a43 - a33 * a44;
				if (r < 0.0) {
					state = State.L150;
					break;
				}
	
				// determine single shift zero-th column of a
				ish = 1;
				r = Math.sqrt(r);
				sh = -t + r;
				s = -t - r;
				if (Math.abs(s - a44) < Math.abs(sh - a44)) {
					sh = s;
				}
	
				// look for two consecutive small sub-diagonal elements of a.
				for (ll = ld; ll < enm2; ll++) {
					l = enm2 + ld - ll - 1;
					if (l == ld) {
						state = State.L140;
						//break loop130;
						continue StateLoop;
					}
					lm1 = l - 1;
					l1 = l + 1;
//					t = a[l + 1][l + 1];	// CHECK THIS!!
					t = a[l][l];
	
					if (Math.abs(b[l][l]) > epsb) {
						t = t - sh * b[l][l];
					}
	
					if (Math.abs(a[l][lm1]) <= Math.abs(t / a[l1][l]) * epsa) {
						state = State.L100;
						continue StateLoop;
					}
				}
				state = State.L140;
				break;
	
			case L140:
				a1 = a11 - sh;
				a2 = a21;
				if (l != ld) {
					a[l][lm1] = -a[l][lm1];
				}
				state = State.L160;
				break;
	
			case L150: 
				// determine double shift zero-th column of a
				a12 = a[l][l1] / b22;
				a22 = a[l1][l1] / b22;
				b12 = b[l][l1] / b22;
				a1 = ((a33 - a11) * (a44 - a11) - a34 * a43 + a43 * b34 * a11) / a21 + a12 - a11 * b12;
				a2 = a22 - a11 - a21 * b12 - (a33 - a11) - (a44 - a11) + a43 * b34;
				a3 = a[l1 + 1][l1] / b22;
				state = State.L160;
				break;
	
			case L155: 
				// ad hoc shift
				a1 = 0.0;
				a2 = 1.0;
				a3 = 1.1605;			// magic!
				state = State.L160;
				break;
	
			case L160:
				its = its + 1;
			    itn = itn - 1;
				if (!matz) {
					lor1 = ld;
				}
	
				mainloop: for (k = l; k <= na; k++) {
					notlas = (k != na && ish == 2);
					k1 = k + 1;
					k2 = k + 2;
					km1 = Math.max(k, l + 1) - 1;
					ll = Math.min(en, k1 + ish);
	
					if (!notlas) {
						// zero a(k+1,k-1)
						if (k != l) {
							a1 = a[k][km1];
							a2 = a[k1][km1];
						}
	
						s = Math.abs(a1) + Math.abs(a2);
						if (s == 0.0) {
							state = State.L70;
							continue StateLoop;
						}
						u1 = a1 / s;
						u2 = a2 / s;
						r = Math.copySign(Math.hypot(u1, u2), u1);
						v1 = -(u1 + r) / r;
						v2 = -u2 / r;
						u2 = v2 / v1;
	
						for (j = km1; j < enorn; j++) {
							t = a[k][j] + u2 * a[k1][j];
							a[k][j] = a[k][j] + t * v1;
							a[k1][j] = a[k1][j] + t * v2;
	
							t = b[k][j] + u2 * b[k1][j];
							b[k][j] = b[k][j] + t * v1;
							b[k1][j] = b[k1][j] + t * v2;
						}
	
						if (k != l) {
							a[k1][km1] = 0.0;
						}
						// goto L240;
					}
	
					else {
						// zero a(k+1,k-1) and a(k+2,k-1)
						//L190:
						if (k != l) {
							a1 = a[k][km1];
							a2 = a[k1][km1];
							a3 = a[k2][km1];
						}
						//L200:
						s = Math.abs(a1) + Math.abs(a2) + Math.abs(a3);
						if (s == 0.0) {
							continue mainloop;	// goto L260;
						}
						u1 = a1 / s;
						u2 = a2 / s;
						u3 = a3 / s;
						r = Math.copySign(Math.sqrt(u1 * u1 + u2 * u2 + u3 * u3), u1);
						v1 = -(u1 + r) / r;
						v2 = -u2 / r;
						v3 = -u3 / r;
						u2 = v2 / v1;
						u3 = v3 / v1;
	
						for (j = km1; j < enorn; j++) {
							t = a[k][j] + u2 * a[k1][j] + u3 * a[k2][j];
							a[k][j] = a[k][j] + t * v1;
							a[k1][j] = a[k1][j] + t * v2;
							a[k2][j] = a[k2][j] + t * v3;
	
							t = b[k][j] + u2 * b[k1][j] + u3 * b[k2][j];
							b[k][j] = b[k][j] + t * v1;
							b[k1][j] = b[k1][j] + t * v2;
							b[k2][j] = b[k2][j] + t * v3;
						}
	
						if (k != l) {
							a[k1][km1] = 0.0;
							a[k2][km1] = 0.0;
						}
	
						// zero b(k+2,k+1) and b(k+2,k)
						s = Math.abs(b[k2][k2]) + Math.abs(b[k2][k1]) + Math.abs(b[k2][k]);
						if (s != 0.0) {
							u1 = b[k2][k2] / s;
							u2 = b[k2][k1] / s;
							u3 = b[k2][k] / s;
							r = Math.copySign(Math.sqrt(u1 * u1 + u2 * u2 + u3 * u3), u1);
							v1 = -(u1 + r) / r;
							v2 = -u2 / r;
							v3 = -u3 / r;
							u2 = v2 / v1;
							u3 = v3 / v1;
	
							for (i = lor1; i < ll + 1; i++) {
								t = a[i][k2] + u2 * a[i][k1] + u3 * a[i][k];
								a[i][k2] = a[i][k2] + t * v1;
								a[i][k1] = a[i][k1] + t * v2;
								a[i][k] = a[i][k] + t * v3;
	
								t = b[i][k2] + u2 * b[i][k1] + u3 * b[i][k];
								b[i][k2] = b[i][k2] + t * v1;
								b[i][k1] = b[i][k1] + t * v2;
								b[i][k] = b[i][k] + t * v3;
							}
	
							b[k2][k] = 0.0;
							b[k2][k1] = 0.0;
	
							if (matz) {
								for (i = 0; i < n; i++) {
									t = z[i][k2] + u2 * z[i][k1] + u3 * z[i][k];
									z[i][k2] = z[i][k2] + t * v1;
									z[i][k1] = z[i][k1] + t * v2;
									z[i][k] = z[i][k] + t * v3;
								}
							}
						}
					}
	
					// L240:
					// zero b(k+1,k)
					s = (Math.abs(b[k1][k1])) + (Math.abs(b[k1][k]));
					if (s == 0.0) {
						continue;	// goto L260;
					}
					u1 = b[k1][k1] / s;
					u2 = b[k1][k] / s;
					r = Math.copySign(Math.hypot(u1, u2), u1);
					v1 = -(u1 + r) / r;
					v2 = -u2 / r;
					u2 = v2 / v1;
	
					for (i = lor1; i <= ll; i++) {
						t = a[i][k1] + u2 * a[i][k];
						a[i][k1] = a[i][k1] + t * v1;
						a[i][k] = a[i][k] + t * v2;
	
						t = b[i][k1] + u2 * b[i][k];
						b[i][k1] = b[i][k1] + t * v1;
						b[i][k] = b[i][k] + t * v2;
					}
	
					b[k1][k] = 0.0;
	
					if (matz) {
						for (i = 0; i < n; i++) {
							t = z[i][k1] + u2 * z[i][k];
							z[i][k1] = z[i][k1] + t * v1;
							z[i][k] = z[i][k] + t * v2;
						}
					}
					// L260: ;
				}
	
				state = State.L70; // end qz step
				break;
	
			case L1000: 
				// set error -- all eigenvalues have not converged after 30*n iterations
				ierr = en + 1;
				state = State.L1001;
				break;
	
			case L1001: 
				// save epsb for use by qzval and qzvec
				if (n > 1) {
					b[n - 1][0] = epsb;
				}
				state = State.Final;
				break;
	
			case Final:
				throw new RuntimeException("this should never happen!");
			}
		}
	
		return ierr;	// return eigenvalue index if iteration count exceeded
	} // end of qzit()
	

	private enum State {
		L60, L70, L90, L95, L100, L120, L140, L150, L155, L160, L1000, L1001, Final;
	}

	private static double EpsDouble;
	static {
		double eps;
		double a = 4.0 / 3.0;
		do {
			double b = a - 1.0;
			double c = b + b + b;
			eps = Math.abs(c - 1.0);
		} while (eps == 0);
		EpsDouble = eps;
	}
	
	private static float EpsFloat;
	static {
		float eps;
		float a = 4.0f / 3.0f;
		do {
			float b = a - 1.0f;
			float c = b + b + b;
			eps = Math.abs(c - 1.0f);
		} while (eps == 0);
		EpsFloat = eps;
	}

	/**
	 * Estimates unit round-off in quantities of size x. This is a port of the epsilon function from EISPACK. See also
	 * https://www.researchgate.net/publication/2860253_A_Comment_on_the_Eispack_Machine_Epsilon_Routine
	 *
	 * @param x some quantity
	 * @return returns the smallest number e of the same kind as x such that 1 + e > 1.
	 */
	private static double epslon(double x) {
//		double eps;
//		double a = 4.0 / 3.0;
//		do {
//			double b = a - 1.0;
//			double c = b + b + b;
//			eps = Math.abs(c - 1.0);
//		} while (eps == 0);
		return EpsDouble * Math.abs(x);
	}
	
	private static float epslon(float x) {
		return EpsFloat * Math.abs(x);
		
	}
	
/* original FORTRAN code:
	   a = 4.0d0/3.0d0
	10 b = a - 1.0d0
	   c = b + b + b
	   eps = dabs(c-1.0d0)
	   if (eps .eq. 0.0d0) go to 10
	   epslon = eps*dabs(x)
	   return
	   end
*/
	
/*
program test_epsilon
    real :: x = 3.143
    real(8) :: y = 2.33
    print *, epsilon(x)
    print *, epsilon(y)
end program test_epsilon
 */
	
	// public static void main(String[] args) {
	// 	double x = 3.143;
	// 	System.out.println(epslon(x));
	// 	float y = 2.33f;
	// 	System.out.println(epslon(y));
	//
	// }

}

package imagingbook.common.math.eigen.accord;

public abstract class QZIT {

	enum State {
		L60, L70, L90, L95, L100, L120, L140, L150, L155, L160, L1000, L1001, Final;
	}

	/// <summary>  (line 474)
	///   Adaptation of the original Fortran QZIT routine from EISPACK.
	/// </summary>
	/// <remarks>
	///   This subroutine is the second step of the qz algorithm
	///   for solving generalized matrix eigenvalue problems,
	///   Siam J. Numer. anal. 10, 241-256(1973) by Moler and Stewart,
	///   as modified in technical note nasa tn d-7305(1973) by ward.
	///   
	///   This subroutine accepts a pair of real matrices, one of them
	///   in upper Hessenberg form and the other in upper triangular form.
	///   it reduces the Hessenberg matrix to quasi-triangular form using
	///   orthogonal transformations while maintaining the triangular form
	///   of the other matrix.  it is usually preceded by  qzhes  and
	///   followed by  qzval  and, possibly,  qzvec.
	///   
	///   For the full documentation, please check the original function.
	/// </remarks>
	static int qzit(int n, double[][] a, double[][] b, double eps1, boolean matz, double[][] z, int ierr) { // wilbur: was ref int ierr
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
		double epsa, epsb, anorm = 0, bnorm = 0;
		int enorn;
		boolean notlas;
	
		ierr = 0;
	
		// Compute epsa and epsb
		for (i = 0; i < n; ++i) {
			ani = 0.0;
			bni = 0.0;
	
			if (i != 0)
				ani = (Math.abs(a[i][(i - 1)]));
	
			for (j = i; j < n; ++j) {
				ani += Math.abs(a[i][j]);
				bni += Math.abs(b[i][j]);
			}
	
			if (ani > anorm)
				anorm = ani;
			if (bni > bnorm)
				bnorm = bni;
		}
	
		if (anorm == 0.0)
			anorm = 1.0;
		if (bnorm == 0.0)
			bnorm = 1.0;
	
		ep = eps1;
		if (ep == 0.0) {
			// Use round-off level if eps1 is zero
			ep = Special.Epslon(1.0);
		}
	
		epsa = ep * anorm;
		epsb = ep * bnorm;
	
		// Reduce a to quasi-triangular form, while keeping b triangular
		lor1 = 0;
		enorn = n;
		en = n - 1;
		itn = n * 30;
	
		State STATE = State.L60;
	
		while (STATE != State.Final) {
			switch (STATE) {
	
			case L60: 
				// Begin QZ step
				if (en <= 1) {
					STATE = State.L1001;
					break;
				}
				if (!matz) {
					enorn = en + 1;
				}
				its = 0;
				na = en - 1;
				enm2 = na;
				STATE = State.L70;
				break;
	
			case L70:
				ish = 2;
				// Check for convergence or reducibility.
				loop70: for (ll = 0; ll <= en; ++ll) {
					lm1 = en - ll - 1;
					l = lm1 + 1;
					if (l + 1 == 1) {
						STATE = State.L95;
						break loop70;
					}
					if ((Math.abs(a[l][lm1])) <= epsa)
						break loop70;
				}
				STATE = State.L90;
				break;
	
			case L90:
				a[l][lm1] = 0.0;
				if (l < na) {
					STATE = State.L95;
					break;
				}
				// 1-by-1 or 2-by-2 block isolated
				en = lm1;
				STATE = State.L60;
				break;
	
			case L95: // Check for small top of b
				ld = l;
				STATE = State.L100;
				break;
	
			case L100:
				l1 = l + 1;
				b11 = b[l][l];
	
				if (Math.abs(b11) > epsb) {
					STATE = State.L120;
					break;
				}
	
				b[l][l] = 0.0;
				s = (Math.abs(a[l][l]) + Math.abs(a[l1][l]));
				u1 = a[l][l] / s;
				u2 = a[l1][l] / s;
				r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1);
				v1 = -(u1 + r) / r;
				v2 = -u2 / r;
				u2 = v2 / v1;
	
				for (j = l; j < enorn; ++j) {
					t = a[l][j] + u2 * a[l1][j];
					a[l][j] += t * v1;
					a[l1][j] += t * v2;
	
					t = b[l][j] + u2 * b[l1][j];
					b[l][j] += t * v1;
					b[l1][j] += t * v2;
				}
	
				if (l != 0)
					a[l][lm1] = -a[l][lm1];
	
				lm1 = l;
				l = l1;
				STATE = State.L90;
				break;
	
			case L120:
				a11 = a[l][l] / b11;
				a21 = a[l1][l] / b11;
				if (ish == 1) {
					STATE = State.L140;
					break;
				}
	
				if (itn == 0) { // Iteration strategy
					STATE = State.L1000;
					break;
				}
				if (its == 10) {
					STATE = State.L155;
					break;
				}
	
				// Determine type of shift
				b22 = b[l1][l1];
				if (Math.abs(b22) < epsb)
					b22 = epsb;
				b33 = b[na][na];
				if (Math.abs(b33) < epsb)
					b33 = epsb;
				b44 = b[en][en];
				if (Math.abs(b44) < epsb)
					b44 = epsb;
				a33 = a[na][na] / b33;
				a34 = a[na][en] / b44;
				a43 = a[en][na] / b33;
				a44 = a[en][en] / b44;
				b34 = b[na][en] / b44;
				t = (a43 * b34 - a33 - a44) * .5;
				r = t * t + a34 * a43 - a33 * a44;
				if (r < 0.0) {
					STATE = State.L150;
					break;
				}
	
				// Determine single shift zero-th column of a
				ish = 1;
				r = Math.sqrt(r);
				sh = -t + r;
				s = -t - r;
				if (Math.abs(s - a44) < Math.abs(sh - a44))
					sh = s;
	
				// Look for two consecutive small sub-diagonal elements of a.
				loop120: for (ll = ld; ll + 1 <= enm2; ++ll) {
					l = enm2 + ld - ll - 1;
	
					if (l == ld) {
						STATE = State.L140;
						break loop120;
					}
					lm1 = l - 1;
					l1 = l + 1;
					t = a[l + 1][l + 1];
	
					if (Math.abs(b[l][l]) > epsb)
						t -= sh * b[l][l];
	
					if (Math.abs(a[l][lm1]) <= (Math.abs(t / a[l1][l])) * epsa) {
						STATE = State.L100;
						break loop120;
					}
				}
				STATE = State.L140;
				break;
	
			case L140:
				a1 = a11 - sh;
				a2 = a21;
				if (l != ld) {
					a[l][lm1] = -a[l][lm1];
				}
				STATE = State.L160;
				break;
	
			case L150: // Determine double shift zero-th column of a
				a12 = a[l][l1] / b22;
				a22 = a[l1][l1] / b22;
				b12 = b[l][l1] / b22;
				a1 = ((a33 - a11) * (a44 - a11) - a34 * a43 + a43 * b34 * a11) / a21 + a12 - a11 * b12;
				a2 = a22 - a11 - a21 * b12 - (a33 - a11) - (a44 - a11) + a43 * b34;
				a3 = a[l1 + 1][l1] / b22;
				STATE = State.L160;
				break;
	
			case L155: // Ad hoc shift
				a1 = 0.0;
				a2 = 1.0;
				a3 = 1.1605;
				STATE = State.L160;
				break;
	
			case L160:
				++its;
				--itn;
	
				if (!matz) {
					lor1 = ld;
				}
	
				mainloop: for (k = l; k <= na; ++k) {
					notlas = k != na && ish == 2;
					k1 = k + 1;
					k2 = k + 2;
	
					km1 = Math.max(k, l + 1) - 1; // Computing MAX
					ll = Math.min(en, k1 + ish); // Computing MIN
	
					if (!notlas) {
						// Zero a(k+1,k-1)
						if (k != l) {
							a1 = a[k][km1];
							a2 = a[k1][km1];
						}
	
						s = Math.abs(a1) + Math.abs(a2);
						if (s == 0.0) {
							STATE = State.L70;
							break mainloop;
						}
						u1 = a1 / s;
						u2 = a2 / s;
						r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1);
						v1 = -(u1 + r) / r;
						v2 = -u2 / r;
						u2 = v2 / v1;
	
						for (j = km1; j < enorn; ++j) {
							t = a[k][j] + u2 * a[k1][j];
							a[k][j] += t * v1;
							a[k1][j] += t * v2;
	
							t = b[k][j] + u2 * b[k1][j];
							b[k][j] += t * v1;
							b[k1][j] += t * v2;
						}
	
						if (k != l) {
							a[k1][km1] = 0.0;
						}
						// goto L240;
					}
	
					else {
						// Zero a(k+1,k-1) and a(k+2,k-1)
						if (k != l) {
							a1 = a[k][km1];
							a2 = a[k1][km1];
							a3 = a[k2][km1];
						}
	
						s = Math.abs(a1) + Math.abs(a2) + Math.abs(a3);
						if (s == 0.0) {
							// goto L260;
							continue;
						}
	
						u1 = a1 / s;
						u2 = a2 / s;
						u3 = a3 / s;
						r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2 + u3 * u3), u1);
						v1 = -(u1 + r) / r;
						v2 = -u2 / r;
						v3 = -u3 / r;
						u2 = v2 / v1;
						u3 = v3 / v1;
	
						for (j = km1; j < enorn; ++j) {
							t = a[k][j] + u2 * a[k1][j] + u3 * a[k2][j];
							a[k][j] += t * v1;
							a[k1][j] += t * v2;
							a[k2][j] += t * v3;
	
							t = b[k][j] + u2 * b[k1][j] + u3 * b[k2][j];
							b[k][j] += t * v1;
							b[k1][j] += t * v2;
							b[k2][j] += t * v3;
						}
	
						if (k != l) {
							a[k1][km1] = 0.0;
							a[k2][km1] = 0.0;
						}
	
						// Zero b(k+2,k+1) and b(k+2,k)
						s = (Math.abs(b[k2][k2])) + (Math.abs(b[k2][k1])) + (Math.abs(b[k2][k]));
	
						if (s != 0.0) {
							u1 = b[k2][k2] / s;
							u2 = b[k2][k1] / s;
							u3 = b[k2][k] / s;
							r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2 + u3 * u3), u1);
							v1 = -(u1 + r) / r;
							v2 = -u2 / r;
							v3 = -u3 / r;
							u2 = v2 / v1;
							u3 = v3 / v1;
	
							for (i = lor1; i < ll + 1; ++i) {
								t = a[i][k2] + u2 * a[i][k1] + u3 * a[i][k];
								a[i][k2] += t * v1;
								a[i][k1] += t * v2;
								a[i][k] += t * v3;
	
								t = b[i][k2] + u2 * b[i][k1] + u3 * b[i][k];
								b[i][k2] += t * v1;
								b[i][k1] += t * v2;
								b[i][k] += t * v3;
							}
	
							b[k2][k] = 0.0;
							b[k2][k1] = 0.0;
	
							if (matz) {
								for (i = 0; i < n; ++i) {
									t = z[i][k2] + u2 * z[i][k1] + u3 * z[i][k];
									z[i][k2] += t * v1;
									z[i][k1] += t * v2;
									z[i][k] += t * v3;
								}
							}
						}
					}
	
					// L240:
					// Zero b(k+1,k)
					s = (Math.abs(b[k1][k1])) + (Math.abs(b[k1][k]));
					if (s == 0.0) {
						// goto L260;
						continue;
					}
					u1 = b[k1][k1] / s;
					u2 = b[k1][k] / s;
					r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1);
					v1 = -(u1 + r) / r;
					v2 = -u2 / r;
					u2 = v2 / v1;
	
					for (i = lor1; i < ll + 1; ++i) {
						t = a[i][k1] + u2 * a[i][k];
						a[i][k1] += t * v1;
						a[i][k] += t * v2;
	
						t = b[i][k1] + u2 * b[i][k];
						b[i][k1] += t * v1;
						b[i][k] += t * v2;
					}
	
					b[k1][k] = 0.0;
	
					if (matz) {
						for (i = 0; i < n; ++i) {
							t = z[i][k1] + u2 * z[i][k];
							z[i][k1] += t * v1;
							z[i][k] += t * v2;
						}
					}
	
					// L260: ;
				}
	
				STATE = State.L70; // End QZ step
				break;
	
			case L1000: // Set error -- all eigenvalues have not converged after 30*n iterations
				ierr = en + 1;
				STATE = State.L1001;
				break;
	
			case L1001: // Save epsb for use by qzval and qzvec
				if (n > 1) {
					b[n - 1][0] = epsb;
				}
				STATE = State.Final;
				break;
	
			case Final:
				break;
			}
		}
	
		return 0;
	} // end of qzit()

}

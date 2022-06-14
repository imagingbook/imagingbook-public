package imagingbook.common.math.eigen.accord;

public abstract class EisPack {



	// EISPACK Routines (starting in line 305), see http://www.netlib.org/eispack/


	/// <summary>
	///   Adaptation of the original Fortran QZHES routine from EISPACK.
	/// </summary>
	/// <remarks>
	///   This subroutine is the first step of the qz algorithm
	///   for solving generalized matrix eigenvalue problems,
	///   Siam J. Numer. anal. 10, 241-256(1973) by Moler and Stewart.
	///
	///   This subroutine accepts a pair of real general matrices and
	///   reduces one of them to upper Hessenberg form and the other
	///   to upper triangular form using orthogonal transformations.
	///   it is usually followed by  qzit,  qzval  and, possibly,  qzvec.
	///   
	///   For the full documentation, please check the original function.
	/// </remarks>

	static int qzhes(int n, double[][] a, double[][] b, boolean matz, double[][] z) {
		int i, j, k, l;
		double r, s, t;
		int l1;
		double u1, u2, v1, v2;
		int lb, nk1;
		double rho;

		if (matz) {
			// If we are interested in computing the
			// eigenvectors, set Z to identity(n,n)
			for (j = 0; j < n; ++j) {
				for (i = 0; i < n; ++i)
					z[i][j] = 0.0;
				z[j][j] = 1.0;
			}
		}

		// Reduce b to upper triangular form
		if (n <= 1)
			return 0;
		for (l = 0; l < n - 1; ++l) {
			l1 = l + 1;
			s = 0.0;

			for (i = l1; i < n; ++i)
				s += (Math.abs(b[i][l]));

			if (s == 0.0)
				continue;
			s += (Math.abs(b[l][l]));
			r = 0.0;

			for (i = l; i < n; ++i) {
				// Computing 2nd power
				b[i][l] /= s;
				r += b[i][l] * b[i][l];
			}

			r = Special.Sign(Math.sqrt(r), b[l][l]);
			b[l][l] += r;
			rho = r * b[l][l];

			for (j = l1; j < n; ++j) {
				t = 0.0;
				for (i = l; i < n; ++i)
					t += b[i][l] * b[i][j];
				t = -t / rho;
				for (i = l; i < n; ++i)
					b[i][j] += t * b[i][l];
			}

			for (j = 0; j < n; ++j) {
				t = 0.0;
				for (i = l; i < n; ++i)
					t += b[i][l] * a[i][j];
				t = -t / rho;
				for (i = l; i < n; ++i)
					a[i][j] += t * b[i][l];
			}

			b[l][l] = -s * r;
			for (i = l1; i < n; ++i)
				b[i][l] = 0.0;
		}

		// Reduce a to upper Hessenberg form, while keeping b triangular
		if (n == 2)
			return 0;
		for (k = 0; k < n - 2; ++k) {
			nk1 = n - 2 - k;

			// for l=n-1 step -1 until k+1 do
			for (lb = 0; lb < nk1; ++lb) {
				l = n - lb - 2;
				l1 = l + 1;

				// Zero a(l+1,k)
				s = (Math.abs(a[l][k])) + (Math.abs(a[l1][k]));

				if (s == 0.0)
					continue;
				u1 = a[l][k] / s;
				u2 = a[l1][k] / s;
				r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1); // Special.Sign
				v1 = -(u1 + r) / r;
				v2 = -u2 / r;
				u2 = v2 / v1;

				for (j = k; j < n; ++j) {
					t = a[l][j] + u2 * a[l1][j];
					a[l][j] += t * v1;
					a[l1][j] += t * v2;
				}

				a[l1][k] = 0.0;

				for (j = l; j < n; ++j) {
					t = b[l][j] + u2 * b[l1][j];
					b[l][j] += t * v1;
					b[l1][j] += t * v2;
				}

				// Zero b(l+1,l)
				s = (Math.abs(b[l1][l1])) + (Math.abs(b[l1][l]));

				if (s == 0.0)
					continue;
				u1 = b[l1][l1] / s;
				u2 = b[l1][l] / s;
				r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1);
				v1 = -(u1 + r) / r;
				v2 = -u2 / r;
				u2 = v2 / v1;

				for (i = 0; i <= l1; ++i) {
					t = b[i][l1] + u2 * b[i][l];
					b[i][l1] += t * v1;
					b[i][l] += t * v2;
				}

				b[l1][l] = 0.0;

				for (i = 0; i < n; ++i) {
					t = a[i][l1] + u2 * a[i][l];
					a[i][l1] += t * v1;
					a[i][l] += t * v2;
				}

				if (matz) {
					for (i = 0; i < n; ++i) {
						t = z[i][l1] + u2 * z[i][l];
						z[i][l1] += t * v1;
						z[i][l] += t * v2;
					}
				}
			}
		}

		return 0;
	} // end of qzhes

	

	private enum qzitState {
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

	static int qzit(int n, double[][] a, double[][] b, double eps1, boolean matz, double[][] z, int ierr)	// wilbur: was ref int ierr
	{

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
		for (i = 0; i < n; ++i)
		{
			ani = 0.0;
			bni = 0.0;

			if (i != 0)
				ani = (Math.abs(a[i][(i - 1)]));

			for (j = i; j < n; ++j)
			{
				ani += Math.abs(a[i][j]);
				bni += Math.abs(b[i][j]);
			}

			if (ani > anorm) anorm = ani;
			if (bni > bnorm) bnorm = bni;
		}

		if (anorm == 0.0) anorm = 1.0;
		if (bnorm == 0.0) bnorm = 1.0;

		ep = eps1;
		if (ep == 0.0)
		{
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

		qzitState STATE = qzitState.L60;

		while (STATE != qzitState.Final) {
			switch (STATE) {
			// Begin QZ step
			case L60 :
				if (en <= 1) {
					STATE = qzitState.L1001;
					break;
				}
				if (!matz) {
					enorn = en + 1;
				}
				its = 0;
				na = en - 1;
				enm2 = na;
				STATE = qzitState.L70;
				break;

			case L70 :
				ish = 2;
				// Check for convergence or reducibility.
				STATE = qzitState.L90;
				loop70: for (ll = 0; ll <= en; ++ll)
				{
					lm1 = en - ll - 1;
					l = lm1 + 1;
					if (l + 1 == 1) {
						STATE = qzitState.L95;
						break loop70;
					}
					if ((Math.abs(a[l][lm1])) <= epsa)
						break loop70;
				}
				break;

			case L90 :
				a[l][lm1] = 0.0;
				if (l < na) {
					STATE = qzitState.L95;
					break;
				}
				// 1-by-1 or 2-by-2 block isolated
				en = lm1;
				STATE = qzitState.L60;
				break;

				// Check for small top of b 
			case L95:
				ld = l;
				STATE = qzitState.L100;
				break;

			case L100:
				l1 = l + 1;
				b11 = b[l][l];

				if (Math.abs(b11) > epsb) {
					STATE = qzitState.L120;
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

				for (j = l; j < enorn; ++j)
				{
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
				STATE = qzitState.L90;
				break;

			case L120:
				STATE = qzitState.L140;
				a11 = a[l][l] / b11;
				a21 = a[l1][l] / b11;
				if (ish == 1) {
					STATE = qzitState.L140;
					break;
				}

				// Iteration strategy
				if (itn == 0) {
					STATE = qzitState.L1000;
					break;
				}
				if (its == 10) {
					STATE = qzitState.L155;
					break;
				}

				// Determine type of shift
				b22 = b[l1][l1];
				if (Math.abs(b22) < epsb) b22 = epsb;
				b33 = b[na][na];
				if (Math.abs(b33) < epsb) b33 = epsb;
				b44 = b[en][en];
				if (Math.abs(b44) < epsb) b44 = epsb;
				a33 = a[na][na] / b33;
				a34 = a[na][en] / b44;
				a43 = a[en][na] / b33;
				a44 = a[en][en] / b44;
				b34 = b[na][en] / b44;
				t = (a43 * b34 - a33 - a44) * .5;
				r = t * t + a34 * a43 - a33 * a44;
				if (r < 0.0) {
					STATE = qzitState.L150;
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
				loop120: for (ll = ld; ll + 1 <= enm2; ++ll)
				{
					l = enm2 + ld - ll - 1;

					if (l == ld) {
						STATE = qzitState.L140;
						break loop120;
					}
					lm1 = l - 1;
					l1 = l + 1;
					t = a[l + 1][l + 1];

					if (Math.abs(b[l][l]) > epsb)
						t -= sh * b[l][l];

					if (Math.abs(a[l][lm1]) <= (Math.abs(t / a[l1][l])) * epsa) {
						STATE = qzitState.L100;
						break loop120;
					}
				}
				break;

			case L140 :
				a1 = a11 - sh;
				a2 = a21;
				if (l != ld)
					a[l][lm1] = -a[l][lm1];
				STATE = qzitState.L160;
				break;

			case L150 : // Determine double shift zero-th column of a
				a12 = a[l][l1] / b22;
				a22 = a[l1][l1] / b22;
				b12 = b[l][l1] / b22;
				a1 = ((a33 - a11) * (a44 - a11) - a34 * a43 + a43 * b34 * a11) / a21 + a12 - a11 * b12;
				a2 = a22 - a11 - a21 * b12 - (a33 - a11) - (a44 - a11) + a43 * b34;
				a3 = a[l1 + 1][l1] / b22;
				STATE = qzitState.L160;
				break;

			case L155 :	// Ad hoc shift
				STATE = qzitState.L160;
				a1 = 0.0;
				a2 = 1.0;
				a3 = 1.1605;
				break;

			case L160 :
				++its;
				--itn;

				if (!matz) {
					lor1 = ld;
				}

				mainloop : for (k = l; k <= na; ++k) {
					notlas = k != na && ish == 2;
					k1 = k + 1;
					k2 = k + 2;

					km1 = Math.max(k, l + 1) - 1; // Computing MAX
					ll = Math.min(en, k1 + ish);  // Computing MIN

					if (!notlas) {
						// Zero a(k+1,k-1)
						if (k != l) {
							a1 = a[k][km1];
							a2 = a[k1][km1];
						}

						s = Math.abs(a1) + Math.abs(a2);
						if (s == 0.0) {
							STATE = qzitState.L70;
							break mainloop;
						}
						u1 = a1 / s;
						u2 = a2 / s;
						r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1);
						v1 = -(u1 + r) / r;
						v2 = -u2 / r;
						u2 = v2 / v1;

						for (j = km1; j < enorn; ++j)
						{
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

						for (j = km1; j < enorn; ++j)
						{
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

							for (i = lor1; i < ll + 1; ++i)
							{
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

							if (matz)
							{
								for (i = 0; i < n; ++i)
								{
									t = z[i][k2] + u2 * z[i][k1] + u3 * z[i][k];
									z[i][k2] += t * v1;
									z[i][k1] += t * v2;
									z[i][k] += t * v3;
								}
							}
						}
					}
					
					//L240:
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

					for (i = lor1; i < ll + 1; ++i)
					{
						t = a[i][k1] + u2 * a[i][k];
						a[i][k1] += t * v1;
						a[i][k] += t * v2;

						t = b[i][k1] + u2 * b[i][k];
						b[i][k1] += t * v1;
						b[i][k] += t * v2;
					}

					b[k1][k] = 0.0;

					if (matz)
					{
						for (i = 0; i < n; ++i)
						{
							t = z[i][k1] + u2 * z[i][k];
							z[i][k1] += t * v1;
							z[i][k] += t * v2;
						}
					}

					//L260: ;
				}

				STATE = qzitState.L70; // End QZ step
				break;
				
			case L1000 : // Set error -- all eigenvalues have not converged after 30*n iterations
				ierr = en + 1;
				break;
				
			case L1001 : // Save epsb for use by qzval and qzvec
				if (n > 1) {
					b[n - 1][0] = epsb;
				}
				STATE = qzitState.Final;
				break;
			
			case Final :
				break;
			}

		}

		return 0;
	} // end of qzit()



	/// <summary>
	///   Adaptation of the original Fortran QZVAL routine from EISPACK.
	/// </summary>
	/// <remarks>
	///   This subroutine is the third step of the qz algorithm
	///   for solving generalized matrix eigenvalue problems,
	///   Siam J. Numer. anal. 10, 241-256(1973) by Moler and Stewart.
	///   
	///   This subroutine accepts a pair of real matrices, one of them
	///   in quasi-triangular form and the other in upper triangular form.
	///   it reduces the quasi-triangular matrix further, so that any
	///   remaining 2-by-2 blocks correspond to pairs of complex
	///   Eigenvalues, and returns quantities whose ratios give the
	///   generalized eigenvalues.  it is usually preceded by  qzhes
	///   and  qzit  and may be followed by  qzvec.
	///   
	///   For the full documentation, please check the original function.
	/// </remarks>
	static int qzval(int n, double[][] a, double[][] b, double[] alfr, double[] alfi, double[] beta, boolean matz, double[][] z) {
		return 0;
	}

	/*
    static int qzval(int n, double[][] a, double[][] b, double[] alfr, double[] alfi, double[] beta, boolean matz, double[][] z)
    {
        int i, j;
        int na, en, nn;
        double c, d, e = 0;
        double r, s, t;
        double a1, a2, u1, u2, v1, v2;
        double a11, a12, a21, a22;
        double b11, b12, b22;
        double di, ei;
        double an = 0, bn;
        double cq, dr;
        double cz, ti, tr;
        double a1i, a2i, a11i, a12i, a22i, a11r, a12r, a22r;
        double sqi, ssi, sqr, szi, ssr, szr;

        double epsb = b[n - 1, 0];
        int isw = 1;


        // Find eigenvalues of quasi-triangular matrices.
        for (nn = 0; nn < n; ++nn)
        {
            en = n - nn - 1;
            na = en - 1;

            if (isw == 2) goto L505;
            if (en == 0) goto L410;
            if (a[en, na] != 0.0) goto L420;

        // 1-by-1 block, one real root
        L410:
            alfr[en] = a[en, en];
            if (b[en, en] < 0.0)
            {
                alfr[en] = -alfr[en];
            }
            beta[en] = (Math.Abs(b[en, en]));
            alfi[en] = 0.0;
            goto L510;

        // 2-by-2 block
        L420:
            if (Math.Abs(b[na, na]) <= epsb) goto L455;
            if (Math.Abs(b[en, en]) > epsb) goto L430;
            a1 = a[en, en];
            a2 = a[en, na];
            bn = 0.0;
            goto L435;

        L430:
            an = Math.Abs(a[na, na]) + Math.Abs(a[na, en]) + Math.Abs(a[en, na]) + Math.Abs(a[en, en]);
            bn = Math.Abs(b[na, na]) + Math.Abs(b[na, en]) + Math.Abs(b[en, en]);
            a11 = a[na, na] / an;
            a12 = a[na, en] / an;
            a21 = a[en, na] / an;
            a22 = a[en, en] / an;
            b11 = b[na, na] / bn;
            b12 = b[na, en] / bn;
            b22 = b[en, en] / bn;
            e = a11 / b11;
            ei = a22 / b22;
            s = a21 / (b11 * b22);
            t = (a22 - e * b22) / b22;

            if (Math.Abs(e) <= Math.Abs(ei))
                goto L431;

            e = ei;
            t = (a11 - e * b11) / b11;

        L431:
            c = (t - s * b12) * .5;
            d = c * c + s * (a12 - e * b12);
            if (d < 0.0) goto L480;

            // Two real roots. Zero both a(en,na) and b(en,na)
            e += c + Special.Sign(Math.Sqrt(d), c);
            a11 -= e * b11;
            a12 -= e * b12;
            a22 -= e * b22;

            if (Math.Abs(a11) + Math.Abs(a12) < Math.Abs(a21) + Math.Abs(a22))
                goto L432;

            a1 = a12;
            a2 = a11;
            goto L435;

        L432:
            a1 = a22;
            a2 = a21;

        // Choose and apply real z
        L435:
            s = Math.Abs(a1) + Math.Abs(a2);
            u1 = a1 / s;
            u2 = a2 / s;
            r = Special.Sign(Math.Sqrt(u1 * u1 + u2 * u2), u1);
            v1 = -(u1 + r) / r;
            v2 = -u2 / r;
            u2 = v2 / v1;

            for (i = 0; i <= en; ++i)
            {
                t = a[i, en] + u2 * a[i, na];
                a[i, en] += t * v1;
                a[i, na] += t * v2;

                t = b[i, en] + u2 * b[i, na];
                b[i, en] += t * v1;
                b[i, na] += t * v2;
            }

            if (matz)
            {
                for (i = 0; i < n; ++i)
                {
                    t = z[i, en] + u2 * z[i, na];
                    z[i, en] += t * v1;
                    z[i, na] += t * v2;
                }
            }

            if (bn == 0.0) goto L475;
            if (an < System.Math.Abs(e) * bn) goto L455;
            a1 = b[na, na];
            a2 = b[en, na];
            goto L460;

        L455:
            a1 = a[na, na];
            a2 = a[en, na];

        // Choose and apply real q
        L460:
            s = System.Math.Abs(a1) + System.Math.Abs(a2);
            if (s == 0.0) goto L475;
            u1 = a1 / s;
            u2 = a2 / s;
            r = Special.Sign(Math.Sqrt(u1 * u1 + u2 * u2), u1);
            v1 = -(u1 + r) / r;
            v2 = -u2 / r;
            u2 = v2 / v1;

            for (j = na; j < n; ++j)
            {
                t = a[na, j] + u2 * a[en, j];
                a[na, j] += t * v1;
                a[en, j] += t * v2;

                t = b[na, j] + u2 * b[en, j];
                b[na, j] += t * v1;
                b[en, j] += t * v2;
            }

        L475:
            a[en, na] = 0.0;
            b[en, na] = 0.0;
            alfr[na] = a[na, na];
            alfr[en] = a[en, en];

            if (b[na, na] < 0.0)
                alfr[na] = -alfr[na];

            if (b[en, en] < 0.0)
                alfr[en] = -alfr[en];

            beta[na] = (System.Math.Abs(b[na, na]));
            beta[en] = (System.Math.Abs(b[en, en]));
            alfi[en] = 0.0;
            alfi[na] = 0.0;
            goto L505;

            // Two complex roots
        L480:
            e += c;
            ei = System.Math.Sqrt(-d);
            a11r = a11 - e * b11;
            a11i = ei * b11;
            a12r = a12 - e * b12;
            a12i = ei * b12;
            a22r = a22 - e * b22;
            a22i = ei * b22;

            if (System.Math.Abs(a11r) + System.Math.Abs(a11i) +
                System.Math.Abs(a12r) + System.Math.Abs(a12i) <
                System.Math.Abs(a21) + System.Math.Abs(a22r)
                + System.Math.Abs(a22i))
                goto L482;

            a1 = a12r;
            a1i = a12i;
            a2 = -a11r;
            a2i = -a11i;
            goto L485;

        L482:
            a1 = a22r;
            a1i = a22i;
            a2 = -a21;
            a2i = 0.0;

        // Choose complex z
        L485:
            cz = System.Math.Sqrt(a1 * a1 + a1i * a1i);
            if (cz == 0.0) goto L487;
            szr = (a1 * a2 + a1i * a2i) / cz;
            szi = (a1 * a2i - a1i * a2) / cz;
            r = System.Math.Sqrt(cz * cz + szr * szr + szi * szi);
            cz /= r;
            szr /= r;
            szi /= r;
            goto L490;

        L487:
            szr = 1.0;
            szi = 0.0;

        L490:
            if (an < (System.Math.Abs(e) + ei) * bn) goto L492;
            a1 = cz * b11 + szr * b12;
            a1i = szi * b12;
            a2 = szr * b22;
            a2i = szi * b22;
            goto L495;

        L492:
            a1 = cz * a11 + szr * a12;
            a1i = szi * a12;
            a2 = cz * a21 + szr * a22;
            a2i = szi * a22;

        // Choose complex q
        L495:
            cq = System.Math.Sqrt(a1 * a1 + a1i * a1i);
            if (cq == 0.0) goto L497;
            sqr = (a1 * a2 + a1i * a2i) / cq;
            sqi = (a1 * a2i - a1i * a2) / cq;
            r = System.Math.Sqrt(cq * cq + sqr * sqr + sqi * sqi);
            cq /= r;
            sqr /= r;
            sqi /= r;
            goto L500;

        L497:
            sqr = 1.0;
            sqi = 0.0;

        // Compute diagonal elements that would result if transformations were applied
        L500:
            ssr = sqr * szr + sqi * szi;
            ssi = sqr * szi - sqi * szr;
            i = 0;
            tr = cq * cz * a11 + cq * szr * a12 + sqr * cz * a21 + ssr * a22;
            ti = cq * szi * a12 - sqi * cz * a21 + ssi * a22;
            dr = cq * cz * b11 + cq * szr * b12 + ssr * b22;
            di = cq * szi * b12 + ssi * b22;
            goto L503;

        L502:
            i = 1;
            tr = ssr * a11 - sqr * cz * a12 - cq * szr * a21 + cq * cz * a22;
            ti = -ssi * a11 - sqi * cz * a12 + cq * szi * a21;
            dr = ssr * b11 - sqr * cz * b12 + cq * cz * b22;
            di = -ssi * b11 - sqi * cz * b12;

        L503:
            t = ti * dr - tr * di;
            j = na;

            if (t < 0.0)
                j = en;

            r = Math.Sqrt(dr * dr + di * di);
            beta[j] = bn * r;
            alfr[j] = an * (tr * dr + ti * di) / r;
            alfi[j] = an * t / r;
            if (i == 0) goto L502;

        L505:
            isw = 3 - isw;

        L510:
            ;
        }

        b[n - 1, 0] = epsb;

        return 0;
    }	// end of qzval()
	 */

	/// <summary>
	///   Adaptation of the original Fortran QZVEC routine from EISPACK.
	/// </summary>
	/// <remarks>
	///   This subroutine is the optional fourth step of the qz algorithm
	///   for solving generalized matrix eigenvalue problems,
	///   Siam J. Numer. anal. 10, 241-256(1973) by Moler and Stewart.
	///   
	///   This subroutine accepts a pair of real matrices, one of them in
	///   quasi-triangular form (in which each 2-by-2 block corresponds to
	///   a pair of complex eigenvalues) and the other in upper triangular
	///   form.  It computes the eigenvectors of the triangular problem and
	///   transforms the results back to the original coordinate system.
	///   it is usually preceded by  qzhes,  qzit, and  qzval.
	///   
	///   For the full documentation, please check the original function.
	/// </remarks>
	static int qzvec(int n, double[][] a, double[][] b, double[] alfr, double[] alfi, double[] beta, double[][] z) {
		return 0;
	}

	/*
    static int qzvec(int n, double[][] a, double[][] b, double[] alfr, double[] alfi, double[] beta, double[][] z)
    {
        int i, j, k, m;
        int na, ii, en, jj, nn, enm2;
        double d, q;
        double r = 0, s = 0, t, w, x = 0, y, t1, t2, w1, x1 = 0, z1 = 0, di;
        double ra, dr, sa;
        double ti, rr, tr, zz = 0;
        double alfm, almi, betm, almr;

        double epsb = b[n - 1, 0];
        int isw = 1;


        // for en=n step -1 until 1 do --
        for (nn = 0; nn < n; ++nn)
        {
            en = n - nn - 1;
            na = en - 1;
            if (isw == 2) goto L795;
            if (alfi[en] != 0.0) goto L710;

            // Real vector
            m = en;
            b[en, en] = 1.0;
            if (na == -1) goto L800;
            alfm = alfr[m];
            betm = beta[m];

            // for i=en-1 step -1 until 1 do --
            for (ii = 0; ii <= na; ++ii)
            {
                i = en - ii - 1;
                w = betm * a[i, i] - alfm * b[i, i];
                r = 0.0;

                for (j = m; j <= en; ++j)
                    r += (betm * a[i, j] - alfm * b[i, j]) * b[j, en];

                if (i == 0 || isw == 2)
                    goto L630;

                if (betm * a[i, i - 1] == 0.0)
                    goto L630;

                zz = w;
                s = r;
                goto L690;

            L630:
                m = i;
                if (isw == 2) goto L640;

                // Real 1-by-1 block
                t = w;
                if (w == 0.0)
                    t = epsb;
                b[i, en] = -r / t;
                goto L700;

            // Real 2-by-2 block
            L640:
                x = betm * a[i, i + 1] - alfm * b[i, i + 1];
                y = betm * a[i + 1, i];
                q = w * zz - x * y;
                t = (x * s - zz * r) / q;
                b[i, en] = t;
                if (Math.Abs(x) <= Math.Abs(zz)) goto L650;
                b[i + 1, en] = (-r - w * t) / x;
                goto L690;

            L650:
                b[i + 1, en] = (-s - y * t) / zz;

            L690:
                isw = 3 - isw;

            L700:
                ;
            }
            // End real vector
            goto L800;

        // Complex vector
        L710:
            m = na;
            almr = alfr[m];
            almi = alfi[m];
            betm = beta[m];

            // last vector component chosen imaginary so that eigenvector matrix is triangular
            y = betm * a[en, na];
            b[na, na] = -almi * b[en, en] / y;
            b[na, en] = (almr * b[en, en] - betm * a[en, en]) / y;
            b[en, na] = 0.0;
            b[en, en] = 1.0;
            enm2 = na;
            if (enm2 == 0) goto L795;

            // for i=en-2 step -1 until 1 do --
            for (ii = 0; ii < enm2; ++ii)
            {
                i = na - ii - 1;
                w = betm * a[i, i] - almr * b[i, i];
                w1 = -almi * b[i, i];
                ra = 0.0;
                sa = 0.0;

                for (j = m; j <= en; ++j)
                {
                    x = betm * a[i, j] - almr * b[i, j];
                    x1 = -almi * b[i, j];
                    ra = ra + x * b[j, na] - x1 * b[j, en];
                    sa = sa + x * b[j, en] + x1 * b[j, na];
                }

                if (i == 0 || isw == 2) goto L770;
                if (betm * a[i, i - 1] == 0.0) goto L770;

                zz = w;
                z1 = w1;
                r = ra;
                s = sa;
                isw = 2;
                goto L790;

            L770:
                m = i;
                if (isw == 2) goto L780;

                // Complex 1-by-1 block 
                tr = -ra;
                ti = -sa;

            L773:
                dr = w;
                di = w1;

                // Complex divide (t1,t2) = (tr,ti) / (dr,di)
            L775:
                if (Math.Abs(di) > Math.Abs(dr)) goto L777;
                rr = di / dr;
                d = dr + di * rr;
                t1 = (tr + ti * rr) / d;
                t2 = (ti - tr * rr) / d;

                switch (isw)
                {
                    case 1: goto L787;
                    case 2: goto L782;
                }

            L777:
                rr = dr / di;
                d = dr * rr + di;
                t1 = (tr * rr + ti) / d;
                t2 = (ti * rr - tr) / d;
                switch (isw)
                {
                    case 1: goto L787;
                    case 2: goto L782;
                }

               // Complex 2-by-2 block 
            L780:
                x = betm * a[i, i + 1] - almr * b[i, i + 1];
                x1 = -almi * b[i, i + 1];
                y = betm * a[i + 1, i];
                tr = y * ra - w * r + w1 * s;
                ti = y * sa - w * s - w1 * r;
                dr = w * zz - w1 * z1 - x * y;
                di = w * z1 + w1 * zz - x1 * y;
                if (dr == 0.0 && di == 0.0)
                    dr = epsb;
                goto L775;

            L782:
                b[i + 1, na] = t1;
                b[i + 1, en] = t2;
                isw = 1;
                if (Math.Abs(y) > Math.Abs(w) + Math.Abs(w1))
                    goto L785;
                tr = -ra - x * b[(i + 1), na] + x1 * b[(i + 1), en];
                ti = -sa - x * b[(i + 1), en] - x1 * b[(i + 1), na];
                goto L773;

            L785:
                t1 = (-r - zz * b[(i + 1), na] + z1 * b[(i + 1), en]) / y;
                t2 = (-s - zz * b[(i + 1), en] - z1 * b[(i + 1), na]) / y;

            L787:
                b[i, na] = t1;
                b[i, en] = t2;

            L790:
                ;
            }

            // End complex vector
        L795:
            isw = 3 - isw;

        L800:
            ;
        }

        // End back substitution. Transform to original coordinate system.
        for (jj = 0; jj < n; ++jj)
        {
            j = n - jj - 1;

            for (i = 0; i < n; ++i)
            {
                zz = 0.0;
                for (k = 0; k <= j; ++k)
                    zz += z[i, k] * b[k, j];
                z[i, j] = zz;
            }
        }

        // Normalize so that modulus of largest component of each vector is 1.
        // (isw is 1 initially from before)
        for (j = 0; j < n; ++j)
        {
            d = 0.0;
            if (isw == 2) goto L920;
            if (alfi[j] != 0.0) goto L945;

            for (i = 0; i < n; ++i)
            {
                if ((Math.Abs(z[i, j])) > d)
                    d = (Math.Abs(z[i, j]));
            }

            for (i = 0; i < n; ++i)
                z[i, j] /= d;

            goto L950;

        L920:
            for (i = 0; i < n; ++i)
            {
                r = System.Math.Abs(z[i, j - 1]) + System.Math.Abs(z[i, j]);
                if (r != 0.0)
                {
                    // Computing 2nd power
                    double u1 = z[i, j - 1] / r;
                    double u2 = z[i, j] / r;
                    r *= Math.Sqrt(u1 * u1 + u2 * u2);
                }
                if (r > d)
                    d = r;
            }

            for (i = 0; i < n; ++i)
            {
                z[i, j - 1] /= d;
                z[i, j] /= d;
            }

        L945:
            isw = 3 - isw;

        L950:
            ;
        }

        return 0;
    }	// end of qzvec()
	 */

}

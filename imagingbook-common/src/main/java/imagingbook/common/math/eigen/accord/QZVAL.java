package imagingbook.common.math.eigen.accord;

public abstract class QZVAL {
	
	// EISPACK Routines, see http://www.netlib.org/eispack/

	enum State {
		Linit, Lfinal, L505, L410, L420, L455, L430, L435, L480, L475, L485, L503, L502, L460, L510
	}

	/// <summary>
	/// Adaptation of the original Fortran QZVAL routine from EISPACK.
	/// </summary>
	/// <remarks>
	/// This subroutine is the third step of the qz algorithm
	/// for solving generalized matrix eigenvalue problems,
	/// Siam J. Numer. anal. 10, 241-256(1973) by Moler and Stewart.
	///
	/// This subroutine accepts a pair of real matrices, one of them
	/// in quasi-triangular form and the other in upper triangular form.
	/// it reduces the quasi-triangular matrix further, so that any
	/// remaining 2-by-2 blocks correspond to pairs of complex
	/// Eigenvalues, and returns quantities whose ratios give the
	/// generalized eigenvalues. it is usually preceded by qzhes
	/// and qzit and may be followed by qzvec.
	///
	/// For the full documentation, please check the original function.
	/// </remarks>
	static int qzval(int n, double[][] a, double[][] b, double[] alfr, double[] alfi, double[] beta, boolean matz,
			double[][] z) {
		System.out.println("runnin qzval");
		int i = 0, j;
		int na = 0, en = 0, nn;
		double c = 0, d = 0, e = 0;
		double r, s, t;
		double a1 = 0, a2 = 0, u1, u2, v1, v2;
		double a11 = 0, a12 = 0, a21 = 0, a22 = 0;
		double b11 = 0, b12 = 0, b22 = 0;
		double di = 0, ei = 0;
		double an = 0, bn = 0;
		double cq = 0, dr = 0;
		double cz = 0, ti = 0, tr = 0;
		double a1i = 0, a2i = 0, a11i, a12i, a22i, a11r, a12r, a22r;
		double sqi = 0, ssi = 0, sqr = 0, szi = 0, ssr = 0, szr = 0;

		double epsb = b[n - 1][0];
		int isw = 1;

		// Find eigenvalues of quasi-triangular matrices.
		for (nn = 0; nn < n; ++nn) {
			State state = State.Linit;
			StateLoop: while (state != State.Lfinal) {
				System.out.println("    StateLoop: " + state);
				switch (state) {
				case Linit:
					en = n - nn - 1;
					na = en - 1;

					if (isw == 2) {
						state = State.L505;
						break;
					}
					if (en == 0) {
						state = State.L410;
						break;
					}
					if (a[en][na] != 0.0) {
						state = State.L420;
						break;
					}
					state = State.L410;
					break;

				case L410:
					// 1-by-1 block, one real root
					alfr[en] = a[en][en];
					if (b[en][en] < 0.0) {
						alfr[en] = -alfr[en];
					}
					beta[en] = (Math.abs(b[en][en]));
					alfi[en] = 0.0;
					state = State.L510;
					break;

				case L420:
					// 2-by-2 block
					if (Math.abs(b[na][na]) <= epsb) {
						state = State.L455;
						break;
					}
					if (Math.abs(b[en][en]) > epsb) {
						state = State.L430;
						break;
					}
					a1 = a[en][en];
					a2 = a[en][na];
					bn = 0.0;
					state = State.L435;
					break;

				case L430:
					an = Math.abs(a[na][na]) + Math.abs(a[na][en]) + Math.abs(a[en][na]) + Math.abs(a[en][en]);
					bn = Math.abs(b[na][na]) + Math.abs(b[na][en]) + Math.abs(b[en][en]);
					a11 = a[na][na] / an;
					a12 = a[na][en] / an;
					a21 = a[en][na] / an;
					a22 = a[en][en] / an;
					b11 = b[na][na] / bn;
					b12 = b[na][en] / bn;
					b22 = b[en][en] / bn;
					e = a11 / b11;
					ei = a22 / b22;
					s = a21 / (b11 * b22);
					t = (a22 - e * b22) / b22;

					if (Math.abs(e) > Math.abs(ei)) {
						e = ei;
						t = (a11 - e * b11) / b11;
					}

					c = (t - s * b12) * .5;
					d = c * c + s * (a12 - e * b12);

					if (d < 0.0) {
						state = State.L480;
						break;
					}

					// Two real roots. Zero both a(en,na) and b(en,na)
					e += c + Special.Sign(Math.sqrt(d), c);
					a11 -= e * b11;
					a12 -= e * b12;
					a22 -= e * b22;

					if (Math.abs(a11) + Math.abs(a12) < Math.abs(a21) + Math.abs(a22)) {
						a1 = a22;
						a2 = a21;
					} else {
						a1 = a12;
						a2 = a11;
					}
					state = State.L435;
					break;

				case L435:
					// Choose and apply real z
					s = Math.abs(a1) + Math.abs(a2);
					u1 = a1 / s;
					u2 = a2 / s;
					r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1);
					v1 = -(u1 + r) / r;
					v2 = -u2 / r;
					u2 = v2 / v1;

					for (i = 0; i <= en; ++i) {
						t = a[i][en] + u2 * a[i][na];
						a[i][en] += t * v1;
						a[i][na] += t * v2;

						t = b[i][en] + u2 * b[i][na];
						b[i][en] += t * v1;
						b[i][na] += t * v2;
					}

					if (matz) {
						for (i = 0; i < n; ++i) {
							t = z[i][en] + u2 * z[i][na];
							z[i][en] += t * v1;
							z[i][na] += t * v2;
						}
					}

					if (bn == 0.0) {
						state = State.L475;
						break;
					}
					if (an < Math.abs(e) * bn) {
						state = State.L455;
						break;
					}
					a1 = b[na][na];
					a2 = b[en][na];
					state = State.L460;
					break;

				case L455:
					a1 = a[na][na];
					a2 = a[en][na];
					state = State.L475;
					break;

				case L460:
					s = Math.abs(a1) + Math.abs(a2);
					if (s == 0.0) {
						state = State.L475;
						break;
					}
					u1 = a1 / s;
					u2 = a2 / s;
					r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1);
					v1 = -(u1 + r) / r;
					v2 = -u2 / r;
					u2 = v2 / v1;

					for (j = na; j < n; ++j) {
						t = a[na][j] + u2 * a[en][j];
						a[na][j] += t * v1;
						a[en][j] += t * v2;

						t = b[na][j] + u2 * b[en][j];
						b[na][j] += t * v1;
						b[en][j] += t * v2;
					}
					state = State.L475;
					break;

				case L475:
					a[en][na] = 0.0;
					b[en][na] = 0.0;
					alfr[na] = a[na][na];
					alfr[en] = a[en][en];

					if (b[na][na] < 0.0)
						alfr[na] = -alfr[na];

					if (b[en][en] < 0.0)
						alfr[en] = -alfr[en];

					beta[na] = (Math.abs(b[na][na]));
					beta[en] = (Math.abs(b[en][en]));
					alfi[en] = 0.0;
					alfi[na] = 0.0;
					// goto L505;
					state = State.L505;
					break;

				case L480:
					// Two complex roots
					e += c;
					ei = Math.sqrt(-d);
					a11r = a11 - e * b11;
					a11i = ei * b11;
					a12r = a12 - e * b12;
					a12i = ei * b12;
					a22r = a22 - e * b22;
					a22i = ei * b22;

					if (Math.abs(a11r) + Math.abs(a11i) + Math.abs(a12r) + Math.abs(a12i) < Math.abs(a21) + Math.abs(a22r)
					+ Math.abs(a22i)) {
						// goto L482;
						a1 = a22r;
						a1i = a22i;
						a2 = -a21;
						a2i = 0.0;
					} else {
						a1 = a12r;
						a1i = a12i;
						a2 = -a11r;
						a2i = -a11i;
					}
					// goto L485;
					state = State.L485;
					break;

				case L485:
					// Choose complex z
					cz = Math.sqrt(a1 * a1 + a1i * a1i);
					if (cz == 0.0) {
						szr = 1.0;
						szi = 0.0;
					} else {
						szr = (a1 * a2 + a1i * a2i) / cz;
						szi = (a1 * a2i - a1i * a2) / cz;
						r = Math.sqrt(cz * cz + szr * szr + szi * szi);
						cz /= r;
						szr /= r;
						szi /= r;
					}

					// L490:
					if (an < (Math.abs(e) + ei) * bn) {
						// goto L492;
						a1 = cz * a11 + szr * a12;
						a1i = szi * a12;
						a2 = cz * a21 + szr * a22;
						a2i = szi * a22;
					} else {
						a1 = cz * b11 + szr * b12;
						a1i = szi * b12;
						a2 = szr * b22;
						a2i = szi * b22;
					}

					// Choose complex q
					// L495:
					cq = Math.sqrt(a1 * a1 + a1i * a1i);
					if (cq == 0.0) {
						sqr = 1.0;
						sqi = 0.0;
					} else {
						sqr = (a1 * a2 + a1i * a2i) / cq;
						sqi = (a1 * a2i - a1i * a2) / cq;
						r = Math.sqrt(cq * cq + sqr * sqr + sqi * sqi);
						cq /= r;
						sqr /= r;
						sqi /= r;
					}

					// Compute diagonal elements that would result if transformations were applied
					// L500:
					ssr = sqr * szr + sqi * szi;
					ssi = sqr * szi - sqi * szr;
					i = 0;
					tr = cq * cz * a11 + cq * szr * a12 + sqr * cz * a21 + ssr * a22;
					ti = cq * szi * a12 - sqi * cz * a21 + ssi * a22;
					dr = cq * cz * b11 + cq * szr * b12 + ssr * b22;
					di = cq * szi * b12 + ssi * b22;
					// goto L503;
					state = State.L503;
					break;

				case L502:
					i = 1;
					tr = ssr * a11 - sqr * cz * a12 - cq * szr * a21 + cq * cz * a22;
					ti = -ssi * a11 - sqi * cz * a12 + cq * szi * a21;
					dr = ssr * b11 - sqr * cz * b12 + cq * cz * b22;
					di = -ssi * b11 - sqi * cz * b12;
					state = State.L503;
					break;

				case L503:
					t = ti * dr - tr * di;
					j = na;

					if (t < 0.0) {
						j = en;
					}
					r = Math.sqrt(dr * dr + di * di);
					beta[j] = bn * r;
					alfr[j] = an * (tr * dr + ti * di) / r;
					alfi[j] = an * t / r;
					if (i == 0) {
						// goto L502;
						state = State.L502;
						break;
					}
					state = State.L505;
					break;

				case L505:
					isw = 3 - isw;
					state = State.L510;
					break;

				case L510:
					state = State.Lfinal;
					break;
					
				case Lfinal:
					throw new RuntimeException("this should never happen!");
//					break StateLoop;
				}
			}	// end StateLoop: while (state

		}	// end for (nn ..

		b[n - 1][0] = epsb;

		System.out.println("done qzval");
		return 0;
	} // end of qzval()

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.math.eigen.eispack;

abstract class QZVEC {
	
	public static boolean VERBOSE = false;

	// EISPACK Routines, see http://www.netlib.org/eispack/

	/**
	 * <p>
	 * This subroutine is the optional fourth step of the qz algorithm for solving
	 * generalized matrix eigenvalue problems, Siam J. Numer. Anal. 10, 241-256
	 * (1973) by Moler and Stewart. This description has been adapted from the
	 * original version (dated August 1983).
	 * </p>
	 * <p>
	 * This subroutine accepts a pair of real matrices, one of them in
	 * quasi-triangular form (in which each 2-by-2 block corresponds to a pair of
	 * complex eigenvalues) and the other in upper triangular form. It computes the
	 * eigenvectors of the triangular problem and transforms the results back to the
	 * original coordinate system. It is usually preceded by qzhes, qzit, and qzval.
	 * </p>
	 * <p>
	 * On output:
	 * </p>
	 * <ul>
	 * <li><strong>a</strong> is unaltered. Its subdiagonal elements provide
	 * information about the storage of the complex eigenvectors.</li>
	 * 
	 * <li><strong>b</strong> has been destroyed.</li>
	 * 
	 * <li><strong>alfr</strong>, <strong>alfi</strong> and <strong>beta</strong>
	 * are unaltered.</li>
	 * 
	 * <li><strong>z</strong> contains the real and imaginary parts of the
	 * eigenvectors. If alfi(i) = 0.0, the i-th eigenvalue is real and the i-th
	 * column of z contains its eigenvector. if alfi(i) &ne; 0.0, the i-th
	 * eigenvalue is complex. if alfi(i) &gt; 0.0, the eigenvalue is the first of a
	 * complex pair and the i-th and (i+1)-th columns of z contain its eigenvector.
	 * if alfi(i) &lt; 0.0, the eigenvalue is the second of a complex pair and the
	 * (i-1)-th and i-th columns of z contain the conjugate of its eigenvector. Each
	 * eigenvector is normalized so that the modulus of its largest component is
	 * 1.0.</li>
	 * </ul>
	 * 
	 * @param a    contains a real upper quasi-triangular matrix.
	 * @param b    contains a real upper triangular matrix. In addition, location
	 *             b[n-1][0] contains the tolerance quantity (epsb) computed and
	 *             saved in qzit.
	 * @param alfr , <strong>alfi</strong>, and <strong>beta</strong> are vectors
	 *             with components whose ratios ((alfr + i * alfi) / beta) are the
	 *             generalized eigenvalues. They are usually obtained from qzval.
	 * @param alfi see <strong>alfr</strong>
	 * @param beta see <strong>alfr</strong>
	 * @param z    contains the transformation matrix produced in the reductions by
	 *             qzhes, qzit, and qzval, if performed. If the eigenvectors of the
	 *             triangular problem are desired, z must contain the identity
	 *             matrix.
	 */
	@SuppressWarnings("unused")
	public static void qzvec(double[][] a, double[][] b, double[] alfr, double[] alfi, double[] beta, double[][] z) {

		final int n = a.length;

		int i = 0, j, k, m;
		int na = 0, ii, en = 0, jj, nn, enm2;
		double d = 0, q;
		double r = 0, s = 0, t = 0, w = 0, x = 0, y = 0, t1 = 0, t2 = 0, w1 = 0, x1 = 0, z1 = 0, di = 0;
		double ra = 0, dr = 0, sa = 0;
		double ti = 0, rr, tr = 0, zz = 0;
		double alfm, almi, betm, almr;

		double epsb = b[n - 1][0];
		int isw = 1;

		if (VERBOSE) System.out.println("QZVEC: starting Loop A ***********************");
		LoopA: for (nn = 0; nn < n; nn++) {

			StateA stateA = StateA.Ainit;
			StateLoopA: while (stateA != StateA.Afinal) {

				switch(stateA) {
				case Ainit :
					en = n - 1 - nn;	// TODO: check!!
					na = en - 1;
					if (isw == 2) {
						stateA = StateA.A795;
						break;
					}
					if (alfi[en] != 0.0) {
						stateA = StateA.A710;
						break;
					}

					// Real vector
					m = en;
					b[en][en] = 1.0;
					if (na == -1) {
						stateA = StateA.A800;
						break;
					}
					alfm = alfr[m];
					betm = beta[m];

					if (VERBOSE) System.out.println("QZVEC: starting Loop B ***********************");
					
					// for i=en-1 step -1 until 1 do --
					LoopB: for (ii = 0; ii <= na; ii++) {
						
						StateB stateB = StateB.Binit;
						StateLoopB: while (stateB != StateB.Bfinal) {
							switch (stateB) {
							case Binit :
								i = en - ii - 1;
								w = betm * a[i][i] - alfm * b[i][i];
								r = 0.0;

								for (j = m; j <= en; j++) {
									r = r +(betm * a[i][j] - alfm * b[i][j]) * b[j][en];
								}	// L610

								if (i == 0 || isw == 2) {
									stateB = StateB.B630;
									break;
								}
								if (betm * a[i][i - 1] == 0.0) {
									stateB = StateB.B630;
									break;
								}
								zz = w;
								s = r;
								stateB = StateB.B690;
								break;

							case B630 :
								m = i;
								if (isw == 2) {
									stateB = StateB.B640;
									break;
								}
								// real 1-by-1 block
								t = w;
								if (w == 0.0) {
									t = epsb;
								}
								b[i][en] = -r / t;
								stateB = StateB.B700;
								break;

							case B640 :
								// real 2-by-2 block
								x = betm * a[i][i + 1] - alfm * b[i][i + 1];
								y = betm * a[i + 1][i];
								q = w * zz - x * y;
								t = (x * s - zz * r) / q;
								b[i][en] = t;
								if (Math.abs(x) <= Math.abs(zz)) {
									stateB = StateB.B650;
									break;
								}
								b[i + 1][en] = (-r - w * t) / x;
								stateB = StateB.B690;
								break;

							case B650 :
								b[i + 1][en] = (-s - y * t) / zz;
								stateB = StateB.B690;
								break;

							case B690 :
								isw = 3 - isw;
								stateB = StateB.B700;
								break;

							case B700 :
								stateB = StateB.Bfinal;
								break;

							case Bfinal :
								throw new RuntimeException("this should never happen!");

							}	// end switch (stateB)	
						}	// end StateLoopB    			
					}	// L700: end of LoopB
					if (VERBOSE) System.out.println("QZVEC: finished Loop B ***********************");
					// end real vector
					stateA = StateA.A800;
					break;

				case A710:
					// complex vector
					m = na;
					almr = alfr[m];
					almi = alfi[m];
					betm = beta[m];
					// last vector component chosen imaginary so that eigenvector matrix is triangular
					y = betm * a[en][na];
					b[na][na] = -almi * b[en][en] / y;
					b[na][en] = (almr * b[en][en] - betm * a[en][en]) / y;
					b[en][na] = 0.0;
					b[en][en] = 1.0;
					enm2 = na;			// TODO: check!
					if (enm2 == 0) {
						stateA = StateA.A795;
						break;
					}

					if (VERBOSE) System.out.println("QZVEC: starting Loop C, enm2 = " + enm2);
					LoopC: for (ii = 0; ii < enm2; ii++) {
						if (VERBOSE) System.out.println("    ii = " + ii);
						StateC stateC = StateC.Cinit;
						StateLoopC: while (stateC != StateC.Cfinal) {
							if (VERBOSE) System.out.println("       StateLoopC: " + stateC);
							switch (stateC) {
							
							case Cinit :
								i = na - ii - 1;
								w = betm * a[i][i] - almr * b[i][i];
								w1 = -almi * b[i][i];
								ra = 0.0;
								sa = 0.0;

								for (j = m; j <= en; j++)
								{
									x = betm * a[i][j] - almr * b[i][j];
									x1 = -almi * b[i][j];
									ra = ra + x * b[j][na] - x1 * b[j][en];
									sa = sa + x * b[j][en] + x1 * b[j][na];
								}	// L760

								if (i == 0 || isw == 2) {
									stateC = StateC.C770;
									break;
								}
								if (betm * a[i][i - 1] == 0.0) {
									stateC = StateC.C770;
									break;
								}
								zz = w;
								z1 = w1;
								r = ra;
								s = sa;
								isw = 2;
								stateC = StateC.C790;
								break;

							case C770 :
								m = i;
								if (isw == 2) {
									stateC = StateC.C780;
									break;
								}
								// complex 1-by-1 block 
								tr = -ra;
								ti = -sa;
								stateC = StateC.C773;
								break;

							case C773:
								dr = w;
								di = w1;
								stateC = StateC.C775;
								break;

							case C775:
								// complex divide (t1,t2) = (tr,ti) / (dr,di)
								if (Math.abs(di) > Math.abs(dr)) {
									stateC = StateC.C777;
									break;
								}							
								rr = di / dr;
								d = dr + di * rr;
								t1 = (tr + ti * rr) / d;
								t2 = (ti - tr * rr) / d;

								switch (isw) {	// go to (787,782), isw
								case 1: 
									stateC = StateC.C787;
									continue StateLoopC;
									//break;
								case 2:
									stateC = StateC.C782;
									continue StateLoopC;
									//break;
								default: 
									throw new RuntimeException("illegal state isw = " + isw);
								}
								//stateC = StateC.C777;
								//break;

							case C777:
								rr = dr / di;
								d = dr * rr + di;
								t1 = (tr * rr + ti) / d;
								t2 = (ti * rr - tr) / d;
								switch (isw) {	// go to (787,782), isw
								case 1: 
									stateC = StateC.C787;
									continue StateLoopC;
									//break;
								case 2: 
									stateC = StateC.C782;
									continue StateLoopC;
									//break;
								default: 
									throw new RuntimeException("illegal state isw = " + isw);
								}
								//stateC = StateC.C780;
								//break;

							case C780:
								// complex 2-by-2 block 
								x = betm * a[i][i + 1] - almr * b[i][i + 1];
								x1 = -almi * b[i][i + 1];
								y = betm * a[i + 1][i];
								tr = y * ra - w * r + w1 * s;
								ti = y * sa - w * s - w1 * r;
								dr = w * zz - w1 * z1 - x * y;
								di = w * z1 + w1 * zz - x1 * y;
								if (dr == 0.0 && di == 0.0) {
									dr = epsb;
								}
								stateC = StateC.C775;
								break;

							case C782:
								b[i + 1][na] = t1;
								b[i + 1][en] = t2;
								isw = 1;
								if (Math.abs(y) > Math.abs(w) + Math.abs(w1)) {
									stateC = StateC.C785;
									break;
								}
								tr = -ra - x * b[(i + 1)][na] + x1 * b[(i + 1)][en];
								ti = -sa - x * b[(i + 1)][en] - x1 * b[(i + 1)][na];
								stateC = StateC.C773;
								break;

							case C785:
								t1 = (-r - zz * b[(i + 1)][na] + z1 * b[(i + 1)][en]) / y;
								t2 = (-s - zz * b[(i + 1)][en] - z1 * b[(i + 1)][na]) / y;
								stateC = StateC.C787;
								break;

							case C787:
								b[i][na] = t1;
								b[i][en] = t2;
								stateC = StateC.C790;
								break;

							case C790:
								stateC = StateC.Cfinal;
								break;

							case Cfinal:
								throw new RuntimeException("this should never happen!");

							}	// end switch(stateC)
						}	// end StateLoopC

					}	// L790: end of LoopC:

					if (VERBOSE) System.out.println("QZVEC: finished Loop C ***********************");
					stateA = StateA.A795;
					break;
					// end complex vector

				case A795:
					isw = 3 - isw;
					stateA = StateA.A800;
					break;

				case A800:
					stateA = StateA.Afinal;
					break;
					
				case Afinal:
					throw new RuntimeException("this should never happen!");
				}	// end switch(stateA)

			}	// end StateLoopA 
		}	// L800: end LoopA
		if (VERBOSE) System.out.println("QZVEC: finished Loop A ***********************");

		// end back substitution. transform to original coordinate system.
		for (jj = 0; jj < n; jj++) {
			j = n - jj - 1;

			for (i = 0; i < n; i++) {
				zz = 0.0;
				for (k = 0; k <= j; k++) {
					zz = zz + z[i][k] * b[k][j];
				}	// L860
				z[i][j] = zz;
			}
		} 	// L880: end for (jj ...

		if (VERBOSE) System.out.println("QZVEC: starting Loop D ***********************");
		// normalize so that modulus of largest component of each vector is 1.
		// (isw is 1 initially from before)
		LoopD: for (j = 0; j < n; j++) {
			
			StateD stateD = StateD.Dinit;
			StateLoopD: while (stateD != StateD.Dfinal) {

				switch (stateD) {
				case Dinit :
					d = 0.0;
					if (isw == 2) {
						stateD = StateD.D920;
						break;
					}
					if (alfi[j] != 0.0) {
						stateD = StateD.D945;
						break;
					}

					for (i = 0; i < n; i++) {
						//	if ((Math.abs(z[i][j])) > d) d = (Math.abs(z[i][j]));
						d = Math.max(d, Math.abs(z[i][j]));
					} // L890

					for (i = 0; i < n; i++) {
						z[i][j] = z[i][j] / d;
					}	// L900

					stateD = StateD.D950;
					break;

				case D920:
					for (i = 0; i < n; i++) {
						r = Math.abs(z[i][j - 1]) + Math.abs(z[i][j]);
						if (r != 0.0) {
							// r = r * dsqrt((z(i,j-1)/r)**2 + (z(i,j)/r)**2)
							r = r * Math.hypot(z[i][j - 1] / r, z[i][j] / r);
						}
						if (r > d) {
							d = r;
						}
					}	// L930

					for (i = 0; i < n; i++) {
						z[i][j - 1] = z[i][j - 1] / d;
						z[i][j] = z[i][j] / d;
					}	// L940

					stateD = StateD.D945;
					break;

				case D945:
					isw = 3 - isw;
					stateD = StateD.D950;
					break;

				case D950:
					stateD = StateD.Dfinal;
					break;

				case Dfinal:
					throw new RuntimeException("this should never happen!");

				}	// end switch(stateD)
			}	// end StateLoopD

		}	// L950: end LoopD
		if (VERBOSE) System.out.println("QZVEC: finished Loop D ***********************");

	}	// end of qzvec()


	private enum StateA {
		Ainit, Afinal, A795, A710, A800
	}

	private enum StateB {
		Binit, Bfinal, B630, B690, B640, B700, B650
	}

	private enum StateC {
		Cinit, Cfinal, C770, C790, C780, C773, C775, C777, C787, C782, C785
	}

	private enum StateD {
		Dinit, Dfinal, D920, D945, D950
	}

}

package imagingbook.common.math.eigen.accord;

public abstract class QZVEC {
	
	enum StateA {
		Initial, Final, L795, L710, L800
	}
	
	enum StateB {
		Initial, Final, L630, L690, L640, L700, L650
	}
	
	enum StateC {
		Initial, Final, L770, L790, L780, L773, L775, L777, L787, L782, L785
	}
	
	enum StateD {
		Initial, Final, L920, L945, L950
	}
	
	
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
        int i = 0, j, k, m;
        int na = 0, ii, en = 0, jj, nn, enm2;
        double d = 0, q;
        double r = 0, s = 0, t = 0, w = 0, x = 0, y = 0, t1 = 0, t2 = 0, w1 = 0, x1 = 0, z1 = 0, di = 0;
        double ra = 0, dr = 0, sa = 0;
        double ti = 0, rr, tr = 0, zz = 0;
        double alfm, almi, betm, almr;

        double epsb = b[n - 1][0];
        int isw = 1;

        // for en=n step -1 until 1 do --
        LoopA: for (nn = 0; nn < n; ++nn) {

        	StateA stateA = StateA.Initial;
        	while (stateA != StateA.Final) {
        		
        		switch(stateA) {
        		case Initial :
        			en = n - nn - 1;
            		na = en - 1;
            		if (isw == 2) {
            			stateA = StateA.L795;
            			break;
            		}
            		if (alfi[en] != 0.0) {
            			stateA = StateA.L710;
            			break;
            		}
            		
            		// Real vector
            		m = en;
            		b[en][en] = 1.0;
            		if (na == -1) {
            			stateA = StateA.L800;
            			break;
            		}
            		alfm = alfr[m];
            		betm = beta[m];
            		
               		// for i=en-1 step -1 until 1 do --
            		LoopB: for (ii = 0; ii <= na; ++ii) {
            			StateB stateB = StateB.Initial;
            			while (stateB != StateB.Final) {
            				switch (stateB) {
            				case Initial :
            					i = en - ii - 1;
                    			w = betm * a[i][i] - alfm * b[i][i];
                    			r = 0.0;

                    			for (j = m; j <= en; ++j)
                    				r += (betm * a[i][j] - alfm * b[i][j]) * b[j][en];

                    			if (i == 0 || isw == 2) {
                    				stateB = StateB.L630;
                    				break;
                    			}
                    			if (betm * a[i][i - 1] == 0.0) {
                    				stateB = StateB.L630;
                    				break;
                    			}
                    			zz = w;
                    			s = r;
                    			stateB = StateB.L690;
                    			break;
                    			
            				case L630 :
            					m = i;
                    			if (isw == 2) {
                    				stateB = StateB.L640;
                    				break;
                    			}
                    			// Real 1-by-1 block
                    			t = w;
                    			if (w == 0.0)
                    				t = epsb;
                    			b[i][en] = -r / t;
                    			stateB = StateB.L700;
                    			break;
                    			
            				case L640 :
            					// Real 2-by-2 block
            					x = betm * a[i][i + 1] - alfm * b[i][i + 1];
                    			y = betm * a[i + 1][i];
                    			q = w * zz - x * y;
                    			t = (x * s - zz * r) / q;
                    			b[i][en] = t;
                    			if (Math.abs(x) <= Math.abs(zz)) {
                    				stateB = StateB.L650;
                    				break;
                    			}
                    			b[i + 1][en] = (-r - w * t) / x;
                    			stateB = StateB.L690;
                    			break;
                    			
            				case L650 :
            					b[i + 1][en] = (-s - y * t) / zz;
            					break;
            					
            				case L690 :
            					isw = 3 - isw;
            					break;
            					
            				case L700 :
            					stateB = StateB.Final;
            					break;
            					
            				case Final :
            					break LoopB;
                    			
            				}	// end switch (stateB)
	
            			}	// end while (stateB ...
            			
            		}	// end of LoopB, for (ii ..
            		// End real vector
            		
            		stateA = StateA.L800;
            		break;
				
				case L710:
					// Complex vector
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
	        		enm2 = na;
	        		if (enm2 == 0) {
	        			stateA = StateA.L795;
	        			break;
	        		}
	        		
	        		// for i=en-2 step -1 until 1 do --
	        		LoopC: for (ii = 0; ii < enm2; ++ii) {
	        			StateC stateC = StateC.Initial;
	        			while (stateC != StateC.Final) {
	        				switch (stateC) {
	        				case Initial :
	        					i = na - ii - 1;
	    	        			w = betm * a[i][i] - almr * b[i][i];
	    	        			w1 = -almi * b[i][i];
	    	        			ra = 0.0;
	    	        			sa = 0.0;

	    	        			for (j = m; j <= en; ++j)
	    	        			{
	    	        				x = betm * a[i][j] - almr * b[i][j];
	    	        				x1 = -almi * b[i][j];
	    	        				ra = ra + x * b[j][na] - x1 * b[j][en];
	    	        				sa = sa + x * b[j][en] + x1 * b[j][na];
	    	        			}

	    	        			if (i == 0 || isw == 2) {
	    	        				stateC = StateC.L770;
	    	        				break;
	    	        			}
	    	        			if (betm * a[i][i - 1] == 0.0) {
	    	        				stateC = StateC.L770;
	    	        				break;
	    	        			}

	    	        			zz = w;
	    	        			z1 = w1;
	    	        			r = ra;
	    	        			s = sa;
	    	        			isw = 2;
	    	        			stateC = StateC.L790;
	        					break;
	        				
	        				case L770 :
	        					m = i;
	    	        			if (isw == 2) {
	    	        				stateC = StateC.L780;
	    	        				break;
	    	        			}
	    	        			// Complex 1-by-1 block 
	    	        			tr = -ra;
	    	        			ti = -sa;
	    	        			stateC = StateC.L773;
	        					break;
	        					
	        				case L773:
	    	        			dr = w;
	    	        			di = w1;
	    	        			stateC = StateC.L775;
	        					break;
	        				
	        				case L775:
	        					// Complex divide (t1,t2) = (tr,ti) / (dr,di)
		        				if (Math.abs(di) > Math.abs(dr)) {
		        					stateC = StateC.L777;
		        					break;
		        				}
			        			rr = di / dr;
			        			d = dr + di * rr;
			        			t1 = (tr + ti * rr) / d;
			        			t2 = (ti - tr * rr) / d;
	
			        			switch (isw) {
			        			case 1: 
			        				stateC = StateC.L787;
			        				break;
			        			case 2:
			        				stateC = StateC.L782;
			        				break;
			        			}
			        			stateC = StateC.L777;
			        			break;

	        				case L777:
		        				rr = dr / di;
			        			d = dr * rr + di;
			        			t1 = (tr * rr + ti) / d;
			        			t2 = (ti * rr - tr) / d;
			        			switch (isw)
			        			{
			        			case 1: 
			        				stateC = StateC.L787;
			        				break;
			        			case 2: 
			        				stateC = StateC.L782;
			        				break;
			        			}
			        			stateC = StateC.L780;
			        			break;
			        			
	        				case L780:
	        					// Complex 2-by-2 block 
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
			        			stateC = StateC.L775;
			        			break;
			        			
	        				case L782:
		        				b[i + 1][na] = t1;
			        			b[i + 1][en] = t2;
			        			isw = 1;
			        			if (Math.abs(y) > Math.abs(w) + Math.abs(w1)) {
			        				stateC = StateC.L785;
			        				break;
			        			}
			        			tr = -ra - x * b[(i + 1)][na] + x1 * b[(i + 1)][en];
			        			ti = -sa - x * b[(i + 1)][en] - x1 * b[(i + 1)][na];
			        			stateC = StateC.L773;
			        			break;
			        			
	        				case L785:
		        				t1 = (-r - zz * b[(i + 1)][na] + z1 * b[(i + 1)][en]) / y;
			        			t2 = (-s - zz * b[(i + 1)][en] - z1 * b[(i + 1)][na]) / y;
			        			stateC = StateC.L787;
			        			break;

	        				case L787:
			        			b[i][na] = t1;
			        			b[i][en] = t2;
			        			stateC = StateC.L790;
			        			break;
							
							case L790:
								stateC = StateC.Final;
								break;

							case Final:
								break LoopC;
			        			
	        				}	// end switch (stateC)
	        			}	// end of while (stateC
	        			
	        		}	// end of LoopC: for (ii ..
	        		
					break;
					// End complex vector
					
				case L795:
					isw = 3 - isw;
					stateA = StateA.L800;
					break;
					
				case L800:
					stateA = StateA.Final;
					break;
				case Final:
					break LoopA;
        		
        		}	// end switch (stateA)
        		
        	}	// end while
        }	// end LoopA, for (nn ...
 
        // End back substitution. Transform to original coordinate system.
        for (jj = 0; jj < n; ++jj) {
            j = n - jj - 1;

            for (i = 0; i < n; ++i)
            {
                zz = 0.0;
                for (k = 0; k <= j; ++k)
                    zz += z[i][k] * b[k][j];
                z[i][j] = zz;
            }
        } 	// end for (jj ...

        // Normalize so that modulus of largest component of each vector is 1.
        // (isw is 1 initially from before)
        LoopD: for (j = 0; j < n; ++j) {
        	StateD stateD = StateD.Initial;
        	while (stateD != StateD.Final) {
				switch (stateD) {
				case Initial :
					d = 0.0;
		            if (isw == 2) {
		            	stateD = StateD.L920;
		            	break;
		            }
		            if (alfi[j] != 0.0) {
		            	stateD = StateD.L945;
		            	break;
		            }

		            for (i = 0; i < n; ++i) {
		                if ((Math.abs(z[i][j])) > d)
		                    d = (Math.abs(z[i][j]));
		            }

		            for (i = 0; i < n; ++i) {
		                z[i][j] /= d;
		            }
		            
		            stateD = StateD.L950;
					break;
					
				case L920:
		            for (i = 0; i < n; ++i) {
		                r = Math.abs(z[i][j - 1]) + Math.abs(z[i][j]);
		                if (r != 0.0) {
		                    // Computing 2nd power
		                    double u1 = z[i][j - 1] / r;
		                    double u2 = z[i][j] / r;
		                    r *= Math.sqrt(u1 * u1 + u2 * u2);
		                }
		                if (r > d)
		                    d = r;
		            }

		            for (i = 0; i < n; ++i) {
		                z[i][j - 1] /= d;
		                z[i][j] /= d;
		            }
		            
		            stateD = StateD.L945;
					break;
					
				case L945:
		            isw = 3 - isw;
		            stateD = StateD.L950;
					break;
					
				case L950:
					stateD = StateD.Final;
					break;
					
				case Final:
					break LoopD;
		            
				}	// end switch (stateD)
        	}	// end while (stateD ...
 
        }	// end LoopD: for (j..

        return 0;
    }	// end of qzvec()

}

package imagingbook.common.math.eigen.accord;

public abstract class QZHES {
	
	// EISPACK Routines, see http://www.netlib.org/eispack/

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
		
//		System.out.println("runnin qzhes");
		int i, j, k, l, nm1, nm2;
		double r, s, t;
		int l1;
		double u1, u2, v1, v2;
		int lb, nk1;
		double rho;
	
		if (matz) {
			// If we are interested in computing the
			// eigenvectors, set Z to identity(n,n)
			for (j = 0; j < n; j++) {
				for (i = 0; i < n; i++)
					z[i][j] = 0.0;
				z[j][j] = 1.0;
			}
		}
	
		// reduce b to upper triangular form
		if (n <= 1) {
			return 0;
		}
		
		nm1 = n - 1;
		for (l = 0; l < nm1; l++) {
			l1 = l + 1;
			s = 0.0;
	
			for (i = l1; i < n; i++)
				s = s + (Math.abs(b[i][l]));
	
			if (s == 0.0) {
				continue;
			}
			s = s + Math.abs(b[l][l]);
			r = 0.0;
	
			for (i = l; i < n; ++i) {
				// Computing 2nd power
				b[i][l] = b[i][l] / s;
				r = r + b[i][l] * b[i][l];
			}
	
			r = Special.Sign(Math.sqrt(r), b[l][l]);
			b[l][l] = b[l][l] + r;
			rho = r * b[l][l];
	
			for (j = l1; j < n; j++) {
				t = 0.0;
				for (i = l; i < n; i++)
					t += b[i][l] * b[i][j];
				t = -t / rho;
				for (i = l; i < n; i++) {
					b[i][j] = b[i][j] + t * b[i][l];
				}
			}
	
			for (j = 0; j < n; j++) {
				t = 0.0;
				for (i = l; i < n; i++) {
					t += b[i][l] * a[i][j];
				}
				t = -t / rho;
				for (i = l; i < n; i++) {
					a[i][j] = a[i][j] + t * b[i][l];
				}
			}
	
			b[l][l] = -s * r;
			for (i = l1; i < n; i++) {
				b[i][l] = 0.0;
			}
		}
	
		// reduce a to upper Hessenberg form, while keeping b triangular
		if (n == 2) {
			return 0;
		}
		nm2 = n - 2;
		for (k = 0; k < nm2; k++) {
			nk1 = nm1 - k - 1; // = n - 2 - k;
	
			// for l=n-1 step -1 until k+1 do
			for (lb = 0; lb < nk1; lb++) {
				l = n - lb - 2;
				l1 = l + 1;
	
				// zero a(l+1,k)
				s = (Math.abs(a[l][k])) + (Math.abs(a[l1][k]));
	
				if (s == 0.0) {
					continue;
				}
				
				u1 = a[l][k] / s;
				u2 = a[l1][k] / s;
//				r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1); // Special.Sign
				r = Math.copySign(Math.hypot(u1, u2), u1); 
				v1 = -(u1 + r) / r;
				v2 = -u2 / r;
				u2 = v2 / v1;
	
				for (j = k; j < n; j++) {
					t = a[l][j] + u2 * a[l1][j];
					a[l][j] = a[l][j] + t * v1;
					a[l1][j] = a[l1][j] + t * v2;
				}
	
				a[l1][k] = 0.0;
	
				for (j = l; j < n; j++) {
					t = b[l][j] + u2 * b[l1][j];
					b[l][j] = b[l][j] + t * v1;
					b[l1][j] = b[l1][j] + t * v2;
				}
	
				// Zero b(l+1,l)
				s = (Math.abs(b[l1][l1])) + (Math.abs(b[l1][l]));
				if (s == 0.0) {
					continue;
				}
				u1 = b[l1][l1] / s;
				u2 = b[l1][l] / s;
//				r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1);
				r = Math.copySign(Math.hypot(u1, u2), u1);
				v1 = -(u1 + r) / r;
				v2 = -u2 / r;
				u2 = v2 / v1;
	
				for (i = 0; i <= l1; i++) {
					t = b[i][l1] + u2 * b[i][l];
					b[i][l1] = b[i][l1] + t * v1;
					b[i][l] = b[i][l] + t * v2;
				}
	
				b[l1][l] = 0.0;
	
				for (i = 0; i < n; i++) {
					t = a[i][l1] + u2 * a[i][l];
					a[i][l1] = a[i][l1] + t * v1;
					a[i][l] = a[i][l] + t * v2;
				}
	
				if (matz) {
					for (i = 0; i < n; i++) {
						t = z[i][l1] + u2 * z[i][l];
						z[i][l1] = z[i][l1] + t * v1;
						z[i][l] = z[i][l] + t * v2;
					}
				}
			}
		}
	
//		System.out.println("done qzhes");
		return 0;
	} // end of qzhes

}

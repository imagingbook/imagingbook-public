package imagingbook.common.math.eigen;

/**
 * Ported from https://github.com/accord-net/framework/blob/development/Sources/Accord.Math/Decompositions/GeneralizedEigenvalueDecomposition.cs
 * @author WB
 *
 */
public class GeneralzedEigenSolverAccord {
	
	
	// EISPACK Routines (starting in line 305)
	
	
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
	
	private static int qzhes(int n, double[][] a, double[][] b, boolean matz, double[][] z)
    {
        int i, j, k, l;
        double r, s, t;
        int l1;
        double u1, u2, v1, v2;
        int lb, nk1;
        double rho;


        if (matz)
        {
            // If we are interested in computing the
            //  eigenvectors, set Z to identity(n,n)
            for (j = 0; j < n; ++j)
            {
                for (i = 0; i < n; ++i)
                    z[i][j] = 0.0;
                z[j][j] = 1.0;
            }
        }

        // Reduce b to upper triangular form
        if (n <= 1) return 0;
        for (l = 0; l < n - 1; ++l)
        {
            l1 = l + 1;
            s = 0.0;

            for (i = l1; i < n; ++i)
                s += (Math.abs(b[i][l]));

            if (s == 0.0) continue;
            s += (Math.abs(b[l][l]));
            r = 0.0;

            for (i = l; i < n; ++i)
            {
                // Computing 2nd power
                b[i][l] /= s;
                r += b[i][l] * b[i][l];
            }

            r = Special.Sign(Math.sqrt(r), b[l][l]);
            b[l][l] += r;
            rho = r * b[l][l];

            for (j = l1; j < n; ++j)
            {
                t = 0.0;
                for (i = l; i < n; ++i)
                    t += b[i][l] * b[i][j];
                t = -t / rho;
                for (i = l; i < n; ++i)
                    b[i][j] += t * b[i][l];
            }

            for (j = 0; j < n; ++j)
            {
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
        if (n == 2) return 0;
        for (k = 0; k < n - 2; ++k)
        {
            nk1 = n - 2 - k;

            // for l=n-1 step -1 until k+1 do
            for (lb = 0; lb < nk1; ++lb)
            {
                l = n - lb - 2;
                l1 = l + 1;

                // Zero a(l+1,k)
                s = (Math.abs(a[l][k])) + (Math.abs(a[l1][k]));

                if (s == 0.0) continue;
                u1 = a[l][k] / s;
                u2 = a[l1][k] / s;
                r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1);	// Special.Sign
                v1 = -(u1 + r) / r;
                v2 = -u2 / r;
                u2 = v2 / v1;

                for (j = k; j < n; ++j)
                {
                    t = a[l][j] + u2 * a[l1][j];
                    a[l][j] += t * v1;
                    a[l1][j] += t * v2;
                }

                a[l1][k] = 0.0;

                for (j = l; j < n; ++j)
                {
                    t = b[l][j] + u2 * b[l1][j];
                    b[l][j] += t * v1;
                    b[l1][j] += t * v2;
                }

                // Zero b(l+1,l)
                s = (Math.abs(b[l1][l1])) + (Math.abs(b[l1][l]));

                if (s == 0.0) continue;
                u1 = b[l1][l1] / s;
                u2 = b[l1][l] / s;
                r = Special.Sign(Math.sqrt(u1 * u1 + u2 * u2), u1);
                v1 = -(u1 + r) / r;
                v2 = -u2 / r;
                u2 = v2 / v1;

                for (i = 0; i <= l1; ++i)
                {
                    t = b[i][l1] + u2 * b[i][l];
                    b[i][l1] += t * v1;
                    b[i][l] += t * v2;
                }

                b[l1][l] = 0.0;

                for (i = 0; i < n; ++i)
                {
                    t = a[i][l1] + u2 * a[i][l];
                    a[i][l1] += t * v1;
                    a[i][l] += t * v2;
                }

                if (matz)
                {
                    for (i = 0; i < n; ++i)
                    {
                        t = z[i][l1] + u2 * z[i][l];
                        z[i][l1] += t * v1;
                        z[i][l] += t * v2;
                    }
                }
            }
        }

        return 0;
    }
	
/* Can't do, since no goto's allowed in Java!

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
    private static int qzit(int n, double[][] a, double[][] b, double eps1, bool matz, double[][] z, int ierr)	// wilbur: was ref int ierr
    {

        int i, j, k, l = 0;
        double r, s, t, a1, a2, a3 = 0;
        int k1, k2, l1, ll;
        double u1, u2, u3;
        double v1, v2, v3;
        double a11, a12, a21, a22, a33, a34, a43, a44;
        double b11, b12, b22, b33, b34, b44;
        int na, en, ld;
        double ep;
        double sh = 0;
        int km1, lm1 = 0;
        double ani, bni;
        int ish, itn, its, enm2, lor1;
        double epsa, epsb, anorm = 0, bnorm = 0;
        int enorn;
        boolean notlas;


        ierr = 0;

//        #region Compute epsa and epsb
        for (i = 0; i < n; ++i)
        {
            ani = 0.0;
            bni = 0.0;

            if (i != 0)
                ani = (Math.Abs(a[i][(i - 1)]));

            for (j = i; j < n; ++j)
            {
                ani += Math.Abs(a[i][j]);
                bni += Math.Abs(b[i][j]);
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
//        #endregion


        // Reduce a to quasi-triangular form, while keeping b triangular
        lor1 = 0;
        enorn = n;
        en = n - 1;
        itn = n * 30;

    // Begin QZ step
    L60:
        if (en <= 1) goto L1001;
        if (!matz) enorn = en + 1;

        its = 0;
        na = en - 1;
        enm2 = na;

    L70:
        ish = 2;
        // Check for convergence or reducibility.
        for (ll = 0; ll <= en; ++ll)
        {
            lm1 = en - ll - 1;
            l = lm1 + 1;

            if (l + 1 == 1)
                goto L95;

            if ((Math.Abs(a[l][lm1])) <= epsa)
                break;
        }

    L90:
        a[l][lm1] = 0.0;
        if (l < na) goto L95;

        // 1-by-1 or 2-by-2 block isolated
        en = lm1;
        goto L60;

    // Check for small top of b 
    L95:
        ld = l;

    L100:
        l1 = l + 1;
        b11 = b[l][l];

        if (Math.Abs(b11) > epsb) goto L120;

        b[l][l] = 0.0;
        s = (Math.Abs(a[l][l]) + Math.Abs(a[l1][l]));
        u1 = a[l][l] / s;
        u2 = a[l1][l] / s;
        r = Special.Sign(Math.Sqrt(u1 * u1 + u2 * u2), u1);
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
        goto L90;

    L120:
        a11 = a[l][l] / b11;
        a21 = a[l1][l] / b11;
        if (ish == 1) goto L140;

        // Iteration strategy
        if (itn == 0) goto L1000;
        if (its == 10) goto L155;

        // Determine type of shift
        b22 = b[l1][l1];
        if (Math.Abs(b22) < epsb) b22 = epsb;
        b33 = b[na][na];
        if (Math.Abs(b33) < epsb) b33 = epsb;
        b44 = b[en][en];
        if (Math.Abs(b44) < epsb) b44 = epsb;
        a33 = a[na][na] / b33;
        a34 = a[na][en] / b44;
        a43 = a[en][na] / b33;
        a44 = a[en][en] / b44;
        b34 = b[na][en] / b44;
        t = (a43 * b34 - a33 - a44) * .5;
        r = t * t + a34 * a43 - a33 * a44;
        if (r < 0.0) goto L150;

        // Determine single shift zero-th column of a
        ish = 1;
        r = Math.Sqrt(r);
        sh = -t + r;
        s = -t - r;
        if (Math.Abs(s - a44) < Math.Abs(sh - a44))
            sh = s;

        // Look for two consecutive small sub-diagonal elements of a.
        for (ll = ld; ll + 1 <= enm2; ++ll)
        {
            l = enm2 + ld - ll - 1;

            if (l == ld)
                goto L140;

            lm1 = l - 1;
            l1 = l + 1;
            t = a[l + 1][l + 1];

            if (Math.Abs(b[l][l]) > epsb)
                t -= sh * b[l][l];

            if (Math.Abs(a[l][lm1]) <= (Math.Abs(t / a[l1][l])) * epsa)
                goto L100;
        }

    L140:
        a1 = a11 - sh;
        a2 = a21;
        if (l != ld)
            a[l][lm1] = -a[l][lm1];
        goto L160;

    // Determine double shift zero-th column of a
    L150:
        a12 = a[l][l1] / b22;
        a22 = a[l1][l1] / b22;
        b12 = b[l][l1] / b22;
        a1 = ((a33 - a11) * (a44 - a11) - a34 * a43 + a43 * b34 * a11) / a21 + a12 - a11 * b12;
        a2 = a22 - a11 - a21 * b12 - (a33 - a11) - (a44 - a11) + a43 * b34;
        a3 = a[l1 + 1][l1] / b22;
        goto L160;

    // Ad hoc shift
    L155:
        a1 = 0.0;
        a2 = 1.0;
        a3 = 1.1605;

    L160:
        ++its;
        --itn;

        if (!matz) lor1 = ld;

        // Main loop
        for (k = l; k <= na; ++k)
        {
            notlas = k != na && ish == 2;
            k1 = k + 1;
            k2 = k + 2;

            km1 = Math.Max(k, l + 1) - 1; // Computing MAX
            ll = Math.Min(en, k1 + ish);  // Computing MIN

            if (notlas) goto L190;

            // Zero a(k+1,k-1)
            if (k == l) goto L170;
            a1 = a[k][km1];
            a2 = a[k1][km1];

        L170:
            s = Math.Abs(a1) + Math.Abs(a2);
            if (s == 0.0) goto L70;
            u1 = a1 / s;
            u2 = a2 / s;
            r = Special.Sign(Math.Sqrt(u1 * u1 + u2 * u2), u1);
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

            if (k != l)
                a[k1][km1] = 0.0;
            goto L240;

            // Zero a(k+1,k-1) and a(k+2,k-1)
        L190:
            if (k == l) goto L200;
            a1 = a[k][km1];
            a2 = a[k1][km1];
            a3 = a[k2][km1];

        L200:
            s = Math.Abs(a1) + Math.Abs(a2) + Math.Abs(a3);
            if (s == 0.0) goto L260;
            u1 = a1 / s;
            u2 = a2 / s;
            u3 = a3 / s;
            r = Special.Sign(Math.Sqrt(u1 * u1 + u2 * u2 + u3 * u3), u1);
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

            if (k == l) goto L220;
            a[k1][km1] = 0.0;
            a[k2][km1] = 0.0;

        // Zero b(k+2,k+1) and b(k+2,k)
        L220:
            s = (Math.Abs(b[k2][k2])) + (Math.Abs(b[k2][k1])) + (Math.Abs(b[k2][k]));
            if (s == 0.0) goto L240;
            u1 = b[k2][k2] / s;
            u2 = b[k2][k1] / s;
            u3 = b[k2][k] / s;
            r = Special.Sign(Math.Sqrt(u1 * u1 + u2 * u2 + u3 * u3), u1);
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

        // Zero b(k+1,k)
        L240:
            s = (Math.Abs(b[k1][k1])) + (Math.Abs(b[k1][k]));
            if (s == 0.0) goto L260;
            u1 = b[k1][k1] / s;
            u2 = b[k1][k] / s;
            r = Special.Sign(Math.Sqrt(u1 * u1 + u2 * u2), u1);
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

        L260:
            ;
        }

        goto L70; // End QZ step

    // Set error -- all eigenvalues have not converged after 30*n iterations
    L1000:
        ierr = en + 1;

    // Save epsb for use by qzval and qzvec
    L1001:
        if (n > 1)
            b[n - 1][0] = epsb;
        return 0;
    }

*/
	
	
}

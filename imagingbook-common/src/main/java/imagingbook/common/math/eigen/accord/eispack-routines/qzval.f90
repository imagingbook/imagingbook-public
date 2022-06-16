subroutine qzval ( n, a, b, alfr, alfi, beta, matz, z )

!*****************************************************************************80
!
!! QZVAL computes eigenvalues for a generalized eigenvalue problem.
!
!  Discussion:
!
!    This subroutine is the third step of the QZ algorithm
!    for solving generalized matrix eigenvalue problems.
!
!    This subroutine accepts a pair of real matrices, one of them
!    in quasi-triangular form and the other in upper triangular form.
!    It reduces the quasi-triangular matrix further, so that any
!    remaining 2-by-2 blocks correspond to pairs of complex
!    eigenvalues, and returns quantities whose ratios give the
!    generalized eigenvalues.  It is usually preceded by QZHES
!    and QZIT and may be followed by QZVEC.
!
!  Licensing:
!
!    This code is distributed under the GNU LGPL license.
!
!  Modified:
!
!    18 October 2009
!
!  Author:
!
!    Original FORTRAN77 version by Smith, Boyle, Dongarra, Garbow, Ikebe,
!    Klema, Moler.
!    FORTRAN90 version by John Burkardt.
!
!  Reference:
!
!    James Wilkinson, Christian Reinsch,
!    Handbook for Automatic Computation,
!    Volume II, Linear Algebra, Part 2,
!    Springer, 1971,
!    ISBN: 0387054146,
!    LC: QA251.W67.
!
!    Brian Smith, James Boyle, Jack Dongarra, Burton Garbow,
!    Yasuhiko Ikebe, Virginia Klema, Cleve Moler,
!    Matrix Eigensystem Routines, EISPACK Guide,
!    Lecture Notes in Computer Science, Volume 6,
!    Springer Verlag, 1976,
!    ISBN13: 978-3540075462,
!    LC: QA193.M37.
!
!  Parameters:
!
!    Input, integer ( kind = 4 ) N, the order of the matrices.
!
!    Input/output, real ( kind = 8 ) A(N,N).  On input, a real upper
!    quasi-triangular matrix.  On output, A has been reduced further to a
!    quasi-triangular matrix in which all nonzero subdiagonal elements
!    correspond to pairs of complex eigenvalues.
!
!    Input/output, real ( kind = 8 ) B(N,N).  On input, a real upper triangular 
!    matrix.  In addition, location B(n,1) contains the tolerance quantity EPSB
!    computed and saved in QZIT.  On output, B is still in upper triangular
!    form, although its elements have been altered.  B(N,1) is unaltered.
!
!    Output, real ( kind = 8 ) ALFR(N), ALFI(N), the real and imaginary parts of
!    the diagonal elements of the triangular matrix that would be obtained
!    if A were reduced completely to triangular form by unitary
!    transformations.  Non-zero values of ALFI occur in pairs, the first
!    member positive and the second negative.
!
!    Output, real ( kind = 8 ) BETA(N), the diagonal elements of the 
!    corresponding B, normalized to be real and non-negative.  The generalized 
!    eigenvalues are then the ratios (ALFR + I * ALFI) / BETA.
!
!    Input, logical MATZ, should be TRUE if the right hand transformations
!    are to be accumulated for later use in computing eigenvectors, and
!    to FALSE otherwise.
!
!    Input/output, real ( kind = 8 ) Z(N,N), is only used if MATZ is TRUE.
!    On input, the transformation matrix produced in the reductions by QZHES
!    and QZIT, if performed, or else the identity matrix.  On output,
!    the product of the right hand transformations for all three steps.
!
  implicit none

  integer ( kind = 4 ) n

  real ( kind = 8 ) a(n,n)
  real ( kind = 8 ) a1
  real ( kind = 8 ) a11
  real ( kind = 8 ) a11i
  real ( kind = 8 ) a11r
  real ( kind = 8 ) a12
  real ( kind = 8 ) a12i
  real ( kind = 8 ) a12r
  real ( kind = 8 ) a1i
  real ( kind = 8 ) a2
  real ( kind = 8 ) a21
  real ( kind = 8 ) a22
  real ( kind = 8 ) a22i
  real ( kind = 8 ) a22r
  real ( kind = 8 ) a2i
  real ( kind = 8 ) an
  real ( kind = 8 ) alfi(n)
  real ( kind = 8 ) alfr(n)
  real ( kind = 8 ) b(n,n)
  real ( kind = 8 ) b11
  real ( kind = 8 ) b12
  real ( kind = 8 ) b22
  real ( kind = 8 ) beta(n)
  real ( kind = 8 ) bn
  real ( kind = 8 ) c
  real ( kind = 8 ) cq
  real ( kind = 8 ) cz
  real ( kind = 8 ) d
  real ( kind = 8 ) di
  real ( kind = 8 ) dr
  real ( kind = 8 ) e
  real ( kind = 8 ) ei
  integer ( kind = 4 ) en
  real ( kind = 8 ) epsb
  integer ( kind = 4 ) i
  integer ( kind = 4 ) isw
  integer ( kind = 4 ) j
  logical matz
  integer ( kind = 4 ) na
  integer ( kind = 4 ) nn
  real ( kind = 8 ) r
  real ( kind = 8 ) s
  real ( kind = 8 ) sqi
  real ( kind = 8 ) sqr
  real ( kind = 8 ) ssi
  real ( kind = 8 ) ssr
  real ( kind = 8 ) szi
  real ( kind = 8 ) szr
  real ( kind = 8 ) t
  real ( kind = 8 ) ti
  real ( kind = 8 ) tr
  real ( kind = 8 ) u1
  real ( kind = 8 ) u2
  real ( kind = 8 ) v1
  real ( kind = 8 ) v2
  real ( kind = 8 ) z(n,n)

  epsb = b(n,1)
  isw = 1
!
!  Find eigenvalues of quasi-triangular matrices.
!
  do nn = 1, n

     en = n + 1 - nn
     na = en - 1

     if ( isw == 2 ) then
       go to 505
     end if

     if ( en == 1 ) then
       go to 410
     end if

     if ( a(en,na) /= 0.0D+00 ) then
       go to 420
     end if
!
!  1-by-1 block, one real root.
!
410  continue

     alfr(en) = a(en,en)
     if ( b(en,en) < 0.0D+00 ) then
       alfr(en) = -alfr(en)
     end if

     beta(en) = abs ( b(en,en) )
     alfi(en) = 0.0D+00
     go to 510
!
!  2-by-2 block.
!
420  continue

     if ( abs ( b(na,na) ) <= epsb ) then
       a1 = a(na,na)
       a2 = a(en,na)
       go to 460
     end if

     if ( abs ( b(en,en) ) <= epsb ) then
       a1 = a(en,en)
       a2 = a(en,na)
       bn = 0.0D+00
       go to 435
     end if

     an = abs ( a(na,na) ) + abs ( a(na,en) ) + abs ( a(en,na) ) &
       + abs ( a(en,en) )
     bn = abs ( b(na,na) ) + abs ( b(na,en) ) + abs ( b(en,en) )
     a11 = a(na,na) / an
     a12 = a(na,en) / an
     a21 = a(en,na) / an
     a22 = a(en,en) / an
     b11 = b(na,na) / bn
     b12 = b(na,en) / bn
     b22 = b(en,en) / bn
     e = a11 / b11
     ei = a22 / b22
     s = a21 / ( b11 * b22 )
     t = ( a22 - e * b22 ) / b22

     if ( abs ( e ) > abs ( ei ) ) then
       e = ei
       t = ( a11 - e * b11 ) / b11
     end if

     c = 0.5D+00 * ( t - s * b12 )
     d = c**2 + s * ( a12 - e * b12 )

     if ( d < 0.0D+00 ) then
       go to 480
     end if
!
!  Two real roots.
!  Zero both A(EN,NA) and B(EN,NA).
!
     e = e + ( c + sign ( sqrt ( d ), c ) )
     a11 = a11 - e * b11
     a12 = a12 - e * b12
     a22 = a22 - e * b22

     if ( abs ( a11 ) + abs ( a12 ) >= abs ( a21 ) + abs ( a22 ) ) then
       a1 = a12
       a2 = a11
     else
       a1 = a22
       a2 = a21
     end if
!
!  Choose and apply real Z.
!
435  continue

     s = abs ( a1 ) + abs ( a2 )
     u1 = a1 / s
     u2 = a2 / s
     r = sign ( sqrt ( u1**2 + u2**2 ), u1 )
     v1 = - ( u1 + r ) / r
     v2 = - u2 / r
     u2 = v2 / v1

     do i = 1, en
       t = a(i,en) + u2 * a(i,na)
       a(i,en) = a(i,en) + t * v1
       a(i,na) = a(i,na) + t * v2
       t = b(i,en) + u2 * b(i,na)
       b(i,en) = b(i,en) + t * v1
       b(i,na) = b(i,na) + t * v2
     end do

     if ( matz ) then

       do i = 1, n
         t = z(i,en) + u2 * z(i,na)
         z(i,en) = z(i,en) + t * v1
         z(i,na) = z(i,na) + t * v2
       end do

     end if

450  continue

     if ( bn == 0.0D+00 ) then
       go to 475
     end if

     if ( abs ( e ) * bn <= an ) then
       a1 = b(na,na)
       a2 = b(en,na)
     else
       a1 = a(na,na)
       a2 = a(en,na)
     end if
!
!  Choose and apply real Q.
!
460  continue

     s = abs ( a1 ) + abs ( a2 )
     if ( s == 0.0D+00 ) then
       go to 475
     end if

     u1 = a1 / s
     u2 = a2 / s
     r = sign ( sqrt ( u1**2 + u2**2 ), u1 )
     v1 = -(u1 + r) / r
     v2 = -u2 / r
     u2 = v2 / v1

     do j = na, n
       t = a(na,j) + u2 * a(en,j)
       a(na,j) = a(na,j) + t * v1
       a(en,j) = a(en,j) + t * v2
       t = b(na,j) + u2 * b(en,j)
       b(na,j) = b(na,j) + t * v1
       b(en,j) = b(en,j) + t * v2
     end do

475  continue

     a(en,na) = 0.0D+00
     b(en,na) = 0.0D+00
     alfr(na) = a(na,na)
     alfr(en) = a(en,en)
     if ( b(na,na) < 0.0D+00 ) then 
       alfr(na) = -alfr(na)
     end if

     if ( b(en,en) < 0.0D+00 ) then
       alfr(en) = -alfr(en)
     end if

     beta(na) = abs ( b(na,na) )
     beta(en) = abs ( b(en,en) )
     alfi(en) = 0.0D+00
     alfi(na) = 0.0D+00
     go to 505
!
!  Two complex roots.
!
480  continue

     e = e + c
     ei = sqrt ( -d )
     a11r = a11 - e * b11
     a11i = ei * b11
     a12r = a12 - e * b12
     a12i = ei * b12
     a22r = a22 - e * b22
     a22i = ei * b22

     if ( abs ( a11r ) + abs ( a11i ) + abs ( a12r ) + abs ( a12i ) >= &
            abs ( a21 ) + abs ( a22r ) + abs ( a22i ) ) then
       a1 = a12r
       a1i = a12i
       a2 = -a11r
       a2i = -a11i
     else
       a1 = a22r
       a1i = a22i
       a2 = -a21
       a2i = 0.0D+00
     end if
!
!  Choose complex Z.
!
     cz = sqrt ( a1**2 + a1i**2 )

     if ( cz /= 0.0D+00 ) then
       szr = ( a1 * a2 + a1i * a2i) / cz
       szi = ( a1 * a2i - a1i * a2) / cz
       r = sqrt ( cz**2 + szr**2 + szi**2 )
       cz = cz / r
       szr = szr / r
       szi = szi / r
     else
       szr = 1.0D+00
       szi = 0.0D+00
     end if

     if ( an >= ( abs ( e ) + ei ) * bn ) then
       a1 = cz * b11 + szr * b12
       a1i = szi * b12
       a2 = szr * b22
       a2i = szi * b22
     else
       a1 = cz * a11 + szr * a12
       a1i = szi * a12
       a2 = cz * a21 + szr * a22
       a2i = szi * a22
     end if
!
!  Choose complex Q.
!
     cq = sqrt ( a1**2 + a1i**2 )

     if ( cq /= 0.0D+00 ) then
       sqr = ( a1 * a2 + a1i * a2i ) / cq
       sqi = ( a1 * a2i - a1i * a2 ) / cq
       r = sqrt ( cq**2 + sqr**2 + sqi**2 )
       cq = cq / r
       sqr = sqr / r
       sqi = sqi / r
     else
       sqr = 1.0D+00
       sqi = 0.0D+00
     end if
!
!  Compute diagonal elements that would result if transformations were applied.
!
     ssr = sqr * szr + sqi * szi
     ssi = sqr * szi - sqi * szr
     i = 1
     tr = cq * cz * a11 + cq * szr * a12 + sqr * cz * a21 + ssr * a22
     ti = cq * szi * a12 - sqi * cz * a21 + ssi * a22
     dr = cq * cz * b11 + cq * szr * b12 + ssr * b22
     di = cq * szi * b12 + ssi * b22
     go to 503

502  continue

     i = 2
     tr = ssr * a11 - sqr * cz * a12 - cq * szr * a21 + cq * cz * a22
     ti = -ssi * a11 - sqi * cz * a12 + cq * szi * a21
     dr = ssr * b11 - sqr * cz * b12 + cq * cz * b22
     di = -ssi * b11 - sqi * cz * b12

503  continue

     t = ti * dr - tr * di

     if ( t < 0.0D+00 ) then
       j = en
     else
       j = na
     end if

     r = sqrt ( dr**2 + di**2 )
     beta(j) = bn * r
     alfr(j) = an * (tr * dr + ti * di) / r
     alfi(j) = an * t / r

     if ( i == 1 ) then
       go to 502
     end if

505  continue

     isw = 3 - isw

510  continue

  end do

  b(n,1) = epsb

  return
end
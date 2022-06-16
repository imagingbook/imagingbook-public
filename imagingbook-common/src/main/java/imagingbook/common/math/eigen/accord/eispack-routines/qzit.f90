subroutine qzit ( n, a, b, eps1, matz, z, ierr )

!*****************************************************************************80
!
!! QZIT carries out iterations to solve a generalized eigenvalue problem.
!
!  Discussion:
!
!    This subroutine is the second step of the QZ algorithm
!    for solving generalized matrix eigenvalue problems.
!
!    This subroutine accepts a pair of real matrices, one of them
!    in upper Hessenberg form and the other in upper triangular form.
!    It reduces the Hessenberg matrix to quasi-triangular form using
!    orthogonal transformations while maintaining the triangular form
!    of the other matrix.  It is usually preceded by QZHES and
!    followed by QZVAL and, possibly, QZVEC.
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
!    Input/output, real ( kind = 8 ) A(N,N).  On input, a real upper Hessenberg 
!    matrix.  On output, A has been reduced to quasi-triangular form.  The 
!    elements below the first subdiagonal are still zero and no two consecutive
!    subdiagonal elements are nonzero.
!
!    Input/output, real ( kind = 8 ) B(N,N).  On input, a real upper triangular 
!    matrix.  On output, B is still in upper triangular form, although its 
!    elements have been altered.  The location B(N,1) is used to store EPS1 
!    times the norm of B for later use by QZVAL and QZVEC.
!
!    Input, real ( kind = 8 ) EPS1, a tolerance used to determine negligible
!    elements.  EPS1 = 0.0 (or negative) may be input, in which case an element
!    will be neglected only if it is less than roundoff error times the
!    norm of its matrix.  If the input EPS1 is positive, then an element
!    will be considered negligible if it is less than EPS1 times the norm
!    of its matrix.  A positive value of EPS1 may result in faster execution,
!    but less accurate results.
!
!    Input, logical MATZ, should be TRUE if the right hand transformations
!    are to be accumulated for later use in computing eigenvectors.
!
!    Input/output, real ( kind = 8 ) Z(N,N).  If MATZ is FALSE, Z is not 
!    referenced.  Otherwise, on input, the transformation matrix produced in the
!    reduction by QZHES, if performed, or else the identity matrix.  On output, 
!    Z contains the product of the right hand transformations for both steps.
!
!    Output, integer ( kind = 4 ) IERR, error flag.
!    0, for normal return,
!    J, if the limit of 30*N iterations is exhausted while the J-th
!      eigenvalue is being sought.
!
  implicit none

  integer ( kind = 4 ) n

  real ( kind = 8 ) a(n,n)
  real ( kind = 8 ) a1
  real ( kind = 8 ) a11
  real ( kind = 8 ) a12
  real ( kind = 8 ) a2
  real ( kind = 8 ) a21
  real ( kind = 8 ) a22
  real ( kind = 8 ) a3
  real ( kind = 8 ) a33
  real ( kind = 8 ) a34
  real ( kind = 8 ) a43
  real ( kind = 8 ) a44
  real ( kind = 8 ) ani
  real ( kind = 8 ) anorm
  real ( kind = 8 ) b(n,n)
  real ( kind = 8 ) b11
  real ( kind = 8 ) b12
  real ( kind = 8 ) b22
  real ( kind = 8 ) b33
  real ( kind = 8 ) b34
  real ( kind = 8 ) b44
  real ( kind = 8 ) bni
  real ( kind = 8 ) bnorm
  integer ( kind = 4 ) en
  integer ( kind = 4 ) enm2
  integer ( kind = 4 ) enorn
  real ( kind = 8 ) ep
  real ( kind = 8 ) eps1
  real ( kind = 8 ) epsa
  real ( kind = 8 ) epsb
  integer ( kind = 4 ) i
  integer ( kind = 4 ) ierr
  integer ( kind = 4 ) ish
  integer ( kind = 4 ) itn
  integer ( kind = 4 ) its
  integer ( kind = 4 ) j
  integer ( kind = 4 ) k
  integer ( kind = 4 ) k1
  integer ( kind = 4 ) k2
  integer ( kind = 4 ) km1
  integer ( kind = 4 ) l
  integer ( kind = 4 ) l1
  integer ( kind = 4 ) ld
  integer ( kind = 4 ) ll
  integer ( kind = 4 ) lm1
  integer ( kind = 4 ) lor1
  logical matz
  integer ( kind = 4 ) na
  logical notlas
  real ( kind = 8 ) r
  real ( kind = 8 ) s
  real ( kind = 8 ) sh
  real ( kind = 8 ) t
  real ( kind = 8 ) u1
  real ( kind = 8 ) u2
  real ( kind = 8 ) u3
  real ( kind = 8 ) v1
  real ( kind = 8 ) v2
  real ( kind = 8 ) v3
  real ( kind = 8 ) z(n,n)

  ierr = 0
!
!  Compute EPSA and EPSB.
!
  anorm = 0.0D+00
  bnorm = 0.0D+00

  do i = 1, n

    if ( i == 1 ) then
      ani = 0.0D+00
    else
      ani = abs ( a(i,i-1) )
    end if

    bni = 0.0D+00

    do j = i, n
      ani = ani + abs ( a(i,j) )
      bni = bni + abs ( b(i,j) )
    end do

    anorm = max ( anorm, ani )
    bnorm = max ( bnorm, bni )

  end do

  if ( anorm == 0.0D+00 ) then
    anorm = 1.0D+00
  end if

  if ( bnorm == 0.0D+00 ) then
    bnorm = 1.0D+00
  end if

  ep = eps1

  if ( ep > 0.0D+00 ) then
    go to 50
  end if
!
!  Use roundoff level if EPS1 is 0.
!
  ep = epsilon ( ep )

50 continue

  epsa = ep * anorm
  epsb = ep * bnorm
!
!  Reduce A to quasi-triangular form, while keeping B triangular.
!
  lor1 = 1
  enorn = n
  en = n
  itn = 30 * n
!
!  Begin QZ step.
!
60 continue

  if ( en <= 2 ) then
    go to 1001
  end if

  if ( .not. matz ) then
    enorn = en
  end if

  its = 0
  na = en - 1
  enm2 = na - 1

70 continue

  ish = 2
!
!  Check for convergence or reducibility.
!
  do ll = 1, en
    lm1 = en - ll
    l = lm1 + 1
    if ( l == 1 ) then
      go to 95
    end if
    if ( abs ( a(l,lm1) ) <= epsa ) then
      exit
    end if
  end do

90 continue

  a(l,lm1) = 0.0D+00

  if ( l < na ) then
    go to 95
  end if
!
!  1-by-1 or 2-by-2 block isolated.
!
  en = lm1
  go to 60
!
!  Check for small top of B.
!
95 continue

  ld = l

100 continue

  l1 = l + 1
  b11 = b(l,l)

  if ( abs ( b11 ) > epsb ) then
    go to 120
  end if

  b(l,l) = 0.0D+00
  s = abs ( a(l,l) ) + abs ( a(l1,l) )
  u1 = a(l,l) / s
  u2 = a(l1,l) / s
  r = sign ( sqrt ( u1**2 + u2**2 ), u1 )
  v1 = - ( u1 + r ) / r
  v2 = -u2 / r
  u2 = v2 / v1

  do j = l, enorn
    t = a(l,j) + u2 * a(l1,j)
    a(l,j) = a(l,j) + t * v1
    a(l1,j) = a(l1,j) + t * v2
    t = b(l,j) + u2 * b(l1,j)
    b(l,j) = b(l,j) + t * v1
    b(l1,j) = b(l1,j) + t * v2
  end do

  if ( l /= 1 ) then
    a(l,lm1) = -a(l,lm1)
  end if
  lm1 = l
  l = l1
  go to 90

120 continue

  a11 = a(l,l) / b11
  a21 = a(l1,l) / b11
  if ( ish == 1 ) then
    go to 140
  end if
!
!  Iteration strategy.
!
  if ( itn == 0 ) then
    go to 1000
  end if

  if ( its == 10 ) then
    go to 155
  end if
!
!  Determine type of shift.
!
  b22 = b(l1,l1)
  if ( abs ( b22 ) < epsb ) then
    b22 = epsb
  end if

  b33 = b(na,na)
  if ( abs ( b33 ) < epsb ) then
    b33 = epsb
  end if

  b44 = b(en,en)
  if ( abs ( b44 ) < epsb ) then
    b44 = epsb
  end if

  a33 = a(na,na) / b33
  a34 = a(na,en) / b44
  a43 = a(en,na) / b33
  a44 = a(en,en) / b44
  b34 = b(na,en) / b44
  t = 0.5D+00 * (a43 * b34 - a33 - a44)
  r = t * t + a34 * a43 - a33 * a44

  if ( r < 0.0D+00 ) then
    go to 150
  end if
!
!  Determine single shift zeroth column of A.
!
  ish = 1
  r = sqrt ( r )
  sh = -t + r
  s = -t - r
  if ( abs ( s - a44 ) < abs ( sh - a44 ) ) then
    sh = s
  end if
!
!  Look for two consecutive small sub-diagonal elements of A.
!
  do ll = ld, enm2
    l = enm2 + ld - ll
    if ( l == ld ) then
      exit
    end if
    lm1 = l - 1
    l1 = l + 1
    t = a(l,l)

    if ( abs ( b(l,l) ) > epsb ) then
      t = t - sh * b(l,l)
    end if

    if ( abs ( a(l,lm1) ) <= abs ( t / a(l1,l) ) * epsa ) then
      go to 100
    end if

  end do

140 continue

  a1 = a11 - sh
  a2 = a21

  if ( l /= ld ) then
    a(l,lm1) = -a(l,lm1)
  end if

  go to 160
!
!  Determine double shift zeroth column of A.
!
150 continue

  a12 = a(l,l1) / b22
  a22 = a(l1,l1) / b22
  b12 = b(l,l1) / b22
  a1 = ( ( a33 - a11 ) * ( a44 - a11 ) - a34 * a43 + a43 * b34 * a11 ) &
    / a21 + a12 - a11 * b12
  a2 = (a22 - a11) - a21 * b12 - (a33 - a11) - (a44 - a11) + a43 * b34
  a3 = a(l1+1,l1) / b22
  go to 160
!
!  Ad hoc shift.
!
155 continue

  a1 = 0.0D+00
  a2 = 1.0D+00
  a3 = 1.1605D+00

  160 continue
  its = its + 1
  itn = itn - 1
  if ( .not. matz ) then
    lor1 = ld
  end if
!
!  Main loop.
!
  do k = l, na

     notlas = ( k /= na .and. ish == 2 )
     k1 = k + 1
     k2 = k + 2
     km1 = max ( k - 1, l )
     ll = min ( en, k1 + ish )

     if ( notlas ) then
       go to 190
     end if
!
!  Zero A(k+1,k-1).
!
     if ( k /= l ) then
       a1 = a(k,km1)
       a2 = a(k1,km1)
     end if

     s = abs ( a1 ) + abs ( a2 )

     if ( s == 0.0D+00 ) then
       go to 70
     end if

     u1 = a1 / s
     u2 = a2 / s
     r = sign ( sqrt ( u1**2 + u1**2 ), u1 )
     v1 = -( u1 + r ) / r
     v2 = -u2 / r
     u2 = v2 / v1

     do j = km1, enorn
       t = a(k,j) + u2 * a(k1,j)
       a(k,j) = a(k,j) + t * v1
       a(k1,j) = a(k1,j) + t * v2
       t = b(k,j) + u2 * b(k1,j)
       b(k,j) = b(k,j) + t * v1
       b(k1,j) = b(k1,j) + t * v2
     end do

     if ( k /= l ) then
       a(k1,km1) = 0.0D+00
     end if

     go to 240
!
!  Zero A(k+1,k-1) and A(k+2,k-1).
!
190  continue

     if ( k /= l ) then
       a1 = a(k,km1)
       a2 = a(k1,km1)
       a3 = a(k2,km1)
     end if

     s = abs ( a1 ) + abs ( a2 ) + abs ( a3 )

     if ( s == 0.0D+00 ) then
       go to 260
     end if

     u1 = a1 / s
     u2 = a2 / s
     u3 = a3 / s
     r = sign ( sqrt ( u1**2 + u2**2 + u3**2 ), u1 )
     v1 = -(u1 + r) / r
     v2 = -u2 / r
     v3 = -u3 / r
     u2 = v2 / v1
     u3 = v3 / v1

     do j = km1, enorn
       t = a(k,j) + u2 * a(k1,j) + u3 * a(k2,j)
       a(k,j) = a(k,j) + t * v1
       a(k1,j) = a(k1,j) + t * v2
       a(k2,j) = a(k2,j) + t * v3
       t = b(k,j) + u2 * b(k1,j) + u3 * b(k2,j)
       b(k,j) = b(k,j) + t * v1
       b(k1,j) = b(k1,j) + t * v2
       b(k2,j) = b(k2,j) + t * v3
     end do

     if ( k /= l ) then
       a(k1,km1) = 0.0D+00
       a(k2,km1) = 0.0D+00
     end if
!
!  Zero B(k+2,k+1) and B(k+2,k).
!
     s = abs ( b(k2,k2) ) + abs ( b(k2,k1) ) + abs ( b(k2,k) )
     if ( s == 0.0D+00 ) then
       go to 240
     end if

     u1 = b(k2,k2) / s
     u2 = b(k2,k1) / s
     u3 = b(k2,k) / s
     r = sign ( sqrt ( u1**2 + u2**2 + u3**2 ), u1 )
     v1 = -(u1 + r) / r
     v2 = -u2 / r
     v3 = -u3 / r
     u2 = v2 / v1
     u3 = v3 / v1

     do i = lor1, ll
       t = a(i,k2) + u2 * a(i,k1) + u3 * a(i,k)
       a(i,k2) = a(i,k2) + t * v1
       a(i,k1) = a(i,k1) + t * v2
       a(i,k) = a(i,k) + t * v3
       t = b(i,k2) + u2 * b(i,k1) + u3 * b(i,k)
       b(i,k2) = b(i,k2) + t * v1
       b(i,k1) = b(i,k1) + t * v2
       b(i,k) = b(i,k) + t * v3
     end do

     b(k2,k) = 0.0D+00
     b(k2,k1) = 0.0D+00

     if ( matz ) then

       do i = 1, n
         t = z(i,k2) + u2 * z(i,k1) + u3 * z(i,k)
         z(i,k2) = z(i,k2) + t * v1
         z(i,k1) = z(i,k1) + t * v2
         z(i,k) = z(i,k) + t * v3
       end do

     end if
!
!  Zero B(k+1,k).
!
240  continue

     s = abs ( b(k1,k1) ) + abs ( b(k1,k) )

     if ( s /= 0.0D+00 ) then

       u1 = b(k1,k1) / s
       u2 = b(k1,k) / s
       r = sign ( sqrt ( u1**2 + u2**2 ), u1 )
       v1 = -( u1 + r ) / r
       v2 = -u2 / r
       u2 = v2 / v1
  
       do i = lor1, ll
         t = a(i,k1) + u2 * a(i,k)
         a(i,k1) = a(i,k1) + t * v1
         a(i,k) = a(i,k) + t * v2
         t = b(i,k1) + u2 * b(i,k)
         b(i,k1) = b(i,k1) + t * v1
         b(i,k) = b(i,k) + t * v2
       end do

       b(k1,k) = 0.0D+00

       if ( matz ) then

         do i = 1, n
           t = z(i,k1) + u2 * z(i,k)
           z(i,k1) = z(i,k1) + t * v1
           z(i,k) = z(i,k) + t * v2
         end do

       end if

     end if

260  continue

  end do

  go to 70
!
!  Set error: not all eigenvalues have converged after 30*N iterations.
!
1000 continue

  ierr = en
!
!  Save EPSB for use by QZVAL and QZVEC.
!
 1001 continue

  if ( n > 1 ) then
    b(n,1) = epsb
  end if

  return
end
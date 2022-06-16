
subroutine qzhes ( n, a, b, matz, z )

!*****************************************************************************80
!
!! QZHES carries out transformations for a generalized eigenvalue problem.
!
!  Discussion:
!
!    This subroutine is the first step of the QZ algorithm
!    for solving generalized matrix eigenvalue problems.
!
!    This subroutine accepts a pair of real general matrices and
!    reduces one of them to upper Hessenberg form and the other
!    to upper triangular form using orthogonal transformations.
!    it is usually followed by QZIT, QZVAL and, possibly, QZVEC.
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
!    Input/output, real ( kind = 8 ) A(N,N).  On input, the first real general
!    matrix.  On output, A has been reduced to upper Hessenberg form.  The
!    elements below the first subdiagonal have been set to zero.
!
!    Input/output, real ( kind = 8 ) B(N,N).  On input, a real general matrix.
!    On output, B has been reduced to upper triangular form.  The elements
!    below the main diagonal have been set to zero.
!
!    Input, logical MATZ, should be TRUE if the right hand transformations
!    are to be accumulated for later use in computing eigenvectors.
!
!    Output, real ( kind = 8 ) Z(N,N), contains the product of the right hand
!    transformations if MATZ is TRUE.
!
  implicit none

  integer ( kind = 4 ) n

  real ( kind = 8 ) a(n,n)
  real ( kind = 8 ) b(n,n)
  integer ( kind = 4 ) i
  integer ( kind = 4 ) j
  integer ( kind = 4 ) k
  integer ( kind = 4 ) l
  integer ( kind = 4 ) l1
  integer ( kind = 4 ) lb
  logical matz
  integer ( kind = 4 ) nk1
  integer ( kind = 4 ) nm1
  real ( kind = 8 ) r
  real ( kind = 8 ) rho
  real ( kind = 8 ) s
  real ( kind = 8 ) t
  real ( kind = 8 ) u1
  real ( kind = 8 ) u2
  real ( kind = 8 ) v1
  real ( kind = 8 ) v2
  real ( kind = 8 ) z(n,n)
!
!  Set Z to the identity matrix.
!
  if ( matz ) then

    z(1:n,1:n) = 0.0D+00

    do i = 1, n
      z(i,i) = 1.0D+00
    end do

  end if
!
!  Reduce B to upper triangular form.
!
  if ( n <= 1 ) then
    return
  end if

  nm1 = n - 1

  do l = 1, n - 1

    l1 = l + 1

    s = sum ( abs ( b(l+1:n,l) ) )

    if ( s /= 0.0D+00 ) then

      s = s + abs ( b(l,l) )
      b(l:n,l) = b(l:n,l) / s

      r = sqrt ( sum ( b(l:n,l)**2 ) )
      r = sign ( r, b(l,l) )
      b(l,l) = b(l,l) + r
      rho = r * b(l,l)

      do j = l + 1, n

        t = dot_product ( b(l:n,l), b(l:n,j) )

        b(l:n,j) = b(l:n,j) - t * b(l:n,l) / rho

      end do

      do j = 1, n

        t = dot_product ( b(l:n,l), a(l:n,j) )

        a(l:n,j) = a(l:n,j) - t * b(l:n,l) / rho

      end do

      b(l,l) = - s * r
      b(l+1:n,l) = 0.0D+00

    end if

  end do
!
!  Reduce A to upper Hessenberg form, while keeping B triangular.
!
  if ( n == 2 ) then
    return
  end if

  do k = 1, n - 2

     nk1 = nm1 - k

     do lb = 1, nk1

        l = n - lb
        l1 = l + 1
!
!  Zero A(l+1,k).
!
        s = abs ( a(l,k) ) + abs ( a(l1,k) )

        if ( s /= 0.0D+00 ) then

        u1 = a(l,k) / s
        u2 = a(l1,k) / s
        r = sign ( sqrt ( u1**2 + u2**2 ), u1 )
        v1 = - ( u1 + r) / r
        v2 = - u2 / r
        u2 = v2 / v1

        do j = k, n
          t = a(l,j) + u2 * a(l1,j)
          a(l,j) = a(l,j) + t * v1
          a(l1,j) = a(l1,j) + t * v2
        end do

        a(l1,k) = 0.0D+00

        do j = l, n
          t = b(l,j) + u2 * b(l1,j)
          b(l,j) = b(l,j) + t * v1
          b(l1,j) = b(l1,j) + t * v2
        end do
!
!  Zero B(l+1,l).
!
        s = abs ( b(l1,l1) ) + abs ( b(l1,l) )

        if ( s /= 0.0 ) then

          u1 = b(l1,l1) / s
          u2 = b(l1,l) / s
          r = sign ( sqrt ( u1**2 + u2**2 ), u1 )
          v1 =  -( u1 + r ) / r
          v2 = -u2 / r
          u2 = v2 / v1

          do i = 1, l1
            t = b(i,l1) + u2 * b(i,l)
            b(i,l1) = b(i,l1) + t * v1
            b(i,l) = b(i,l) + t * v2
          end do

          b(l1,l) = 0.0D+00

          do i = 1, n
            t = a(i,l1) + u2 * a(i,l)
            a(i,l1) = a(i,l1) + t * v1
            a(i,l) = a(i,l) + t * v2
          end do

          if ( matz ) then

            do i = 1, n
              t = z(i,l1) + u2 * z(i,l)
              z(i,l1) = z(i,l1) + t * v1
              z(i,l) = z(i,l) + t * v2
            end do

          end if

        end if

      end if

    end do

  end do

  return
end
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
subroutine qzvec ( n, a, b, alfr, alfi, beta, z )

!*****************************************************************************80
!
!! QZVEC computes eigenvectors for a generalized eigenvalue problem.
!
!  Discussion:
!
!    This subroutine is the optional fourth step of the QZ algorithm
!    for solving generalized matrix eigenvalue problems.
!
!    This subroutine accepts a pair of real matrices, one of them in
!    quasi-triangular form (in which each 2-by-2 block corresponds to
!    a pair of complex eigenvalues) and the other in upper triangular
!    form.  It computes the eigenvectors of the triangular problem and
!    transforms the results back to the original coordinate system.
!    it is usually preceded by QZHES, QZIT, and QZVAL.
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
!    Input, real ( kind = 8 ) A(N,N), contains a real upper quasi-triangular 
!    matrix.  Its subdiagonal elements provide information about the storage of
!    the complex eigenvectors.
!
!    Input/output, real ( kind = 8 ) B(N,N).  On input, a real upper triangular
!    matrix.  In addition, location B(N,1) contains the tolerance quantity EPSB
!    computed and saved in QZIT.  On output, B has been destroyed.
!
!    Input, real ( kind = 8 ) ALFR(N), ALFI(N), BETA(N), vectors whose ratios
!      ( ALFR + I * ALFI ) / BETA
!    are the generalized eigenvalues.  They are usually obtained from QZVAL.
!
!    Input/output, real ( kind = 8 ) Z(N,N).  On input, the transformation
!    matrix produced in the reductions by QZHES, QZIT, and QZVAL, if performed.
!    If the eigenvectors of the triangular problem are desired, Z must contain
!    the identity matrix.  On output, Z contains the real and imaginary parts of
!    the eigenvectors:
!    If ALFI(I) == 0.0, the I-th eigenvalue is real and the I-th column of Z
!    contains its eigenvector.
!    If ALFI(I) > 0.0, the eigenvalue is the first of a complex pair and the
!    I-th and (I+1)-th columns of Z contain its eigenvector.
!    If ALFI(I) < 0.0, the eigenvalue is the second of a complex pair and the
!    (I-1)-th and I-th columns of Z contain the conjugate of its eigenvector.
!    Each eigenvector is normalized so that the modulus of its largest
!    component is 1.0D+00 .
!
  implicit none

  integer ( kind = 4 ) n

  real ( kind = 8 ) a(n,n)
  real ( kind = 8 ) alfi(n)
  real ( kind = 8 ) alfm
  real ( kind = 8 ) alfr(n)
  real ( kind = 8 ) almi
  real ( kind = 8 ) almr
  real ( kind = 8 ) b(n,n)
  real ( kind = 8 ) beta(n)
  real ( kind = 8 ) betm
  real ( kind = 8 ) d
  real ( kind = 8 ) di
  real ( kind = 8 ) dr
  integer ( kind = 4 ) en
  integer ( kind = 4 ) enm2
  real ( kind = 8 ) epsb
  integer ( kind = 4 ) i
  integer ( kind = 4 ) ii
  integer ( kind = 4 ) isw
  integer ( kind = 4 ) j
  integer ( kind = 4 ) jj
  integer ( kind = 4 ) k
  integer ( kind = 4 ) m
  integer ( kind = 4 ) na
  integer ( kind = 4 ) nn
  real ( kind = 8 ) q
  real ( kind = 8 ) r
  real ( kind = 8 ) ra
  real ( kind = 8 ) rr
  real ( kind = 8 ) s
  real ( kind = 8 ) sa
  real ( kind = 8 ) t
  real ( kind = 8 ) t1
  real ( kind = 8 ) t2
  real ( kind = 8 ) ti
  real ( kind = 8 ) tr
  real ( kind = 8 ) w
  real ( kind = 8 ) w1
  real ( kind = 8 ) x
  real ( kind = 8 ) x1
  real ( kind = 8 ) y
  real ( kind = 8 ) z(n,n)
  real ( kind = 8 ) z1
  real ( kind = 8 ) zz

  epsb = b(n,1)
  isw = 1

  do nn = 1, n

     en = n + 1 - nn
     na = en - 1

     if ( isw == 2 ) then
       go to 795
     end if

     if ( alfi(en) /= 0.0D+00 ) then
       go to 710
     end if
!
!  Real vector.
!
     m = en
     b(en,en) = 1.0D+00

     if ( na == 0 ) then
       go to 800
     end if

     alfm = alfr(m)
     betm = beta(m)

     do ii = 1, na

        i = en - ii
        w = betm * a(i,i) - alfm * b(i,i)
        r = 0.0D+00

        do j = m, en
          r = r + ( betm * a(i,j) - alfm * b(i,j) ) * b(j,en)
        end do

        if ( i == 1 .or. isw == 2 ) then
          go to 630
        end if

        if ( betm * a(i,i-1) == 0.0D+00 ) then
          go to 630
        end if

        zz = w
        s = r
        go to 690

630     continue

        m = i

        if ( isw == 2 ) then
          go to 640
        end if
!
!  Real 1-by-1 block.
!
        if ( w == 0.0D+00 ) then
          t = epsb
        else
          t = w
        end if

        b(i,en) = - r / t
        go to 700
!
!  Real 2-by-2 block.
!
640     continue

        x = betm * a(i,i+1) - alfm * b(i,i+1)
        y = betm * a(i+1,i)
        q = w * zz - x * y
        t = ( x * s - zz * r ) / q
        b(i,en) = t

        if ( abs ( x ) <= abs ( zz ) ) then
          go to 650
        end if

        b(i+1,en) = (-r - w * t) / x

        go to 690

650     continue

        b(i+1,en) = (-s - y * t) / zz

690     continue

        isw = 3 - isw

700     continue

     end do
!
!  End real vector.
!
     go to 800
!
!  Complex vector.
!
710  continue

     m = na
     almr = alfr(m)
     almi = alfi(m)
     betm = beta(m)
!
!  Last vector component chosen imaginary so eigenvector matrix is triangular.
!
     y = betm * a(en,na)
     b(na,na) = -almi * b(en,en) / y
     b(na,en) = ( almr * b(en,en) - betm * a(en,en) ) / y
     b(en,na) = 0.0D+00
     b(en,en) = 1.0D+00
     enm2 = na - 1

     do ii = 1, enm2

        i = na - ii
        w = betm * a(i,i) - almr * b(i,i)
        w1 = -almi * b(i,i)
        ra = 0.0D+00
        sa = 0.0D+00

        do j = m, en
          x = betm * a(i,j) - almr * b(i,j)
          x1 = -almi * b(i,j)
          ra = ra + x * b(j,na) - x1 * b(j,en)
          sa = sa + x * b(j,en) + x1 * b(j,na)
        end do

        if ( i == 1 .or. isw == 2 ) then
          go to 770
        end if

        if ( betm * a(i,i-1) == 0.0D+00 ) then
          go to 770
        end if

        zz = w
        z1 = w1
        r = ra
        s = sa
        isw = 2
        go to 790
770     continue

        m = i
        if ( isw == 2 ) then
          go to 780
        end if
!
!  Complex 1-by-1 block.
!
        tr = -ra
        ti = -sa

773     continue

        dr = w
        di = w1
!
!  Complex divide (t1,t2) = (tr,ti) / (dr,di),
!
775     continue

        if ( abs ( di ) > abs ( dr ) ) then
          go to 777
        end if

        rr = di / dr
        d = dr + di * rr
        t1 = (tr + ti * rr) / d
        t2 = (ti - tr * rr) / d
        go to ( 787, 782 ), isw

777     continue

        rr = dr / di
        d = dr * rr + di
        t1 = ( tr * rr + ti ) / d
        t2 = ( ti * rr - tr ) / d
        go to ( 787, 782 ), isw
!
!  Complex 2-by-2 block.
!
780     continue

        x = betm * a(i,i+1) - almr * b(i,i+1)
        x1 = -almi * b(i,i+1)
        y = betm * a(i+1,i)
        tr = y * ra - w * r + w1 * s
        ti = y * sa - w * s - w1 * r
        dr = w * zz - w1 * z1 - x * y
        di = w * z1 + w1 * zz - x1 * y
        if ( dr == 0.0D+00 .and. di == 0.0D+00 ) then
          dr = epsb
        end if
        go to 775

782     continue

        b(i+1,na) = t1
        b(i+1,en) = t2
        isw = 1

        if ( abs ( y ) > abs ( w ) + abs ( w1 ) ) then
          go to 785
        end if

        tr = -ra - x * b(i+1,na) + x1 * b(i+1,en)
        ti = -sa - x * b(i+1,en) - x1 * b(i+1,na)
        go to 773

785     continue

        t1 = (-r - zz * b(i+1,na) + z1 * b(i+1,en) ) / y
        t2 = (-s - zz * b(i+1,en) - z1 * b(i+1,na) ) / y

787     continue

        b(i,na) = t1
        b(i,en) = t2

790     continue

     end do
!
!  End complex vector.
!
795   continue

      isw = 3 - isw

800   continue

  end do
!
!  End back substitution.
!  Transform to original coordinate system.
!
  do jj = 1, n

     j = n + 1 - jj

     do i = 1, n

        zz = 0.0D+00

        do k = 1, j
          zz = zz + z(i,k) * b(k,j)
        end do

        z(i,j) = zz

      end do

  end do
!
!  Normalize so that modulus of largest component of each vector is 1.
!  (ISW is 1 initially from before).
!
  do j = 1, n

     d = 0.0D+00
     if ( isw == 2 ) then
       go to 920
     end if

     if ( alfi(j) /= 0.0D+00 ) then
       go to 945
     end if

     do i = 1, n
       d = max ( d, abs ( z(i,j) ) )
     end do

     z(1:n,j) = z(1:n,j) / d

     go to 950

920  continue

     do i = 1, n
       r = abs ( z(i,j-1) ) + abs ( z(i,j) )
       if ( r /= 0.0D+00 ) then
         r = r * sqrt ( ( z(i,j-1) / r )**2 + ( z(i,j) / r )**2 )
       end if
       d = max ( d, r )
     end do

     z(1:n,j-1) = z(1:n,j-1) / d
     z(1:n,j) = z(1:n,j) / d

945  continue

     isw = 3 - isw

950  continue

  end do

  return
end

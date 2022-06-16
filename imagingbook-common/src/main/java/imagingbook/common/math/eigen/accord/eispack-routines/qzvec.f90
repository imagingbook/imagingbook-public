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

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
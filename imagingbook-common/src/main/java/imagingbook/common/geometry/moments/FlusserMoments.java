package imagingbook.common.geometry.moments;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.math.Complex;

/**
 * <p>
 * Naive implementation of Flusser's complex invariant moments [1]. See Sec. 8.6.5 (Eq. 8.51 - 8.54) of [2] for
 * additional details. This abstract class defines static methods only.
 * </p>
 * <p>
 * [1] J. Flusser, B. Zitova, and T. Suk. "Moments and Moment Invariants in Pattern Recognition". John Wiley and Sons
 * (2009).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/28
 */
public class FlusserMoments {

    private final Iterable<Pnt2d> points;
    private final int n;
    private final double xc, yc;

    public FlusserMoments(Iterable<Pnt2d> points) {
        this.points = points;
        double sx = 0;
        double sy = 0;
        int i = 0;
        for (Pnt2d p : points) {
            sx = sx + p.getX();
            sy = sy + p.getY();
            i++;
        }
        this.n = i;
        if (n == 0) {
            throw new IllegalArgumentException("at least one point is required");
        }
        this.xc = sx / n;
        this.yc = sy / n;
    }
    /**
     * Returns the scale-normalized complex moment of order (p,q) for the 2D point set associated with
     * this {@link FlusserMoments} instance.
     *
     * @param p order index p
     * @param q order index q
     * @return the complex moment of order (p,q)
     */
    public Complex getComplexMoment(int p, int q) {
        Complex sum = new Complex(0, 0);
        for (Pnt2d pnt : points) {
            double x = pnt.getX() - xc;
            double y = pnt.getY() - yc;
            Complex zp = (x == 0.0 && y == 0 && p == 0) ? 			// beware: 0^0 is undefined!
                    Complex.ZERO : new Complex(x, y).pow(p);
            Complex zq = (x == 0.0 && y == 0 && q == 0) ?
                    Complex.ZERO : new Complex(x, -y).pow(q);
            sum = sum.add(zp.multiply(zq));
        }
        checkForNaN(sum);
        return sum;
    }

    /**
     * Returns the scale-normalized complex moment of order (p,q) for the specified set of 2D points.
     *
     * @param p order index p
     * @param q order index q
     * @return the complex moment of order (p,q)
     */
    public Complex getScaleNormalizedMoment(int p, int q) {
        Complex cpq = getComplexMoment(p, q);
        return cpq.multiply(1.0 / Math.pow(n, 0.5 * (p + q) + 1));
    }

    /**
     * Calculates and returns a vector of 11 invariant moments for the specified set of 2D points.
     *
     * @return the vector of invariant moments
     */
    public double[] getInvariantMoments() {
        Complex c11 = getScaleNormalizedMoment(1, 1);
        Complex c12 = getScaleNormalizedMoment(1, 2);
        Complex c21 = getScaleNormalizedMoment(2, 1);
        Complex c20 = getScaleNormalizedMoment(2, 0);
        Complex c22 = getScaleNormalizedMoment(2, 2);
        Complex c30 = getScaleNormalizedMoment(3, 0);
        Complex c31 = getScaleNormalizedMoment(3, 1);
        Complex c40 = getScaleNormalizedMoment(4, 0);
        double p1 = c11.getRe();
        double p2 = c21.multiply(c12).getRe();
        double p3 = c20.multiply(c12.pow(2)).getRe();
        double p4 = c20.multiply(c12.pow(2)).getIm();
        double p5 = c30.multiply(c12.pow(3)).getRe();
        double p6 = c30.multiply(c12.pow(3)).getIm();
        double p7 = c22.getRe();
        double p8 = c31.multiply(c12.pow(2)).getRe();
        double p9 = c31.multiply(c12.pow(2)).getIm();
        double p10 = c40.multiply(c12.pow(4)).getRe();
        double p11 = c40.multiply(c12.pow(4)).getIm();
        return new double[] {p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11};
    }

    private void checkForNaN(Complex z) {
        if (z.isNaN()) {
            throw new RuntimeException("NaN encountered in complex quantity");
        }
    }



}

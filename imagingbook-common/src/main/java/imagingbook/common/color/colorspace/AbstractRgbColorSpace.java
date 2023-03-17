/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.colorspace;

import imagingbook.common.color.cie.CieUtils;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

import java.awt.color.ColorSpace;

/**
 * A color space specified by its RGB primaries and white point coordinates, all defined
 * in two-dimensional CIE xy-space. The associated 3D XYZ coordinates are calculated from
 * the 2D coordinates. This guarantees that all associated XYZ coordinates are consistent.
 * Note that there is a substantial variation in published XYZ data. This setup relies
 * on xy color coordinates only, and consistent XYZ coordinates are obtained by solving
 * a system of linear equations to recover the unknown Yr, Yg, and Yb coordinates.
 *
 */
public abstract class AbstractRgbColorSpace extends ColorSpace implements RgbReferenceData {

    // private final double xr, yr, xg, yg, xb, yb;        // xy coordinates of RGB primaries
    // private final double xw, yw;                        // xy coordinates of white point
    // private final double[] XYZr, XYZg, XYZb, XYZw;      // XYZ coordinates of primaries and white point
    private final double[] XYZw;      // XYZ coordinates of white point
    protected final double[][] Mrgb, Mrgbi;

    /**
     * Constructor, builds a color space from CIE xy-coordinates.
     * @param xr x-coordinate of primary R
     * @param yr y-coordinate of primary R
     * @param xg x-coordinate of primary G
     * @param yg y-coordinate of primary G
     * @param xb x-coordinate of primary B
     * @param yb y-coordinate of primary B
     * @param xw x-coordinate of white point
     * @param yw y-coordinate of white point
     */
    public AbstractRgbColorSpace(double xr, double yr, double xg, double yg, double xb, double yb, double xw, double yw) {
        super(ColorSpace.TYPE_RGB, 3);
        // this.xr = xr;
        // this.yr = yr;
        //
        // this.xg = xg;
        // this.yg = yg;
        //
        // this.xb = xb;
        // this.yb = yb;
        //
        // this.xw = xw;
        // this.yw = yw;

        this.XYZw = CieUtils.xyYToXYZ(xw, yw, 1);

        double[][] M =
               {{xr / yr, xg / yg, xb / yb},
                {1, 1, 1},
                {(1 - xr - yr) / yr, (1 - xg - yg) / yg, (1 - xb - yb) / yb }};
        double[] YYY = Matrix.solve(M, XYZw);   // = {Yr, Yg, Yb}
        PrintPrecision.set(9);
        // System.out.println("YYY = " + Matrix.toString(YYY));

        // RGB primaries in XYZ
        double[] XYZr = CieUtils.xyYToXYZ(xr, yr, YYY[0]);
        double[] XYZg = CieUtils.xyYToXYZ(xg, yg, YYY[1]);
        double[] XYZb = CieUtils.xyYToXYZ(xb, yb, YYY[2]);

        this.Mrgbi = Matrix.transpose(new double[][] {XYZr, XYZg, XYZb});
        this.Mrgb  = Matrix.inverse(Mrgbi);

    }

    @Override
    public float[] getWhitePoint() {
        return Matrix.toFloat(XYZw);
    }

    @Override
    public float[] getPrimary(int idx) {
        return Matrix.toFloat(Matrix.getColumn(Mrgbi, idx));
    }

    /**
     * Returns the 3x3 matrix for converting from CIE-XYZ color space to linear RGB.
     * Its column vectors are the XYZ coordinates of the RGB primaries.
     * @return a 3x3 matrix
     * @see #getMrgb()
     */
    public double[][] getMrgbi() {
        return this.Mrgbi;
    }

    /**
     * Returns the 3x3 matrix for converting from linear RGB to CIE-XYZ color space.
     * @return a 3x3 matrix
     * @see #getMrgbi()
     */
    public double[][] getMrgb() {
        return this.Mrgb;
    }

    private static final String[] ComponentNames = {"R", "G", "B"};

    @Override
    public String getName (int idx) {
        return ComponentNames[idx];
    }

    // --------------------------------------------------

    private static class  TestColorSpace extends AbstractRgbColorSpace {

        TestColorSpace() {
            super(0.64,0.33,0.30,0.60,0.15,0.06,0.3127,0.3290);
        }
        @Override
        public float[] toRGB(float[] colorvalue) {
            return new float[0];
        }

        @Override
        public float[] fromRGB(float[] rgbvalue) {
            return new float[0];
        }

        @Override
        public float[] toCIEXYZ(float[] colorvalue) {
            return new float[0];
        }

        @Override
        public float[] fromCIEXYZ(float[] colorvalue) {
            return new float[0];
        }
    }

    public static void main(String[] args) {
        double xr = 0.64, yr = 0.33;        // sRGB primaries
        double xg = 0.30, yg = 0.60;
        double xb = 0.15, yb = 0.06;
        double x65 = 0.3127, y65 = 0.3290;  // D65 white point
        // AbstractRgbColorSpace cs = new AbstractRgbColorSpace(xr, yr, xg, yg, xb, yb, x65, y65);
        AbstractRgbColorSpace cs = new TestColorSpace();

        PrintPrecision.set(9);
        System.out.println("XYZw = " + Matrix.toString(cs.getWhitePoint()));

        System.out.println("XYZr = " + Matrix.toString(cs.getPrimary(0)));
        System.out.println("XYZg = " + Matrix.toString(cs.getPrimary(1)));
        System.out.println("XYZb = " + Matrix.toString(cs.getPrimary(2)));

        System.out.println("Mrgbi = \n" + Matrix.toString(cs.getMrgbi()));
        System.out.println("Mrgb  = \n" + Matrix.toString(cs.getMrgb()));

    }
}

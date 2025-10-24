/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.colorspace;

import static imagingbook.common.color.cie.StandardIlluminant.D50;

import java.awt.color.ColorSpace;

import imagingbook.common.color.adapt.BradfordAdaptation;
import imagingbook.common.color.adapt.ChromaticAdaptation;
import imagingbook.common.color.cie.CieUtils;
import imagingbook.common.math.Matrix;
//import imagingbook.common.math.PrintPrecision;

/**
 * A color space specified by its RGB primaries and white point coordinates, all defined
 * in two-dimensional CIE xy-space. The associated 3D XYZ coordinates are calculated from
 * the 2D coordinates. This guarantees that all associated XYZ coordinates are consistent.
 * Note that there is a substantial variation in published XYZ data. This setup relies
 * on xy color coordinates only, and consistent XYZ coordinates are obtained by solving
 * a system of linear equations to recover the unknown Yr, Yg, and Yb coordinates.
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractRgbColorSpace extends ColorSpace {

	private static final ColorSpace CS_sRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);

    private final double[] XYZw;      // XYZ coordinates of white point
    /** The 3x3 matrix for converting (normalized) RGB to XYZ coordinates. */
    protected final double[][] Mrgb;
    /** The 3x3 matrix for converting XYZ to (normalized) RGB coordinates. Inverse of {@link #Mrgb}. */
    protected final double[][] Mrgbi;
    /** Chromatic adaptation instance for converting colors from this color space's white point to D50. */
    protected final ChromaticAdaptation catWtoD50; // = BradfordAdaptation.getInstance(D65, D50);
    /** Chromatic adaptation instance for converting colors from D50 to this color space's white point. */
    protected final ChromaticAdaptation catD50toW; // = BradfordAdaptation.getInstance(D50, D65);

    /**
     * Constructor, builds a {@link AbstractRgbColorSpace} from CIE xy-coordinates of tristimulus values and white point.
     * @param xr x-coordinate of primary R
     * @param yr y-coordinate of primary R
     * @param xg x-coordinate of primary G
     * @param yg y-coordinate of primary G
     * @param xb x-coordinate of primary B
     * @param yb y-coordinate of primary B
     * @param xw x-coordinate of white point
     * @param yw y-coordinate of white point
     */
    protected AbstractRgbColorSpace(double xr, double yr, double xg, double yg, double xb, double yb, double xw, double yw) {
        super(ColorSpace.TYPE_RGB, 3);
        this.XYZw = CieUtils.xyYToXYZ(xw, yw, 1);

        this.catWtoD50 = BradfordAdaptation.getInstance(XYZw, D50.getXYZ());
        this.catD50toW = BradfordAdaptation.getInstance(D50.getXYZ(), XYZw);

        double[][] M =
               {{xr / yr, xg / yg, xb / yb},
                {1, 1, 1},
                {(1 - xr - yr) / yr, (1 - xg - yg) / yg, (1 - xb - yb) / yb }};
        double[] YYY = Matrix.solve(M, XYZw);   // determine {Yr, Yg, Yb}

        // RGB primaries in XYZ
        double[] XYZr = CieUtils.xyYToXYZ(xr, yr, YYY[0]);
        double[] XYZg = CieUtils.xyYToXYZ(xg, yg, YYY[1]);
        double[] XYZb = CieUtils.xyYToXYZ(xb, yb, YYY[2]);

        this.Mrgbi = Matrix.transpose(new double[][] {XYZr, XYZg, XYZb});
        this.Mrgb  = Matrix.inverse(Mrgbi);
    }

    /**
	 * Returns the XYZ coordinates of the white point associated with this color space (typically D65 or D50).
	 *
	 * @return the white point
	 */
    public float[] getWhitePoint() {
        return Matrix.toFloat(XYZw);
    }

    /**
	 * Returns the XYZ color coordinates for the primary color with the specified index, measured relative to the white
	 * point of this color space (see {@link #getWhitePoint()}).
	 *
	 * @param idx the color index (R = 0, G = 1, B = 2)
	 * @return the XYZ coordinate for the primary color
	 */
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

    // -----------------------------------------------------------

    @Override   // convert from sRGB to a color in this space (via XYZ50)
    public float[] fromRGB(float[] srgb) {
        float[] xyz50 = CS_sRGB.fromRGB(srgb);
        return this.fromCIEXYZ(xyz50);
    }

    @Override   // convert from a color in this space to sRGB (via XYZ50)
    public float[] toRGB(float[] rgbTHIS) {
        float[] xyz50 = this.toCIEXYZ(rgbTHIS);
        return CS_sRGB.toRGB(xyz50);
    }

    // --------------------------------------------------------

    private static final String[] ComponentNames = {"R", "G", "B"};

    @Override
    public String getName (int idx) {
        return ComponentNames[idx];
    }

    // --------------------------------------------------

//    private static class  TestColorSpace extends AbstractRgbColorSpace {
//
//        TestColorSpace() {
//            super(0.64, 0.33, 0.30, 0.60, 0.15, 0.06, 0.3127, 0.3290);
//        }
//
//        @Override
//        public float[] toCIEXYZ(float[] colorvalue) {
//            return new float[0];
//        }
//
//        @Override
//        public float[] fromCIEXYZ(float[] colorvalue) {
//            return new float[0];
//        }
//    }
//
//    public static void main(String[] args) {
//        double xr = 0.64, yr = 0.33;        // sRGB primaries
//        double xg = 0.30, yg = 0.60;
//        double xb = 0.15, yb = 0.06;
//        double x65 = 0.3127, y65 = 0.3290;  // D65 white point
//        // AbstractRgbColorSpace cs = new AbstractRgbColorSpace(xr, yr, xg, yg, xb, yb, x65, y65);
//        AbstractRgbColorSpace cs = new TestColorSpace();
//
//        PrintPrecision.set(9);
//        System.out.println("XYZw = " + Matrix.toString(cs.getWhitePoint()));
//
//        System.out.println("XYZr = " + Matrix.toString(cs.getPrimary(0)));
//        System.out.println("XYZg = " + Matrix.toString(cs.getPrimary(1)));
//        System.out.println("XYZb = " + Matrix.toString(cs.getPrimary(2)));
//
//        System.out.println("Mrgbi = \n" + Matrix.toString(cs.getMrgbi()));
//        System.out.println("Mrgb  = \n" + Matrix.toString(cs.getMrgb()));
//
//    }
}

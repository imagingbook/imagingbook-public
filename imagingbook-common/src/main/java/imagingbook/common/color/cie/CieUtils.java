/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.color.cie;

import imagingbook.common.math.Arithmetic;

/**
 * <p>
 * Defines static methods for converting between CIE-XYZ coordinates (3D) and xy chromaticity values (2D). See Sec.
 * 14.1.2 of [1] for details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public abstract class CieUtils {

    private CieUtils() {
    }

    /**
     * Calculates the XYZ coordinates for a given point (x,y) in the CIE xy-color diagram. XYZ is located on the 3D
     * plane X + Y + Z = 1. Returns (0,0,0) for y = 0;
     *
     * @param x x-coordinate (in the 2D xy-diagram)
     * @param y y-coordinate (in the 2D xy-diagram)
     * @return the associated XYZ coordinate
     */
    public static double[] xyToXYZ(double x, double y) {
        return xyYToXYZ(x, y, 1.0);
    }

    /**
     * Float version of {@link #xyToXYZ(double, double)}.
     *
     * @param x x-coordinate (in the 2D xy-diagram)
     * @param y y-coordinate (in the 2D xy-diagram)
     * @return the associated XYZ coordinate
     */
    public static float[] xyToXYZ(float x, float y) {
        return xyYToXYZ(x, y, 1.0f);
    }

    /**
     * Calculates the XYZ coordinates for a given point (x,y) in the CIE xy-color diagram, with Y explicitly specified.
     *
     * @param x x-coordinate (in the 2D xy-diagram)
     * @param y y-coordinate (in the 2D xy-diagram)
     * @param Y the Y-coordinate (in 3D color space)
     * @return the associated XYZ coordinate
     */
    public static double[] xyYToXYZ(double x, double y, double Y) {
        if (Arithmetic.isZero(y)) {
            return new double[]{0, 0, 0};
        } else {
            double X = x * Y / y;
            double Z = (1 - x - y) * Y / y;
            return new double[]{X, Y, Z};
        }
    }

    /**
     * Float version of {@link #xyYToXYZ(double, double, double)}.
     *
     * @param x x-coordinate (in the 2D xy-diagram)
     * @param y y-coordinate (in the 2D xy-diagram)
     * @param Y the Y-coordinate (in 3D color space)
     * @return the associated XYZ coordinate
     */
    public static float[] xyYToXYZ(float x, float y, float Y) {
        if (Arithmetic.isZero(y)) {
            return new float[]{0, 0, 0};
        } else {
            float X = x * Y / y;
            float Z = (1 - x - y) * Y / y;
            return new float[]{X, Y, Z};
        }
    }

    /**
     * Calculates the 2D (x,y) color diagram coordinates for 3D XYZ color coordinates (X,Y,Z).
     *
     * @param XYZ the XYZ coordinate (3D)
     * @return the xy-coordinate (2D)
     */
    public static double[] XYZToxy(double[] XYZ) {
        double X = XYZ[0];
        double Y = XYZ[1];
        double Z = XYZ[2];
        double mag = X + Y + Z;
        return (Arithmetic.isZero(mag)) ?
                new double[]{0, 0} : new double[]{X / mag, Y / mag};
    }

    /**
     * Calculates the 3D (x,y,z) color diagram coordinates for 3D XYZ color coordinates (X,Y,Z).
     *
     * @param XYZ the XYZ coordinate (3D)
     * @return the xyz-coordinate (3D)
     */
    public static double[] XYZToxyz(double[] XYZ) {
        double X = XYZ[0];
        double Y = XYZ[1];
        double Z = XYZ[2];
        double mag = X + Y + Z;
        return (Arithmetic.isZero(mag)) ?
                new double[]{0, 0, 0} : new double[]{X / mag, Y / mag, Z / mag};
    }

    /**
     * Float version of {@link #XYZToxy(double[])}.
     *
     * @param XYZ the XYZ coordinate (3D)
     * @return the xy-coordinate (2D)
     */
    public static float[] XYZToxy(float[] XYZ) {
        double X = XYZ[0];
        double Y = XYZ[1];
        double Z = XYZ[2];
        double mag = X + Y + Z;
        return (Arithmetic.isZero(mag)) ?
                new float[]{0, 0} : new float[]{(float) (X / mag), (float) (Y / mag)};
    }

    /**
     * Float version of {@link #XYZToxyz(double[])}.
     *
     * @param XYZ the XYZ coordinate (3D)
     * @return the xyz-coordinate (§D)
     */
    public static float[] XYZToxyz(float[] XYZ) {
        double X = XYZ[0];
        double Y = XYZ[1];
        double Z = XYZ[2];
        double mag = X + Y + Z;
        return (Arithmetic.isZero(mag)) ?
                new float[]{0, 0, 0} : new float[]{(float) (X / mag), (float) (Y / mag), (float) (Z / mag)};
    }

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.color.colorspace;

import imagingbook.common.color.cie.NamedIccProfile;
import imagingbook.common.color.cie.StandardIlluminant;
import imagingbook.common.math.Matrix;
import imagingbook.common.math.PrintPrecision;

import java.awt.color.ICC_ColorSpace;


/**
 * <p>
 * This color space class is based on the "AdobeRGB1998.icc" profile [1]. It only serves as an example for creating color
 * spaces from ICC profiles. This color space uses D65 as its white point, primaries are the columns of matrix
 * <pre>
 *             | 0.57667, 0.18556, 0.18823 |
 *     Mrgbi = | 0.29734, 0.62736, 0.07529 |
 *             | 0.02703, 0.07069, 0.99134 |</pre>
 * See Sec. 14.5 of [2] for details.
 * </p>
 * <p>
 * [1] https://www.adobe.com/digitalimag/pdfs/AdobeRGB1998.pdf<br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2023/03/17
 * @see NamedIccProfile#AdobeRGB1998
 */
@SuppressWarnings("serial")
public class AdobeRgbColorSpace extends ICC_ColorSpace implements DirectD65Conversion, RgbReferenceData {

    private static AdobeRgbColorSpace instance = null;

    private AdobeRgbColorSpace() {
        super(NamedIccProfile.AdobeRGB1998.getProfile());    // constructor of ICC_ColorSpace
    }

    /**
     * Returns an instance of {@link AdobeRgbColorSpace}.
     * @return an instance of {@link AdobeRgbColorSpace}
     */
    public static AdobeRgbColorSpace getInstance() {
        if (instance == null) {
            instance = new AdobeRgbColorSpace();
        }
        return instance;
    }

    @Override
    public float[] getWhitePoint() {
        // (0.9505, 1.0000, 1.0891)
        float[] rgb = {1, 1, 1};
        return this.toCIEXYZ65(rgb);
        // return StandardIlluminant.D65.getXYZ();
    }

    @Override
    public float[] getPrimary(int idx) {
        float[] rgb = new float[3];
        rgb[idx] = 1;
        return this.toCIEXYZ65(rgb);
    }

    // public static void main(String[] args) {
    //     AdobeRgbColorSpace cs = AdobeRgbColorSpace.getInstance();
    //     PrintPrecision.set(5);
    //     for (int i = 0; i < 3; i++) {
    //         System.out.println("XYZ=" + Matrix.toString(cs.getPrimary(i)));
    //     }
    // }

}

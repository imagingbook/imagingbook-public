/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch05_Edges_Contours;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.Color;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * ImageJ plugin implementing a simple Sobel-type edge operator. This implementation uses built-in ImageJ functionality
 * only. The plugin displays the two derivative images ("Ix", "Iy"), edge magnitude and orientation ("E", "phi"), and
 * color-coded edge magnitude ("E colors", hue controlled by edge orientation). Edge magnitude is &ge; 0, orientation is
 * in [&minus;&pi;,+&pi;]. See Sec. 5.3.1 of [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 */
public class Sobel_Edges_Colored implements PlugInFilter, JavaDocHelp {

    /**
     * The 2D Sobel kernel for the derivative filter in x-direction.
     */
    public static final float[] sobelX = {
            -1, 0, 1,
            -2, 0, 2,
            -1, 0, 1};

    /**
     * The 2D Sobel kernel for the derivative filter in y-direction.
     */
    public static final float[] sobelY = {
            -1, -2, -1,
            0, 0, 0,
            1, 2, 1};

    /**
     * Constructor, asks to open a predefined sample image if no other image is currently open.
     */
    public Sobel_Edges_Colored() {
        if (noCurrentImage()) {
            DialogUtils.askForSampleImage(GeneralSampleImage.Boats);
        }
    }

    @Override
    public int setup(String arg, ImagePlus I) {
        return DOES_8G + PlugInFilter.NO_CHANGES;
    }

    @Override
    public void run(ImageProcessor ip) {
        FloatProcessor Ix = (FloatProcessor) ip.convertToFloat();
        FloatProcessor Iy = (FloatProcessor) Ix.duplicate();
        Ix.convolve(sobelX, 3, 3);
        Iy.convolve(sobelY, 3, 3);

        (new ImagePlus("Gradient Ix", Ix)).show();
        (new ImagePlus("Gradient Iy", Iy)).show();

        FloatProcessor Ix2 = (FloatProcessor) Ix.duplicate();
        Ix2.sqr();
        FloatProcessor Iy2 = (FloatProcessor) Iy.duplicate();
        Iy2.sqr();

        FloatProcessor E = (FloatProcessor) Ix2.duplicate();
        E.copyBits(Iy2, 0, 0, Blitter.ADD);        // E = Ix2 + Iy2
        E.sqrt();                                // E = sqrt(Ix2 + Iy2)

        FloatProcessor Phi = makeOrientation(Ix, Iy);

        ColorProcessor cp = makeColorEdges(E, Phi);

        (new ImagePlus("Edge Magnitude E", E)).show();
        (new ImagePlus("Edge Orientation Phi", Phi)).show();
        (new ImagePlus("Edge Magitide E (colored)", cp)).show();
    }

    FloatProcessor makeOrientation(FloatProcessor fpx, FloatProcessor fpy) {
        FloatProcessor orient = (FloatProcessor) fpx.duplicate();
        int M = fpx.getPixelCount();
        for (int i = 0; i < M; i++) {
            double dx = fpx.getf(i);
            double dy = fpy.getf(i);
            float phi = (float) Math.atan2(dy, dx);
            orient.setf(i, phi);
        }
        return orient;
    }

    ColorProcessor makeColorEdges(FloatProcessor mag, FloatProcessor ort) {
        // edge magnitude is all positive, orientation is in -pi ... +pi
        int w = mag.getWidth();
        int h = mag.getHeight();
        int M = mag.getPixelCount();
        mag.resetMinAndMax();
        ort.resetMinAndMax();
        double maxMag = mag.getMax();

        int[] colorPixels = new int[M];
        for (int i = 0; i < M; i++) {
            float m = mag.getf(i);
            float p = ort.getf(i);
            float hue = (float) (p / (2 * Math.PI)) + 1;
            float saturation = (float) (m / maxMag); // 1;
            float brightness = 1; //(float) (m / maxMag);
            colorPixels[i] = Color.HSBtoRGB(hue, saturation, brightness);
        }
        return new ColorProcessor(w, h, colorPixels);
    }


}

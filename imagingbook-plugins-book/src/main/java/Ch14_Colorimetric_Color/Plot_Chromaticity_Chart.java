/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch14_Colorimetric_Color;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.cie.CieUtils;
import imagingbook.common.color.cie.CieXyPlot;
import imagingbook.common.color.colorspace.sRgbColorSpace;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.Color;
import java.awt.Shape;
import java.awt.color.ColorSpace;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * ImageJ plugin, takes any RGB color image and plots its color distribution in CIE xy-space, together with the
 * outline of the xy ("horseshoe") chromaticity outline.
 */
public class Plot_Chromaticity_Chart implements PlugInFilter {

    private static int ImageSize = 512;

    /**
     * Constructor, asks to open a predefined sample image if no other image is currently open.
     */
    public Plot_Chromaticity_Chart() {
        if (noCurrentImage()) {
            DialogUtils.askForSampleImage(GeneralSampleImage.Flower);
        }
    }

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_RGB + NO_CHANGES;
    }

    @Override
    public void run(ImageProcessor ip) {
        ColorProcessor cp = (ColorProcessor) ip;
        ImagePlus imPlot =
                NewImage.createRGBImage("Cie Chromaticity (xy)", ImageSize, ImageSize, 1, NewImage.FILL_WHITE);
        ColorProcessor cpPlot = (ColorProcessor) imPlot.getProcessor();
        cpPlot.setColor(Color.lightGray);
        cpPlot.fill();

        ColorSpace cs = sRgbColorSpace.getInstance();

        int[] pixels = (int[]) cp.getPixels();

        int[] RGB = new int[3];
        for (int i = 0; i < pixels.length; i++) {
            int p = pixels[i];
            // from RGB, calculate XYZ and xy:
            RgbUtils.decodeIntToRgb(p, RGB);
            float[] srgb = RgbUtils.normalize(RGB);
            float[] XYZ = cs.toCIEXYZ(srgb);
            float[] xy = CieUtils.XYZToxy(XYZ);
            int xx = (int) Math.round(xy[0] * ImageSize);
            int yy = (int) Math.round((1 - xy[1]) * ImageSize);

            // place a brighness-normalized image pixel at the (xy) position:
            float[] XYZn = CieUtils.xyToXYZ(xy[0], xy[1]);
            float[] srgbn = cs.fromCIEXYZ(XYZn);
            int[] RGBn = RgbUtils.denormalize(srgbn);
            cpPlot.putPixel(xx, yy, RGBn);
        }

        ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
        Shape xyPlot = new CieXyPlot(ImageSize);
        ola.addShape(xyPlot, new ColoredStroke(0.35, Color.blue));

        imPlot.setOverlay(ola.getOverlay());
        imPlot.show();
    }
}

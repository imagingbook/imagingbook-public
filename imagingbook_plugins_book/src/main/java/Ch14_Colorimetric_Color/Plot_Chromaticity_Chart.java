/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch14_Colorimetric_Color;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.color.cie.CieUtils;
import imagingbook.common.color.cie.CieXyPlot;
import imagingbook.common.color.colorspace.sRGB65ColorSpace;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.Color;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.Path2D;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * ImageJ plugin, takes any RGB color image and plots its color distribution in CIE xy-space, together with the
 * outline of the xy ("horseshoe") chromaticity curve, sRGB gamut and white point.
 * Image colors are assumed to be in sRGB.
 */
public class Plot_Chromaticity_Chart implements PlugInFilter, JavaDocHelp {

    private static int ImageSize = 512;
    private static boolean ShowSrgbGamut = true;
    private static boolean MarkWhitePoint = true;

    private static final ColorSpace CS = sRGB65ColorSpace.getInstance();

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
        if (!runDialog()) {
            return;
        }

        ColorProcessor cp = (ColorProcessor) ip;
        ImagePlus imPlot =
                NewImage.createRGBImage("Cie Chromaticity (xy)", ImageSize, ImageSize, 1, NewImage.FILL_WHITE);
        ColorProcessor cpPlot = (ColorProcessor) imPlot.getProcessor();
        cpPlot.setColor(Color.lightGray);
        cpPlot.fill();

        int[] pixels = (int[]) cp.getPixels();

        int[] RGB = new int[3];
        for (int p : pixels) {
            // from RGB, calculate XYZ and xy:
            RgbUtils.decodeIntToRgb(p, RGB);
            float[] xy = getxy(RGB);
            int xx = Math.round(xy[0] * ImageSize);
            int yy = Math.round((1 - xy[1]) * ImageSize);

            // place a brighness-normalized image pixel at the (xy) position:
            float[] XYZn = CieUtils.xyToXYZ(xy[0], xy[1]);
            float[] srgbn = CS.fromCIEXYZ(XYZn);
            int[] RGBn = RgbUtils.denormalize(srgbn);
            cpPlot.putPixel(xx, yy, RGBn);
        }

        ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
        Shape xyPlot = new CieXyPlot(ImageSize);
        ola.addShape(xyPlot, new ColoredStroke(0.35, Color.blue));

        if (ShowSrgbGamut) {
            float[] xyR = getxy(new int[] {1, 0, 0});
            float[] xyG = getxy(new int[] {0, 1, 0});
            float[] xyB = getxy(new int[] {0, 0, 1});
            Path2D path = new Path2D.Float();
            path.moveTo(xyR[0] * ImageSize, (1 - xyR[1]) * ImageSize);
            path.lineTo(xyG[0] * ImageSize, (1 - xyG[1]) * ImageSize);
            path.lineTo(xyB[0] * ImageSize, (1 - xyB[1]) * ImageSize);
            path.closePath();
            ola.addShape(path, new ColoredStroke(0.35, Color.black));
        }

        if (MarkWhitePoint) {
            float[] xyW = getxy(new int[] {1, 1, 1});
            Pnt2d wp = Pnt2d.from(xyW[0] * ImageSize, (1 - xyW[1]) * ImageSize);
            ola.addShape(wp.getShape(), new ColoredStroke(0.35, Color.black));
        }

        imPlot.setOverlay(ola.getOverlay());
        imPlot.show();
    }

    private float[] getxy(int[] RGB) {
        float[] srgb = RgbUtils.normalize(RGB);
        float[] XYZ = CS.toCIEXYZ(srgb);
        return CieUtils.XYZToxy(XYZ);
    }

    private boolean runDialog() {
        GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
        gd.addHelp(this.getJavaDocUrl());
        gd.addCheckbox("Show sRGB gamut", ShowSrgbGamut);
        gd.addCheckbox("Mark D65 white point", MarkWhitePoint);

        gd.showDialog();
        if (gd.wasCanceled())
            return false;

        ShowSrgbGamut = gd.getNextBoolean();
        MarkWhitePoint = gd.getNextBoolean();
        return true;
    }
}

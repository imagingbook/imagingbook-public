/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package More_;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.image.access.ImageAccessor;
import imagingbook.core.jdoc.JavaDocHelp;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.GeneralSampleImage;

import java.awt.Color;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * This ImageJ plugin demonstrates the effect of different out-of-bounds strategies
 * for handling pixels that are outside the image. The input image is placed centered inside
 * a larger canvas and the missing pixels are inserted. The user can select one of the
 * predefined out-of-bounds strategies and the size factor (min. 1), which specifies how
 * much larger the output image is compared to the original image.
 * In the resulting composite image, the original image is outlined by a colored rectangle.
 * The use of {@link ImageAccessor} makes this plugin work with any type of input image.
 * If no image is currently open, the user is asked to load a predefined sample image.
 *
 * @author WB
 * @version 2023/04/02
 * @see OutOfBoundsStrategy
 * @see ImageAccessor
 */
public class OutOfBoundsStrategy_Demo implements PlugInFilter, JavaDocHelp {

    private static ImageResource SampleImage = GeneralSampleImage.MortarSmall;
    private static OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
    private static double SizeFactor = 5.0;
    private static Color OutlineColor = Color.green;

    private ImagePlus im = null;

    /**
     * Constructor, asks to open a predefined sample image if no other image is currently open.
     */
    public OutOfBoundsStrategy_Demo() {
        if (noCurrentImage()) {
            DialogUtils.askForSampleImage(SampleImage);
        }
    }

    @Override
    public int setup(String arg, ImagePlus im) {
        this.im = im;
        return DOES_ALL + NO_CHANGES;
    }

    @Override
    public void run(ImageProcessor ip1) {
        if (!runDialog()) {
            return;
        }

        int w1 = ip1.getWidth();
        int h1 = ip1.getHeight();

        int dw = (int) (0.5 * (SizeFactor - 1) * w1);
        int dh = (int) (0.5 * (SizeFactor - 1) * h1);

        dw = Math.max(dw, 0);
        dh = Math.max(dh, 0);

        int w2 = dw + w1 + dw;
        int h2 = dh + h1 + dh;

        ImageProcessor ip2 = ip1.createProcessor(w2, h2);

        ImageAccessor ia1 = ImageAccessor.create(ip1, OBS, null);
        ImageAccessor ia2 = ImageAccessor.create(ip2, null, null);

        for (int u = 0; u < w2; u++) {
            for (int v = 0; v < h2; v++) {
                float[] val = ia1.getPix(u - dw, v - dh);
                ia2.setPix(u, v, val);
            }
        }

        // mark the embedded original in the composite image
        Overlay oly = new Overlay();
        Roi rect = new Roi(dw, dh, w1, h1);
        rect.setStrokeWidth(1.0f);
        rect.setStrokeColor(OutlineColor);
        oly.add(rect);

        // show the composite image
        ImagePlus im2 = new ImagePlus("", ip2);
        im2.setOverlay(oly);
        ImagePlus im2f = im2.flatten();
        im2f.setTitle(im.getShortTitle() + " (" + OBS + ")");
        im2f.show();
    }

    private boolean runDialog() {
        GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
        gd.addHelp(getJavaDocUrl());

        gd.addEnumChoice("Out-of-bounds strategy", OBS);
        gd.addNumericField("Size factor (>1)", SizeFactor, 1);

        gd.showDialog();
        if(gd.wasCanceled())
            return false;

        OBS = gd.getNextEnumChoice(OutOfBoundsStrategy.class);
        SizeFactor = gd.getNextNumber();
        SizeFactor = Math.max(1, SizeFactor);

        return true;
    }
}

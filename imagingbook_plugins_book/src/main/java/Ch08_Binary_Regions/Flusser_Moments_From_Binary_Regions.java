/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch08_Binary_Regions;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.moments.FlusserMoments;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.BinaryRegionSegmentation;
import imagingbook.common.regions.Contour;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.sampleimages.kimia.KimiaCollage;

import java.awt.Color;
import java.awt.Font;
import java.util.Comparator;
import java.util.List;

import static imagingbook.common.ij.IjUtils.noCurrentImage;

/**
 * <p>
 * This ImageJ plugin calculates and lists the 11 scale and rotation invariant Flusser moments for the binary regions
 * contained in the given image. See Sec. 8.6.5 of [1] for additional details. The plugin expects a binary image (with 0
 * background and non-zero foreground). A sample image is automatically loaded if no other image is currently open.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/12/28
 */
public class Flusser_Moments_From_Binary_Regions implements PlugInFilter {

    private static int MIN_REGION_SIZE = 100;
    private static int MAX_REGION_COUNT = 30;
    private static boolean MARK_OUTER_CONTOURS = true;

    private static final Font MarkerFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    private static final Color MarkerColor = Color.blue;

    private ImagePlus im;

    /**
     * Constructor, asks to open a predefined sample image if no other image is currently open.
     */
    public Flusser_Moments_From_Binary_Regions() {
        if (noCurrentImage()) {
            DialogUtils.askForSampleImage(KimiaCollage.ShapeCollage1);  // Kimia216.bird02
        }
    }

    public int setup(String arg0, ImagePlus im) {
        this.im = im;
        return DOES_8G + DOES_8C;
    }

    public void run(ImageProcessor ip) {
        if (!runDialog())
            return;

        BinaryRegionSegmentation segmenter = new RegionContourSegmentation((ByteProcessor) ip);
        List<BinaryRegion> regions = segmenter.getRegions(true);
        if (regions.isEmpty()) {
            IJ.log("No regions found!");
            return;
        }

        IJ.log("Regions found: " + regions.size());
        regions.sort(Comparator.comparingDouble(r -> r.getCenter().getY()));    // sort regions by Y-coordinates

        ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
        ola.setStroke(new ColoredStroke(0.5, Color.green));
        ola.setFont(MarkerFont);
        ola.setTextColor(MarkerColor);
        int i = 0;
        for (BinaryRegion r : regions) {
            if (r.getSize() >= MIN_REGION_SIZE) {
                IJ.log("Region " + i + ", size = " + r.getSize());
                Contour oc = r.getOuterContour();
                ola.addShape(oc.getPolygonPath());
                Pnt2d c = r.getCenter();
                ola.addText(c.getX(), c.getY(), "R" + i);
                double[] moments = new FlusserMoments(r).getInvariantMoments();
                print(moments);
                i++;
                if (i > MAX_REGION_COUNT)
                    break;
            }
        }
        im.setOverlay(ola.getOverlay());
    }

    private void print(double[] moments) {
        for (int i = 0; i < moments.length; i++) {
            IJ.log("   p" + (i+1) + " = " + moments[i]);
        }
    }

    // ------------------------------------------------------

    private boolean runDialog() {
        GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
        if (im.isInvertedLut()) {
            gd.setInsets(0, 0, 0);
            gd.addMessage("NOTE: Image has inverted LUT (0 = white)!");
        }
        gd.addNumericField("Minimum region size (pixels)", MIN_REGION_SIZE);
        gd.addNumericField("Maximum region count", MAX_REGION_COUNT);
        gd.addCheckbox("Mark outer contours", MARK_OUTER_CONTOURS);

        gd.showDialog();
        if (gd.wasCanceled()) {
            return false;
        }

        MIN_REGION_SIZE = (int) gd.getNextNumber();
        MAX_REGION_COUNT = (int) gd.getNextNumber();
        MARK_OUTER_CONTOURS = gd.getNextBoolean();
        return true;
    }

}
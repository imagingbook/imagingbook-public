package Ch08_Binary_Regions;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.moments.FlusserMoments;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.BinaryRegionSegmentation;
import imagingbook.common.regions.Contour;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.sampleimages.kimia.Kimia216;

import java.awt.Color;
import java.util.List;
import java.util.Locale;

import static imagingbook.common.ij.IjUtils.noCurrentImage;


/**
 * This ImageJ plugin assumes a binary image (with 0 background) and calculates the 11 scale and rotation invariant
 * Flusser moments for the largest contained region. Optionally the moments are calculated from the region's contour
 * points only (ignoring all inner points).
 *
 * @author WB
 * @version 2022/12/28
 */
public class Flusser_Moments_Image implements PlugInFilter {

    private static int MIN_REGION_SIZE = 10;
    private static int MAX_REGION_COUNT = 5;
    private static boolean USE_CONTOURS_ONLY = false;
    private static boolean MARK_OUTER_CONTOURS = true;

    private ImagePlus im;

    /**
     * Constructor, asks to open a predefined sample image if no other image is currently open.
     */
    public Flusser_Moments_Image() {
        if (noCurrentImage()) {
            DialogUtils.askForSampleImage(Kimia216.bird02);
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
        ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
        int i = 0;
        for (BinaryRegion r : regions) {
            if (r.getSize() >= MIN_REGION_SIZE) {
                Contour c = r.getOuterContour();
                ola.setStroke(new ColoredStroke(0.5, Color.green));
                ola.addShape(c.getPolygonPath());
                double[] moments;
                if (USE_CONTOURS_ONLY) {
                    IJ.log("Contour " + i + ", length = " + c.getLength());
                    moments = new FlusserMoments(c).getInvariantMoments();
                }
                else {
                    IJ.log("Region " + i + ", size = " + r.getSize());
                    moments = new FlusserMoments(r).getInvariantMoments();
                }
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
            // IJ.log("p" + (i+1) + " = " + moments[i]);
            IJ.log(String.format(Locale.US, "   p%d = %9e", i + 1, moments[i]));
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
        gd.addCheckbox("Calculate moments from contour", USE_CONTOURS_ONLY);
        gd.addCheckbox("Mark outer contours", MARK_OUTER_CONTOURS);

        gd.showDialog();
        if (gd.wasCanceled()) {
            return false;
        }

        MIN_REGION_SIZE = (int) gd.getNextNumber();
        MAX_REGION_COUNT = (int) gd.getNextNumber();
        USE_CONTOURS_ONLY = gd.getNextBoolean();
        MARK_OUTER_CONTOURS = gd.getNextBoolean();
        return true;
    }

}
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
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import imagingbook.common.color.iterate.ColorSequencer;
import imagingbook.common.color.iterate.FiniteLinearColorSequencer;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.moments.FlusserMoments;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.math.MahalanobisDistance;
import imagingbook.common.math.Matrix;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.BinaryRegionSegmentation;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.common.util.GenericProperties.PropertyKey;
import imagingbook.common.util.tuples.Tuple2;
import imagingbook.core.plugin.JavaDocHelp;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.kimia.Kimia1070;
import imagingbook.sampleimages.kimia.KimiaCollage;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * This ImageJ plugin demonstrates the use of complex invariant Flusser moments for 2D shape matching. It opens a
 * special binary input image (collage of shapes), where the top row are the reference shapes. They are assigned
 * different colors. All other shapes are compared to the reference shapes and colored with the color of the most
 * similar reference shape. See Sec. 8.6.5 of [1] for additional details.
 * </p>
 * <p>
 * The plugin offers two options for finding the "closest" reference moment vector for a given shape:
 * <br>
 * (a) Mahalonobis distance (MD, see Eq. 8.57 and Sec. G.3 of [1]): The MD is calculated from the (pre-calculated)
 * covariance matrix of the moment vectors of the entire 'Kimia1070' dataset to obtain representative statistical shape
 * parameters. The MD compensates the large magnitude variation between the 11 moment space dimensions.
 * <br>
 * (b) Euclidean distance (ED): Here the distance between moment vectors is measured using the ED. The large-magnitude
 * moment elements obviously dominate this quantity. Typically only the 3-4 largest moments are actually used, all
 * others have no effect. Thus results with the ED are inferior to the ones obtained with the MD.
 * <br>
 * In both cases matching is performed linearly, i.e., a given moment vector is simply compared to all reference
 * vectors.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2023/01/02
 * @see Flusser_Moments_Covariance_Matrix
 */
public class Flusser_Moments_Matching_Demo implements PlugIn, JavaDocHelp {

    private static final ImageResource ir = KimiaCollage.ShapeCollage1;
    private static final int ReferenceBoundaryY = 130;    // everything above this position is a reference shape

    private static int MinRegionSize = 100;
    private static boolean UseMahalanobisDistance = true;
    private static double MaxMomentDistance = 1.0;

    private static final int FontSize = 20;
    private static final Color UnmatchedColor = Color.gray;
    private static final Color[] ReferenceShapeColors = {Color.yellow, Color.green, Color.cyan, Color.magenta, Color.pink};
    private static final Font MarkerFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    private static final Color MarkerColor = Color.white;

    private static final PropertyKey<double[]> MomentKey = new PropertyKey<>("mts");
    private static final PropertyKey<String> LabelKey = new PropertyKey<>("lbl");

    private MahalanobisDistance md;     // new MahalanobisDistance(cov);
    private double[][] U;               // md.getWhiteningTransformation();

    @Override
    public void run(String str) {
        ImagePlus im = ir.getImagePlus();
        im.show();
        if (!runDialog()) {
            return;
        }

        // segment the image into binary regions
        ByteProcessor ip = (ByteProcessor) im.getProcessor();
        BinaryRegionSegmentation segmenter = new RegionContourSegmentation((ByteProcessor) ip);
        List<BinaryRegion> regions = segmenter.getRegions(true);
        if (regions.isEmpty()) {
            IJ.log("No regions found!");
            return;
        }

        // calculate invariant moment for all regions
        // and split into reference/other shapes
        List<BinaryRegion> refShapes = new ArrayList<BinaryRegion>();
        List<BinaryRegion> othShapes = new ArrayList<BinaryRegion>();
        for (BinaryRegion r : regions) {
            if (r.getSize() > MinRegionSize) {
                // calculate invariant Flusser moments for region r
                double[] moments = new FlusserMoments(r).getInvariantMoments();
                r.setProperty(MomentKey, moments);
                if (r.getCenter().getY() < ReferenceBoundaryY)
                    refShapes.add(r);
                else
                    othShapes.add(r);
            }
        }

        // sort reference shapes by center x-coordinates and assign labels:
        refShapes.sort(Comparator.comparingDouble(r -> r.getCenter().getX()));
        {
            int k = 0;
            for (BinaryRegion r : refShapes) {
                r.setProperty(LabelKey, "R" + k);
                k++;
            }
        }

        // sort other shapes by center y-coordinates and assign labels:
        othShapes.sort(Comparator.comparingDouble(r -> r.getCenter().getY()));
        {
            int k = 0;
            for (BinaryRegion s : othShapes) {
                s.setProperty(LabelKey, "S" + k);
                k++;
            }
        }

        if (UseMahalanobisDistance) {
            // Matrix.fromLongBits(Kimia1070.covarianceRegionBits);
            double[][] cov = Matrix.fromLongBits(Kimia1070.covarianceRegionBits);
            md = new MahalanobisDistance(cov);
            // U = md.getWhiteningTransformation();
        }

        // ----- perform matching and create output image -------

        ColorProcessor cp = new ColorProcessor(ip.getWidth(), ip.getHeight());
        ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
        ola.setFont(MarkerFont);
        ola.setTextColor(MarkerColor);
        ColorSequencer randomColor = new FiniteLinearColorSequencer(ReferenceShapeColors);

        // process reference shapes
        Color[] refColors = new Color[refShapes.size()];
        double[][] refMoments = new double[refShapes.size()][];
        {
            int k = 0;
            for (BinaryRegion r : refShapes) {
                refMoments[k] = r.getProperty(MomentKey);
                refColors[k] = randomColor.next();
                paintRegion(r, cp, refColors[k]);
                markRegion(r, ola, r.getProperty(LabelKey));
                k++;
            }
        }

        // process other shapes
        for (BinaryRegion s : othShapes) {
            double[] moments = s.getProperty(MomentKey);
            // find best-matching reference shape
            Tuple2<Integer, Double> match = (UseMahalanobisDistance) ?
                    findBestMatchMahalanobis(moments, refMoments) :
                    findBestMatchEuclidean(moments, refMoments);
            int k = match.get0();
            double dist = match.get1();
            Color col = (dist <= MaxMomentDistance) ? refColors[k] : UnmatchedColor;
            paintRegion(s, cp, col);
            markRegion(s, ola, s.getProperty(LabelKey));
        }

        ImagePlus imResult = new ImagePlus("Matched Regions", cp);
        imResult.setOverlay(ola.getOverlay());
        imResult.show();
    }

    /**
     * Finds the reference moment vector closest to the given sample moment vector and returns the associated index (k)
     * and distance (d).
     *
     * @param moments a sample moment vector
     * @param refMoments an array of reference moment vectors
     * @return a pair (k, d) consisting of reference index k and min. vector distance d
     */
    private Tuple2<Integer, Double> findBestMatchEuclidean(double[] moments, double[][] refMoments) {
        int iMin = -1;
        double dMin = Double.POSITIVE_INFINITY;
        for (int i = 0; i < refMoments.length; i++) {
            double d = Matrix.distL2(moments, refMoments[i]);    // Euclidean distance
            if (d < dMin) {
                dMin = d;
                iMin = i;
            }
        }
        return new Tuple2<>(iMin, dMin);
    }

    private Tuple2<Integer, Double> findBestMatchMahalanobis(double[] moments, double[][] refMoments) {
        int iMin = -1;
        double dMin = Double.POSITIVE_INFINITY;
        for (int i = 0; i < refMoments.length; i++) {
            double d = md.distance(moments, refMoments[i]);    // Euclidean distance
            if (d < dMin) {
                dMin = d;
                iMin = i;
            }
        }
        return new Tuple2<>(iMin, dMin);
    }

    private void markRegion(BinaryRegion r, ShapeOverlayAdapter ola, String s) {
        Pnt2d c = r.getCenter();
        ola.addText(c.getX(), c.getY(), s);
    }

    private void paintRegion(BinaryRegion r, ColorProcessor cp, Color col) {
        cp.setColor(col);
        for (Pnt2d p : r) {
            cp.drawDot(p.getXint(), p.getYint());
        }
    }

    // ----------------------------------------------------------------------

    private boolean runDialog() {
        GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
        gd.addHelp(getJavaDocUrl());
        gd.addNumericField("Minimum region size", MinRegionSize, 0);
        gd.addNumericField("Uax. moment vector distance", MaxMomentDistance, 3);
        gd.addCheckbox("Use Mahalanobis distance", UseMahalanobisDistance);

        gd.showDialog();
        if (gd.wasCanceled()) {
            return false;
        }

        MinRegionSize = (int) gd.getNextNumber();
        MaxMomentDistance = gd.getNextNumber();
        UseMahalanobisDistance = gd.getNextBoolean();
        return true;
    }

}


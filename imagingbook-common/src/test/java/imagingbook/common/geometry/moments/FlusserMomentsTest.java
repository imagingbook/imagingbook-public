/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.moments;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.linear.LinearMapping2D;
import imagingbook.common.geometry.mappings.linear.Rotation2D;
import imagingbook.common.geometry.mappings.linear.Scaling2D;
import imagingbook.common.geometry.mappings.linear.Translation2D;
import imagingbook.common.math.Complex;
import imagingbook.common.regions.BinaryRegion;
import imagingbook.common.regions.BinaryRegionSegmentation;
import imagingbook.common.regions.Contour;
import imagingbook.common.regions.RegionContourSegmentation;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.kimia.Kimia1070;
import imagingbook.sampleimages.kimia.Kimia99;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class FlusserMomentsTest {

    private static double TOL = 1e-6;

    private static List<Pnt2d> points = Arrays.asList(
            Pnt2d.from(10, 15),
            Pnt2d.from(3, 7),
            Pnt2d.from(-1, 5),
            Pnt2d.from(5, -5),
            Pnt2d.from(-7, 3)
    );

    @Test
    public void testFlusserMoments1() {
        FlusserMoments fm = new FlusserMoments(points);
        Complex c00 = fm.getComplexMoment(0, 0);
        Complex c01 = fm.getComplexMoment(0, 1);
        Complex c10 = fm.getComplexMoment(1, 0);
        Complex c11 = fm.getComplexMoment(1, 1);
        Complex c20 = fm.getComplexMoment(2, 0);
        Complex c02 = fm.getComplexMoment(0, 2);

        // System.out.println(" c00 = " + c00);
        // System.out.println(" c10 = " + c10);
        // System.out.println(" c01 = " + c01);
        // System.out.println(" c11 = " + c11);
        // System.out.println(" c20 = " + c20);
        // System.out.println(" c02 = " + c02);

        assertTrue(c00.equals(5, 0 , TOL));
        assertTrue(c01.equals(0, 0 , TOL));
        assertTrue(c10.equals(0, 0 , TOL));
        assertTrue(c11.equals(372, 0 , TOL));
        assertTrue(c20.equals(-44, 140 , TOL));
        assertTrue(c02.equals(-44, -140 , TOL));
    }

    @Test
    public void testSCaleNormalizedFlusserMoments() {
        FlusserMoments fm = new FlusserMoments(points);

        Complex m00 = fm.getScaleNormalizedMoment(0, 0);
        Complex m01 = fm.getScaleNormalizedMoment(0, 1);
        Complex m10 = fm.getScaleNormalizedMoment(1, 0);
        Complex m11 = fm.getScaleNormalizedMoment(1, 1);
        Complex m20 = fm.getScaleNormalizedMoment(2, 0);
        Complex m02 = fm.getScaleNormalizedMoment(0, 2);

        // System.out.println(" m00 = " + m00);
        // System.out.println(" m10 = " + m10);
        // System.out.println(" m01 = " + m01);
        // System.out.println(" m11 = " + m11);
        // System.out.println(" m20 = " + m20);
        // System.out.println(" m02 = " + m02);

        assertTrue(m00.equals(1, 0 , TOL));
        assertTrue(m01.equals(0, 0 , TOL));
        assertTrue(m10.equals(0, 0 , TOL));
        assertTrue(m11.equals(14.88, 0 , TOL));
        assertTrue(m20.equals(-1.76, 5.6 , TOL));
        assertTrue(m02.equals(-1.76, -5.6 , TOL));
    }

    private static double[] invMomentsExp =
            {14.88, 280.96128, 867.7251072, 1402.53696, 14203.272830976013, 304180.56570470415,
                    368.864, 25223.15354112, 49134.5455104, 4447152.773082437, 8866036.876121216};

    @Test
    public void testFlusserInvariantMoments() {
        FlusserMoments fm = new FlusserMoments(points);
        double[] moments = fm.getInvariantMoments();
        // System.out.println(Arrays.toString(moments));
        Assert.assertArrayEquals(invMomentsExp, moments, TOL);
    }

    @Test
    public void testFlusserInvariantMomentsRotated() {
        // rotation+translation of the point set should not change the moments
        LinearMapping2D map = new Rotation2D(0.5).concat(new Translation2D(-300, 20));
        Pnt2d[] pointA = map.applyTo(points.toArray(new Pnt2d[0]));
        FlusserMoments fm = new FlusserMoments(Arrays.asList(pointA));
        double[] moments = fm.getInvariantMoments();
        // System.out.println(Arrays.toString(moments));
        Assert.assertArrayEquals(invMomentsExp, moments, TOL);
    }

    // @Test
    // public void testFlusserInvariantMomentsScale() {
    //     // scale?
    //     LinearMapping2D map = new Scaling2D(2.0);
    //     Pnt2d[] pointA = map.applyTo(points.toArray(new Pnt2d[0]));
    //     FlusserMoments fm = new FlusserMoments(Arrays.asList(pointA));
    //     double[] moments = fm.getInvariantMoments();
    //     // System.out.println(Arrays.toString(moments));
    //     // Assert.assertArrayEquals(invMomentsExp, moments, TOL);
    //     for (int i = 0; i < moments.length; i++) {
    //         System.out.format(Locale.US, "%.8f / %.8f  = %.8f \n", invMomentsExp[i], moments[i], moments[i]/invMomentsExp[i]);
    //     }
    // }

    // ---------------------------------------------------------------------------------------
    @Test
    public void testFlusserInvariantMomentsImageR() {
        ImageResource ir = Kimia1070.bird02; ImageProcessor xxx;
        ByteProcessor ip = (ByteProcessor) ir.getImagePlus().getProcessor();
        // System.out.println("ip1 = " + ip);
        {
            BinaryRegionSegmentation segmenter = new RegionContourSegmentation(ip);
            BinaryRegion r = segmenter.getRegions(true).get(0);
            // System.out.println("   r.size = = " + r.getSize());
            double[] moments = new FlusserMoments(r).getInvariantMoments();

            // System.out.println(Arrays.toString(moments));
            //     [0.25374273452323387, 1.6511699439525E-4, 5.20902798567151E-6, 2.416343273212465E-5, 1.2311370528758843E-7,
            //     6.443592161668993E-8, 0.11847904398583721, 2.5604374283752694E-6, 1.382435638611414E-5, -1.4036909566376298E-9,
            //     1.0176945756381126E-9]
        }
        ByteProcessor ip2 = (ByteProcessor) ip.resize(2 * ip.getWidth(), 2 * ip.getHeight(), false);
        // ip2.flipHorizontal();
        // ip2.flipVertical();
        // System.out.println("ip2 = " + ip2);
        {
            BinaryRegionSegmentation segmenter = new RegionContourSegmentation(ip2);
            BinaryRegion r = segmenter.getRegions(true).get(0);
            // System.out.println("   r.size = = " + r.getSize());
            double[] moments = new FlusserMoments(r).getInvariantMoments();

            // System.out.println(Arrays.toString(moments));
            //     [0.25374273452323387, 1.6511699439525E-4, 5.20902798567151E-6, 2.416343273212465E-5, 1.2311370528758843E-7,
            //     6.443592161668993E-8, 0.11847904398583721, 2.5604374283752694E-6, 1.382435638611414E-5, -1.4036909566376298E-9,
            //     1.0176945756381126E-9]
        }
    }

    @Test
    public void testFlusserInvariantMomentsImageC() {
        ImageResource ir = Kimia1070.bird02; ImageProcessor xxx;
        ByteProcessor ip = (ByteProcessor) ir.getImagePlus().getProcessor();
        // System.out.println("ip1 = " + ip);
        double[] moments1;
        {
            BinaryRegionSegmentation segmenter = new RegionContourSegmentation(ip);
            BinaryRegion r = segmenter.getRegions(true).get(0);
            Contour c = r.getOuterContour();
            // System.out.println("   c.size = = " + c.getLength());
            moments1 = new FlusserMoments(c).getInvariantMoments();

            // System.out.println(Arrays.toString(moments1));
            //     [0.25374273452323387, 1.6511699439525E-4, 5.20902798567151E-6, 2.416343273212465E-5, 1.2311370528758843E-7,
            //     6.443592161668993E-8, 0.11847904398583721, 2.5604374283752694E-6, 1.382435638611414E-5, -1.4036909566376298E-9,
            //     1.0176945756381126E-9]
        }
        ByteProcessor ip2 = (ByteProcessor) ip.resize(2 * ip.getWidth(), 2 * ip.getHeight(), false);
        // ip2.flipHorizontal();
        // ip2.flipVertical();
        // System.out.println("ip2 = " + ip2);
        double[] moments2;
        {
            BinaryRegionSegmentation segmenter = new RegionContourSegmentation(ip2);
            BinaryRegion r = segmenter.getRegions(true).get(0);
            Contour c = r.getOuterContour();
            // System.out.println("   c.size = = " + c.getLength());
            moments2 = new FlusserMoments(c).getInvariantMoments();

            // System.out.println(Arrays.toString(moments2));
            //     [0.25374273452323387, 1.6511699439525E-4, 5.20902798567151E-6, 2.416343273212465E-5, 1.2311370528758843E-7,
            //     6.443592161668993E-8, 0.11847904398583721, 2.5604374283752694E-6, 1.382435638611414E-5, -1.4036909566376298E-9,
            //     1.0176945756381126E-9]
        }
        // for (int i = 0; i < moments1.length; i++) {
        //     System.out.format(Locale.US, "%.8f / %.8f  = %.8f \n", moments1[i], moments2[i], moments2[i]/moments1[i]);
        // }
    }
}

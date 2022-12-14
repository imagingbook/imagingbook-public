/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.image;

import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.SiftTestImage;
import org.junit.Test;
import ij.process.ImageProcessor;

import static org.junit.Assert.*;

public class MinMaxFinderTest {

    @Test
    public void test1() {
        // ImageResource ir = SiftTestImage.Ireland02tiny;
        // ImageProcessor ip = ir.getImagePlus().getProcessor();

        ByteProcessor ip = new ByteProcessor(30, 20);
        ip.setValue(128);
        ip.fill();

        ip.setValue(160);
        ip.drawDot(14, 12);
        ip.drawDot(5, 13);
        ip.drawDot(9, 12);

        ip.setValue(20);
        ip.drawDot(5, 7);
        ip.drawDot(3, 9);

        ip.setValue(30);
        ip.drawDot(17, 4);

        MinMaxFinder mf = new MinMaxFinder(ip, 1, 1);

        Pnt2d[] maxPts = mf.getLocalMaxima(3);
        for (int i = 0; i < maxPts.length; i++) {
            System.out.println("max " + maxPts[i]);
        }

        System.out.println();

        Pnt2d[] minPts = mf.getLocalMinima(3);
        for (int i = 0; i < minPts.length; i++) {
            System.out.println("min " + minPts[i]);
        }

    }

}
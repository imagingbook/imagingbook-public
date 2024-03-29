/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.ij;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.ColorTestImage;
import org.junit.Test;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.math.Matrix;
import imagingbook.core.FileUtils;
import imagingbook.sampleimages.GeneralSampleImage;
import imagingbook.testutils.ImageTestUtils;

public class IjUtilsTest {

    private static final int W = 97;
    private static final int H = 51;

    @Test
    public void testByteArrayReadWrite() {
        ByteProcessor bp1 = makeRandomByteProcessor(W, H);
        byte[][] A = IjUtils.toByteArray(bp1);
        assertEquals(W, A.length);
        assertEquals(H, A[0].length);

        ByteProcessor bp2 = IjUtils.toByteProcessor(A);
        assertTrue(ImageTestUtils.match(bp1, bp2));
    }

    @Test
    public void testIntArrayReadWrite() {
        ByteProcessor bp1 = makeRandomByteProcessor(W, H);
        int[][] A = IjUtils.toIntArray(bp1);
        assertEquals(W, A.length);
        assertEquals(H, A[0].length);

        ByteProcessor bp2 = IjUtils.toByteProcessor(A);
        assertTrue(ImageTestUtils.match(bp1, bp2));
    }

    @Test
    public void testFloatArrayReadWrite() {
        FloatProcessor fp1 = makeRandomFloatProcessor(W, H);
        float[][] A = IjUtils.toFloatArray(fp1);
        assertEquals(W, A.length);
        assertEquals(H, A[0].length);

        FloatProcessor fp2 = IjUtils.toFloatProcessor(A);
        assertTrue(ImageTestUtils.match(fp1, fp2, 1e-6));
    }

    @Test
    public void testDoubleArrayReadWrite() {
        FloatProcessor fp1 = makeRandomFloatProcessor(W, H);
        double[][] A = IjUtils.toDoubleArray(fp1);
        assertEquals(W, A.length);
        assertEquals(H, A[0].length);

        FloatProcessor fp2 = IjUtils.toFloatProcessor(A);
        assertTrue(ImageTestUtils.match(fp1, fp2, 1e-6));
    }

    // ----------------------------------------------------------------

    @Test    // convert using default weights
    public void testToByteProcessorColorProcessor() {
        ImageProcessor ip = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertTrue(ip instanceof ColorProcessor);
        ByteProcessor fp1 = IjUtils.toByteProcessor((ColorProcessor) ip, null);
        ByteProcessor fp2 = makeLumaByte((ColorProcessor) ip, RgbUtils.getDefaultWeights());    // see below
        assertTrue(ImageTestUtils.match(fp1, fp2, 1e-3));
    }

    @Test    // convert using default weights
    public void testToByteProcessorColorProcessorWeights1() {
        ImageProcessor ip = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertTrue(ip instanceof ColorProcessor);
        ByteProcessor fp1 = IjUtils.toByteProcessor((ColorProcessor) ip, null);
        ByteProcessor fp2 = makeLumaByte((ColorProcessor) ip, RgbUtils.getDefaultWeights());    // see below
        assertTrue(ImageTestUtils.match(fp1, fp2, 1e-3));
    }

    @Test    // default conversion must honor the processor's weights (if set)
    public void testToByteProcessorColorProcessorWeights2() {
        double[] weights = {0.3, 0.5, 0.2};    // special weights!
        ImageProcessor ip = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertTrue(ip instanceof ColorProcessor);
        ((ColorProcessor) ip).setRGBWeights(weights);

        ByteProcessor fp1 = IjUtils.toByteProcessor((ColorProcessor) ip);
        ByteProcessor fp2 = makeLumaByte((ColorProcessor) ip, weights);    // see below
        assertTrue(ImageTestUtils.match(fp1, fp2, 1e-3));
    }

    // ----------------------------------------------------------------

    @Test    // convert using default weights
    public void testToFloatProcessorColorProcessor() {
        ImageProcessor ip = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertTrue(ip instanceof ColorProcessor);
        FloatProcessor fp1 = IjUtils.toFloatProcessor((ColorProcessor) ip, null);
        FloatProcessor fp2 = makeLumaFloat((ColorProcessor) ip, RgbUtils.getDefaultWeights());    // see below
        assertTrue(ImageTestUtils.match(fp1, fp2, 1e-3));
    }

    @Test    // convert using specific weights
    public void testToFloatProcessorColorProcessorWeights1() {
        double[] weights = RgbUtils.ITU601RgbWeights;
        ImageProcessor ip = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertTrue(ip instanceof ColorProcessor);
        FloatProcessor fp1 = IjUtils.toFloatProcessor((ColorProcessor) ip, weights);
        FloatProcessor fp2 = makeLumaFloat((ColorProcessor) ip, weights);    // see below
        assertTrue(ImageTestUtils.match(fp1, fp2, 1e-3));
    }

    @Test    // default conversion must honor the processor's weights (if set)
    public void testToFloatProcessorColorProcessorWeights2() {
        double[] weights = {0.3, 0.5, 0.2};    // special weights!
        ImageProcessor ip = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertTrue(ip instanceof ColorProcessor);
        ((ColorProcessor) ip).setRGBWeights(weights);

        FloatProcessor fp1 = IjUtils.toFloatProcessor((ColorProcessor) ip);
        FloatProcessor fp2 = makeLumaFloat((ColorProcessor) ip, weights);    // see below
        assertTrue(ImageTestUtils.match(fp1, fp2, 1e-3));
    }

    // ----------------------------------------------------------------

    @Test
    public void testConvolve() {
        float[][] H = {
                {1, 2, 3},
                {5, 6, 4},
                {2, 1, 0}};
        Matrix.multiplyD((float) (1 / Matrix.sum(H)), H);

        ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
        IjUtils.convolve(ip, H);
        // TODO: unfinished, checks missing!
    }

    @Test
    public void testConvolveX() {
        float[] h = {1, 3, 2, 0, -1};
        Matrix.multiplyD((float) (1 / Matrix.sum(h)), h);

        ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
        IjUtils.convolveX(ip, h);
        // TODO: unfinished, checks missing!
    }

    @Test
    public void testConvolveY() {
        float[] h = {1, 3, 2, 0, -1};
        Matrix.multiplyD((float) (1 / Matrix.sum(h)), h);

        ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
        IjUtils.convolveY(ip, h);
        // TODO: unfinished, checks missing!
    }

    @Test
    public void testConvolveXY() {
        float[] h = {1, 3, 2, 0, -1};
        Matrix.multiplyD((float) (1 / Matrix.sum(h)), h);

        ImageProcessor ip = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
        IjUtils.convolveXY(ip, h);
        // TODO: unfinished, checks missing!
    }

    // ------------------------------------------------------------------------

    @Test
    public void testCrop1A() {
        ImageProcessor ip1 = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
        assertTrue(ip1 instanceof ByteProcessor);
        ImageProcessor ip2 = IjUtils.crop(ip1, 0, 0, ip1.getWidth(), ip1.getHeight());
        assertTrue(ip2 instanceof ByteProcessor);
        assertTrue(ImageTestUtils.match(ip1, ip2, 1e-3));
    }

    @Test
    public void testCrop1B() {
        ImageProcessor ip1 = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertTrue(ip1 instanceof ColorProcessor);
        ImageProcessor ip2 = IjUtils.crop(ip1, 0, 0, ip1.getWidth(), ip1.getHeight());
        assertTrue(ip2 instanceof ColorProcessor);
        assertTrue(ImageTestUtils.match(ip1, ip2, 1e-3));
    }

    @Test
    public void testCrop1C() {
        ImageProcessor ip1 = GeneralSampleImage.Clown.getImagePlus().getProcessor().convertToFloatProcessor();
        assertTrue(ip1 instanceof FloatProcessor);
        ImageProcessor ip2 = IjUtils.crop(ip1, 0, 0, ip1.getWidth(), ip1.getHeight());
        assertTrue(ip2 instanceof FloatProcessor);
        assertTrue(ImageTestUtils.match(ip1, ip2, 1e-3));
    }

    @Test
    public void testCrop2A() {
        ImageProcessor ip1 = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
        assertTrue(ip1 instanceof ByteProcessor);
        int x = ip1.getWidth() / 5;
        int y = ip1.getHeight() / 3;
        int w = ip1.getWidth() / 2;
        int h = ip1.getHeight() / 3;
        ImageProcessor ip2 = IjUtils.crop(ip1, x, y, w, h);
        assertTrue(ip2 instanceof ByteProcessor);
        assertEquals(w, ip2.getWidth());
        assertEquals(h, ip2.getHeight());
    }

    @Test
    public void testCrop2B() {
        ImageProcessor ip1 = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertTrue(ip1 instanceof ColorProcessor);
        int x = ip1.getWidth() / 5;
        int y = ip1.getHeight() / 3;
        int w = ip1.getWidth() / 2;
        int h = ip1.getHeight() / 3;
        ImageProcessor ip2 = IjUtils.crop(ip1, x, y, w, h);
        assertTrue(ip2 instanceof ColorProcessor);
        assertEquals(w, ip2.getWidth());
        assertEquals(h, ip2.getHeight());
    }

    @Test
    public void testCrop3A() {
        ImageProcessor ip1 = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
        assertTrue(ip1 instanceof ByteProcessor);
        int dx = 10;
        int dy = 7;
        int x = ip1.getWidth() - dx;
        int y = ip1.getHeight() - dy;
        int w = ip1.getWidth();
        int h = ip1.getHeight();
        ImageProcessor ip2 = IjUtils.crop(ip1, x, y, w, h);
        assertTrue(ip2 instanceof ByteProcessor);
        assertEquals(dx, ip2.getWidth());
        assertEquals(dy, ip2.getHeight());
    }

    @Test
    public void testCrop3B() {
        ImageProcessor ip1 = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertTrue(ip1 instanceof ColorProcessor);
        int dx = 10;
        int dy = 7;
        int x = ip1.getWidth() - dx;
        int y = ip1.getHeight() - dy;
        int w = ip1.getWidth();
        int h = ip1.getHeight();
        ImageProcessor ip2 = IjUtils.crop(ip1, x, y, w, h);
        assertTrue(ip2 instanceof ColorProcessor);
        assertEquals(dx, ip2.getWidth());
        assertEquals(dy, ip2.getHeight());
    }

    @Test
    public void testCrop4A() {
        ImageProcessor ip1 = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
        assertTrue(ip1 instanceof ByteProcessor);
        int dx = 10;
        int dy = 7;
        int x = -dx;
        int y = -dy;
        int w = ip1.getWidth();
        int h = ip1.getHeight();
        ImageProcessor ip2 = IjUtils.crop(ip1, x, y, w, h);
        assertTrue(ip2 instanceof ByteProcessor);
        assertEquals(w - dx, ip2.getWidth());
        assertEquals(h - dy, ip2.getHeight());
    }

    @Test
    public void testCrop4B() {
        ImageProcessor ip1 = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertTrue(ip1 instanceof ColorProcessor);
        int dx = 10;
        int dy = 7;
        int x = -dx;
        int y = -dy;
        int w = ip1.getWidth();
        int h = ip1.getHeight();
        ImageProcessor ip2 = IjUtils.crop(ip1, x, y, w, h);
        assertTrue(ip2 instanceof ColorProcessor);
        assertEquals(w - dx, ip2.getWidth());
        assertEquals(h - dy, ip2.getHeight());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrop5A() {
        ImageProcessor ip1 = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
        assertTrue(ip1 instanceof ByteProcessor);
        int x = ip1.getWidth();
        int y = ip1.getHeight();
        int w = ip1.getWidth();
        int h = ip1.getHeight();
        IjUtils.crop(ip1, x, y, w, h);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrop5B() {
        ImageProcessor ip1 = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertTrue(ip1 instanceof ColorProcessor);
        int x = ip1.getWidth();
        int y = ip1.getHeight();
        int w = ip1.getWidth();
        int h = ip1.getHeight();
        IjUtils.crop(ip1, x, y, w, h);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrop6A() {
        ImageProcessor ip1 = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
        assertTrue(ip1 instanceof ByteProcessor);
        IjUtils.crop(ip1, 0, 0, 0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrop6B() {
        ImageProcessor ip1 = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertTrue(ip1 instanceof ColorProcessor);
        IjUtils.crop(ip1, 2, 3, 15, 0);
    }

    // ------------------------------------------------------------------------

    @Test
    public void testIsBinary1() {
        ImageProcessor ip = GeneralSampleImage.ToolsSmall.getImagePlus().getProcessor();
        assertTrue(IjUtils.isBinary(ip));
    }

    @Test
    public void testIsBinary2() {
        ImageProcessor ip = GeneralSampleImage.Cat.getImagePlus().getProcessor();
        assertTrue(IjUtils.isBinary(ip));
    }

    @Test
    public void testIsBinary3() {
        ImageProcessor ip;
        ip = GeneralSampleImage.MonasterySmall.getImagePlus().getProcessor();
        assertFalse(IjUtils.isBinary(ip));
        ip = GeneralSampleImage.Clown.getImagePlus().getProcessor();
        assertFalse(IjUtils.isBinary(ip));
        ip = GeneralSampleImage.FlowerIdx256.getImagePlus().getProcessor();
        assertFalse(IjUtils.isBinary(ip));
    }

    @Test
    public void testIsBinary4() {
        ImageProcessor ip;
        int fill = 100;

        ip = new FloatProcessor (30, 20);
        assertTrue(IjUtils.isBinary(ip));
        ip.setColor(fill); ip.fill();
        assertTrue(IjUtils.isBinary(ip));

        ip = new ByteProcessor (30, 20);
        assertTrue(IjUtils.isBinary(ip));
        ip.setColor(fill); ip.fill();
        assertTrue(IjUtils.isBinary(ip));

        ip = new ShortProcessor (30, 20);
        assertTrue(IjUtils.isBinary(ip));
        ip.setColor(fill); ip.fill();
        assertTrue(IjUtils.isBinary(ip));

        ip = new ColorProcessor (30, 20);
        assertTrue(IjUtils.isBinary(ip));
        ip.setColor(fill); ip.fill();
        assertTrue(IjUtils.isBinary(ip));
    }
    @Test
    public void testIsBinary5() {
        ImageProcessor ip;
        ip = new FloatProcessor (30, 20); ip.set(10, 10, 10);
        assertTrue(IjUtils.isBinary(ip));

        ip = new ByteProcessor (30, 20); ip.set(10, 10, 10);
        assertTrue(IjUtils.isBinary(ip));

        ip = new ShortProcessor (30, 20); ip.set(10, 10, 10);
        assertTrue(IjUtils.isBinary(ip));

        ip = new ColorProcessor (30, 20); ip.set(10, 10, 10);
        assertTrue(IjUtils.isBinary(ip));
    }

    // ----------------------------

    @Test
    public void testIsFlat2() {
        ImageProcessor ip;
        int fill = 100;

        ip = new FloatProcessor (30, 20);
        assertTrue(IjUtils.isFlat(ip));
        ip.setColor(fill); ip.fill();
        assertTrue(IjUtils.isFlat(ip));
        ip.set(10, 10, 10);
        assertFalse(IjUtils.isFlat(ip));

        ip = new ByteProcessor (30, 20);
        assertTrue(IjUtils.isFlat(ip));
        ip.setColor(fill); ip.fill();
        assertTrue(IjUtils.isFlat(ip));
        ip.set(10, 10, 10);
        assertFalse(IjUtils.isFlat(ip));

        ip = new ShortProcessor (30, 20);
        assertTrue(IjUtils.isFlat(ip));
        ip.setColor(fill); ip.fill();
        assertTrue(IjUtils.isFlat(ip));
        ip.set(10, 10, 10);
        assertFalse(IjUtils.isFlat(ip));

        ip = new ColorProcessor (30, 20);
        assertTrue(IjUtils.isFlat(ip));
        ip.setColor(fill); ip.fill();
        assertTrue(IjUtils.isFlat(ip));
        ip.set(10, 10, 10);
        assertFalse(IjUtils.isFlat(ip));
    }


    // ------------------------------------------------------------------------

    @Test
    public void testSaveAndReadImageColor() {
        // make a small truly random image:
        ColorProcessor ip2 = ImageTestUtils.makeRandomColorProcessor(51, 43, 0);

        // save it to a file
        String filename = FileUtils.getTempDirectory() + this.getClass().getSimpleName() + "-color.png";
        String absPath = IjUtils.save(ip2, filename);
        assertNotNull(absPath);
//		System.out.println(absPath);

        // read that file again and compare:
        ImagePlus im = IjUtils.openImage(absPath);
        assertNotNull(im);
        ImageProcessor ip3 = im.getProcessor();
        assertTrue(ip3 instanceof ColorProcessor);
        assertTrue(ImageTestUtils.match(ip2, ip3, 1e-3));
    }

    @Test
    public void testSaveAndReadImageGray() {
        // make a small truly random image:
        ByteProcessor ip2 = ImageTestUtils.makeRandomByteProcessor(107, 37, 0);

        // save it to a file
        String filename = FileUtils.getTempDirectory() + this.getClass().getSimpleName() + "-gray.png";
        String absPath = IjUtils.save(ip2, filename);
        assertNotNull(absPath);

        // read that file again and compare:
        ImagePlus im = IjUtils.openImage(absPath);
        assertNotNull(im);
        ImageProcessor ip3 = im.getProcessor();
        assertTrue(ip3 instanceof ByteProcessor);
        assertTrue(ImageTestUtils.match(ip2, ip3, 1e-3));
    }

    @Test
    public void testSaveAndReadImageFloat() {
        // make a small truly random image:
        FloatProcessor ip2 = ImageTestUtils.makeRandomByteProcessor(107, 37, 0).convertToFloatProcessor();

        // save it to a file
        String filename = FileUtils.getTempDirectory() + this.getClass().getSimpleName() + "-float.tif";
        String absPath = IjUtils.save(ip2, filename);
        assertNotNull(absPath);

        // read that file again and compare:
        ImagePlus im = IjUtils.openImage(absPath);
        assertNotNull(im);
        ImageProcessor ip3 = im.getProcessor();
        assertTrue(ip3 instanceof FloatProcessor);
        assertTrue(ImageTestUtils.match(ip2, ip3, 1e-3));
    }

    // ----------------------------------------------------------------

    @Test
    public void testCheckImageNull() {
        ImagePlus im = null;
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8G));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8C));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_16));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_32));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_RGB));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_ALL));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_ALL + PlugInFilter.STACK_REQUIRED));
    }

    @Test
    public void testCheckImage8G() {
        ImagePlus im = new ImagePlus(null, new ByteProcessor(10, 10));
        assertTrue(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8G));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8C));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_16));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_32));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_RGB));
        assertTrue(IjUtils.checkImageFlags(im, PlugInFilter.DOES_ALL));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_ALL + PlugInFilter.STACK_REQUIRED));
    }

    @Test
    public void testCheckImage16() {
        ImagePlus im = new ImagePlus(null, new ShortProcessor(10, 10));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8G));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8C));
        assertTrue(IjUtils.checkImageFlags(im, PlugInFilter.DOES_16));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_32));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_RGB));
        assertTrue(IjUtils.checkImageFlags(im, PlugInFilter.DOES_ALL));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_ALL + PlugInFilter.STACK_REQUIRED));
    }

    @Test
    public void testCheckImage32() {
        ImagePlus im = new ImagePlus(null, new FloatProcessor(10, 10));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8G));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8C));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_16));
        assertTrue(IjUtils.checkImageFlags(im, PlugInFilter.DOES_32));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_RGB));
        assertTrue(IjUtils.checkImageFlags(im, PlugInFilter.DOES_ALL));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_ALL + PlugInFilter.STACK_REQUIRED));
    }

    @Test
    public void testCheckImageRGB() {
        ImagePlus im = new ImagePlus(null, new ColorProcessor(10, 10));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8G));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8C));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_16));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_32));
        assertTrue(IjUtils.checkImageFlags(im, PlugInFilter.DOES_RGB));
        assertTrue(IjUtils.checkImageFlags(im, PlugInFilter.DOES_ALL));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_ALL + PlugInFilter.STACK_REQUIRED));
    }

    @Test
    public void testCheckImageStack() {
        ImagePlus im = NewImage.createByteImage("", 10, 10, 5, NewImage.FILL_RAMP);
        assertTrue(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8G));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8C));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_16));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_32));
        assertFalse(IjUtils.checkImageFlags(im, PlugInFilter.DOES_RGB));
        assertTrue(IjUtils.checkImageFlags(im, PlugInFilter.DOES_8G + PlugInFilter.STACK_REQUIRED));
        assertTrue(IjUtils.checkImageFlags(im, PlugInFilter.DOES_ALL + PlugInFilter.STACK_REQUIRED));
    }

    // ----------------------------------------------------------------
    // ----------------------------------------------------------------

    private ByteProcessor makeRandomByteProcessor(int w, int h) {
        Random rg = new Random(17);
        ByteProcessor bp = new ByteProcessor(w, h);
        for (int v = 0; v < h; v++) {
            for (int u = 0; u < w; u++) {
                bp.set(u, v, rg.nextInt(256));
            }
        }
        return bp;
    }

    private FloatProcessor makeRandomFloatProcessor(int w, int h) {
        Random rg = new Random(17);
        FloatProcessor fp = new FloatProcessor(w, h);
        for (int v = 0; v < h; v++) {
            for (int u = 0; u < w; u++) {
                fp.setf(u, v, rg.nextFloat());
            }
        }
        return fp;
    }

    // ------------------------------------------------------------------------

    private FloatProcessor makeLumaFloat(ColorProcessor cp, double[] weights) {
        int width = cp.getWidth();
        int height = cp.getHeight();
        FloatProcessor fp = new FloatProcessor(width, height);
        int[] rgb = new int[3];
        for (int u = 0; u < width; u++) {
            for (int v = 0; v < height; v++) {
                cp.getPixel(u, v, rgb);
                float luma = (float) (rgb[0] * weights[0] + rgb[1] * weights[1] + rgb[2] * weights[2]);
                fp.setf(u, v, luma);
            }
        }
        return fp;
    }

    private ByteProcessor makeLumaByte(ColorProcessor cp, double[] weights) {
        int width = cp.getWidth();
        int height = cp.getHeight();
        ByteProcessor bp = new ByteProcessor(width, height);
        int[] rgb = new int[3];
        for (int u = 0; u < width; u++) {
            for (int v = 0; v < height; v++) {
                cp.getPixel(u, v, rgb);
                int luma = (int) (rgb[0] * weights[0] + rgb[1] * weights[1] + rgb[2] * weights[2] + 0.5);
                bp.set(u, v, luma);
            }
        }
        return bp;
    }

    @Test
    public void testCountColors() {
        runCountColorsTest(ColorTestImage.Alps01s, 90705);
        runCountColorsTest(ColorTestImage.Balls_jpg, 266919);
        runCountColorsTest(ColorTestImage.RampFire, 98);
        runCountColorsTest(ColorTestImage.RandomColors, 9888);
        runCountColorsTest(ColorTestImage.SingleColor, 1);
        runCountColorsTest(ColorTestImage.TwoColors, 2);
    }

    private void runCountColorsTest(ImageResource ir, int expectedCols) {
        ColorProcessor cp = (ColorProcessor) ir.getImagePlus().getProcessor();
        assertEquals(expectedCols, IjUtils.countColors(cp));
        cp.invert();
        assertEquals(expectedCols, IjUtils.countColors(cp));
    }
}

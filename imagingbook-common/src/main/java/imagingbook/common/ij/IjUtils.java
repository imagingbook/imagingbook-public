/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.ij;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.io.Opener;
import ij.plugin.PlugIn;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.common.color.RgbUtils;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;
import imagingbook.common.math.Matrix;
import imagingbook.common.util.bits.BitMap;

import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


/**
 * This class defines static utility methods adding to ImageJs functionality.
 * @author WB
 *
 */
public abstract class IjUtils {
	
	private IjUtils() {}
	
	/**
	 * Returns a (possibly empty) array of ImagePlus objects that are sorted by
	 * their titles if the 'sortByTitle' flag is set.
	 * 
	 * @param sortByTitle flag, result is sorted if true.
	 * @return an array of currently open images.
	 */
	public static ImagePlus[] getOpenImages(boolean sortByTitle) {
		return getOpenImages(sortByTitle, null);
	}
	
	/**
	 * Returns an array of strings containing the short titles of the images
	 * supplied.
	 * 
	 * @param images array of images.
	 * @return array of names.
	 */
	public static String[] getImageShortTitles(ImagePlus[] images) {
		String[] imageNames = new String[images.length];
		for (int i = 0; i < images.length; i++) {
			imageNames[i] = images[i].getShortTitle();
		}
		return imageNames;
	}
	
	/**
	 * Opens a dialog to let the user select one of the currently open images.
	 * 
	 * @param title string to show in the dialog
	 * @return a {@link ImagePlus} object, use the getProcessor method to obtain the
	 *         associated {@link ImageProcessor}
	 */
	public static ImagePlus selectOpenImage(String title) {
		ImagePlus[] openImages = getOpenImages(true, null);
		String[] imageNames = getImageShortTitles(openImages);
		if (title == null) {
			title = "image:";
		}
		GenericDialog gd = new GenericDialog("Select image");
		gd.addChoice(title, imageNames, imageNames[0]);
		gd.showDialog(); 
		if (gd.wasCanceled()) 
			return null;
		else {
			return openImages[gd.getNextChoiceIndex()];
		}
	}
	
	
	/**
	 * Returns a (possibly empty) array of {@link ImagePlus} objects that are sorted
	 * by their titles if the sortByTitle flag is set. The image "exclude"
	 * (typically the current image) is not included in the returned array (pass
	 * null to exclude no image).
	 * 
	 * @param sortByTitle set {@code true} to return images sorted by title
	 * @param exclude     reference to an image to be excluded (may be {@code null})
	 * @return a (possibly empty) array of {@link ImagePlus} objects
	 */
	public static ImagePlus[] getOpenImages(boolean sortByTitle, ImagePlus exclude) {
		List<ImagePlus> imgList = new LinkedList<ImagePlus>();
		int[] wList = WindowManager.getIDList();
        if (wList != null) {
	    	for (int i : wList) {
	            ImagePlus imp = WindowManager.getImage(i);
	            if (imp != null && imp != exclude) {
	            	imgList.add(imp);
	            }
	    	}
        }
        ImagePlus[] impArr = imgList.toArray(new ImagePlus[0]);
        if (sortByTitle) {
        	Comparator<ImagePlus> cmp = new Comparator<ImagePlus>() {
        		@Override
				public int compare(ImagePlus impA, ImagePlus impB) {
        			return impA.getTitle().compareTo(impB.getTitle());
        		}
        	};
        	Arrays.sort(impArr, cmp);
        }
		return impArr;
	}
	
	@SuppressWarnings("unused")
	private static String encodeURL(String url) {
		//url = url.replaceAll(" ","%20");	// this doesn't work with spaces
		url = url.replace('\\','/');
		return url;
	}
	
	//----------------------------------------------------------------------
	

	/**
	 * Creates an ImageJ {@link ImagePlus} image for the matrix {@code M[r][c]} (2D
	 * array), where {@code r} is treated as the row (vertical) coordinate and
	 * {@code c} is treated as the column (horizontal) coordinate. Use
	 * {@code show()} to display the resulting image.
	 * 
	 * @param title image title
	 * @param M     2D array
	 * @return a new {@link ImagePlus} image
	 */
	public static ImagePlus createImage(String title, float[][] M) {
		FloatProcessor fp = new FloatProcessor(M[0].length, M.length);
		for (int u = 0; u < M[0].length; u++) {
			for (int v = 0; v < M.length; v++) {
				fp.setf(u, v, M[v][u]);
			}
		}
		return new ImagePlus(title, fp);
	}
	

	/**
	 * Creates an ImageJ {@link ImagePlus} image for the matrix {@code M[r][c]} (2D
	 * array), where {@code r} is treated as the row (vertical) coordinate and
	 * {@code c} is treated as the column (horizontal) coordinate. Use
	 * {@code show()} to display the resulting image.
	 * 
	 * @param title the image title
	 * @param M     a 2D array holding the image data
	 * @return a new {@link ImagePlus} instance
	 */
	public static ImagePlus createImage(String title, double[][] M) {
		FloatProcessor fp = new FloatProcessor(M[0].length, M.length);
		for (int u = 0; u < M[0].length; u++) {
			for (int v = 0; v < M.length; v++) {
				fp.setf(u, v, (float) M[v][u]);
			}
		}
		return new ImagePlus(title, fp);
	}
	
	/**
	 * Sets the weighing factors for the color components used in RGB-to-grayscale
	 * conversion for the specified image {@code ip}. Note that this method can be
	 * applied to any {@link ImageProcessor} instance but has no effect unless
	 * {@code ip} is of type {@link ColorProcessor}. Applies standard (ITU-709)
	 * weights.
	 * 
	 * @param ip the affected image
	 */
	public static void setRgbConversionWeights(ImageProcessor ip) {
		setRgbConversionWeights(ip, 0.299, 0.587, 0.114);
	}
	
	/**
	 * Sets the weighing factors for the color components used in RGB-to-grayscale
	 * conversion for the specified image {@code ip}. Note that this method can be
	 * applied to any {@link ImageProcessor} instance but has no effect unless
	 * {@code ip} is of type {@link ColorProcessor}.
	 * 
	 * @param ip the affected image
	 * @param wr red component weight
	 * @param wg green component weight
	 * @param wb blue component weight
	 */
	public static void setRgbConversionWeights(ImageProcessor ip, double wr, double wg, double wb) {
		if (ip instanceof ColorProcessor) {
			((ColorProcessor) ip).setRGBWeights(wr, wg, wb);
		}
	}
	
	// -------------------------------------------------------------------
	
	/**
	 * Extracts (crops) a rectangular region from the given image and returns it as
	 * a new image (of the same sub-type of {@link ImageProcessor}). If the
	 * specified rectangle extends outside the source image, only the overlapping
	 * region is cropped. Thus the returned image may have smaller size than the
	 * specified rectangle. An exception is thrown if the specified width or height
	 * is less than 1. {@code null} is returned if the rectangle does not overlap
	 * the image at all.
	 * 
	 * @param <T>    the generic image type
	 * @param ip     the image to be cropped
	 * @param x      the left corner coordinate of the cropping rectangle
	 * @param y      the top corner coordinate of the cropping rectangle
	 * @param width  the width of the cropping rectangle
	 * @param height the height of the cropping rectangle
	 * @return the cropped image
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ImageProcessor> T crop(T ip, int x, int y, int width, int height) {
//		if (x < 0 || x >= ip.getWidth() || y < 0 || y >= ip.getHeight()) {
//			throw new IllegalArgumentException("(x,y) must be inside the image");
//		}
		if (width < 1 || height < 1) {
			throw new IllegalArgumentException("crop width/height must be at least 1");
		}
//		if (x + width > ip.getWidth() || y + height > ip.getHeight()) {
//			throw new IllegalArgumentException("crop rectangle must not extend outside image");
//		}
		T ipc = null;
		synchronized (ip) {
			Rectangle roiOrig = ip.getRoi();
			ip.setRoi(x, y, width, height);
			Rectangle roiTmp = ip.getRoi();
			if (roiTmp.width > 0 && roiTmp.height > 0) {
				ipc = (T) ip.crop();
			}
			else {
				throw new IllegalArgumentException("empty crop rectangle");
			}
			ip.setRoi(roiOrig);
		}
		return ipc;
	}
	
	// -------------------------------------------------------------------
	
	/**
	 * Returns a copy of the pixel data as a 2D double array with dimensions [x =
	 * 0,..,width-1][y = 0,..,height-1].
	 * 
	 * @param fp the image
	 * @return the resulting array
	 */
	public static double[][] toDoubleArray(FloatProcessor fp) {
		final int width = fp.getWidth();
		final int height = fp.getHeight();
		float[] fPixels = (float[]) fp.getPixels();
		double[][] dPixels = new double[width][height];
		int i = 0;
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				dPixels[u][v] = fPixels[i];
				i++;
			}
		}
		return dPixels;
	}
	
	/**
	 * Creates a new {@link FloatProcessor} instance of size width x height
	 * from the given {@code double[][]} with dimensions 
	 * [x = 0,..,width-1][y = 0,..,height-1].
	 * 
	 * @param A a 2D {@code double} array
	 * @return a new {@link FloatProcessor} instance
	 */
	public static FloatProcessor toFloatProcessor(double[][] A) {
		final int width = A.length;
		final int height = A[0].length;
		float[] fPixels = new float[width * height];
		int i = 0;
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				fPixels[i] = (float) A[u][v];
				i++;
			}
		}
		return new FloatProcessor(width, height, fPixels);
	}
	
	/**
	 * Creates a new {@link FloatProcessor} instance of size width x height
	 * from the given {@code float[][]} with dimensions 
	 * [x = 0,..,width-1][y = 0,..,height-1].
	 * 
	 * @param A a 2D {@code float} array
	 * @return a new {@link FloatProcessor} instance
	 */
	public static FloatProcessor toFloatProcessor(float[][] A) {
//		final int width = A.length;
//		final int height = A[0].length;
//		float[] fPixels = new float[width * height];
//		int i = 0;
//		for (int v = 0; v < height; v++) {
//			for (int u = 0; u < width; u++) {
//				fPixels[i] = A[u][v];
//				i++;
//			}
//		}
//		return new FloatProcessor(width, height, fPixels);
		return new FloatProcessor(A);
	}
	
	/**
	 * Converts a {@link FloatProcessor} to a {@code float[][]}.
	 * 
	 * @param fp a {@link FloatProcessor}
	 * @return the resulting {@code float[][]}
	 */
	public static float[][] toFloatArray(FloatProcessor fp) {
		return fp.getFloatArray();
	}
	
	/**
	 * Converts the given RGB {@link ColorProcessor} to a scalar-valued
	 * {@link ByteProcessor}, using clearly specified RGB component weights. The
	 * processor's individual RGB component weights are used if they have been set
	 * (not null), otherwise ITU709 weights (see {@link RgbUtils#ITU709RgbWeights})
	 * are applied. This is to avoid problems with standard conversion methods in
	 * ImageJ, which depend on a variety of factors (including current user
	 * settings). See also {@link ColorProcessor#getRGBWeights()},
	 * {@link ColorProcessor#setRGBWeights(double[])},
	 * {@link ImageProcessor#convertToByteProcessor()}.
	 * 
	 * @param cp a {@link ColorProcessor}
	 * @return the resulting {@link ByteProcessor}
	 * @see #toByteProcessor(ColorProcessor, double[])
	 */
	public static ByteProcessor toByteProcessor(ColorProcessor cp) {
		if (cp.getRGBWeights() == null) {	// no weights are set
			return toByteProcessor(cp, null);
		}
		else {	// use the FloatProcessor's individual weights
			return cp.convertToByteProcessor();
		}
	}
	
	/**
	 * Converts the given RGB {@link ColorProcessor} to a scalar-valued
	 * {@link ByteProcessor}, applying the specified set of RGB component weights.
	 * The processor's individual weights (if set) are ignored. This is to avoid
	 * problems with standard conversion methods in ImageJ, which depend on a
	 * variety of factors (including current user settings). See also
	 * {@link ColorProcessor#getRGBWeights()},
	 * {@link ColorProcessor#setRGBWeights(double[])},
	 * {@link ImageProcessor#convertToByteProcessor()}.
	 * 
	 * @param cp         a {@link ColorProcessor}
	 * @param rgbWeights a 3-vector of RGB component weights (must sum to 1)
	 * @return the resulting {@link ByteProcessor}
	 * @see RgbUtils#ITU601RgbWeights
	 * @see RgbUtils#ITU709RgbWeights
	 */
	public static ByteProcessor toByteProcessor(ColorProcessor cp, double[] rgbWeights) {
		if (rgbWeights == null) {
			rgbWeights = RgbUtils.getDefaultWeights();
		}
		if (rgbWeights.length != 3) {
			throw new IllegalArgumentException("rgbWeights must be of length 3");
		}
		double[] oldweights = cp.getRGBWeights();
		cp.setRGBWeights(rgbWeights);
		ByteProcessor bp = cp.convertToByteProcessor();
		cp.setRGBWeights(oldweights);
		return bp;
	}
	
	/**
	 * Converts the given RGB {@link ColorProcessor} to a scalar-valued
	 * {@link FloatProcessor}, using clearly specified RGB component weights. The
	 * processor's individual RGB component weights are used if set, otherweise
	 * default weights are used (see {@link RgbUtils#getDefaultWeights()}). This
	 * should avoid problems with standard conversion methods in ImageJ, which
	 * depend on a variety of factors (including current user settings). See also
	 * {@link ColorProcessor#getRGBWeights()},
	 * {@link ColorProcessor#setRGBWeights(double[])},
	 * {@link ImageProcessor#convertToFloatProcessor()}.
	 * 
	 * @param cp a {@link ColorProcessor}
	 * @return the resulting {@link FloatProcessor}
	 * @see #toFloatProcessor(ColorProcessor, double[])
	 */
	public static FloatProcessor toFloatProcessor(ColorProcessor cp) {
		if (cp.getRGBWeights() == null) {	// no weights are set
			return toFloatProcessor(cp, null);
		}
		else {	// use the FloatProcessor's individual weights
			return cp.convertToFloatProcessor();
		}
	}
	
	/**
	 * Converts the given RGB {@link ColorProcessor} to a scalar-valued
	 * {@link FloatProcessor}, applying the specified set of RGB component weights.
	 * If {@code null} is passed for the weights, default weights are used (see
	 * {@link RgbUtils#getDefaultWeights()}). The processor's individual weights (if
	 * set at all) are ignored. This should avoid problems with standard conversion
	 * methods in ImageJ, which depend on a variety of factors (including current
	 * user settings). See also {@link ColorProcessor#getRGBWeights()},
	 * {@link ColorProcessor#setRGBWeights(double[])},
	 * {@link ImageProcessor#convertToFloatProcessor()}.
	 * 
	 * @param cp         a {@link ColorProcessor}
	 * @param rgbWeights a 3-vector of RGB component weights (must sum to 1)
	 * @return the resulting {@link FloatProcessor}
	 * @see RgbUtils#ITU601RgbWeights
	 * @see RgbUtils#ITU709RgbWeights
	 */
	public static FloatProcessor toFloatProcessor(ColorProcessor cp, double[] rgbWeights) {
		if (rgbWeights == null) {
			rgbWeights = RgbUtils.getDefaultWeights();
		}
		if (rgbWeights.length != 3) {
			throw new IllegalArgumentException("rgbWeights must be of length 3");
		}
		double[] oldweights = cp.getRGBWeights();
		cp.setRGBWeights(rgbWeights);
		FloatProcessor fp = cp.convertToFloatProcessor();
		cp.setRGBWeights(oldweights);
		return fp;
	}
	
	/**
	 * Creates and returns a new {@link ByteProcessor} from the specified 2D
	 * {@code byte} array, assumed to be arranged in the form {@code A[x][y]}, i.e.,
	 * the first coordinate is horizontal, the second vertical. Thus
	 * {@code A.length} is the width and {@code A[0].length} the height of the
	 * resulting image.
	 * 
	 * @param A a 2D {@code byte} array
	 * @return a new {@link ByteProcessor} of size {@code A.length} x
	 *         {@code A[0].length}
	 */
	public static ByteProcessor toByteProcessor(byte[][] A) {
		final int w = A.length;
		final int h = A[0].length;
		ByteProcessor bp = new ByteProcessor(w, h);
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				bp.putPixel(u, v, 0xFF & A[u][v]);
			}
		}
		return bp;
	}
	
	/**
	 * Creates and returns a new {@code byte[][]} from the specified
	 * {@link ByteProcessor}. The resulting array is arranged in the form
	 * {@code A[x][y]}, i.e., the first coordinate is horizontal, the second
	 * vertical. Thus {@code A.length} is the width and {@code A[0].length} the
	 * height of the image.
	 * 
	 * @param bp a {@link ByteProcessor}
	 * @return a 2D {@code byte} array
	 */
	public static byte[][] toByteArray(ByteProcessor bp) {
		final int w = bp.getWidth();
		final int h = bp.getHeight();
		byte[][] A = new byte[w][h];
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				A[u][v] =  (byte) (0xFF & bp.get(u, v));
			}
		}
		return A;
	}
	
	/**
	 * Creates and returns a new {@link ByteProcessor} from the specified 2D
	 * {@code int} array, assumed to be arranged in the form {@code A[x][y]}, i.e.,
	 * the first coordinate is horizontal, the second vertical. Thus
	 * {@code A.length} is the width and {@code A[0].length} the height of the
	 * resulting image. Pixel values are clamped to [0, 255].
	 * 
	 * @param A a 2D {@code int} array
	 * @return a new {@link ByteProcessor} of size {@code A.length} x
	 *         {@code A[0].length}
	 */
	public static ByteProcessor toByteProcessor(int[][] A) {
		final int w = A.length;
		final int h = A[0].length;
		ByteProcessor bp = new ByteProcessor(w, h);
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int val = A[u][v];
				if (val < 0)
					val = 0;
				else if (val > 255) 
					val = 255;
				bp.putPixel(u, v, val);
			}
		}
		return bp;
	}
	
	/**
	 * Creates and returns a new {@code int[][]} from the specified
	 * {@link ByteProcessor}. The resulting array is arranged in the form
	 * {@code A[x][y]}, i.e., the first coordinate is horizontal, the second
	 * vertical. Thus {@code A.length} is the width and {@code A[0].length} the
	 * height of the image.
	 * 
	 * @param bp a {@link ByteProcessor}
	 * @return a 2D {@code int} array
	 */
	public static int[][] toIntArray(ByteProcessor bp) {
		return bp.getIntArray();
	}
	
	/**
	 * Opens the image from the specified {@link URI} and returns it as a
	 * {@link ImagePlus} instance.
	 * 
	 * @param uri the URI leading to the image (including extension)
	 * @return a new {@link ImagePlus} instance or {@code null} if unable to open
	 */
	public static ImagePlus openImage(URI uri) {
		Objects.requireNonNull(uri);
		return new Opener().openImage(uri.toString());
	}
	
	/**
	 * Opens the image from the specified filename and returns it as a
	 * {@link ImagePlus} instance.
	 * 
	 * @param filename the path and filename to be opened
	 * @return a new {@link ImagePlus} instance or {@code null} if unable to open
	 */
	public static ImagePlus openImage(String filename) {
		return openImage(new File(filename).toURI());
	}
	
	
	// Methods for checking/comparing images (primarily used for testing)  ---------------------
	
	/**
	 * Checks if two images are of the same type.
	 * 
	 * @param ip1 the first image
	 * @param ip2 the second image
	 * @return true if both images have the same type
	 */
	public static boolean sameType(ImageProcessor ip1, ImageProcessor ip2) {
		return ip1.getClass().equals(ip2.getClass());
	}
	
	/**
	 * Checks if two images have the same size.
	 * 
	 * @param ip1 the first image
	 * @param ip2 the second image
	 * @return true if both images have the same size
	 */
	public static boolean sameSize(ImageProcessor ip1, ImageProcessor ip2) {
		return ip1.getWidth() == ip2.getWidth() && ip1.getHeight() == ip2.getHeight();
	}
	
	/**
	 * Checks if the given image is possibly a binary image. This requires that the
	 * image contains at most two different pixel values, one of which must be zero.
	 * All pixels are checked. This should work for all image types. More efficient
	 * implementations are certainly possible.
	 * 
	 * @param ip the image ({@link ImageProcessor}) to be checked
	 * @return true if the image is binary
	 */
	public static boolean isBinary(ImageProcessor ip) {
		final int width = ip.getWidth();
		final int height = ip.getHeight();
		int fgVal = 0;
		boolean binary = true;
		
		outer:
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int val = 0x007FFFFF & ip.get(u, v); // = mantissa in case of float
				if (val != 0) {
					if (fgVal == 0) {	// first non-zero value
						fgVal = val;
					}
					else if (val != fgVal) {	// found another non-zero value
						binary = false;
						break outer;
					}
				}
			}
		}
		
		return binary;
	}
	
	/**
	 * Checks if the given image is "flat", i.e., all pixels have the same value.
	 * This should work for all image types.
	 * 
	 * @param ip the image ({@link ImageProcessor}) to be checked
	 * @return true if the image is flat
	 */
	public static boolean isFlat(ImageProcessor ip) {
		final int width = ip.getWidth();
		final int height = ip.getHeight();
		boolean flat = true;
		int fgVal = ip.get(0, 0);
		
		outer:
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				int val = ip.get(u, v);
				if (val != fgVal) {
					flat = false;
					break outer;
				}
			}
		}
		
		return flat;
	}
	
	/**
	 * Collects all image coordinates with non-zero pixel values into an array
	 * of 2D points ({@link Pnt2d}).
	 * 
	 * @param ip an image (of any type)
	 * @return an array of 2D points
	 */
	public static Pnt2d[] collectNonzeroPoints(ImageProcessor ip) {
		List<Pnt2d> points = new ArrayList<>();
		int M = ip.getWidth();
		int N = ip.getHeight();
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				int val = 0x007FFFFF & ip.get(u, v); // = mantissa in case of float
				if (val != 0) {
					points.add(PntInt.from(u, v));
				}
			}
		}
		return points.toArray(new Pnt2d[0]);
	}
	
	// -----------------------------------------------------------------
	
	public static final double DefaultMatchTolerance = 1E-6;
	
	/**
	 * Checks if two images have the same type, size and content (using
	 * {@link #DefaultMatchTolerance} for float images).
	 * 
	 * @param ip1 the first image
	 * @param ip2 the second image
	 * @return true if both images have the same type and content
	 */
	public static boolean match(ImageProcessor ip1, ImageProcessor ip2) {
		// TODO: check redundancy with ImageTestUtils.match() - same names but slightly differently implemented!
		return match(ip1, ip2, DefaultMatchTolerance);
	}
	
	/**
	 * Checks if two images have the same type, size and values (using the specified
	 * tolerance for float images).
	 * 
	 * @param ip1       the first image
	 * @param ip2       the second image
	 * @param tolerance the matching tolerance
	 * @return true if both images have the same type, size and content
	 */
	public static boolean match(ImageProcessor ip1, ImageProcessor ip2, double tolerance) {
		if (!sameType(ip1, ip2)) {
			return false;
		}
		if (!sameSize(ip1, ip2)) {
			return false;
		}
		
		if (ip1 instanceof ByteProcessor) {
			return Arrays.equals((byte[]) ip1.getPixels(), (byte[]) ip2.getPixels());
		}
		else if (ip1 instanceof ShortProcessor) {
			return Arrays.equals((short[]) ip1.getPixels(), (short[]) ip2.getPixels());
		}
		else if (ip1 instanceof ColorProcessor) {
			return Arrays.equals((int[]) ip1.getPixels(), (int[]) ip2.getPixels());
		}
		
		else if (ip1 instanceof FloatProcessor) {
			final float[] p1 = (float[]) ip1.getPixels();
			final float[] p2 = (float[]) ip2.getPixels();
			final float toleranceF = (float) tolerance;
			boolean same = true;
			for (int i = 0; i < p1.length; i++) {
				if (Math.abs(p1[i] - p2[i]) > toleranceF) {
					same = false;
					break;
				}
			}
			return same;
		}

		throw new IllegalArgumentException("unknown processor type " + ip1.getClass().getSimpleName());
	}
	
	// BitMap from/to ByteProcessor conversion
	/**
	 * Converts the specified {@link ByteProcessor} to a {@link BitMap} of the same
	 * size, with all zero values set to 0 and non-zero values set to 1.
	 * 
	 * @param bp a {@link ByteProcessor}
	 * @return the corresponding {@link BitMap}
	 * @see #convertToByteProcessor(BitMap)
	 */
	public static BitMap convertToBitMap(ByteProcessor bp) {
		return new BitMap(bp.getWidth(), bp.getHeight(), (byte[]) bp.getPixels());
	}

	/**
	 * <p>
	 * Converts the specified {@link BitMap} to a {@link ByteProcessor} of the same
	 * size, with all zero values set to 0 and non-zero values set to 1. The
	 * resulting image should be multiplied by 255 to achieve full contrast, e.g.:
	 * </p>
	 * <pre>
	 * ByteProcessor bp1 = ... // some ByteProcessor
	 * BitMap bm = IjUtils.convertToBitMap(bp);
	 * ByteProcessor bp2 = IjUtils.convertToByteProcessor(bm);
	 * bp2.multiply(255);
	 * ...
	 * </pre>
	 * 
	 * @param bitmap a {@link BitMap}
	 * @return the corresponding {@link ByteProcessor}
	 * @see #convertToBitMap(ByteProcessor)
	 */
	public static ByteProcessor convertToByteProcessor(BitMap bitmap) {
		byte[] pixels = bitmap.getBitVector().toByteArray();
		return new ByteProcessor(bitmap.getWidth(), bitmap.getHeight(), pixels);
	}
	
	/**
	 * Draws the given set of points onto the specified image (by setting the
	 * corresponding pixels).
	 * 
	 * @param ip     the image to draw to
	 * @param points the 2D points
	 * @param value  the pixel value to use
	 */
	public static void drawPoints(ImageProcessor ip, Pnt2d[] points, int value) {
		for (int i = 0; i < points.length; i++) {
			Pnt2d p = points[i];
			if (p != null) {
				int u = p.getXint();
				int v = p.getYint();
				ip.putPixel(u, v, value);
			}
		}
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * Runs the given {@link PlugInFilter} instance with empty argument string.
	 * 
	 * @param pluginfilter an instance of {@link PlugInFilter}
	 * @return true if no exception was thrown
	 */
	public static boolean run(PlugInFilter pluginfilter) {
		return run(pluginfilter, "");
	}
	
	/**
	 * Runs the given {@link PlugInFilter} instance.
	 * 
	 * @param pluginfilter an instance of {@link PlugInFilter}
	 * @param arg          argument passed to
	 *                     {@link PlugInFilter#setup(String, ImagePlus)}
	 * @return true if no exception was thrown
	 */
	public static boolean run(PlugInFilter pluginfilter, String arg) {
		try {
			new PlugInFilterRunner(pluginfilter, pluginfilter.getClass().getSimpleName(), arg);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Runs the given {@link PlugIn} instance with empty argument string.
	 * 
	 * @param plugin an instance of {@link PlugIn}
	 * @return true if no exception was thrown
	 */
	public static boolean run(PlugIn plugin) {
		return run(plugin, "");
	}
	
	/**
	 * Runs the given {@link PlugIn} instance.
	 * 
	 * @param plugin an instance of {@link PlugIn}
	 * @param arg    argument passed to {@link PlugIn#run(String)}
	 * @return true if no exception was thrown
	 */
	public static boolean run(PlugIn plugin, String arg) {
		try {
			plugin.run(arg);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	// ------------------------------------------------------------------
	
	/**
	 * Run a {@link PlugInFilter} from the associated class with empty argument
	 * string. If the plugin's constructor is available, use method
	 * {@link #run(PlugInFilter)} instead.
	 * 
	 * @param clazz class of the pluginfilter
	 * @return true if no exception was thrown
	 */
	public static boolean runPlugInFilter(Class<? extends PlugInFilter> clazz) {
		return runPlugInFilter(clazz, "");
	}
	
	/**
	 * Run a {@link PlugInFilter} from the associated class. If the plugin's
	 * constructor is available, use method {@link #run(PlugInFilter, String)}
	 * instead.
	 * 
	 * @param clazz class of the plugin
	 * @param arg   argument string
	 * @return true if no exception was thrown
	 */
	public static boolean runPlugInFilter(Class<? extends PlugInFilter> clazz, String arg) {
		PlugInFilter thePlugIn = null;
		try {
			thePlugIn = clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e.getMessage());	// should never happen
		}
		return run(thePlugIn, arg);
	}
	
	/**
	 * Run a {@link PlugIn} from the associated class with empty argument string. If
	 * the plugin's constructor is available, use method {@link #run(PlugIn)}
	 * instead.
	 * 
	 * @param clazz class of the plugin
	 * @return true if no exception was thrown
	 */
	public static boolean runPlugIn(Class<? extends PlugIn> clazz) {
		return runPlugIn(clazz, "");
	}
	
	/**
	 * Run a {@link PlugIn} from the associated class. If the plugin's constructor
	 * is available, use method {@link #run(PlugIn, String)} instead.
	 * 
	 * @param clazz class of the plugin
	 * @param arg   argument string
	 * @return true if no exception was thrown
	 */
	public static boolean runPlugIn(Class<? extends PlugIn> clazz, String arg) {
		PlugIn thePlugIn = null;
		try {
			thePlugIn = clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e.getMessage());	// should never happen
		}
		return run(thePlugIn, arg);
	}

	
	//  static methods for filtering images using ImageJ's {@link Convolver} class. 

	/**
	 * Applies a one-dimensional convolution kernel to the given image, which is
	 * modified. The 1D kernel is applied in horizontal direction only.# The
	 * supplied filter kernel is not normalized.
	 * 
	 * @param ip the image to be filtered (modified)
	 * @param h  the filter kernel
	 * @see Convolver
	 */
	public static void convolveX (ImageProcessor ip, float[] h) { // TODO: unit test missing
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(ip, h, h.length, 1);
	}

	/**
	 * Applies a one-dimensional convolution kernel to the given image, which is
	 * modified. The 1D kernel is applied in vertical direction only. The supplied
	 * filter kernel must be odd-sized. It is not normalized.
	 * 
	 * @param ip the image to be filtered (modified)
	 * @param h  the filter kernel
	 * @see Convolver
	 */
	public static void convolveY (ImageProcessor ip, float[] h) {
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(ip, h, 1, h.length);
	}

	/**
	 * Applies a one-dimensional convolution kernel to the given image, which is
	 * modified. The same 1D kernel is applied twice, once in horizontal and once in
	 * vertical direction. The supplied filter kernel must be odd-sized. It is not
	 * normalized.
	 * 
	 * @param ip the image to be filtered (modified)
	 * @param h  the filter kernel
	 * @see Convolver
	 */
	public static void convolveXY (ImageProcessor ip, float[] h) {
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(ip, h, h.length, 1);
		conv.convolve(ip, h, 1, h.length);
	}
	
	/**
	 * Applies a two-dimensional convolution kernel to the given image, which is
	 * modified. The supplied kernel {@code float[x][y]} must be rectangular and
	 * odd-sized. It is not normalized.
	 * 
	 * @param ip the image to be filtered (modified)
	 * @param H  the filter kernel
	 */
	public static void convolve(ImageProcessor ip, float[][] H) {
		float[] h = Matrix.flatten(H);	// TODO: right order? transpose?
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(ip, h, H[0].length, H.length);
	}
	
	// ---------------------------------------------------------------
	
	/**
	 * Saves the given {@link ImageProcessor} using the specified path. The image
	 * file type is inferred from the file extension. TIFF is used if no file
	 * extension is given. This method simply invokes
	 * {@link IJ#save(ImagePlus, String)}, creating a temporary and titleless
	 * {@link ImagePlus} instance. Existing files with the same path are
	 * overwritten.
	 * 
	 * @param ip       a {@link ImageProcessor}
	 * @param filepath the path where to save the image, e.g.
	 *                 {@code "C:/tmp/MyImage.png"}
	 * @return the absolute file path
	 */
	public static String save(ImageProcessor ip, String filepath) {
		Objects.requireNonNull(filepath);
		// TODO: check if the file was actually written or not
		File file = Paths.get(filepath).toFile();
		String absPath = file.getAbsolutePath();
		IJ.save(new ImagePlus("", ip), absPath);
		return absPath;
	}
	
	// ---------------------------------------------------------------
	
	/**
	 * Returns true if no image is currently open in ImageJ.
	 * 
	 * @return true if no image is open
	 */
	public static boolean noCurrentImage() {
		return (WindowManager.getCurrentImage() == null);
	}
	
	/**
	 * <p>
	 * Returns true if the current (active) image is compatible with the specified
	 * flags (as specified by {@link PlugInFilter}, typically used to compose the
	 * return value of {@link PlugInFilter#setup(String, ImagePlus)}). This method
	 * emulates the compatibility check performed by ImageJ's built-in
	 * {@link PlugInFilterRunner} before a {@link PlugInFilter} is executed. It may
	 * be used, e.g., in the (normally empty) constructor of a class implementing
	 * {@link PlugInFilter}.
	 * </p>
	 * <p>
	 * Example, checking if the current image is either 8-bit or 32-bit gray:
	 * </p>
	 * <pre>
	 * if (checkImageFlagsCurrent(PlugInFilter.DOES_8G + PlugInFilter.DOES_32)) {
	 * 	// some action ...
	 * }
	 * </pre>
	 * 
	 * @param flags int-encoded binary flags
	 * @return true if the current image is compatible
	 * @see PlugInFilter
	 * @see #checkImageFlags(ImagePlus, int)
	 */
	public static boolean checkImageFlagsCurrent(int flags) {
		return checkImageFlags(WindowManager.getCurrentImage(), flags);
	}
	
	/**
	 * <p>
	 * Returns true if the given image is compatible with the specified flags (as
	 * specified by {@link PlugInFilter}, typically used to compose the return value
	 * of {@link PlugInFilter#setup(String, ImagePlus)}). Usage example:
	 * </p>
	 * <pre>
	 * ImagePlus im = WindowManager.getCurrentImage(); // may be null
	 * if (checkImageFlags(im, PlugInFilter.DOES_8G + PlugInFilter.DOES_RGB)) {
	 * 	// some action
	 * }
	 * </pre>
	 * 
	 * @param im    a {@link ImagePlus} or {@code null}
	 * @param flags int-encoded binary flags
	 * @return true if the image is compatible
	 * @see PlugInFilter
	 */
	public static boolean checkImageFlags(ImagePlus im, int flags) {
		// if no image is required, no more checks are needed:
		if ((flags & PlugInFilter.NO_IMAGE_REQUIRED) != 0) {
			return true;
		}
		// void if no active image or one without a processor:
		if (im == null || im.getProcessor() == null) {
			return false;
		}
		// check if the image type is compatible:
		if (!checkImageType(im, flags)) {
			return false;
		}
		// check if im is a stack, if required:
		if (((flags & PlugInFilter.STACK_REQUIRED) != 0) && !im.hasImageStack()) {
			return false;
		}
		// all checks passed:
		return true;
	}
	
	private static boolean checkImageType(ImagePlus im, int flags) {
		switch (im.getType()) {
		case ImagePlus.GRAY8:
			return ((flags & PlugInFilter.DOES_8G) != 0);
		case ImagePlus.COLOR_256:
			return ((flags & PlugInFilter.DOES_8C) != 0);
		case ImagePlus.GRAY16:
			return ((flags & PlugInFilter.DOES_16) != 0);
		case ImagePlus.GRAY32:
			return ((flags & PlugInFilter.DOES_32) != 0);		
		case ImagePlus.COLOR_RGB:
			return ((flags & PlugInFilter.DOES_RGB) != 0);
		default:
			return false;
		}
	}

    /**
     * Determines how many different colors are contained in the specified 24 bit full-color RGB image.
     *
     * @param cp a RGB image
     * @return the number of distinct colors
     */
    public static int countColors(ColorProcessor cp) {
        // duplicate pixel array and sort
        int[] pixels = (int[]) cp.getPixelsCopy();
        Arrays.sort(pixels);

        int k = 1;	// image contains at least one color
        for (int i = 0; i < pixels.length - 1; i++) {
            if (pixels[i] != pixels[i + 1])
                k = k + 1;
        }
        return k;
    }
}

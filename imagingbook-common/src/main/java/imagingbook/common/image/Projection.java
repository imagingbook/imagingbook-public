package imagingbook.common.image;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

/**
 * <p>
 * This class implements horizontal and vertical projections of scalar-valued
 * images. See Sec. 8.7 of [1] for additional details. 
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing - An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/09/28
 *
 */
public class Projection {
	
	private final double[] pHor, pVer;

	/**
	 * Constructor, works only on images of type {@link ByteProcessor},
	 * {@link ShortProcessor}, and {@link FloatProcessor}.
	 * Throws an exception if the specified image is of type
	 * {@link ColorProcessor}.
	 * 
	 * @param ip the image to process
	 */
	public Projection(ImageProcessor ip) {
		this.pHor = new double[ip.getHeight()];
		this.pVer = new double[ip.getWidth()];
		
		if (ip instanceof ByteProcessor || ip instanceof ShortProcessor) {
			makeProjectionsInt(ip);
		}

		else if (ip instanceof FloatProcessor) {
			makeProjectionsFloat((FloatProcessor) ip);
		}
		else {
			throw new IllegalArgumentException("projections not implemented for ColorProcessor");
		}
	}
	
	/**
	 * Returns the horizontal projection of the associated image.
	 * The length of the returned array corresponds to the image height.
	 * @return the horizontal projection array
	 */
	public double[] getHorizontal() {
		return this.pHor;
	}
	
	/**
	 * Returns the vertical projection of the associated image.
	 * The length of the returned array corresponds to the image width.
	 * @return the vertical projection array
	 */
	public double[] getVertical() {
		return this.pVer;
	}
	
	// ----------------------------------------------------------
	
	private void makeProjectionsInt(ImageProcessor ip) {
		long[] lHor = new long[pHor.length];
		long[] lVer = new long[pVer.length];
		for (int v = 0; v < pHor.length; v++) {
			for (int u = 0; u < pVer.length; u++) {
				long p = ip.get(u, v);
				lHor[v] +=  p;
				lVer[u] +=  p;
			}
		}
		copyArray(lHor, pHor);
		copyArray(lVer, pVer);
	}
	
	private void makeProjectionsFloat(FloatProcessor ip) {
		for (int v = 0; v < pHor.length; v++) {
			for (int u = 0; u < pVer.length; u++) {
				double p = ip.getf(u, v);
				pHor[v] +=  p;
				pVer[u] +=  p;
			}
		}
	}

	private void copyArray(long[] la, double[] da) {
		for (int i = 0; i < la.length; i++) {
			da[i] = la[i];
		}
	}

}
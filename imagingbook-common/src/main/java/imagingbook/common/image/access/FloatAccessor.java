package imagingbook.common.image.access;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.interpolation.InterpolationMethod;

/**
 * Image accessor for scalar images with 32-bit (float) values.
 */
public class FloatAccessor extends ScalarAccessor {
	private final float[] pixels;

	/**
	 * Constructor. Creates a new image accessor of type {@link FloatAccessor}. See also the
	 * generic factory method
	 * {@link ScalarAccessor#create(ImageProcessor, OutOfBoundsStrategy, InterpolationMethod)}.
	 * 
	 * @param ip  an instance of {@link FloatProcessor}
	 * @param obs the out-of-bounds strategy to be used (use {@code null} for
	 *            default settings)
	 * @param ipm the interpolation method to be used (use {@code null} for default
	 *            settings)
	 */
	public FloatAccessor(FloatProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		super(ip, obs, ipm);
		this.pixels = (float[]) ip.getPixels();
	}

	@Override
	public float getVal(int u, int v) {
		int i = indexer.getIndex(u, v);
		if (i < 0)
			return this.defaultValue;
		else
			return pixels[i];
	}

	@Override
	public void setVal(int u, int v, float val) {
		if (u >= 0 && u < width && v >= 0 && v < height) {
			pixels[width * v + u] = val;
		}
	}
}
package imagingbook.common.image.access;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.interpolation.InterpolationMethod;

/**
 * Image accessor for scalar images with 8-bit (byte) values.
 */
public class ByteAccessor extends ScalarAccessor {
	private final byte[] pixels;

	/**
	 * Constructor. Creates a new image accessor of type {@link ByteAccessor}. See also the
	 * generic factory method
	 * {@link ScalarAccessor#create(ImageProcessor, OutOfBoundsStrategy, InterpolationMethod)}.
	 * 
	 * @param ip  an instance of {@link ByteProcessor}
	 * @param obs the out-of-bounds strategy to be used (use {@code null} for
	 *            default settings)
	 * @param ipm the interpolation method to be used (use {@code null} for default
	 *            settings)
	 */
	public ByteAccessor(ByteProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		super(ip, obs, ipm);
		this.pixels = (byte[]) this.ip.getPixels();
	}

	@Override
	public float getVal(int u, int v) {
		final int i = indexer.getIndex(u, v);
		if (i < 0)
			return this.defaultValue;
		else {
			return (0xff & pixels[i]);
		}
	}

	@Override
	public void setVal(int u, int v, float val) {
		int i = indexer.getIndex(u, v);
		if (i >= 0) {
			int vali = Math.round(val);
			if (vali < 0) vali = 0;
			if (vali > 255) vali = 255;
			pixels[i] = (byte) (0xFF & vali);
		}
	}
}
package imagingbook.common.image.access;

import ij.process.ColorProcessor;
import imagingbook.common.interpolation.InterpolationMethod;

/**
 * A specific vector-valued image accessor for RGB images
 * (direct subclass of {@link VectorAccessor}).
 */
public class RgbAccessor extends VectorAccessor {
	
	private final int[] pixels;

	public RgbAccessor(ColorProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm) {
		super(ip, 3, obs, ipm);
		this.pixels = (int[]) this.ip.getPixels();
	}
	
	@Override
	protected ScalarAccessor makeComponentAccessor(int k) {
		return new ComponentAccessor((ColorProcessor)ip, outOfBoundsStrategy, interpolationMethod, k);
	}
	
	// ---------------------------------------------------------------------

	@Override
	public float[] getPix(int u, int v) { // returns an RGB value packed into a float[]
		float red = componentAccessors[0].getVal(u, v);  //(c & 0xff0000) >> 16;
		float grn = componentAccessors[1].getVal(u, v);  //(c & 0xff00) >> 8;
		float blu = componentAccessors[2].getVal(u, v);  //(c & 0xff);
		return new float[] { red, grn, blu };
	}
	
	@Override
	public float[] getPix(double x, double y) {
		float red = componentAccessors[0].getVal(x, y);
		float grn = componentAccessors[1].getVal(x, y);
		float blu = componentAccessors[2].getVal(x, y); 
		return new float[] { red, grn, blu };
	}
	
	@Override
	public void setPix(int u, int v, float[] valf) {
		for (int k = 0; k < 3; k++) {
			componentAccessors[k].setVal(u, v, valf[k]);
		}
	}
	
	// ----------------------------------------------------------------
	
	private interface ComponentGetter {
		/**
		 * Function to extract a particular RGB component from an integer.
		 * @param rgb a packed RGB integer
		 * @return a component value
		 */
		int get(int rgb); 
	}
	
	private interface ComponentSetter {
		/**
		 * Function to insert a particular component value into an integer.
		 * @param rgb a packed RGB integer
		 * @param c the component value to insert
		 * @return the modified RGB integer
		 */
		int set(int rgb, int c);
	}
	
	/**
	 * Defines a scalar image accessor for one particular color component.
	 * This is a non-static inner class, which shares data with the enclosing
	 * {@link RgbAccessor} instance.
	 * Note that this subclass of {@link ScalarAccessor} has no ImageProcessor
	 * of is own, but {@link #getProcessor()} returns the original 
	 * {@link ColorProcessor} instance.
	 */
	private class ComponentAccessor extends ScalarAccessor {
		
		private final int k;	// the RGB component index = 0,1,2
			
		private final ComponentGetter[] getComponent = {			// note the fancy use of lambda expressions!
				(rgb) -> (rgb & 0xff0000) >> 16,	// extract red component
				(rgb) -> (rgb & 0x00ff00) >> 8,		// extract green component
				(rgb) -> (rgb & 0x0000ff) };		// extract blue component
		
		private final ComponentSetter[] setComponent = {
				(rgb, r) -> (rgb & 0xff00ffff) | (r & 0xff) << 16,	// insert red component
				(rgb, g) -> (rgb & 0xffff00ff) | (g & 0xff) << 8,	// insert green component
				(rgb, b) -> (rgb & 0xffffff00) | (b & 0xff)			// insert blue component
		};
		
		private ComponentAccessor(ColorProcessor ip, OutOfBoundsStrategy obs, InterpolationMethod ipm, int k) {
			super(ip, obs, ipm);
			this.k = k;
		}

		@Override
		public float getVal(int u, int v) {
			int i = indexer.getIndex(u, v);
			if (i < 0) {
				return this.defaultValue;
			} else {
				return getComponent[k].get(pixels[i]);
			}
		}

		@Override
		public void setVal(int u, int v, float val) {
			int i = indexer.getIndex(u, v);
			if (i >= 0) {
				int vali = clamp(Math.round(val));
				pixels[i] = setComponent[k].set(pixels[i], vali);
			}
		}
		
		private int clamp(int val) {
			if (val < 0) return 0;
			if (val > 255) return 255;
			return val;
		}
	}

}
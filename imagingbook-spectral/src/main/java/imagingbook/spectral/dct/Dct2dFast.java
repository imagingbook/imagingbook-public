package imagingbook.spectral.dct;

/**
 * <p>
 * Fast implementation of the 2-dimensional DCT. 
 * Note that this class has no public constructor -
 * instantiate sub-class {@link Dct2dFast.Float} or {@link Dct2dFast.Double}
 * instead, as shown below. See Ch. 20 of [1] for additional details.
 * </p>
 * <p>
 * Usage example (for {@code float} data):
 * </p>
 * <pre>
 * float[][] data = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {-2, -1, 0}};
 * int w = data.length;       // w = 4
 * int h = data[0].length;    // h = 3
 * Dct2d.Float dct = new Dct2dFast.Float(w, h);
 * dct.forward(data);  // data now is the 2D DCT spectrum
 * dct.inverse(data);  // data now is the original 2D signal 
 * ...</pre>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Approach</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @see Dct1dFast
 */
public class Dct2dFast extends Dct2dImp {
	
	private Dct2dFast(int width, int height) {
		super(width, height);
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * Two-dimensional DCT implementation using {@code float} data. 
	 */
	public static class Float extends Dct2dFast implements Dct2d.Float {
		
		/**
		 * Constructor.
		 * 
		 * @param width width of the data array (dimension 0)
		 * @param height height of the data array (dimension 1)
		 */
		public Float(int width, int height) {
			super(width, height);
		}
		
		// -------------------

		@Override
		public Dct1d.Float get1dDct(int size) {
			return new Dct1dFast.Float(size);
		}

	}
	
	/**
	 * Two-dimensional DCT implementation using {@code double} data. 
	 */
	public static class Double extends Dct2dFast implements Dct2d.Double {
		
		/**
		 * Constructor.
		 * 
		 * @param width width of the data array (dimension 0)
		 * @param height height of the data array (dimension 1)
		 */
		public Double(int width, int height) {
			super(width, height);
		}
		
		// -------------------

		@Override
		public Dct1d.Double get1dDct(int size) {
			return new Dct1dFast.Double(size);
		}

	}

}

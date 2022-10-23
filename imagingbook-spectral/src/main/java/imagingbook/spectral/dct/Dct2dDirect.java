package imagingbook.spectral.dct;

public class Dct2dDirect  extends Dct2dImp {
	
	private Dct2dDirect(int width, int height) {
		super(width, height);
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * Two-dimensional DCT implementation using {@code float} data. 
	 */
	public static class Float extends Dct2dDirect implements Dct2d.Float {
		
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
			return new Dct1dDirect.Float(size);
		}

	}
	
	/**
	 * Two-dimensional DCT implementation using {@code double} data. 
	 */
	public static class Double extends Dct2dDirect implements Dct2d.Double {
		
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
			return new Dct1dDirect.Double(size);
		}

	}

}

package imagingbook.spectral.dft;

public abstract class Dft2dFast extends Dft2dImp {
	
	private Dft2dFast(int width, int height, ScalingMode sm) {
		super(width, height, sm);
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * Two-dimensional DFT implementation using {@code float} data. 
	 */
	public static class Float extends Dft2dFast implements Dft2d.Float {
		
		/**
		 * Constructor using a specific scaling mode.
		 * 
		 * @param sm the scaling mode
		 */
		public Float(int width, int height, ScalingMode sm) {
			super(width, height, sm);
		}
		
		/**
		 * Constructor using the default scaling mode.
		 */
		public Float(int width, int height) {
			this(width, height, ScalingMode.DEFAULT);
		}
		
		// -------------------

		@Override
		public Dft1d.Float get1dDft(int size) {
			return new Dft1dFast.Float(size, this.sm);
		}

	}
	
	// -------------------------------------------------------------------------

	/**
	 * Two-dimensional DFT implementation using {@code double} data. 
	 */
	public static class Double extends Dft2dFast implements Dft2d.Double {
		
		/**
		 * Constructor using a specific scaling mode.
		 * 
		 * @param sm the scaling mode
		 */
		public Double(int width, int height, ScalingMode sm) {
			super(width, height, sm);
		}
		
		/**
		 * Constructor using the default scaling mode.
		 */
		public Double(int width, int height) {
			this(width, height, ScalingMode.DEFAULT);
		}
		
		// -------------------

		@Override
		public Dft1d.Double get1dDft(int size) {
			return new Dft1dFast.Double(size, this.sm);
		}
	
	}
}

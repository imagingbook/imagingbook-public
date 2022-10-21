package imagingbook.spectral.dft;

public abstract class Dft2dFast {
	
	final ScalingMode sm;
	
	private Dft2dFast(ScalingMode sm) {
		this.sm = sm;
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
		public Float(ScalingMode sm) {
			super(sm);
		}
		
		/**
		 * Constructor using the default scaling mode.
		 */
		public Float() {
			this(ScalingMode.DEFAULT);
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
		public Double(ScalingMode sm) {
			super(sm);
		}
		
		/**
		 * Constructor using the default scaling mode.
		 */
		public Double() {
			this(ScalingMode.DEFAULT);
		}
		
		// -------------------

		@Override
		public Dft1d.Double get1dDft(int size) {
			return new Dft1dFast.Double(size, this.sm);
		}
	
	}
}

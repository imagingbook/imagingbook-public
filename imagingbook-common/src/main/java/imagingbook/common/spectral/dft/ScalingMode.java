package imagingbook.common.spectral.dft;

/**
 * Scaling mode used for the DFT.
 */
public enum ScalingMode {
	/**
	 * Same scaling factor (1/sqrt(M)) is applied in forward and inverse transform.
	 */
	DEFAULT {
		@Override
		public double getScale(int M, boolean forward) {
			return 1.0 / Math.sqrt(M);
		}
	},
	
	/**
	 * Scaling by a factor 1/M is applied to the forward transformation only.
	 */
	FORWARD_ONLY {
		@Override
		public double getScale(int M, boolean forward) {
			return forward ? 1.0 / M : 1.0;
		}
	},
	
	/**
	 * Scaling by a factor 1/M is applied to the inverse transformation only.
	 */
	INVERSE_ONLY {
		@Override
		public double getScale(int M, boolean forward) {
			return forward ? 1.0 : 1.0 / M;
		}
	};
	
	public abstract double getScale(int M, boolean forward);
}

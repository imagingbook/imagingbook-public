/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.spectral.dft;

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
	
	/**
	 * Returns the DFT scale factor for the specified data size and transformation
	 * direction.
	 * @param M the data size
	 * @param forward {@code true} for a forward, {@code false} for a inverse transform
	 * @return the scale factor
	 */
	public abstract double getScale(int M, boolean forward);
}

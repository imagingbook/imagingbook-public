/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.filter.edgepreserving;

import static imagingbook.common.math.Arithmetic.sqr;

import imagingbook.common.ij.DialogUtils;
import imagingbook.common.util.ParameterBundle;

/**
 * Common interface for Perona-Malik filter implementations.
 * 
 * @author WB
 */
public interface PeronaMalikF {
	
	/**
	 * Parameter bundle for Perona-Malik filters (implementations of {@link PeronaMalikF}).
	 */
	public static class Parameters implements ParameterBundle<PeronaMalikF> {

		@DialogUtils.DialogLabel("Number of iterations")
		public int iterations = 10;
		
		@DialogUtils.DialogLabel("Update rate (\u03B1 = 0,..,0.25)")@DialogUtils.DialogDigits(2)
		public double alpha = 0.20; 	
		
		@DialogUtils.DialogLabel("Smoothness parameter (\u03BA)")@DialogUtils.DialogDigits(1)
		public double kappa = 25;
		
		@DialogUtils.DialogLabel("Conductance function g(d)")
		public ConductanceFunction.Type conductanceFunType = ConductanceFunction.Type.g1;
		
		@DialogUtils.DialogLabel("Color mode")
		public ColorMode colorMode = ColorMode.SeparateChannels;

	}
	
	public enum ColorMode  {
		SeparateChannels,
		BrightnessGradient,
		ColorGradient;
	}
	
	public interface ConductanceFunction {
		float eval(float d);
		
		public enum Type {
			g1, g2, g3, g4;
		}
		
		static ConductanceFunction get(Type type, float kappa) {
			switch (type) {
			case g1:
				return (d) -> (float) Math.exp(-sqr(d/kappa));
			case g2:
				return (d) -> 1.0f / (1.0f + sqr(d/kappa));
			case g3:
				return (d) -> (float) (1.0 / Math.sqrt(1.0f + sqr(d/kappa)));
			case g4:
				return (d) -> 
				(d <= 2 * kappa) ? sqr(1.0f - sqr(d / (2 * kappa))) : 0.0f;
			}
			return null;
		}
	}

}

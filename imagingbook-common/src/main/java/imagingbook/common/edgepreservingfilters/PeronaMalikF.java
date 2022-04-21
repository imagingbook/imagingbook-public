package imagingbook.common.edgepreservingfilters;

import static imagingbook.common.math.Arithmetic.sqr;

import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.common.util.ParameterBundle;

/**
 * Common interface for Perona-Malik filter implementations.
 * 
 * @author WB
 */
public interface PeronaMalikF {
	
	public static class Parameters implements ParameterBundle {

		@DialogLabel("Number of iterations")
		public int iterations = 10;
		
		@DialogLabel("Update rate (\u03B1 = 0,..,0.25)")@DialogDigits(2)
		public double alpha = 0.20; 	
		
		@DialogLabel("Smoothness parameter (\u03BA)")@DialogDigits(1)
		public double kappa = 25;
		
		@DialogLabel("Conductance function g(d)")
		public ConductanceFunction.Type conductanceFunType = ConductanceFunction.Type.g1;
		
		@DialogLabel("Color mode")
		public ColorMode colorMode = ColorMode.SeparateChannels;
		
		@DialogLabel("Out-of-bounds strategy")
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NearestBorder;
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

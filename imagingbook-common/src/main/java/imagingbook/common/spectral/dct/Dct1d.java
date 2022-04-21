package imagingbook.common.spectral.dct;

/**
 * Interface specifying all one-dimensional DCT implementations.
 * The definition used is the one adopted by MATLAB
 * (see https://www.mathworks.com/help/signal/ref/dct.html), called 
 * "DCT-II" on Wikipedia  (https://en.wikipedia.org/wiki/Discrete_cosine_transform).
 * 
 */
public interface Dct1d {
	
	public interface Float extends Dct1d {
		
		/**
		 * Performs an "in-place" 1D DCT forward transformation on the supplied data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * @param g the signal to be transformed (modified)
		 */
		void forward(float[] g);
		
		/**
		 * Performs an "in-place" 1D DCT inverse transformation on the supplied spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * @param G the spectrum to be transformed (modified)
		 */
		void inverse(float[] G);
		
		default void checkLength(float[] a, int n) {
			if (a.length != n)
				throw new IllegalArgumentException("data array must be of length " + n);
		}
		
	}
	
	public interface Double extends Dct1d {
		
		/**
		 * Performs an "in-place" 1D DCT forward transformation on the supplied data.
		 * The input signal is replaced by the associated DFT spectrum.
		 * @param g the signal to be transformed (modified)
		 */
		void forward(double[] g);
		
		/**
		 * Performs an "in-place" 1D DCT inverse transformation on the supplied spectrum.
		 * The input spectrum is replaced by the associated signal.
		 * @param G the spectrum to be transformed (modified)
		 */
		void inverse(double[] G);
		
		default void checkLength(double[] a, int n) {
			if (a.length != n)
				throw new IllegalArgumentException("data array must be of length " + n);
		}
		
	}

}

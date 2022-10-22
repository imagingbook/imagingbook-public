package imagingbook.spectral.dft;

/**
 * Abstract super class for 2D DFT implementations.
 * Holds information about data size and scaling mode.
 * 
 * @author WB
 *
 */
public abstract class Dft2dImp {
	
	final int M, N;		// width (M) and height (N) of the data array
	final ScalingMode sm;
	
	protected Dft2dImp(int width, int height, ScalingMode sm) {
		this.M = width;
		this.N = height;
		this.sm = sm;
	}
	
	public int getWidth() {
		return this.M;
	}
	
	public int getHeight() {
		return this.N;
	}
	
	public ScalingMode getScalingMode() {
		return this.sm;
	}

}

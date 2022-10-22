package imagingbook.spectral.dft;

/**
 * Abstract super class for all 2D DFT implementations.
 * Holds information about data size and scaling mode.
 * 
 * @author WB
 *
 */
public abstract class Dft2dImp implements Dft2d {
	
	final int M, N;		// width (M) and height (N) of the data array
	final ScalingMode sm;
	
	protected Dft2dImp(int width, int height, ScalingMode sm) {
		this.M = width;
		this.N = height;
		this.sm = sm;
	}
	
	// ----------------------------------------------------------
	
	@Override
	public int getWidth() {
		return this.M;
	}
	
	@Override
	public int getHeight() {
		return this.N;
	}
	
	@Override
	public ScalingMode getScalingMode() {
		return this.sm;
	}

}

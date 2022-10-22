package imagingbook.spectral.dft;

/**
 * Abstract super class for all 1D DFT implementations.
 * Holds information about data size and scaling mode.
 * 
 * @author WB
 *
 */
abstract class Dft1dImp implements Dft1d {
	
	final int M;		// data size
	final ScalingMode sm;
	
	protected Dft1dImp(int size, ScalingMode sm) {
		this.M = size;
		this.sm = sm;
	}
	
	// --------------------------------------------------------------------
	
	@Override
	public int getSize() {
		return this.M;
	}
	
	@Override
	public ScalingMode getScalingMode() {
		return this.sm;
	}

}

package imagingbook.spectral.dct;


/**
 * Abstract super class common to all 2D DCT implementations.
 * Holds information about data size.
 * 
 * @author WB
 *
 */
abstract class Dct2dImp implements Dct2d {
	
	final int M, N;		// width (M) and height (N) of the data array
	
	protected Dct2dImp(int width, int height) {
		this.M = width;
		this.N = height;
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

}

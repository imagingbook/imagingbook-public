package imagingbook.spectral.dct;

abstract class Dct1dImp implements Dct1d {
	
	final int M;		// data size
	
	protected Dct1dImp(int size) {
		this.M = size;
	}
	
	// --------------------------------------------------------------------
	
	@Override
	public int getSize() {
		return this.M;
	}

}

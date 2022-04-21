package imagingbook.common.filter.examples;

import imagingbook.common.filter.GenericFilterVector;
import imagingbook.common.image.data.PixelPack;

// a custom filter that provides its own progress data
public class FilterShowProgressExample extends GenericFilterVector {

	// variables for progress reporting
	private int maxCnt = 5000;
	private int cnt;
	
	@Override
	protected void initFilter(PixelPack source, PixelPack target) {	
	}
	
	@Override
	protected void initPass(PixelPack source, PixelPack target) {
	}
	
	@Override
	protected int passesRequired() {
		return 3;	// needs 3 passes
	}
	
	@Override
	protected float[] doPixel(PixelPack source, int u, int v) {
		float[] dummy = new float[3];
		float sum = 0;
		cnt = 0;
		for (int i = 0; i < maxCnt; i++) {
			this.cnt = i;
			sum += (float) Math.sqrt(i); // some dummy work
		}
		cnt = 0;
		dummy[0] = sum;
		return dummy;
	}
	
	// -------------------------------------------------------------
	
	@Override
	protected double reportProgress() {
		return (double) this.cnt / this.maxCnt;
	}
	
//	public static void main(String[] args) {
//		GenericFilter filter = new FilterShowProgressExample();
//		ProgressMonitor mon = new ProgressMonitor(filter, 500);
//		mon.start();
//		PixelPack pp = new PixelPack(400, 300, 3, null);
//		filter.applyTo(pp);
//		mon.terminate();
//	}

}

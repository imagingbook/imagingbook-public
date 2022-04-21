package imagingbook.common.morphology;

import ij.process.ByteProcessor;

public class BinaryClosing extends BinaryMorphologyFilter {
	
	public BinaryClosing() {
		super();
	}
			
	public BinaryClosing(byte[][] H) {
		super(H);
	}

	@Override
	public void applyTo(ByteProcessor ip) {
		new BinaryDilation(H).applyTo(ip);	//dilate(ip, H);
		new BinaryErosion(H).applyTo(ip);	// erode(ip, H);
	}

}

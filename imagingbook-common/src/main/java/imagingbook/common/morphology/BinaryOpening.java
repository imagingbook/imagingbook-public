package imagingbook.common.morphology;

import ij.process.ByteProcessor;

public class BinaryOpening extends BinaryMorphologyFilter {
	
	public BinaryOpening() {
		super();
	}
	
	public BinaryOpening(byte[][] H) {
		super(H);
	}

	@Override
	public void applyTo(ByteProcessor ip) {
		new BinaryErosion(H).applyTo(ip);	// erode(ip, H);
		new BinaryDilation(H).applyTo(ip);	//dilate(ip, H);
	}

}

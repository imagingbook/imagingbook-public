package imagingbook.common.morphology;

import ij.process.ByteProcessor;

public class BinaryErosion extends BinaryMorphologyFilter {
	
	public BinaryErosion() {
		super();
	}
	
	public BinaryErosion(byte[][] H) {
		super(H);
	}

	@Override
	public void applyTo(ByteProcessor ip) {
		// dilates the background
		ip.invert();
		new BinaryDilation(reflect(H)).applyTo(ip); //dilate(ip, reflect(H));
		ip.invert();
	}

}

package imagingbook.common.morphology;

import ij.process.Blitter;
import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.NeighborhoodType2D;

public class BinaryOutline implements BinaryMorphologyOperator {
	
	private static final byte[][] H4 = { 
			{ 0, 1, 0 }, 
			{ 1, 1, 1 }, 
			{ 0, 1, 0 }};
	
	private static final byte[][] H8 = { 
			{ 1, 1, 1 }, 
			{ 1, 1, 1 }, 
			{ 1, 1, 1 }};
	
	private final NeighborhoodType2D nh;
	
	public BinaryOutline() {
		this.nh = NeighborhoodType2D.N4;
	}
	
	public BinaryOutline(NeighborhoodType2D nh) {
		this.nh = nh;
	}
	
	@Override
	public void applyTo(ByteProcessor ip) {
		ByteProcessor fg = (ByteProcessor) ip.duplicate();
		byte[][] H = (nh == NeighborhoodType2D.N4) ? H4 : H8;
		new BinaryErosion(H).applyTo(fg);	//erode(fg, H);
		ip.copyBits(fg, 0, 0, Blitter.DIFFERENCE);
	}

}

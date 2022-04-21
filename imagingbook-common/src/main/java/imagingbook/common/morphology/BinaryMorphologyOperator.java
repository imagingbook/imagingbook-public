package imagingbook.common.morphology;

import ij.process.ByteProcessor;

public interface BinaryMorphologyOperator {
	
	public void applyTo(ByteProcessor bp);

}

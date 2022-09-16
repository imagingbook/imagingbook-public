package imagingbook.common.image.matching;

/** 
 * Enum type for different distance norms used to calculate distance transforms. 
 * 
 * @see DistanceTransform
 */
public enum DistanceNorm {
	/** L1 distance (Manhattan distance) */ L1, 
	/** L2 distance (Euclidean distance) */ L2;
}
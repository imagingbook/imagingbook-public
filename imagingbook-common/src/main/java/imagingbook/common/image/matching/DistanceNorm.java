/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
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
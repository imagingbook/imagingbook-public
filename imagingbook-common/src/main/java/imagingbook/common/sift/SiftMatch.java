/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.common.sift;

/**
 * <p>
 * Represents a match between two SIFT features. See Secs. 25.5 of [1] for more details. Instances are immutable.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>, 3rd ed, Springer
 * (2022).
 * </p>
 *
 * @author WB
 * @version 2022/11/20
 */
public class SiftMatch implements Comparable<SiftMatch> {
	
	private final SiftDescriptor descriptor1, descriptor2;
	private final double distance;
	
	public SiftMatch(SiftDescriptor descriptor1, SiftDescriptor descriptor2, double distance) {
		this.descriptor1 = descriptor1;
		this.descriptor2 = descriptor2;
		this.distance = distance;
	}
	
	public SiftDescriptor getDescriptor1() {
		return descriptor1;
	}
	
	public SiftDescriptor getDescriptor2() {
		return descriptor2;
	}
	
	public double getDistance() {
		return distance;
	}

	@Override
	public int compareTo(SiftMatch other) {
		return Double.compare(this.distance, other.distance);
	}
	
	@Override
	public String toString() {
		return String.format("%s %.2f", this.getClass().getSimpleName(), distance);
	}

}

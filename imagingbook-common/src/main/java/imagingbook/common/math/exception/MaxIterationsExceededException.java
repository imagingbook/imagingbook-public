/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.math.exception;

/**
 * Exception indicating that some predefined number of iterations was exceeded.
 * 
 * @author WB
 *
 */
@SuppressWarnings("serial")
public class MaxIterationsExceededException extends RuntimeException {
	
	public MaxIterationsExceededException(int maxIterations) {
		super(String.format("max. number of iterations (%d) exceeded", maxIterations));
	}
	
	public MaxIterationsExceededException() {
		super("max. number of iterations exceeded");
	}

}

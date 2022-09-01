/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.filter.edgepreserving;

import imagingbook.common.image.access.OutOfBoundsStrategy;
import imagingbook.common.util.ParameterBundle;

public interface NagaoMatsuyamaF {

	public static class Parameters implements ParameterBundle {

		@DialogLabel("Variance threshold (0,..,10)")
		public double varThreshold = 0.0;

		@DialogLabel("Out-of-bounds strategy")
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NearestBorder;
	}
	
	abstract class Constants {
		protected static final int[][] R0 =
			{{-1,-1}, {0,-1}, {1,-1},
			 {-1, 0}, {0, 0}, {1, 0},
			 {-1, 1}, {0, 1}, {1, 1}};
			
			// -------------------
			
		protected static final int[][] R1 =
			{{-2,-1}, {-1,-1},
			 {-2, 0}, {-1, 0}, {0, 0},
			 {-2, 1}, {-1, 1}};
			
		protected static final int[][] R2 =
			{{-2,-2}, {-1,-2},
			 {-2,-1}, {-1,-1}, {0,-1},
			          {-1, 0}, {0, 0}};
			
		protected static final int[][] R3 =
			{{-1,-2}, {0,-2}, {1,-2}, 
			 {-1,-1}, {0,-1}, {1,-1},
			          {0, 0}};

		protected static final int[][] R4 =
			{        {1,-2}, {2,-2},
			 {0,-1}, {1,-1}, {2,-1},
			 {0, 0}, {1, 0}};
			
		protected static final int[][] R5 =
			{        {1,-1}, {2,-1},
			 {0, 0}, {1, 0}, {2, 0},
			         {1, 1}, {2, 1}};
			
		protected static final int[][] R6 =
			{{0,0}, {1,0},
			 {0,1}, {1,1}, {2,1},
			        {1,2}, {2,2}};

		protected static final int[][] R7 =
			{        {0,0},
			 {-1,1}, {0,1}, {1,1},
			 {-1,2}, {0,2}, {1,2}};
			
		protected static final int[][] R8 =
			{        {-1,0}, {0,0},
			 {-2,1}, {-1,1}, {0,1},
			 {-2,2}, {-1,2}};
			
			// -------------------
			
		protected static final int[][][] SubRegions = {R1, R2, R3, R4, R5, R6, R7, R8};
	}
	
}

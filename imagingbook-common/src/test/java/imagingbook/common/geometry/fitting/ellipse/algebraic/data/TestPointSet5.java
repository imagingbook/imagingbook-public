/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.geometry.fitting.ellipse.algebraic.data;

import imagingbook.common.geometry.basic.Pnt2d;

public abstract class TestPointSet5 {
	
	public static final Pnt2d[] points = {
			Pnt2d.from(40, 53),
			Pnt2d.from(107, 20),
			Pnt2d.from(170, 26),
			Pnt2d.from(186, 55),
			Pnt2d.from(135, 103)};
	
	public static double[] qExpected = // normalized ellipse parameters
		{0.317325319, 0.332954818, 0.875173557, -89.442594143, -150.574265066, 7886.192568730};

}

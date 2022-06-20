/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.mser;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.math.Eigensolver2x2;
import imagingbook.common.regions.BinaryRegion;

/**
 * TODO: Move to BinaryRegion!
 * @author WB
 *
 */
public abstract class Utils {
	
	public static GeometricEllipse makeEllipse(BinaryRegion r) {
		final double n = r.getSize();
		Pnt2d xc = r.getCenter();
		double[] moments = r.getCentralMoments(); // = (mu20, mu02, mu11)
		final double mu20 = moments[0];
		final double mu02 = moments[1];
		final double mu11 = moments[2];
		
		Eigensolver2x2 es = new Eigensolver2x2(mu20, mu11, mu11, mu02);
		double ra = 2 * Math.sqrt(es.getRealEigenvalue(0) / n);	// correct (see Book p.238)
		double rb = 2 * Math.sqrt(es.getRealEigenvalue(1) / n);
		double[] x0 = es.getEigenvector(0).toArray();
		double theta = Math.atan2(x0[1], x0[0]);
		return new GeometricEllipse(ra, rb, xc.getX(), xc.getY(), theta);
	}

}

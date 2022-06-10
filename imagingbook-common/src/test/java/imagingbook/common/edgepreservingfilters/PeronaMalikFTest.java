/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.edgepreservingfilters;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.filter.edgepreserving.PeronaMalikF.ConductanceFunction;
import imagingbook.common.filter.edgepreserving.PeronaMalikF.ConductanceFunction.Type;

public class PeronaMalikFTest {

	@Test
	public void testConductanceFunctions() {
		float d = 20;
		float kappa = 30;
		// results checked with Mathematica
		Assert.assertEquals(0.641180, ConductanceFunction.get(Type.g1,kappa).eval(d), 1E-6);
		Assert.assertEquals(0.692308, ConductanceFunction.get(Type.g2,kappa).eval(d), 1E-6);
		Assert.assertEquals(0.832050, ConductanceFunction.get(Type.g3,kappa).eval(d), 1E-6);
		Assert.assertEquals(0.790123, ConductanceFunction.get(Type.g4,kappa).eval(d), 1E-6);
	}

}

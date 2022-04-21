package imagingbook.common.edgepreservingfilters;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.edgepreservingfilters.PeronaMalikF.ConductanceFunction;
import imagingbook.common.edgepreservingfilters.PeronaMalikF.ConductanceFunction.Type;

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

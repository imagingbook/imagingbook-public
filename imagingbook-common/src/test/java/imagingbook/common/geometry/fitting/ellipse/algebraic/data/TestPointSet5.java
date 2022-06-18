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

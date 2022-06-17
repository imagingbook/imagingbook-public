package imagingbook.common.geometry.fitting.ellipse.algebraic;

import imagingbook.common.geometry.basic.Pnt2d;

public abstract class TestPointSet6 {
	
	static final Pnt2d[] points = {
			Pnt2d.from(40, 53),
			Pnt2d.from(107, 20),
			Pnt2d.from(170, 26),
			Pnt2d.from(186, 55),
			Pnt2d.from(135, 103),
			Pnt2d.from(135, 113)};
	
	
	static double[] qExpected = // normalized ellipse parameters
		{0.338021608, 0.315146286, 0.813052446, -93.275080603, -145.094714549, 7969.519273622};

}

package imagingbook.common.geometry.mappings.linear;

import org.junit.Assert;
import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;

public class Translation2DTest {

	@Test
	public void testInvert() {
		Translation2D m = new Translation2D(-1.5, 19.0);
		Pnt2d p = Pnt2d.from(3.5, -17);
		Pnt2d q = m.applyTo(p);
		
		Pnt2d pp = m.getInverse().applyTo(q);
		
		Assert.assertArrayEquals(p.toDoubleArray(), pp.toDoubleArray(), 1E-6);
	}
	
	@Test
	public void testFromPoints() {
		Pnt2d p = Pnt2d.from(3.5, -17);
		Pnt2d q = Pnt2d.from(-5, 7.1);
		
		Translation2D m = Translation2D.fromPoints(p, q);
		Pnt2d pp = m.applyTo(p);

		Assert.assertArrayEquals(q.toDoubleArray(), pp.toDoubleArray(), 1E-6);
	}

}

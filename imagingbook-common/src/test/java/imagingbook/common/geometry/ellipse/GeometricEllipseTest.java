package imagingbook.common.geometry.ellipse;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;

public class GeometricEllipseTest {
	
	private static final double TOL = 0.001;

	@Test
	public void testGeometricToAlgebraicEllipseConversion() {		
		GeometricEllipse eg1 = new GeometricEllipse(120, 50, 200, -70, Math.PI/3);
		AlgebraicEllipse ea = AlgebraicEllipse.from(eg1);	
		GeometricEllipse eg2 = new GeometricEllipse(ea);

		assertEquals(eg1.ra, eg2.ra, TOL);
		assertEquals(eg1.rb, eg2.rb, TOL);
		assertEquals(eg1.xc, eg2.xc, TOL);
		assertEquals(eg1.yc, eg2.yc, TOL);
		assertEquals(eg1.theta, eg2.theta, TOL);
	}
	
	@Test
	public void testGeometricEllipseClosestPoint() {	
		GeometricEllipse ell = new GeometricEllipse(6, 5, 0, 0, 0);
		{
			Pnt2d x = Pnt2d.from(0, -0.000000001);
			Pnt2d xp = Pnt2d.from(0, -5);
			assertEquals(xp, ell.getClosestPoint(x));
		}
		{
			Pnt2d x = Pnt2d.from(10, 0);
			Pnt2d xp = Pnt2d.from(6, 0);
			assertEquals(xp, ell.getClosestPoint(x));
		}
		{
			Pnt2d x = Pnt2d.from(0, 10000);
			Pnt2d xp = Pnt2d.from(0, 5);
			assertEquals(xp, ell.getClosestPoint(x));
		}
		{
			Pnt2d x = Pnt2d.from(0, -10000);
			Pnt2d xp = Pnt2d.from(0, -5);
			assertEquals(xp, ell.getClosestPoint(x));
		}
		{
			Pnt2d x = Pnt2d.from(-1, 0);
			Pnt2d xp = Pnt2d.from(-3.2727272727272725, 4.190702026042222);
			assertEquals(xp, ell.getClosestPoint(x));
		}
		{
			Pnt2d x = Pnt2d.from(1, 0.1);
			Pnt2d xp = Pnt2d.from(3.107598626723163, 4.277104138229151);
			assertEquals(xp, ell.getClosestPoint(x));
		}
	}

}

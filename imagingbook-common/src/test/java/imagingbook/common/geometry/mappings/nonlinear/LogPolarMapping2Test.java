package imagingbook.common.geometry.mappings.nonlinear;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.mappings.Mapping2D;

public class LogPolarMapping2Test {
	
	private static int nr = 60;
	private static int na = 100;
	private static double rmin = 3;
	private static double rmax = 90;

	@Test
	public void test1() {
		LogPolarMapping2 lpm = new LogPolarMapping2(0, 0, nr, na, rmax, rmin);
		Mapping2D lpmi = lpm.getInverse();
		
		Pnt2d xy = Pnt2d.from(13, 11);
//		System.out.println("xy = " + xy);

		Pnt2d ra = lpm.applyTo(xy);
//		System.out.println("ra = " + ra);
		
		Pnt2d xy2 = lpmi.applyTo(ra);
//		System.out.println("xy = " + xy2);
		
//		System.out.println("ra (for rmax) = " + lpm.applyTo(Pnt2d.from(rmax, 0)));
		assertEquals(xy, xy2);
	}

}

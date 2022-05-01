package imagingbook.common.geometry.line;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

/**
 * Various line conversion tests
 * @author WB
 *
 */
public class LineTest {
	
	static double TOL = 1E-6;

	static Pnt2d p1 = PntInt.from(30, 10);
	static Pnt2d p2 = PntInt.from(200, 100);
	static Pnt2d p3 = PntInt.from(90, 40);

	@Test	// check conversions between different line types
	public void test1() {
		AlgebraicLine L1 = AlgebraicLine.from(p1, p2);
		{
			SlopeInterceptLine S1 = SlopeInterceptLine.from(L1);
			AlgebraicLine L2 = AlgebraicLine.from(S1);
			assertTrue(L1.equals(L2, TOL));
		}
		
		{
			ParametricLine P1 =  ParametricLine.from(L1);
			AlgebraicLine L2 = AlgebraicLine.from(P1);
			assertTrue(L1.equals(L2, TOL));
		}
		
		{
			HessianLine H1 =  new HessianLine(L1);
			AlgebraicLine L2 = new AlgebraicLine(H1);
			assertTrue(L1.equals(L2, TOL));
		}
		
	}

}

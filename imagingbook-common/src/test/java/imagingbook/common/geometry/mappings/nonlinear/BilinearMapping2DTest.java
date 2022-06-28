package imagingbook.common.geometry.mappings.nonlinear;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.Pnt2d.PntInt;

public class BilinearMapping2DTest {

	@Test
	public void test1() {
		Pnt2d[] P = {
				PntInt.from(2,5),
				PntInt.from(4,6),
				PntInt.from(7,9),
				PntInt.from(5,9),
				};
		
		Pnt2d[] Q = {
				PntInt.from(4,3),
				PntInt.from(5,2),
				PntInt.from(9,3),
				PntInt.from(7,5),
				};
		
		BilinearMapping2D bmPQ = BilinearMapping2D.fromPoints(P, Q);
//		System.out.println("\nbilinear mapping = \n" + bm.toString());
		
		// forward check P -> Q
		for (int i = 0; i < P.length; i++) {
			Pnt2d Qim = bmPQ.applyTo(P[i]);
//			System.out.println(P[i].toString() + " -> " + Qim.toString());
			assertEquals("mapping fails on point " + P[i], Q[i], Qim);
		}
		
		// backward check Q -> P
		BilinearMapping2D bmQP = BilinearMapping2D.fromPoints(Q, P);
		for (int i = 0; i < Q.length; i++) {
			Pnt2d Pim = bmQP.applyTo(Q[i]);
//			System.out.println(P[i].toString() + " -> " + Pim.toString());
			assertEquals("mapping fails on point " + Q[i], P[i], Pim);
		}
	}

/*
bilinear mapping = 
BilinearMapping[A = (-0,500, -0,333, 0,167, 5,000) / B = (-1,375, 1,167, 0,042, -0,500)]
PntInt[2, 5] -> PntDouble[4.000, 3.000]
PntInt[4, 6] -> PntDouble[5.000, 2.000]
PntInt[7, 9] -> PntDouble[9.000, 3.000]
PntInt[5, 9] -> PntDouble[7.000, 5.000]
*/
	

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.geometry.fitting.line;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.math.PrintPrecision;

public class IncrementalLineFitTest {

	static double TOL = 1e-6;
	
	@Test
	public void test1() {
		double[][] X = {{1, 8}, {4, 5}, {4, 7}, {6, 4}, {9, 4}}; // book example
		AlgebraicLine refLine = new AlgebraicLine(-0.4968900437902618, -0.8678135078357052, 7.244827854073206);
		
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		LineFit fit = new IncrementalLineFit(pts);
		AlgebraicLine line = fit.getLine();
		assertNotNull(line);
		assertTrue(refLine.equals(line, TOL));	
		assertEquals(2.6645834350486606, fit.getSquaredOrthogonalError(pts), TOL);
	}
	
	@Test
	public void test2() {
		double[][] X = {{1, 8}, {4, 5}}; // 2 points only
		AlgebraicLine refLine = new AlgebraicLine(-0.7071067811865476, -0.7071067811865476, 6.3639610306789285);
		
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		LineFit fit = new IncrementalLineFit(pts);
		
		AlgebraicLine line = fit.getLine();
		assertNotNull(line);
		assertTrue(refLine.equals(line, TOL));	
		assertEquals(0.0, fit.getSquaredOrthogonalError(pts), TOL);
	}
	
	@Test(expected = IllegalStateException.class)
	public void test3A() {
		IncrementalLineFit fit = new IncrementalLineFit();
		@SuppressWarnings("unused")
		AlgebraicLine line = fit.getLine();
	}
	
	@Test(expected = IllegalStateException.class)
	public void test3B() {
		double[][] X = {{1, 8}};
		IncrementalLineFit fit = new IncrementalLineFit(PntUtils.fromDoubleArray(X));
		@SuppressWarnings("unused")
		AlgebraicLine line = fit.getLine();
	}
	
	@Test
	public void test4() {
		double[][] X = {{1, 8}, {4, 5}, {4, 7}, {6, 4}, {9, 4}}; // book example
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		IncrementalLineFit fit = new IncrementalLineFit();
		fit.add(pts[0]);
		
		// Adding points to end -------------------------- 
		fit.add(pts[1]);
		checkFit(fit, 2, new AlgebraicLine(-0.707107, -0.707107, 6.363961), 0.0);
		
		fit.add(pts[2]);
		checkFit(fit, 3, new AlgebraicLine(-0.646375, -0.763020, 7.025925), 1.2781583131345187);
		
		fit.add(pts[3]);
		checkFit(fit, 4, new AlgebraicLine(-0.657184, -0.753730, 6.986821), 1.2809113833887924);
		
		fit.add(pts[4]);
		checkFit(fit, 5, new AlgebraicLine(-0.496890, -0.867814, 7.244828), 2.6645834350486606);
		
		// Removing points from end  -------------------------- 
		fit.removeLast();
		checkFit(fit, 4, new AlgebraicLine(-0.657184, -0.753730, 6.986821), 1.2809113833887924);
		
		fit.removeLast();
		checkFit(fit, 3, new AlgebraicLine(-0.646375, -0.763020, 7.025925), 1.2781583131345187);
		
		fit.removeLast();
		checkFit(fit, 2, new AlgebraicLine(-0.707107, -0.707107, 6.363961), 0.0);
	}
	
	
	@Test
	public void test5() {
		PrintPrecision.set(6);
		double[][] X = {{1, 8}, {4, 5}, {4, 7}, {6, 4}, {9, 4}}; // book example
		Pnt2d[] pts = PntUtils.fromDoubleArray(X);
		IncrementalLineFit fit = new IncrementalLineFit(pts);
		
		checkFit(fit, 5, new AlgebraicLine(-0.496890, -0.867814, 7.244828), 2.6645834350486606);
		
		// Removing points from front -------------------------- 
		fit.removeFirst();
		checkFit(fit, 4, new AlgebraicLine(-0.442139, -0.896947, 7.027032), 2.549433445948077);
		
		fit.removeFirst();
		checkFit(fit, 3, new AlgebraicLine(-0.533885, -0.845557, 7.609057), 1.5801977669246623);
		
		fit.removeFirst();
		checkFit(fit, 2, new AlgebraicLine(0.000000, 1.000000, -4.000000), 0.0);
	}
	
	
	// ----------------------------------------------------------
	
	private void checkFit(IncrementalLineFit fit, int size, AlgebraicLine refLine, double error) {
		assertEquals(size, fit.getSize());
		AlgebraicLine line = fit.getLine();
		assertNotNull(line);
		assertTrue(refLine.equals(line, 1e-3));
		assertEquals(error, fit.getSquaredOrthogonalError(), TOL);
	}

//	private void listPoints(IncrementalLineFit fit) {
//		for (Pnt2d p : fit) {
//			System.out.println(p.toString());
//		}
//	}
//	
//	private void listPoints(Pnt2d[] pnts) {
//		for (Pnt2d p : pnts) {
//			System.out.println(p.toString());
//		}
//	}
}

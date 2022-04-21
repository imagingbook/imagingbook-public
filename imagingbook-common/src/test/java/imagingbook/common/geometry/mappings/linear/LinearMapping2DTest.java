package imagingbook.common.geometry.mappings.linear;

import org.junit.Test;

import imagingbook.common.math.Matrix;
import imagingbook.testutils.ArrayTests;

public class LinearMapping2DTest {
	
	static double[][] I = Matrix.idMatrix(3);
	
	@Test
	public void testConstructor1() {
		LinearMapping2D A = new LinearMapping2D();
		ArrayTests.assertArrayEquals(I, A.getTransformationMatrix());
	}
	
	@Test
	public void testConstructor2() {
		double[][] a = new double[0][0];	// empty matrix must work
		LinearMapping2D A = new LinearMapping2D(a);
		ArrayTests.assertArrayEquals(I, A.getTransformationMatrix());
	}
	
	@Test
	public void testConstructor3() {
		double[][] a = {{1, 0}, {0}};		// partial matrix must work
		LinearMapping2D A = new LinearMapping2D(a);
		ArrayTests.assertArrayEquals(I, A.getTransformationMatrix());
	}
	
	@Test
	public void testConstructor4() {
		double[][] a = 
			{{-1.230769, 2.076923, -1.769231}, 
			{-2.461538, 2.615385, -3.538462}, 
			{-0.307692, 0.230769, 1.000000}};
		LinearMapping2D A = new LinearMapping2D(a);
		ArrayTests.assertArrayEquals(a, A.getTransformationMatrix());
	}
	

	@Test
	public void testGetInverse() {
		double[][] a = 
			{{-1.230769, 2.076923, -1.769231}, 
			{-2.461538, 2.615385, -3.538462}, 
			{-0.307692, 0.230769, 1.000000}};
		
		LinearMapping2D A = new LinearMapping2D(a);
		LinearMapping2D Ai = A.getInverse();
		
		double[][] aai = A.concat(Ai).getTransformationMatrix();
		ArrayTests.assertArrayEquals(I, aai);
		
		double[][] aia = Ai.concat(A).getTransformationMatrix();
		ArrayTests.assertArrayEquals(I, aia);
	}
	
	@Test
	public void testNormalize() {
		
	}

}

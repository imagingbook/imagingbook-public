package imagingbook.common.geometry.mappings.linear;

import java.util.Random;

import org.junit.Test;

import imagingbook.common.math.Matrix;
import imagingbook.testutils.NumericTestUtils;

public class Rotation2DTest {

	@Test
	public void test1() {
		Rotation2D R = new Rotation2D(0.5);
		double[][] A = R.getTransformationMatrix();		

		AffineMapping2D Ri = R.getInverse();
		double[][] Ai = Ri.getTransformationMatrix();
		
		double[][] AAi = Matrix.multiply(A, Ai);
		
		// A * Ai = I
		NumericTestUtils.assertArrayEquals(Matrix.idMatrix(3), AAi); 	
	}
	
	@Test
	public void test2() {
		Random rg = new Random(17);
		for (int i = 0; i < 1000; i++) {
			double angle = 2000 * rg.nextDouble() - 1000; 	// degrees
			Rotation2D R1 = new Rotation2D(Math.toRadians( angle));
			Rotation2D R2 = new Rotation2D(Math.toRadians(-angle));
	
			Rotation2D R12 = R1.concat(R2);
			double[][] A12 = R12.getTransformationMatrix();
			NumericTestUtils.assertArrayEquals(Matrix.idMatrix(3), A12);
		}
	}
	
	@Test
	public void test3() {
		double angle1 = 25; 	// degrees
		double angle2 = 87; 	// degrees
		Rotation2D R1 = new Rotation2D(Math.toRadians(angle1));
		Rotation2D R2 = new Rotation2D(Math.toRadians(angle2));
		Rotation2D R3 = new Rotation2D(Math.toRadians(angle1 + angle2));
		
		Rotation2D R12 = R1.concat(R2);
		
		double[][] A3 = R3.getTransformationMatrix();		
		double[][] A12 = R12.getTransformationMatrix();
		NumericTestUtils.assertArrayEquals(A3, A12); 	
	}
	
}

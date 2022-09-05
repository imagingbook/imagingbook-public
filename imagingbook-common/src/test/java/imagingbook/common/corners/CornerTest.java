package imagingbook.common.corners;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class CornerTest {

	@Test
	public void testSorting() {
		Corner c1 = new Corner(1,0,1);
		Corner c2 = new Corner(2,0,2);
		Corner c3 = new Corner(3,0,3);
		Corner c4 = new Corner(4,0,2);
		
		Corner[] corners = {c1, c2, c3, c4};
//		System.out.println("corners orig =   " + Arrays.toString(corners));
		
		Arrays.sort(corners);
//		System.out.println("corners sorted = " + Arrays.toString(corners));
		
		for (int i = 1; i < corners.length; i++) {
			assertTrue(corners[i-1].getQ() >= corners[i].getQ());
		}
	}

}

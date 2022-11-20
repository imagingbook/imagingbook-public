package imagingbook.common.sift;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SiftDescriptorTest {

	@Test
	public void testCompareTo() {
		// SiftDescriptor(double x, double y, double scale, int scaleLevel, double magnitude, double orientation, int[] features)
		double mag1 = 3.3333, mag2 = 5.5555;
		SiftDescriptor sd1 = new SiftDescriptor(1.0, 2.0, 10.0, 3, mag1, 0.0, null);
		SiftDescriptor sd2 = new SiftDescriptor(1.0, 2.0, 10.0, 3, mag2, 0.0, null);
		assertEquals( 1, sd1.compareTo(sd2));
		assertEquals(-1, sd2.compareTo(sd1));
		assertEquals( 0, sd1.compareTo(sd1));
	}


}

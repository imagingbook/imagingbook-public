package imagingbook.common.util;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;

import imagingbook.common.image.interpolation.InterpolationMethod;

public class ParameterBundleTest {
	
	@SuppressWarnings("unused")
	private static class MyParameterBundle implements ParameterBundle {
		// valid parameter fields
		public boolean 	someBool = true;
		public int 		someInt = 39;
		public long		someLong = 10L;
		public float 	someFloat = 1.99f;	
		public double 	someDouble = Math.PI;
		public String 	someString = "SHOW ME";	
		public InterpolationMethod someEnum = InterpolationMethod.Bicubic;
		public double[] someArray = null;
		
		// non-valid fields ignored) 	TODO: detect in validate() and reject!
		private int privateInt = 99;
		public final int finalInt = 77;
		public static int staticInt = 88;
	}

	@Test
	public void test1() {
		MyParameterBundle params = new MyParameterBundle();
		assertTrue(params.validate());	// does nothing yet
		
		Field[] validFields = params.getValidParameterFields();
//		for (Field f : validFields) {
//			System.out.println(f);
//		}
		assertEquals(8, validFields.length);
		
		// check ordering as defined:
		assertTrue(validFields[0].getType() == boolean.class);
		assertTrue(validFields[1].getType() == int.class);
		assertTrue(validFields[2].getType() == long.class);
		assertTrue(validFields[3].getType() == float.class);
		assertTrue(validFields[4].getType() == double.class);
		assertTrue(validFields[5].getType() == String.class);
		assertTrue(validFields[6].getType().isEnum());
		assertTrue(validFields[7].getType().isArray());
	}

}

package imagingbook.common.ij;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ij.gui.GenericDialog;
import imagingbook.common.ij.DialogUtils.DialogLabel;
import imagingbook.common.image.interpolation.InterpolationMethod;
import imagingbook.common.util.ParameterBundle;


public class DialogUtilsTest {
	
	@SuppressWarnings("unused")
	private static class MyParameterBundle implements ParameterBundle<Object> {
		// valid parameter fields
		
		@DialogLabel("Number of random draws") 
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
		
		GenericDialog gd = new GenericDialog("title");
		DialogUtils.addToDialog(params, gd);
		// --------------------
		params.someBool = false;
		params.someInt = 0;
		params.someLong = 10;
		params.someFloat = 0;
		params.someDouble = 0;
		params.someString = null;
		params.someEnum = null;
		// --------------------
		DialogUtils.getFromDialog(params, gd);
		assertEquals(params.someBool, true);
		assertEquals(params.someInt, 39);
		assertEquals(params.someLong, 10);
		assertEquals(params.someFloat, 1.99f, 1e-6f);
		assertEquals(params.someDouble, Math.PI, 1e-6f);
		assertEquals(params.someString, "SHOW ME");
		assertEquals(params.someEnum, InterpolationMethod.Bicubic);
	}

}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util;

import ij.gui.GenericDialog;
import imagingbook.common.ij.DialogUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Interface to be implemented by local 'Parameters' classes. This is part of the 'simple parameter object' scheme,
 * working with public fields. Only non-static, non-final, public fields are accepted as parameters. Current features
 * include: <br> (a) Makes parameter bundles printable by listing all eligible fields. <br> (b) Parameter bundles can be
 * added/modified as a whole by ImageJ's {@link GenericDialog}, supported by specific annotations (use methods
 * {@link DialogUtils#addToDialog(ParameterBundle, GenericDialog)} and
 * {@link DialogUtils#getFromDialog(ParameterBundle, GenericDialog)}). <br> See the example in {@code DemoParameters}
 * below. Other functionality may be added in the future.
 * </p>
 * <pre>
 * public class ClassToBeParameterized {
 * 	enum MyEnum {  // local enum type
 * 		A, B, Cee
 *    };
 * 	// Sample parameter bundle class:
 * 	static class DemoParameters implements ParameterBundle&lt;ClassToBeParameterized&gt; {
 * 		public static int staticInt = 44; // currently static members are listed too!
 * 		&#64;DialogLabel("Make a decision:")
 * 		public boolean someBool = true;
 * 		public int someInt = 39;
 * 		public float someFloat = 1.99f;
 * 		&#64;DialogLabel("Math.PI")
 * 		&#64;DialogDigits(10)
 * 		public double someDouble = Math.PI;
 * 		public String someString = "SHOW ME";
 * 		&#64;DialogHide
 * 		public String hiddenString = "HIDE ME";
 * 		public MyEnum someEnum = MyEnum.B;
 *    }
 * 	public static void main(String[] args) {
 * 		ParameterBundle params = new DemoParameters();
 * 		System.out.println("p1 = \n" + params.printToString());
 * 		GenericDialog gd = new GenericDialog(ParameterBundle.class.getSimpleName());
 * 		gd.addNumericField("some single int", 123, 0);
 * 		params.addToDialog(gd);
 * 		gd.showDialog();
 * 		if (gd.wasCanceled())
 * 			return;
 * 		int singleInt = (int) gd.getNextNumber();
 * 		boolean success = params.getFromDialog(gd);
 * 		System.out.println("success = " + success);
 * 		System.out.println("p2 = \n" + params.printToString());
 *    }
 * }
 * </pre>
 *
 * @param <TargetT> the target class to be parameterized
 * @author WB
 * @version 2022/11/23 added generic target type
 * @see imagingbook.common.ij.DialogUtils.DialogDigits
 * @see imagingbook.common.ij.DialogUtils.DialogLabel
 * @see imagingbook.common.ij.DialogUtils.DialogHide
 */
public interface ParameterBundle<TargetT> {
	
	/**
	 * Returns the valid parameter fields as an array. 
	 * @return the valid parameter fields
	 */
	default Field[] getValidParameterFields() {
		Class<?> clazz = this.getClass();
		List<Field> validFields = new ArrayList<>();
		for (Field f : clazz.getFields()) {
			if (isValidParameterField(f)) {
				validFields.add(f);
			}
		}
		return validFields.toArray(new Field[0]);
	}

	/**
	 * Substitute for {@link Object#toString()}, which cannot be overridden by an interface's default method.
	 *
	 * @return as string representation of theis parameter bundle
	 */
	default String printToString() {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		try (PrintStream strm = new PrintStream(bas)) {
			printToStream(strm);
		}
		return bas.toString();
	}

	/**
	 * Sends a string representation of this parameter bundle to the specified stream.
	 *
	 * @param strm the output stream
	 */
	default void printToStream(PrintStream strm) {
		Class<?> clazz = this.getClass();
		if (!Modifier.isPublic(clazz.getModifiers())) {
			strm.print("[WARNING] class " + clazz.getSimpleName() + " should be declared public or protected!\n");
		}
		Field[] fields = clazz.getFields();		// gets only public fields
//		strm.println(clazz.getCanonicalName());
		for (Field field : fields) {
//			if (!isValidParameterItem(field)) {
//				continue;
//			}
			strm.print(field.getType().getSimpleName() + " ");
			strm.print(field.getName() + " = ");
			try {
				strm.print(field.get(this).toString());
			} catch (IllegalArgumentException | IllegalAccessException e) {	
				strm.print("FIELD VALUE UNREADABLE!");
			}	
			strm.println();
//			int modifiers = field.getModifiers();
//			strm.println("Field is public = " + Modifier.isPublic(modifiers));
//			strm.println("Field is final = " + Modifier.isFinal(modifiers));
		}
	}

	/**
	 * Validates the correctness and compatibility of the parameters in this bundle. Does nothing by default,
	 * implementing classes should override this method.
	 *
	 * @return true if all parameters are OK, false otherwise
	 */
	default boolean validate() {
		return true;
	}

	/**
	 * Returns true iff the specified field is a valid parameter item. This applies if the field is neither private nor
	 * final or static.
	 *
	 * @param f the field
	 * @return true if a valid parameter field
	 */
	static boolean isValidParameterField(Field f) {
		int mod = f.getModifiers();
		if (Modifier.isPrivate(mod) || Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
			return false;
		}
		else {
			return true;
		}
	}

//	static void printModifiers(Field f) {
//		int mod = f.getModifiers();
//		System.out.println("Modifiers of field " + f.getName());
//		System.out.println("abstract     = " + Modifier.isAbstract(mod));
//		System.out.println("final        = " + Modifier.isFinal(mod));
//		System.out.println("interface    = " + Modifier.isInterface(mod));
//		System.out.println("native       = " + Modifier.isNative(mod));
//		System.out.println("private      = " + Modifier.isPrivate(mod));
//		System.out.println("protected    = " + Modifier.isProtected(mod));
//		System.out.println("public       = " + Modifier.isPublic(mod));
//		System.out.println("static       = " + Modifier.isStatic(mod));
//		System.out.println("strict       = " + Modifier.isStrict(mod));
//		System.out.println("synchronized = " + Modifier.isSynchronized(mod));
//		System.out.println("transient    = " + Modifier.isTransient(mod));
//		System.out.println("volatite     = " + Modifier.isVolatile(mod));
//	}

	
	/**
	 * Returns a shallow copy of the specified {@link ParameterBundle}
	 * instance.
	 * 
	 * @param <T> generic type
	 * @param params a {@link ParameterBundle} instance
	 * @return a copy with the same type, fields and values as the original instance
	 */
	public static <T extends ParameterBundle<?>> T duplicate(T params) {
	    return ObjectUtils.copy(params);
	}

}

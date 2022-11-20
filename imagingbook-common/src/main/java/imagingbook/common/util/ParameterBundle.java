/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import ij.gui.GenericDialog;
import imagingbook.common.ij.DialogUtils;

/**
 * <p>
 * Interface to be implemented by local 'Parameters' classes. This is part of
 * the 'simple parameter object' scheme, working with public fields. Only
 * non-static, non-final, public fields are accepted as parameters.
 * Current features include: 
 * <br>
 * (a) Makes parameter bundles printable by listing all eligible fields.
 * <br>
 * (b) Parameter bundles can be added/modified as a whole by ImageJ's
 * {@link GenericDialog}, supported by specific annotations (use methods
 * {@link DialogUtils#addToDialog(ParameterBundle, GenericDialog)} and
 * {@link DialogUtils#getFromDialog(ParameterBundle, GenericDialog)}).
 * <br>
 * See the example in {@code DemoParameters} below. Other functionality may be
 * added in the future.
 * </p>
 * <pre>
 * // Sample parameter bundle:
 * 
 * enum MyEnum { A, B, Cee };
 * 
 * static class DemoParameters implements ParameterBundle {
 * 	public static int staticInt = 44; // currently static members are listed too!
 * 
 * 	&#64;DialogLabel("Make a decision:")
 * 	public boolean someBool = true;
 * 	public int someInt = 39;
 * 	public float someFloat = 1.99f;
 * 
 * 	&#64;DialogLabel("Math.PI")
 * 	&#64;DialogDigits(10)
 * 	public double someDouble = Math.PI;
 * 	public String someString = "SHOW ME";
 * 
 * 	&#64;DialogHide
 * 	public String hiddenString = "HIDE ME";
 * 	public MyEnum someEnum = MyEnum.B;
 * }
 * 
 * public static void main(String[] args) {
 * 	ParameterBundle params = new DemoParameters();
 * 	System.out.println("p1 = \n" + params.printToString());
 * 
 * 	GenericDialog gd = new GenericDialog(ParameterBundle.class.getSimpleName());
 * 	gd.addNumericField("some single int", 123, 0);
 * 	params.addToDialog(gd);
 * 
 * 	gd.showDialog();
 * 	if (gd.wasCanceled())
 * 		return;
 * 
 * 	int singleInt = (int) gd.getNextNumber();
 * 	boolean success = params.getFromDialog(gd);
 * 	System.out.println("success = " + success);
 * 	System.out.println("p2 = \n" + params.printToString());
 * }
 * </pre>
 * 
 * @author WB
 * @version 2022/02/02
 * @version 2022/09/14 moved annotations and methods for {@link GenericDialog} to {@link DialogUtils}
 * 
 * @see DialogUtils.DialogDigits
 * @see DialogUtils.DialogLabel
 * @see DialogUtils.DialogHide
 */
public interface ParameterBundle {
	
	default Field[] getValidParameterFields() {
		Class<? extends ParameterBundle> clazz = this.getClass();
		List<Field> validFields = new ArrayList<>();
		for (Field f : clazz.getFields()) {
			if (isValidParameterItem(f)) {
				validFields.add(f);
			}
		}
		return validFields.toArray(new Field[0]);
	}
	
	default String printToString() {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		try (PrintStream strm = new PrintStream(bas)) {
			printToStream(strm);
		}
		return bas.toString();
	}

	default void printToStream(PrintStream strm) {
		Class<? extends ParameterBundle> clazz = this.getClass();
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
	 * Validates the correctness and compatibility of the
	 * parameters in this bundle. 
	 * Implementing classes should override this method.
	 * 
	 * @return true if all parameters are OK, false otherwise
	 */
	default boolean validate() {
		return true;
	}
	
	static boolean isValidParameterItem(Field f) {
		int mod = f.getModifiers();
		if (Modifier.isPrivate(mod) || Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
			return false;
		}
		else {
			return true;
		}
//		Class<?> clazz = f.getType();
//		if (clazz == boolean.class || clazz == int.class || clazz == long.class || clazz == float.class || clazz == double.class || 
//			clazz == String.class || clazz.isEnum())
//			return true;
//		else
//			return false;
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
	public static <T extends ParameterBundle> T duplicate(T params) {
	    return ObjectUtils.copy(params);
	}

	// ----------------------------------------------------------------------
	


//	/**
//	 * Example parameter bundle
//	 */
//	static class DemoParameters implements DialogParameters {
//		public static int staticInt = 44;	// currently static members are listed too!
//		
//		@DialogLabel("Make a decision:")
//		public boolean someBool = true;
//		public int someInt = 39;
//		public float someFloat = 1.99f;
//		
//		@DialogLabel("Math.PI")@DialogDigits(10)
//		public double someDouble = Math.PI;
//		public String someString = "SHOW ME";
//		
//		@DialogHide
//		public String hiddenString = "HIDE ME";
//		public MyEnum someEnum = MyEnum.B;
//	}
//	
//	public static void main(String[] args) {
//		
//		ParameterBundle params = new DemoParameters();
//		System.out.println("p1 = \n" + params.printToString());
//		
//		GenericDialog gd = new GenericDialog(ParameterBundle.class.getSimpleName());
//		gd.addNumericField("some single int", 123, 0);
//		params.addToDialog(gd);
//		
//		gd.showDialog();
//		if (gd.wasCanceled())
//			return;
//		
//		@SuppressWarnings("unused")
//		int singleInt = (int) gd.getNextNumber();
//		boolean success = params.getFromDialog(gd);
//		System.out.println("success = " + success);
//		System.out.println("p2 = \n" + params.printToString());
//	}
	
	
	// ---- Dialog-related annotations to be used on individual parameter fields ------
	
	
	
	
	// ------------ ImageJ dialog-related (TODO: move to another interface?) ------------------
	
//	/**
//	 * Adds all qualified fields of this parameter bundle to the specified
//	 * {@link GenericDialog} instance, in the order of their definition.
//	 * Qualified means that the field is of suitable type and no 
//	 * {@link DialogUtils.DialogHide} annotation is present.
//	 * 
//	 * @param gd a generic dialog
//	 */
//	public default void addToDialog(GenericDialog gd) {
//		Class<? extends ParameterBundle> clazz = this.getClass();
//		Field[] fields = clazz.getFields();		// gets only public fields
//		for (Field f : fields) {
//			if (!ParameterBundleXX.isValidParameterItem(f) || f.isAnnotationPresent(DialogUtils.DialogHide.class)) {
//				continue;
//			}
//			try {
//				addFieldToDialog(f, gd);
//			} catch (IllegalArgumentException | IllegalAccessException e) {
//				throw new RuntimeException(e.getMessage());	// TODO: refine exception handling!
//			}
//		}
//	}
//	
//	public default boolean getFromDialog(GenericDialog gd) {
//		Class<? extends ParameterBundle> clazz = this.getClass();
//		Field[] fields = clazz.getFields();		// gets only public fields
//		int errorCount = 0;
//		for (Field f : fields) {
//			if (!ParameterBundleXX.isValidParameterItem(f) || f.isAnnotationPresent(DialogUtils.DialogHide.class)) {
//				continue;
//			}
//			try {
//				if (!getFieldFromDialog(f, gd)) {
//					errorCount++;
//				}
//			} catch (IllegalArgumentException | IllegalAccessException e) {	
//				throw new RuntimeException(e.getMessage()); // TODO: refine exception handling!
//			}
//		}
//		return (errorCount == 0);
//	}
//	
//
//	
//	/**
//	 * Adds the specified {@link Field} of this object as new item to 
//	 * the {@link GenericDialog} instance.
//	 * The name of the field is used as the 'label' of the dialog item.
//	 * TODO: this method should be private (possible in Java9)!
//	 * 
//	 * @param field some field
//	 * @param dialog the dialog
//	 * @throws IllegalAccessException when field is accessed illegally
//	 */
//	default void addFieldToDialog(Field field, GenericDialog dialog)
//			throws IllegalAccessException {
//		
//		String name = field.getName();
//		if (field.isAnnotationPresent(DialogUtils.DialogLabel.class)) {
//			name = field.getAnnotation(DialogUtils.DialogLabel.class).value();
//		}
//		
//		int digits = 2; // DefaultDialogDigits;
//		if (field.isAnnotationPresent(DialogUtils.DialogDigits.class)) {
//			digits = field.getAnnotation(DialogUtils.DialogDigits.class).value();
//			digits = Math.max(0,  digits);
//		}
//		
//		Class<?> clazz = field.getType();
//		if  (clazz.equals(boolean.class)) {
//			dialog.addCheckbox(name, field.getBoolean(this));
//		}
//		else if (clazz.equals(int.class)) {
//			dialog.addNumericField(name, field.getInt(this), 0);
//		}
//		else if (clazz.equals(float.class)) {
//			dialog.addNumericField(name, field.getFloat(this), digits);
//		}
//		else if (clazz.equals(double.class)) {
//			dialog.addNumericField(name, field.getDouble(this), digits);
//		}
//		else if (clazz.equals(String.class)) {
//			String str = (String) field.get(this);
//			dialog.addStringField(name, str);
//		}
//		else if (clazz.isEnum()) {
//			dialog.addEnumChoice(name, (Enum<?>) field.get(this));
//		}
//		else {
//			// ignore this field
//			//throw new RuntimeException("cannot handle field of type " + clazz);
//		}
//	}
//
//	/**
//	 * Modifies the specified {@link Field} of this object by reading the next item
//	 * from the {@link GenericDialog} instance.
//	 * TODO: this method should be private (possible in Java9)!
//	 * 
//	 * @param field	a publicly accessible {@link Field} of this object 
//	 * @param gd a {@link GenericDialog} instance
//	 * @return true if successful
//	 * @throws IllegalAccessException illegal field access
//	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	default boolean getFieldFromDialog(Field field, GenericDialog gd)
//					throws IllegalAccessException {
//		Class<?> clazz = field.getType();
//		if  (clazz.equals(boolean.class)) {
//			field.setBoolean(this, gd.getNextBoolean());
//		}
//		else if (clazz.equals(int.class)) {
//			double val = gd.getNextNumber();
//			if (Double.isNaN(val)) {
//				return false;
//			}
//			field.setInt(this, (int) val);
//		}
//		else if (clazz.equals(float.class)) {
//			double val = gd.getNextNumber();
//			if (Double.isNaN(val)) {
//				return false;
//			}
//			field.setFloat(this, (float) val);
//		}
//		else if (clazz.equals(double.class)) {
//			double val = gd.getNextNumber();
//			if (Double.isNaN(val)) {
//				return false;
//			}
//			field.setDouble(this, val);
//		}
//		else if (clazz.equals(String.class)) {
//			String str = gd.getNextString();
//			if (str == null) {
//				return false;
//			}
//			field.set(this, str);
//		}
//		else if (clazz.isEnum()) {
//			Enum en = gd.getNextEnumChoice((Class<Enum>) clazz);
//			if (en == null) {
//				return false;
//			}
//			field.set(this, en);
////			field.set(instance, gd.getNextEnumChoice((Class<? extends Enum>) clazz));	// works			
////			field.set(instance, gd.getNextEnumChoice((Class<Enum>) clazz));	// works	
//		}
//		else {
//			// ignore this field
//			// throw new RuntimeException("cannot handle field of type " + clazz);
//		}
//		return true;
//	}
	

}

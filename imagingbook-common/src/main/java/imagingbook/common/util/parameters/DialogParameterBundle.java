/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.util.parameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import ij.gui.GenericDialog;

/**
 * <p>
 * This interface adds functionality to {@link ParameterBundle} to
 * interact with ImageJ's {@link GenericDialog}.
 * Current features include: 
 * <br>
 * (a) Makes parameter bundles printable by listing all
 * eligible fields.
 * <br>
 * (b) Parameter bundles can be added/modified as a whole by ImageJ's
 * {@link GenericDialog}, supported by specific annotations (use methods
 * {@link #addToDialog(GenericDialog)} and
 * {@link #getFromDialog(GenericDialog)}).
 * </p>
 * <p>
 * See the example in {@code DemoParameters} below. Other functionality may be
 * added in the future.
 * </p>
 * <pre>
 * // Sample parameter bundle:
 * 
 * enum MyEnum { A, B, Cee };
 * 
 * static class DemoParameters implements DialogParameterBundle {
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
 * 	DialogParameterBundle params = new DemoParameters();
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
 * @version 2022/09/13 split from {@link ParameterBundle}
 * 
 * @see DialogDigits
 * @see DialogLabel
 * @see DialogHide
 */
public interface DialogParameterBundle extends ParameterBundle {
	
	// ---- Dialog-related annotations to be used on individual parameter fields ------
		
	/**
	 * Annotation to specify a specific 'label' (value) to be shown for following
	 * parameter fields. Default label is the variable name.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface DialogLabel {
		public String value();
	}
	
	/**
	 * Annotation to specify the number of digits (value) displayed when showing
	 * numeric values in dialogs.
	 * This annotation has no effect on non-floating-point fields.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface DialogDigits {
		public int value();
	}
	
	/**
	 * Annotation to hide the following parameter field in dialogs.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface DialogHide {
	}
	
	// ------------ ImageJ dialog-related (TODO: move to another interface?) ------------------
	
	/**
	 * Adds all qualified fields of this {@link DialogParameterBundle} to the specified
	 * {@link GenericDialog} instance, in the order of their definition.
	 * Qualified means that the field is of suitable type and no 
	 * {@link DialogHide} annotation is present.
	 * 
	 * @param gd a generic dialog
	 */
	public default void addToDialog(GenericDialog gd) {
		Class<? extends DialogParameterBundle> clazz = this.getClass();
		Field[] fields = clazz.getFields();		// gets only public fields
		for (Field f : fields) {
			if (!isValidParameterItem(f) || f.isAnnotationPresent(DialogHide.class)) {
				continue;
			}
			try {
				addFieldToDialog(f, gd);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e.getMessage());	// TODO: refine exception handling!
			}
		}
	}
	
	/**
	 * Retrieves and sets all fields of this {@link DialogParameterBundle}
	 * by reading the associated values from the specified {@link GenericDialog} instance.
	 * Throws an exception if anything goes wrong.
	 * 
	 * @param gd a {@link GenericDialog} instance
	 * @return true if all fields were successfully read
	 */
	public default boolean getFromDialog(GenericDialog gd) {
		Class<? extends DialogParameterBundle> clazz = this.getClass();
		Field[] fields = clazz.getFields();		// gets only public fields
		int errorCount = 0;
		for (Field f : fields) {
			if (!isValidParameterItem(f) || f.isAnnotationPresent(DialogHide.class)) {
				continue;
			}
			try {
				if (!getFieldFromDialog(f, gd)) {
					errorCount++;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {	
				throw new RuntimeException(e.getMessage()); // TODO: refine exception handling!
			}
		}
		return (errorCount == 0);
	}
	
	/**
	 * Checks if the specified parameter field is of a valid type
	 * to be added to a {@link GenericDialog}.
	 * The only permissible types are {@code boolean}, {@code int},
	 * {@code float}, {@code double}, {@link String}, and {@code enum}.
	 * @param f
	 * @return
	 */
	static boolean isValidParameterItem(Field f) {
		int mod = f.getModifiers();
		if (Modifier.isPrivate(mod) || Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
			return false;
		}
		Class<?> clazz = f.getType();
		if (clazz == boolean.class || clazz == int.class || clazz == float.class || clazz == double.class || 
			clazz == String.class || clazz.isEnum())
			return true;
		else
			return false;
	}

	
	/**
	 * Adds the specified {@link Field} of this object as new item to 
	 * the {@link GenericDialog} instance.
	 * The name of the field is used as the 'label' of the dialog item.
	 * TODO: this method should not be public (possible in Java9)!
	 * 
	 * @param field some field
	 * @param dialog the dialog
	 * @throws IllegalAccessException when field is accessed illegally
	 */
	default void addFieldToDialog(Field field, GenericDialog dialog)
			throws IllegalAccessException {
		
		String name = field.getName();
		if (field.isAnnotationPresent(DialogLabel.class)) {
			name = field.getAnnotation(DialogLabel.class).value();
		}
		
		int digits = 2; // DefaultDialogDigits;
		if (field.isAnnotationPresent(DialogDigits.class)) {
			digits = field.getAnnotation(DialogDigits.class).value();
			digits = Math.max(0,  digits);
		}
		
		Class<?> clazz = field.getType();
		if  (clazz.equals(boolean.class)) {
			dialog.addCheckbox(name, field.getBoolean(this));
		}
		else if (clazz.equals(int.class)) {
			dialog.addNumericField(name, field.getInt(this), 0);
		}
		else if (clazz.equals(float.class)) {
			dialog.addNumericField(name, field.getFloat(this), digits);
		}
		else if (clazz.equals(double.class)) {
			dialog.addNumericField(name, field.getDouble(this), digits);
		}
		else if (clazz.equals(String.class)) {
			String str = (String) field.get(this);
			dialog.addStringField(name, str);
		}
		else if (clazz.isEnum()) {
			dialog.addEnumChoice(name, (Enum<?>) field.get(this));
		}
		else {
			// ignore this field
			//throw new RuntimeException("cannot handle field of type " + clazz);
		}
	}

	/**
	 * Modifies the specified {@link Field} of this object by reading the next item
	 * from the {@link GenericDialog} instance.
	 * TODO: this method should be private (possible in Java9)!
	 * 
	 * @param field	a publicly accessible {@link Field} of this object 
	 * @param gd a {@link GenericDialog} instance
	 * @return true if successful
	 * @throws IllegalAccessException illegal field access
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	default boolean getFieldFromDialog(Field field, GenericDialog gd)
					throws IllegalAccessException {
		Class<?> clazz = field.getType();
		if  (clazz.equals(boolean.class)) {
			field.setBoolean(this, gd.getNextBoolean());
		}
		else if (clazz.equals(int.class)) {
			double val = gd.getNextNumber();
			if (Double.isNaN(val)) {
				return false;
			}
			field.setInt(this, (int) val);
		}
		else if (clazz.equals(float.class)) {
			double val = gd.getNextNumber();
			if (Double.isNaN(val)) {
				return false;
			}
			field.setFloat(this, (float) val);
		}
		else if (clazz.equals(double.class)) {
			double val = gd.getNextNumber();
			if (Double.isNaN(val)) {
				return false;
			}
			field.setDouble(this, val);
		}
		else if (clazz.equals(String.class)) {
			String str = gd.getNextString();
			if (str == null) {
				return false;
			}
			field.set(this, str);
		}
		else if (clazz.isEnum()) {
			Enum en = gd.getNextEnumChoice((Class<Enum>) clazz);
			if (en == null) {
				return false;
			}
			field.set(this, en);
//			field.set(instance, gd.getNextEnumChoice((Class<? extends Enum>) clazz));	// works			
//			field.set(instance, gd.getNextEnumChoice((Class<Enum>) clazz));	// works	
		}
		else {
			// ignore this field
			// throw new RuntimeException("cannot handle field of type " + clazz);
		}
		return true;
	}

}

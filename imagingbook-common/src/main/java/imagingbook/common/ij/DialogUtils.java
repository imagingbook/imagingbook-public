/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.ij;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import imagingbook.common.util.ParameterBundle;
import imagingbook.core.resource.ImageResource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods and annotations related to ImageJ's {@link GenericDialog} class.
 * 
 * @author WB
 * @version 2022/09/14
 */
public abstract class DialogUtils {

    private DialogUtils() {}

	/**
	 * Annotation to specify a specific 'label' (value) to be shown for following parameter fields. Default label is the
	 * variable name. Intended to be used on {@link ParameterBundle} fields.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public static @interface DialogLabel {
		public String value();
	}

	/**
	 * Annotation to specify the number of digits (value) displayed when showing numeric values in dialogs. This
	 * annotation has no effect on non-floating-point fields. Intended to be used on {@link ParameterBundle} fields.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public static @interface DialogDigits {
		public int value();
	}

	/**
	 * Annotation to specify the number of "columns" (value) displayed when showing string items in dialogs. This
	 * annotation has no effect on non-string fields. Intended to be used on {@link ParameterBundle} fields.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public static @interface DialogStringColumns {
		public int value();
	}


	/**
	 * Annotation to hide the following parameter field in dialogs. Intended to be used on {@link ParameterBundle}
	 * fields.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public static @interface DialogHide {
	}

	// -----------------------------------------------------------------------

	private static final char NEWLINE = '\n';
	private static final String SPACE_SEPARATOR = " ";
	//if text has \n, \r or \t symbols it's better to split by \s+
	private static final String SPLIT_REGEXP= "\\s+";

	/**
	 * Splits a long string into multiple lines of the specified maximum length and builds a new string with newline
	 * characters separating successive lines. Multiple input strings are first joined into a single sstring using blank
	 * spaces as separators. Intended mainly to format message texts of plugin dialogs. Inspired by:
	 * https://stackoverflow.com/a/21002193
	 *
	 * @param maxLineLength the maximum number oc characters per line
	 * @param strings one ore mor strings
	 * @return a new string with newline characters separating successive lines
	 */
	public static String splitLines(int maxLineLength, String... strings) {
		if (strings.length == 0) {
			throw new IllegalArgumentException("must pass at least one string");
		}
		String input = String.join(SPACE_SEPARATOR, strings);
		String[] tokens = input.split(SPLIT_REGEXP);
		StringBuilder output = new StringBuilder(input.length());
		int lineLen = 0;
		for (int i = 0; i < tokens.length; i++) {
			String word = tokens[i];

			if (lineLen + (SPACE_SEPARATOR + word).length() > maxLineLength) {
				if (i > 0) {
					output.append(NEWLINE);
				}
				lineLen = 0;
			}
			if (i < tokens.length - 1 && (lineLen + (word + SPACE_SEPARATOR).length() + tokens[i + 1].length() <=
					maxLineLength)) {
				word += SPACE_SEPARATOR;
			}
			output.append(word);
			lineLen += word.length();
		}
		return output.toString();
	}

	/**
	 * Creates a HTML string by formatting the supplied strings as individual text lines separated by {@literal <br>}.
	 * The complete text is wrapped by {@literal <html>...</html>}. Mainly to be used with
	 * {@link GenericDialog#addHelp(String)}.
	 *
	 * @param lines a sequence of strings interpreted as text lines
	 * @return a HTML string
	 */
	public static String makeHtmlString(CharSequence... lines) {
		return "<html>\n" + String.join("<br>\n", lines) + "\n</html>";
	}

	/**
	 * Creates a string by formatting the supplied strings as individual text lines separated by newline. Mainly to be
	 * used with {@link GenericDialog#addMessage(String)}.
	 *
	 * @param lines a sequence of strings interpreted as text lines
	 * @return a newline-separated string
	 */
	public static String makeLineSeparatedString(CharSequence... lines) {
		return String.join("\n", lines);
	}
	
	// ------------ Methods related to ParameterBundle  ------------------
	
	static Field[] getDialogFields(ParameterBundle<?> params) {
		Class<?> clazz = params.getClass();
		List<Field> dialogFields = new ArrayList<>();
		for (Field f : clazz.getFields()) {
			if (isValidDialogField(f)) {
				dialogFields.add(f);
			}
		}
		return dialogFields.toArray(new Field[0]);
	}

	/**
	 * Adds all qualified fields of the given {@link ParameterBundle} to the specified {@link GenericDialog} instance,
	 * in the exact order of their definition. Qualified means that the field is of suitable type and no
	 * {@link DialogUtils.DialogHide} annotation is present. Allowed field types are {@code boolean}, {@code int},
	 * {@code long}, {@code float}, {@code double}, {@code enum}, and {@code String}.
	 *
	 * @param params a {@link ParameterBundle} instance
	 * @param gd a generic dialog
	 */
	public static void addToDialog(ParameterBundle<?> params, GenericDialog gd) {
		Field[] dialogFields = getDialogFields(params);		// gets only public fields
		for (Field f : dialogFields) {
			if (!isValidDialogField(f) || f.isAnnotationPresent(DialogHide.class)) {
				continue;
			}
			try {
				addFieldToDialog(params, f, gd);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e.getMessage());	// TODO: refine exception handling!
			}
		}
	}

	/**
	 * Retrieves the field values of the specified {@link ParameterBundle} from the {@link GenericDialog} instance. The
	 * {@link ParameterBundle} is modified. Throws an exception if anything goes wrong.
	 *
	 * @param params a {@link ParameterBundle} instance
	 * @param gd a generic dialog
	 * @return true if successful
	 */
	public static boolean getFromDialog(ParameterBundle<?> params, GenericDialog gd) {
		Class<?> clazz = params.getClass();
		Field[] fields = clazz.getFields();		// gets only public fields
		int errorCount = 0;
		for (Field f : fields) {
			if (!isValidDialogField(f) || f.isAnnotationPresent(DialogHide.class)) {
				continue;
			}
			try {
				if (!getFieldFromDialog(params, f, gd)) {
					errorCount++;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {	
				throw new RuntimeException(e.getMessage()); // TODO: refine exception handling!
			}
		}
		return (errorCount == 0);
	}

	/**
	 * Adds the specified {@link Field} of this object as new item to the {@link GenericDialog} instance. The name of
	 * the field is used as the 'label' of the dialog item unless a {@link DialogLabel} annotation is present. Allowed
	 * field types are {@code boolean}, {@code int}, {@code float}, {@code double}, {@code enum}, and {@code String}.
	 *
	 * @param params a {@link ParameterBundle} instance
	 * @param field some field
	 * @param dialog the dialog
	 * @throws IllegalAccessException when field is accessed illegally
	 */
	private static void addFieldToDialog(ParameterBundle<?> params, Field field, GenericDialog dialog)
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
		
		int stringColumns = 8;
		if (field.isAnnotationPresent(DialogStringColumns.class)) {
			stringColumns = 
					Math.max(stringColumns, field.getAnnotation(DialogStringColumns.class).value());
		}
		
		Class<?> clazz = field.getType();
		if  (clazz.equals(boolean.class)) {
			dialog.addCheckbox(name, field.getBoolean(params));
		}
		else if (clazz.equals(int.class)) {
			dialog.addNumericField(name, field.getInt(params), 0);
		}
		else if (clazz.equals(long.class)) {
			dialog.addNumericField(name, field.getLong(params), 0);
		}
		else if (clazz.equals(float.class)) {
			dialog.addNumericField(name, field.getFloat(params), digits);
		}
		else if (clazz.equals(double.class)) {
			dialog.addNumericField(name, field.getDouble(params), digits);
		}
		else if (clazz.equals(String.class)) {
			String str = (String) field.get(params);
			dialog.addStringField(name, str, stringColumns);
		}
		else if (clazz.isEnum()) {
			dialog.addEnumChoice(name, (Enum<?>) field.get(params));
		}
		else {
			// ignore this field
			//throw new RuntimeException("cannot handle field of type " + clazz);
		}
	}

	/**
	 * Modifies the specified {@link Field} of this object by reading the next item from the {@link GenericDialog}
	 * instance.
	 *
	 * @param params a {@link ParameterBundle} instance
	 * @param field a publicly accessible {@link Field} of this object
	 * @param gd a {@link GenericDialog} instance
	 * @return true if successful
	 * @throws IllegalAccessException illegal field access
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean getFieldFromDialog(ParameterBundle params, Field field, GenericDialog gd)
					throws IllegalAccessException {
		Class<?> clazz = field.getType();
		if  (clazz.equals(boolean.class)) {
			field.setBoolean(params, gd.getNextBoolean());
		}
		else if (clazz.equals(int.class)) {
			double val = gd.getNextNumber();
			if (Double.isNaN(val)) {
				return false;
			}
			field.setInt(params, (int) val);
		}
		else if (clazz.equals(long.class)) {
			double val = gd.getNextNumber();
			if (Double.isNaN(val)) {
				return false;
			}
			field.setLong(params, (long) val);
		}
		else if (clazz.equals(float.class)) {
			double val = gd.getNextNumber();
			if (Double.isNaN(val)) {
				return false;
			}
			field.setFloat(params, (float) val);
		}
		else if (clazz.equals(double.class)) {
			double val = gd.getNextNumber();
			if (Double.isNaN(val)) {
				return false;
			}
			field.setDouble(params, val);
		}
		else if (clazz.equals(String.class)) {
			String str = gd.getNextString();
			if (str == null) {
				return false;
			}
			field.set(params, str);
		}
		else if (clazz.isEnum()) {
			Enum en = gd.getNextEnumChoice((Class<Enum>) clazz);
			if (en == null) {
				return false;
			}
			field.set(params, en);
//			field.set(instance, gd.getNextEnumChoice((Class<? extends Enum>) clazz));	// works			
//			field.set(instance, gd.getNextEnumChoice((Class<Enum>) clazz));	// works	
		}
		else {
			// ignore this field
			// throw new RuntimeException("cannot handle field of type " + clazz);
		}
		return true;
	}
	
	private static boolean isValidDialogField(Field f) {
		if (!ParameterBundle.isValidParameterField(f)) {
			return false;
		}
//		int mod = f.getModifiers();
//		if (Modifier.isPrivate(mod) || Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
//			return false;
//		}
		// accept only certain field types in dialogs:
		Class<?> clazz = f.getType();
		return (clazz == boolean.class || clazz == int.class || clazz == long.class || 
				clazz == float.class   || clazz == double.class || 
				clazz == String.class  || clazz.isEnum());
	}
	
	// various static methods for simple dialogs -------------------------------
	
//	/**
//	 * Values that may be returned by dialog methods, to be used in
//	 * switch clauses.
//	 */
//	public enum DialogResponse {
//		Yes, No, Cancel;
//	}
//	
//	public static boolean isYes(DialogResponse response) {
//		return response.equals(DialogResponse.Yes);
//	}

	/**
	 * Opens a simple dialog with the specified title and message that allows only a "Yes" or "Cancel" response.
	 *
	 * @param title the text displayed in the dialog's title bar
	 * @param message the dialog message (may be multiple lines separated by newlines)
	 * @return true if "yes" was selected, false otherwise
	 */
	@Deprecated
	public static boolean askYesOrCancel(String title, String message) {
		GenericDialog gd = new GenericDialog(title);
		gd.addMessage(message);
		gd.enableYesNoCancel("Yes", "Cancel");
		gd.hideCancelButton();
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		return gd.wasOKed();
	}

	/**
	 * Opens a very specific dialog asking if the suggested sample image (resource) should be opened and made the active
	 * image. If the answer is YES, the suggested image is opened, otherwise not. This if typically used in the
	 * (otherwise empty) constructor of demo plugins when no (or no suitable) image is currently open.
	 *
	 * @param suggested a sample image ({@link ImageResource})
	 * @return true if user accepted
	 */
	public static boolean askForSampleImage(ImageResource suggested) {	// TODO: allow multiple sample images?
		String title = "Open sample image";
		String message = "No image is currently open.\nUse a sample image?\n";
		GenericDialog gd = new GenericDialog(title);
		gd.setInsets(0, 0, 0);
		gd.addMessage(message);

		if (suggested != null) {
			gd.setInsets(10, 10, 10);
			gd.addImage(suggested.getImageIcon());
		}

		gd.enableYesNoCancel("Yes", "Cancel");
		gd.hideCancelButton();
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}

		boolean ok = gd.wasOKed();
		if (ok && suggested != null) {
			ImagePlus im = suggested.getImagePlus();
			im.show();
			WindowManager.setCurrentWindow(im.getWindow());
		}
		return ok;
	}
	
	public static boolean askForSampleImage() {
		return askForSampleImage(null);
	}

	
	// ----------------------------------------------------
	
//	public static void main(String[] args) {
//		String html = makeHtmlString(
//	            "Get busy living or",
//	            "get busy dying.",
//	            "--Stephen King");
//		System.out.println(html);
//		
//		System.out.println();
//		
//		String lines = makeLineSeparatedString(
//	            "Get busy living or",
//	            "get busy dying.",
//	            "--Stephen King");
//		System.out.println(lines);
//	}

	// -------------------------------------------------------------

	// static String input1 =
	// 		"THESE TERMS AND CONDITIONS OF SERVICE (the Terms) ARE A     LEGAL AND BINDING " +
	// 				"AGREEMENT BETWEEN YOU AND NATIONAL GEOGRAPHIC governing     your use of this site, " +
	// 				"www.nationalgeographic.com, which includes but is not limited to products, " +
	// 				"software and services offered by way of the website such as the Video Player.";
	//
	// static String[] input2 = {
	// 		"THESE TERMS AND CONDITIONS OF SERVICE (the Terms) ARE A     LEGAL AND BINDING",
	// 		"AGREEMENT BETWEEN YOU AND NATIONAL GEOGRAPHIC governing     your use of this site,",
	// 		"www.nationalgeographic.com, which includes but is not limited to products,",
	// 		"software and services offered by way of the website such as the Video Player."};
	//
	//
	// public static void main(String[] args) {
	// 	// System.out.println(splitLines(20, input1));
	// 	System.out.println(DialogUtils.splitLines(20, input2));
	// }

}

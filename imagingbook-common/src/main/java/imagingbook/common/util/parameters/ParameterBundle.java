package imagingbook.common.util.parameters;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import imagingbook.common.util.ObjectUtils;


/**
 * Interface to be implemented by local 'Parameters' classes. This is part of
 * the 'simple parameter object' scheme, working with public fields. Only
 * non-static, non-final, public fields are accepted as parameters.
 * 
 * @author WB
 * @version 2022/02/02
 * @version 2022/09/13 moved dialog-related methods to {@link DialogParameterBundle}
 */
public interface ParameterBundle {
	
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
	 * Returns a shallow copy of the specified {@link DialogParameterBundle}
	 * instance.
	 * 
	 * @param <T> generic type
	 * @param params a {@link DialogParameterBundle} instance
	 * @return a copy with the same type, fields and values as the original instance
	 */
	public static <T extends DialogParameterBundle> T duplicate(T params) {
	    return ObjectUtils.copy(params);
	}

	// ----------------------------------------------------------------------
	


//	/**
//	 * Example parameter bundle
//	 */
//	static class DemoParameters implements ParameterBundle {
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

	
}

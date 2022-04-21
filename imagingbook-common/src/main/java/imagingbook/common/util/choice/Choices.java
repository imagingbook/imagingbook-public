package imagingbook.common.util.choice;

import java.util.LinkedList;
import java.util.List;

/**
 * This is the core of a scheme to collect and instantiate dedicated subclasses
 * ("choices") of a given class or interface.
 * <p>
 * The <strong>traditional</strong> scheme (see {@link ExampleTraditional}) requires the following steps:
 * </p>
 * <ol>
 * <li>Define a superclass (or interface) with a set of nested, concrete
 * subclasses that are to be instantiated after being chosen by the user.</li>
 * <li>Available choices are specified by an {@code enum} type, which maps to
 * the associated subclasses.</li>
 * <li>Dispatching (i.e., dynamic instantiation of choices) is done by a switch
 * over all enum-values or by the enum elements themselves.</li>
 * </ol>
 * <p>
 * The <strong>proposed scheme</strong> (see {@link ExampleWithChoices}) works as follows:
 * <ol>
 * <li>Define a superclass (or interface) with a set of nested, concrete
 * subclasses that are to be instantiated after being chosen by the user.</li>
 * <li>Selected subclasses are marked with a {@link DeclareChoice} annotation. To
 * be accepted as a choice, a subclass must fulfill the following requirements:
 * <ul>
 * <li>The subclass definition must be {@code static}, so it can be instantiated
 * without a containing class instance.</li>
 * <li>The subclass must extend its containing class.</li>
 * <li>The subclass must carry a {@link DeclareChoice} annotation. A runtime
 * exception may occur if a subclass is annotated but does not satisfy the above
 * requirements.
 * </ul>
 * </li>
 * <li>Static methods defined in {@link Choices} allow listing and instantiation
 * of the defined choices in a given containing class. There is no need for a
 * separate {@code enum} type.</li>
 * </ol>
 * <p>
 * A descriptive name ({@code String}) for the choice item may be passed to the
 * {@link DeclareChoice} annotation, which my be retrieved at runtime with
 * {@link #getChoiceName(Class)}.
 * </p>
 * <p>
 * This is defined as an interface to be implemented by enclosing classes
 * (with additional functionality) in the future.
 * Note that this code is still experimental!
 * </p>
 * <p>
 * See {@link ExampleWithChoices} for a simple usage example:
 * </p>
 * 
 * @author WB
 * @version 2020/11/03
 *
 */
public interface Choices {
	
	/**
	 * Searches the specified class or interface for inner classes marked
	 * as 'choices' by a {@link DeclareChoice} annotation.
	 * An array of subclasses is returned, which may be empty.
	 * 
	 * @param <E> the type of the containing class (or interface)
	 * @param outerClass the class (or interface) containing the choice subclasses
	 * @return array of subclasses, which may be empty
	 */
	public static <E> Class<? extends E>[] getChoices(Class<E> outerClass) {
		@SuppressWarnings("unchecked")
		Class<? extends E>[] subclasses = (Class<? extends E>[]) outerClass.getDeclaredClasses();
		List<Class<? extends E>> choices = new LinkedList<>();
		
		for (Class<? extends E> sc : subclasses) {
			if (isChoice(outerClass, sc)) {
				choices.add(sc);
			}
		}
		
		@SuppressWarnings("unchecked")
		Class<? extends E>[] arr = (Class<? extends E>[]) new Class<?>[choices.size()];
		return choices.toArray(arr);
	}
	
	/**
	 * Checks if the specified class is a choice class.
	 * 
	 * @param choiceClass the potential choice class
	 * @return true if a choice class
	 */
	public static boolean isChoice(Class<?> choiceClass) {
		Class<?> enclosingClass = choiceClass.getEnclosingClass();
		if (enclosingClass == null) {	// there is no enclosing class
			return false;
		}
		else
			return isChoice(enclosingClass, choiceClass);
	}
	
	/**
	 * Checks if the specified class is a choice class within the given
	 * enclosing class.
	 * 
	 * @param enclosingClass the enclosing class
	 * @param choiceClass the potential (inner) choice class
	 * @return true if a choice class
	 */
	public static boolean isChoice(Class<?> enclosingClass, Class<?> choiceClass) {
		return
			choiceClass.isAnnotationPresent(DeclareChoice.class) &&
			choiceClass.isMemberClass() && 
			enclosingClass.isAssignableFrom(choiceClass) &&
			hasConstructor(choiceClass);
	}
	
	static <E> boolean hasConstructor(Class<E> clazz) {
		boolean hasConstr = false;
		try {
			clazz.getConstructor();
			hasConstr = true;
		} catch (NoSuchMethodException | SecurityException e) { }
		if (!hasConstr) {
			throw new RuntimeException(String.format(
					"getChoices: class %s has no constructor, must be declared static!", clazz.getName()));
		}
		return hasConstr;
	}
	
	// ---------------------------------------------------------------------------
	
	/**
	 * Returns the name of a specified single choice class. The result is its simple class name or
	 * the descriptive name specified by the associated {@link DeclareChoice} annotation.
	 * Throws a runtime exception if the specified class is not a choice class.
	 * 
	 * @param <E> the type of the choice class
	 * @param choiceClass the choice class
	 * @return the (optional) descriptive name specified by the associated {@link DeclareChoice} annotation,
	 * otherwise the simple name of the class. 
	 */
	public static <E> String getChoiceName(Class<E> choiceClass) {
		DeclareChoice annot = choiceClass.getAnnotation(DeclareChoice.class);
		if (annot == null) {
			throw new IllegalArgumentException("class " + choiceClass.getSimpleName() + " is no valid choice");
//			return null;
		}
		String annotValue = annot.value();
		if (annotValue.isEmpty()) {
			return choiceClass.getSimpleName();
		}
		else {
			return annotValue;
		}
	}
	
	/**
	 * Returns an array of choice names (see {@link #getChoiceName(Class)})
	 * for the specified containing class.
	 * 
	 * @param <E> the generic type of the containing class
	 * @param outerClass the choice class
	 * @return an array of choice names
	 */
	public static <E> String[] getChoiceNames(Class<E> outerClass) {
		Class<? extends E>[] choices = getChoices(outerClass);
		String[] names = new String[choices.length];
		for (int i = 0; i < choices.length; i++) {
			//names[i] = choices[i].getSimpleName();
			names[i] = getChoiceName(choices[i]);
		}
		return names;
	}
	
	// --------------------------------------------------------
	
	public static <E> Class<? extends E> getChoiceForName(Class<E> outerClass, String name) {
		Class<? extends E>[] choices = getChoices(outerClass);
		Class<? extends E> choice = null;
		for (int i = 0; i < choices.length; i++) {
			if (name.equals(choices[i].getSimpleName())) {
				choice = choices[i];
				break;
			}
		}
		if (choice == null) {
			throw new IllegalArgumentException("class " + outerClass.getSimpleName() + " defines no choice " + name);
		}
		return choice;
	}
	
	
	/**
	 * Creates and returns a new instance of the specified choice class.
	 * Throws a runtime exception if the specified class is not a choice class.
	 * 
	 * @param <E> the generic type of the enclosing class
	 * @param choiceClass the choice class
	 * @return a new instance 
	 */
	public static <E> E getInstance(Class<? extends E> choiceClass) { 
		E instance = null;
		try {
			instance = choiceClass.getDeclaredConstructor().newInstance(); //choiceClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}

}

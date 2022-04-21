package imagingbook.common.util.choice;

/**
 * A usage example for the mechanism provided by the {@link Choices} interface.
 * The enclosing class (or interface) contains concrete inner classes
 * that are marked as choices by the {@link DeclareChoice} annotation.
 * The inner classes must be static and must extend the enclosing class.
 * @author WB
 *
 */
public abstract class ExampleWithChoices implements Choices {
	
	// Concrete subclasses/choices to be instantiated:
	
	@DeclareChoice
	public static class ChoiceA extends ExampleWithChoices {
		// ...
	}
	@DeclareChoice("I love to be chosen")
	public static class ChoiceB extends ExampleWithChoices {
		// ...
	}
	
	@DeclareChoice("The Choice C")
	public static class ChoiceC extends ExampleWithChoices {
		// ...
	}
	
	// -----------------------------------------------------------------
	
	public static void main(String[] args) {
		for (Class<? extends ExampleWithChoices> c : Choices.getChoices(ExampleWithChoices.class)) {
			System.out.println("choice = " + c.getSimpleName() + " / " + Choices.getChoiceName(c));
			
			ExampleWithChoices instance = Choices.getInstance(c);
			System.out.println("instance = " + instance.getClass().getName());
			// do something more with instance ...
		}
	}

}

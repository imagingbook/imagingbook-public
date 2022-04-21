package imagingbook.common.util.choice;

// Superclass or interface
public abstract class ExampleTraditional {
	
	// Choices for the user to select
	public enum Choice {
		A, B, C;
	}
	
	// Dispatcher to instantiate choices
	public static ExampleTraditional getInstance(Choice m) {
		ExampleTraditional instance = null;
		switch(m) {
		case A:
			instance = new ChoiceA(); break;
		case B:
			instance = new ChoiceB(); break;
		case C:
			instance = new ChoiceC(); break;
		}
		return instance;
	}
	
	// Concrete subclasses to be instantiated:
	
	public static class ChoiceA extends ExampleTraditional {
		// ...
	}
	
	public static class ChoiceB extends ExampleTraditional {
		// ...
	}
	
	public static class ChoiceC extends ExampleTraditional {
		// ...
	}
	
	// -----------------------------------------------------------------
	
	public static void main(String[] args) {
		for (Choice c : ExampleTraditional.Choice.values()) {
			ExampleTraditional instance = ExampleTraditional.getInstance(c);
			System.out.println("instance = " + instance.getClass().getName());
			// do something with instance ...
		}
	}

}

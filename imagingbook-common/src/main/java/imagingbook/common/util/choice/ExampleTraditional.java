/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
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

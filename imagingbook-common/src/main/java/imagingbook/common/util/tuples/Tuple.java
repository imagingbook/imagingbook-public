/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util.tuples;

/**
 * <p>
 * Elementary implementation of tuples, i.e, ordered sequences
 * of items of arbitrary types.
 * Tuples are generally immutable and their main use is to get methods
 * return multiple values with maximum compile-time safety.
 * A concrete tuple class is defined for
 * each "arity", e.g., {@link Tuple2} for 2 elements.
 * Currently tuple classes for 2, 3 and 4 elements are defined.
 * </p>
 * <p>
 * A tuple may be instantiated using either the class constructor, e.g.,
 * </p>
 * <pre>new Tuple2&lt;Integer, String&gt;(10, "Foo")</pre>
 * <p>
 * or the associated static method
 * </p>
 * <pre>Tuple2.of(10, "Foo")</pre>
 * Individual tuple elements can be accessed (read-only) by the associated
 * field names, e.g. {@code f0}, {@code f1} ... etc.
 * There are no getter methods.
 * @author WB
 *
 */
public interface Tuple {
	
	
	// ------------------------------------------------------------------------
	
//	public static void main(String[] args) {
//		Tuple2<Integer, String> tA = new Tuple2<>(10, "Foo");
//		Tuple tB = new Tuple2<Integer, String>(-3, "Bar");
//		Tuple tC = Tuple2.from(17, "Kaputnik");
//		System.out.println("tA = " + tA.toString());
//		System.out.println("tB = " + tB.toString());
//		System.out.println("tC = " + tC.toString());
//		
//		int k = tA.get0();
//		String s = tA.get1();
//		
//		System.out.println("tA.f0 = " + tA.get0().toString());
//		System.out.println("tA.f1 = " + tA.get1().toString());
//		
//		System.out.println("tA class = " + tA.getClass());
//		System.out.println("tB class = " + tB.getClass());
//		System.out.println("tC class = " + tC.getClass());
//	}
}



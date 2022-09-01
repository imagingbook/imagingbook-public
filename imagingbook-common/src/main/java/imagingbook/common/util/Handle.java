/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.util;

/**
 * <p>
 * An elementary "handle" mechanism to facilitate indirect (two-stage)
 * references to objects. The main purpose is to allow true "calls by
 * reference", as shown in the example below.
 * </p>
 * <pre>
 * private static void foo(Handle&gt;Intege&lt; h) {
 * 	h.set(33);
 * }
 * 
 * public static void main(String[] args) {
 * 	Handle&gt;Integer&lt; a1 = Handle.of(10);
 * 	Handle&gt;Integer&lt; a2 = Handle.&gt;Integer&lt;of(10);
 * 
 * 	Handle&gt;Double&lt; b = new Handle&gt;&lt;(10.0);
 * 	Handle&gt;String&lt; s = Handle.of("prima!");
 * 
 * 	System.out.println("orig a = " + a1.get());
 * 
 * 	// Example: call by reference (foo() modifies the contents of a1)
 * 	foo(a1);
 * 	System.out.println("modified a = " + a1.get());
 * }
 * </pre>
 * 
 * 
 * @author WB
 *
 * @param <T> the element type of this handle
 */
public class Handle<T> {
	
	private T obj;
	
	/**
	 * Constructor. Creates a {@link Handle} with a reference to the specified 
	 * object.
	 * 
	 * @param obj the object the handle should reference
	 */
	public Handle(T obj) {
		this.obj = obj;
	}
	
	/**
	 * Returns the object referenced by this handle.
	 * @return the referenced object
	 */
	public T get() {
		return this.obj;
	}
	
	/**
	 * Sets (replaces) the object referenced by this handle.
	 * @param obj the referenced object
	 */
	public void set(T obj) {
		this.obj = obj;
	}
	
	/**
	 * Static convenience method. Creates a {@link Handle} with a reference to the specified 
	 * object.
	 * @param <T> type parameter
	 * @param obj the object referenced
	 * @return a new handle
	 */
	public static <T> Handle<T> of(T obj) {
		return new Handle<T>(obj);
	}
	
	// ---------------------------------------------
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Handle<Integer> a1 = Handle.<Integer>of(10);
		Handle<Integer> a2 = Handle.of(15);
		
		Handle<Double> b = new Handle<>(10.0);
		Handle<String> s = Handle.of("prima!");
		
		System.out.println("orig a = " + a1.get());
		
		// Example: call by reference (foo modifies the contents of a1)
		foo(a1);
		System.out.println("modified a = " + a1.get());
	}
	
	// call by reference
	private static void foo(Handle<Integer> h) {
		h.set(33);
	}

}

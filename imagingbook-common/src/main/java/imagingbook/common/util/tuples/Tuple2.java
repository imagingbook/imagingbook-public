/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.util.tuples;

/**
 * A tuple with exactly 2 elements of arbitrary types.
 *
 * @param <T0> the type of element 0
 * @param <T1> the type of element 1
 */
public final class Tuple2<T0, T1> implements Tuple {
	
	private final T0 item0;
	private final T1 item1;
	
	public Tuple2(T0 item0, T1 item1) {
		this.item0 = item0;
		this.item1 = item1;
	}
	
	public T0 get0() {
		return item0;
	}
	
	public T1 get1() {
		return item1;
	}
	
//	// needed??
//	public Tuple2(Tuple2<? extends T0, ? extends T1> entry) {
//        this(entry.item0, entry.item1);
//    }
	
	@Override
	public String toString() {
		return String.format("<%s,%s>", item0.toString(), item1.toString());
	}

	public static <T0, T1> Tuple2<T0, T1> from(T0 val0, T1 val1) {
		return new Tuple2<>(val0, val1);
	}
	
}



package imagingbook.common.tuples;

/**
 * A tuple with exactly 4 elements of arbitrary types.
 *
 * @param <T0> the type of element 0
 * @param <T1> the type of element 1
 * @param <T2> the type of element 2
 * @param <T3> the type of element 3
 */
public final class Tuple4<T0, T1, T2, T3> implements Tuple {
	
	public final T0 item0;
	public final T1 item1;
	public final T2 item2;
	public final T3 item3;
	
	public Tuple4(T0 item0, T1 item1, T2 item2, T3 item3) {
		this.item0 = item0;
		this.item1 = item1;
		this.item2 = item2;
		this.item3 = item3;
	}
	
	@Override
	public String toString() {
		return String.format("<%s,%s,%s,%s>", item0.toString(), item1.toString(), item2.toString(), item3.toString());
	}

	public static <T0, T1, T2, T3> Tuple4<T0, T1, T2, T3> from(T0 val0, T1 val1, T2 val2, T3 val3) {
		return new Tuple4<>(val0, val1, val2, val3);
	}
	
}

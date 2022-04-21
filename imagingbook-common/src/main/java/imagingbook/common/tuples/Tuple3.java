package imagingbook.common.tuples;

/**
 * A tuple with exactly 3 elements of arbitrary types.
 *
 * @param <T0> the type of element 0
 * @param <T1> the type of element 1
 * @param <T2> the type of element 2
 */
public final class Tuple3<T0, T1, T2> implements Tuple {
	
	public final T0 item0;
	public final T1 item1;
	public final T2 item2;
	
	public Tuple3(T0 item0, T1 item1, T2 item2) {
		this.item0 = item0;
		this.item1 = item1;
		this.item2 = item2;
	}
	
	@Override
	public String toString() {
		return String.format("<%s,%s,%s>", item0.toString(), item1.toString(), item2.toString());
	}

	public static <T0, T1, T2> Tuple3<T0, T1, T2> from(T0 val0, T1 val1, T2 val2) {
		return new Tuple3<>(val0, val1, val2);
	}
	
}

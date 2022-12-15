/*
 *  This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge.
 * All rights reserved. Visit https://imagingbook.com for additional details.
 */
package imagingbook.common.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Defines a generic vector of comparable elements that allows sorted insertions without changing exceeding its
 * predefined capacity. This implementation is based on {@link PriorityQueue}. New items are inserted using the elements
 * natural ordering or some supplied comparator. Once the predefined capacity is exceeded, redundant items are removed
 * from the underlying priority queue.
 * Example:
 * <pre>
 *     SortedVector<Integer> sv = new SortedVector<>(new Integer[3]);
 *     sv.insert(5);
 *     sv.insert(2);
 *     sv.insert(9);
 *     sv.insert(1);
 *     Integer[] arr = sv.getArray();       // [2, 5, 9]
 * </pre>
 *
 * @param <T> the generic element type
 * @author WB
 * @version 2022/12/14
 */
public class SortedVector<T extends Comparable<T>> {    // extends PriorityQueue<T>

    private final int capacity;
    private final  Comparator<T> comp;
    private final T[] arr;
    private final PriorityQueue<T> queue;

    /**
     * Constructor using a specific comparator. The length of the supplied array specifies the capacity of this
     * container.
     *
     * @param arr an array of the generic element type (content is ignored)
     * @param comp
     */
    public SortedVector(T[] arr, Comparator<T> comp) {
        //super(comp);
        this.queue = new PriorityQueue<T>(comp);
        this.comp = comp;
        if (arr.length < 1) {
            throw new IllegalArgumentException("array must be non-empty");
        }
        this.capacity = arr.length;
        this.arr = arr;
        Arrays.fill(this.arr, null);
    }

    /**
     * Constructor, using the natural-order comparator of the generic element type. The length of the supplied array
     * specifies the capacity of this container.
     *
     * @param arr an array of the generic element type (content is ignored)
     */
    public SortedVector(T[] arr) {
        this(arr, Comparator.naturalOrder());
    }

    /**
     * Insert an item into this {@link SortedVector} instance. It is inserted into the vector if it is "greater than"
     * the first element (head) of this vector, otherwise it is discarded.
     *
     * @param item
     */
    public void insert(T item) {
        T head = queue.peek();
        // System.out.println("adding " + item + "  head = " + head);
        if (head != null &&  comp.compare(item, head) < 0) { // item is smaller than head (= min.)
            return;
        }
        queue.add(item);
        if (queue.size() > capacity) {
            queue.poll();    // remove "smallest" item after insertion
        }
    }

    /**
     * Returns an array holding the elements of this {@link SortedVector} instance. The elements are sorted according to
     * the specified comparator. The length of the array is equal to the value returned by {@link #size()}; the maximum
     * length equals the capacity of this vector. The array contains no {@code null} elements.
     *
     * @return a sorted array of elements
     */
    public T[] getArray() {
        T[] arr2 = queue.toArray(arr);                   // arr = arr2
        T[] arr3 = Arrays.copyOf(arr2, queue.size());    // trim null elements
        // we need to sort once more since PriorityQueue#toArray() does not guarantee any order!
        Arrays.sort(arr3, comp);
        return arr3;
    }

    public int size() {
        return queue.size();
    }

    @Override
    public String toString() {
        return "   "  + Arrays.toString(this.getArray());
    }

}

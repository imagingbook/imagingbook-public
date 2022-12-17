/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * Defines a generic container for "comparable" elements that allows sorted insertions without exceeding the predefined
 * capacity. This implementation is based on {@link PriorityQueue}. New items are inserted using the elements natural
 * ordering or a supplied comparator. Once the predefined capacity is exceeded, redundant items are removed from the
 * underlying priority queue. Example (using natural ordering of Integer):
 * <pre>
 *     SortedVector&lt;Integer&gt; sv = new SortedVector&lt;&gt;(new Integer[3]);
 *     sv.insert(5);
 *     sv.insert(2);
 *     sv.insert(9);
 *     sv.insert(1);
 *     Integer[] arr = sv.getArray();       // [9, 5, 2]
 * </pre>
 *
 * @param <T> the generic element type
 * @author WB
 * @version 2022/12/14
 */
public class SortedVector<T extends Comparable<T>> {    // extends PriorityQueue<T>

    private final int capacity;
    private final PriorityQueue<T> queue;
    private final  Comparator<T> comp;
    private final T[] arr;

    /**
     * Constructor using a specific comparator. The length of the supplied array specifies the capacity of the
     * new container.
     *
     * @param arr an array of the generic element type (content is ignored)
     * @param comp comparator
     */
    public SortedVector(T[] arr, Comparator<T> comp) {
        if (arr.length < 1) {
            throw new IllegalArgumentException("supplied array must be non-empty");
        }
        this.capacity = arr.length;
        this.queue = new PriorityQueue<T>(capacity, comp);
        this.comp = comp;
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
     * Tries to adds a new item to this {@link SortedVector} instance. It is inserted into the vector if it is "greater"
     * than the current "smallest" element (head) in this vector, otherwise it is discarded. The meaning of "greater"
     * and "smallest" depend on the associated comparator (see {@link #SortedVector(Comparable[], Comparator)}).
     * Items contained in the vector are not replaced by new items with identical values.
     *
     * @param item to add
     */
    public void add(T item) {
        if (Objects.isNull(item)) {
            throw new IllegalArgumentException("cannot add null item");
        }
        // System.out.println("     head = " + queue.peek());
        // case1: queue is not yet full, add item without trimming the queue:
        if (queue.size() < capacity) {
            queue.add(item);
            return;
        }
        // case2: queue is full, add item only if greater than head
        T head = queue.peek();                  // head should never be null

        if (comp.compare(item, head) > 0) {     // item is greater than head (= current min.)
            // System.out.println("   removing " + head);
            queue.poll();                       // remove "smallest" queue item (head)
            queue.add(item);
        }
    }

    /**
     * Returns an array holding the elements of this {@link SortedVector} instance. The elements are sorted according to
     * the specified comparator. The length of the array is equal to the value returned by {@link #size()}; the maximum
     * length equals the capacity of this vector. If the {@link SortedVector} was completely filled to its capacity, the
     * returned array is identical to the one that was passed to the constructor. The array contains no {@code null}
     * elements. The returned array is sorted in descending order, such that the "greatest" item added to the
     * {@link SortedVector} appears at its front (i.e., in position 0).
     *
     * @return a sorted array of elements
     */
    public T[] getArray() {
        T[] arr2 = queue.toArray(arr);                      // arr = arr2
        T[] arr3 = (size() < capacity) ?
                Arrays.copyOf(arr2, queue.size()) :  arr2;  // trim null elements if necessary
        // we need to sort once more since PriorityQueue#toArray() does not guarantee any order!
        Arrays.sort(arr3, comp.reversed());     // move largest item to front (arr3[0] = max)
        return arr3;
    }

    /**
     * Returns the current number of non-null items in this vector.
     * @return the number of contained items
     */
    public int size() {
        return queue.size();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + Arrays.toString(this.getArray());
    }

}

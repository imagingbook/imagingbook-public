/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.util;

/**
 * Defines static utility methods for measuring time.
 */
public final class Timing {

    private Timing() {}

    /**
     * Times the execution of the provided expression and returns the elapsed
     * time in nanoseconds.
     * Usage:
     * <pre>
     * long nanos = time(() -> {
     *     doSomething();
     * });
     * System.out.println("Took " + nanos + " ns");
     * </pre>
     *
     * @param r the {@link Runnable} expression to be timed
     * @return the elapsed time in nanoseconds
     */
    public static long timeNanos(Runnable r) {
        long start = System.nanoTime();
        r.run();
        return System.nanoTime() - start;
    }

}

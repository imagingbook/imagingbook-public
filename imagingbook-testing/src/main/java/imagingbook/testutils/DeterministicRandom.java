/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.testutils;

import java.util.random.RandomGenerator;

/**
 * Fully deterministic clone of java.util.Random (current LCG algorithm),
 * ensuring stable sequences across all Java versions and platforms.
 * To be used when absolute reproducibility is required.
 * This is a full implementation of the {@link RandomGenerator} interface
 * to be completely independent of variations in JDK implementations.
 * The code is copied from class java.util.Random.
 * The
 */
public class DeterministicRandom implements RandomGenerator {

    // Constants from java.util.Random
    private static final long MULTIPLIER = 0x5DEECE66DL;
    private static final long ADDEND = 0xBL;
    private static final long MASK = (1L << 48) - 1;

    private long seed;

    public DeterministicRandom(long seed) {
        // java.util.Random scrambles the seed with this XOR
        this.seed = (seed ^ MULTIPLIER) & MASK;
    }

    // ------------------------------------

    private int next(int bits) {
        seed = (seed * MULTIPLIER + ADDEND) & MASK;
        return (int) (seed >>> (48 - bits));
    }

    @Override
    public int nextInt() {
        return next(32);
    }

    @Override
    public int nextInt(int bound) {
        if (bound <= 0)
            throw new IllegalArgumentException("bound must be positive");

        if ((bound & -bound) == bound)  // power of two
            return (int) ((bound * (long) next(31)) >> 31);

        int bits, val;
        do {
            bits = next(31);
            val = bits % bound;
        } while (bits - val + (bound - 1) < 0);
        return val;
    }

    @Override
    public long nextLong() {
        return ((long) (next(32)) << 32) + next(32);
    }

    @Override
    public double nextDouble() {
        return (((long) next(26) << 27) + next(27)) / (double) (1L << 53);
    }

    @Override
    public boolean nextBoolean() {
        return next(1) != 0;
    }

    @Override
    public float nextFloat() {
        return next(24) / ((float) (1 << 24));
    }

 }


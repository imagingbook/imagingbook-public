/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/

/**
 * EISPACK code (FORTRAN) for solving generalized eigenvalue problems (from http://www.netlib.no/netlib/eispack).
 * Untangled to goto-free Java using a sequential state machine concept, inspired by D. E. Knuth,
 * "Structured Programming with Goto Statements", Computing Surveys, Vol. 6, No. 4 (1974).
 */
package imagingbook.common.math.eigen.eispack;

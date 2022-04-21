/**
 * <p>
 * Implementation of the randomized incremental algorithm described in
 * Guibas, Knuth, Sharir, "Randomized incremental construction of Delaunay and Voronoi diagrams", 
 * Algorithmica, no. 7, p. 381-413 (1992).
 * See also Berg et al., <em>Computational Geometry - Algorithms and Applications</em>, 
 * 3rd ed., Springer (2008), Sec. 9.3-9.6.
 * </p>
 * <p>
 * This code is partly based on an earlier implementation by Johannes Diemke
 * (https://github.com/jdiemke/delaunay-triangulator) published under the 
 * following license:
 * </p>
 * <pre>
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Johannes Diemke
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * </pre>
 */
package imagingbook.common.geometry.delaunay.guibas;
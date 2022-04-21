/**
 * <p>
 * Implementation of the incremental triangulation algorithm proposed by Paul Chew.
 * This code is partly based on Paul Chew's original (applet) implementation, downloaded from 
 * http://www.cs.cornell.edu/Info/People/chew/Delaunay.html (DelaunaySourceCodeJava60.zip)
 * under the following license:
 * </p>
 * <pre>
 * Copyright (c) 2005, 2007 by L. Paul Chew.
 * 
 * Permission is hereby granted, without written agreement and without
 * license or royalty fees, to use, copy, modify, and distribute this
 * software and its documentation for any purpose, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * </pre>
 * <p>
 * <strong>Original description:</strong> The actual data structure here is a Delaunay Triangulation. The Voronoi Diagram is built on-the-fly
 * from the Delaunay Triangulation. The Delaunay Triangulation is built within a large triangle whose vertices
 * are well off-screen. That's why in the Delaunay Triangulation there are lines heading off-screen. 
 * This technique makes the code simpler since otherwise additional code would be needed to handle new sites
 * that are outside the convex hull of the previous sites.
 * </p>
 * 
 * <p>
 * The algorithm: To insert a new site, we walk across the triangulation, starting from the most recently created
 * triangle, until we find the triangle that contains the new site. This triangle and any adjacent triangles that
 * contain this new site in their circumcircle are eliminated and the resulting empty spot is re-triangulated.
 * The site-insertion part of this technique is commonly called the Bowyer-Watson Algorithm. The expected time to
 * insert a new site is roughly O(n1/2) where n is the current number of sites.
 * </p>
 * 
 * Adapted by W. Burger (2019)
 */
package imagingbook.common.geometry.delaunay.chew;
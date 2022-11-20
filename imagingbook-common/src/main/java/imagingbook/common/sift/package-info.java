/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/

/**
 * <p>
 * This package implements David Lowe's SIFT feature detection scheme [1].
 * See Ch. 25 of [2] for more details.
 * </p>
 * <p>
 * [1] D. G. Lowe. Distinctive image features from scale-invariant keypoints.
 * International Journal of Computer Vision 60, 91–110 (2004).
 * <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 */
package imagingbook.common.sift;
// TODO: cleanup point-related classes (KeyPoint, SiftKeyPoint, SiftDescriptor)
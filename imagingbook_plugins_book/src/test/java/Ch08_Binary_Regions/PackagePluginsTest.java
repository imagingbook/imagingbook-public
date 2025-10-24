/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch08_Binary_Regions;

import imagingbook.testutils.PluginTestUtils;
import org.junit.Test;

public class PackagePluginsTest {

    private static Class<?>[] PLUGINS = {
            Axis_Aligned_Bounding_Box.class,
            Convex_Hull_Demo.class,
            Flusser_Moments_Covariance_Matrix.class,
            Flusser_Moments_From_Binary_Regions.class,
            Flusser_Moments_Matching_Demo.class,
            Major_Axis_Demo.class,
            Make_Projections.class,
            Region_Contours_Demo.class,
            Region_Eccentricity_Ellipse_Demo.class,
            Region_Segmentation_Demo.class
    };

    @Test
    public void test1() throws Exception {
        for (Class<?> clazz : PLUGINS) {
            PluginTestUtils.CheckJavaDocHelp(clazz);
        }
    }

}
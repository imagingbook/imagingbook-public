/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch21_Geometric_Operations;

import imagingbook.testutils.PluginTestUtils;
import org.junit.Test;

public class PackagePluginsTest {

    private static Class<?>[] PLUGINS = {
            Draw_Test_Grid.class,
            Map_Affine_Matrix.class,
            Map_Affine_Triangles.class,
            Map_Bilinear.class,
            Map_LogPolar_Demo.class,
            Map_Nonlinear_Ripple.class,
            Map_Nonlinear_Tapestry.class,
            Map_Nonlinear_Twirl.class,
            Map_Projective.class,
            Map_Rotate_Center.class,
            Map_Rotate_Origin.class,
            Map_Translate.class,
            Mesh_Warp_Interactive.class,
            Rectify_Quad_Selection.class
    };

    @Test
    public void test1() throws Exception {
        for (Class<?> clazz : PLUGINS) {
            PluginTestUtils.CheckJavaDocHelp(clazz);
        }
    }

}
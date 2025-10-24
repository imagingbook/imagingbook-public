/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch16_Color_Edges;

import imagingbook.testutils.PluginTestUtils;
import org.junit.Test;

public class PackagePluginsTest {

    private static Class<?>[] PLUGINS = {
            Color_Edges_Canny.class,
            Color_Edges_DiZenzo.class,
            Color_Edges_Gray.class,
            Color_Edges_Monochromatic.class
    };

    @Test
    public void test1() throws Exception {
        for (Class<?> clazz : PLUGINS) {
            PluginTestUtils.CheckJavaDocHelp(clazz);
        }
    }

}
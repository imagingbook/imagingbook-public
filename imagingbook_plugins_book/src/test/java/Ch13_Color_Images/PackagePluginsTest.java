/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch13_Color_Images;

import imagingbook.testutils.PluginTestUtils;
import org.junit.Test;

public class PackagePluginsTest {

    private static Class<?>[] PLUGINS = {
            Brighten_Index_Image.class,
            Brighten_Rgb_1.class,
            Brighten_Rgb_2.class,
            Convert_Rgb_To_Gray.class,
            Count_Colors.class,
            Desaturate_RGB.class,
            Hsv_Rotate_Hue.class,
            Index_To_Rgb.class,
            Quantize_Color_Image.class,
            Random_Hues_Demo.class
    };

    @Test
    public void test1() throws Exception {
        for (Class<?> clazz : PLUGINS) {
            PluginTestUtils.CheckJavaDocHelp(clazz);
        }
    }

}
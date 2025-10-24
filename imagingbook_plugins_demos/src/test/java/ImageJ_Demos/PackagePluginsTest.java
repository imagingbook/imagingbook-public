/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package ImageJ_Demos;

import imagingbook.testutils.PluginTestUtils;
import org.junit.Test;

public class PackagePluginsTest {

    private static Class<?>[] PLUGINS = {
            Alpha_Blending_Stack.class,
            Convert_ImagePlus_To_Gray.class,
            Create_New_Byte_Image.class,
            Data_Transfer_Plugin_Consumer.class,
            Data_Transfer_Plugin_Producer.class,
            Direct_Byte_Pixel_Access.class,
            Generic_Dialog_Example.class,
            Make_Uniform_Noise.class,
            My_Inverter_A.class,
            My_Inverter_B.class,
            Open_Sample_Image_For_PlugInFilter.class,
            Roi_Extract_Subimage_Demo.class,
            Roi_Processing_Demo1.class,
            Roi_Processing_Demo2.class,
            Run_Command_From_PlugIn.class,
            Run_Command_From_PlugInFilter.class,
            Stack_Processing_Demo.class,
            UpdateAndDraw_Demo.class
    };

    @Test
    public void test1() throws Exception {
        for (Class<?> clazz : PLUGINS) {
            PluginTestUtils.CheckJavaDocHelp(clazz);
        }
    }

}
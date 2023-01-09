/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.testutils;

import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;
import imagingbook.core.plugin.JavaDocHelp;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertNotNull;

public class PluginTestUtils {

    public static void CheckJavaDocHelp(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        if (isIjPlugin(clazz) && JavaDocHelp.class.isAssignableFrom(clazz)) {
            JavaDocHelp plug = (JavaDocHelp) clazz.getDeclaredConstructor().newInstance();  // instantiate plugin
            assertNotNull("null JavaDocUrl for plugin " + clazz.getSimpleName(), plug.getJavaDocUrl());
        }
    }

    public static boolean isIjPlugin(Class<?> clazz) {
        return PlugIn.class.isAssignableFrom(clazz) || PlugInFilter.class.isAssignableFrom(clazz);
    }
}

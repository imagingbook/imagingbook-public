/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.testutils;

import imagingbook.core.jdoc.JavaDocHelp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PluginTestUtils {

    /**
     * Checks if the specified class (if implementing {@link JavaDocHelp}) returns a useful URL when invoking
     * {@link JavaDocHelp#getJavaDocUrl()} on it.
     *
     * @param clazz a class implementing {@link JavaDocHelp}
     */
    public static void CheckJavaDocHelp(Class<?> clazz)  {
        if (JavaDocHelp.class.isAssignableFrom(clazz)) {
            JavaDocHelp plug = null;  // instantiate plugin
            try {
                plug = (JavaDocHelp) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {  }
            assertNotNull("could not instantiate plugin class " + clazz.getSimpleName(), plug);
            String url = plug.getJavaDocUrl();
            assertNotNull("null JavaDoc URL for plugin " + clazz.getSimpleName(), url);
            assertTrue("invalid JavaDoc URL " + url, JavaDocHelp.isValidURL(url));
        }
    }


}

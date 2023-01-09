/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ClassUtils {


    public static String getModuleFromPackage(Class<?> clazz) {
        Package pkg = clazz.getPackage();
        if (pkg == null)
            return null;
        String specs = pkg.getSpecificationTitle();
        if (specs == null)
            return null;
        int k = specs.lastIndexOf(':');
        if (k < 0) {
            return null;
        }
        return specs.substring(specs.lastIndexOf(':') + 1);
    }

    /**
     * Tries to discover the name of the module defining the specified class.
     * Call with arguments (clazz, "imagingbook-public").
     *
     * @param clazz a class instance
     * @param projectName the name of the containing project
     * @return the module name or {@code null} if not found.
     */
    public static String getModuleFromClasspath(Class<?> clazz, String projectName) {
        Path ppp = Paths.get(projectName);
        String moduleName = null;
        try {
            URI uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
            if (uri != null) {
                Path p = Paths.get(uri);
                int n = p.getNameCount();
                int k = -1;
                // find 'projectName' in path components:
                for (int i = 0; i < n; i++) {
                    if (p.getName(i).equals(ppp)) {
                        k = i + 1;
                        break;
                    }
                }
                // the following component should be the module part
                if (k >= 0 && k < n-1) {
                    moduleName = p.getName(k).toString();
                }
            }
        } catch (URISyntaxException e) { }
        return moduleName;
    }


    // -----------------------------------------------------------------

    public static void main(String[] args) throws URISyntaxException {

        Class<?> clazz = FileUtils.class;
        String specs = clazz.getPackage().getSpecificationTitle();
        System.out.println("specs = " + specs);

        System.out.println("module from class = " + clazz.getModule().getName());
        System.out.println("module from package = " + getModuleFromPackage(clazz));
        System.out.println("module from classpath = " + getModuleFromClasspath(clazz, "imagingbook-public"));

    }
}

![frankenburg-1918-960x200k.png](docs/img/443892583-frankenburg-1918-960x200k.png)

# 'imagingbook' Main Source Code Repository

This repository contains the **Java source code** accompanying the 
**digital image processing books** by **W. Burger and M. J. Burge**, 
published by Springer. This software is based on **[ImageJ](http://rsbweb.nih.gov/ij/index.html)**.
Please visit the main website **[imagingbook.com](https://imagingbook.com/)** for more information.

**Index terms:** digital image processing, computer algorithms, Java, ImageJ, textbook support.

## Repository Structure

The source code is built as a modular Maven project, which includes the following packages:

### Library and Data Packages

These "library-only" packages are packaged as `JAR` files, which are typically imported as Maven
dependencies. They may also be copied manually to ImageJ's `jars/` directory:

* **imagingbook-core**:
    Minimal infrastructure required for building other `imagingbook` library packages.
    Includes code for handling resources and automatically compiling `plugins.config` files
    for ImageJ plugin sets.  
* **imagingbook-common**: 
    This is the main `imagingbook` library with implementions of image processing algorithms,
    associated data structures and utility code. Users interested in applying `imagingbook`
    functionality in their own ImageJ plugins only require this package as a Maven dependency.
* **imagingbook-spectral**: 
    Library code related to spectral image processing (Fourier transforms etc.),
    separated from the main `imagingbook` library to minimize third-party dependencies.
* **imagingbook-pdf**: 
    PDF-related library code,
    separated from the main `imagingbook` library to minimize third-party dependencies.
* **imagingbook-sample-images**: 
    A small set of sample images used for demos and testing.

### ImageJ Plugin Packages

These packages contain sets of `ImageJ` plugins that make use of `imagingbook` library.
Each plugin set is packaged to a `JAR` file that can be copied to ImageJ's `plugins/` directory
or imported as a Maven dependency:

* **imagingbook-plugins-book**: 
    A collection of `ImageJ` plugins related to individual book chapters.
* **imagingbook-plugins-extras**: 
    `ImageJ` plugins related to chapters in previous book editions or other publications.
* **imagingbook-plugins-demos**: 
    Various plugins illustrating basic `ImageJ` concepts.
* **imagingbook-plugins-tools**: 
    Plugins providing tools for working with `ImageJ` (e.g., exact zooming and PDF-export).

<!-- [**Change Log**](CHANGES.md) -->

## Documentation (JavaDoc):

* [**imagingbook-core**](https://imagingbook.github.io/imagingbook-public/imagingbook-core/javadoc)
* [**imagingbook-common**](https://imagingbook.github.io/imagingbook-public/imagingbook-common/javadoc)
* [**imagingbook-spectral**](https://imagingbook.github.io/imagingbook-public/imagingbook-spectral/javadoc)
* [**imagingbook-pdf**](https://imagingbook.github.io/imagingbook-public/imagingbook-pdf/javadoc)
* [**imagingbook-sample-images**](https://imagingbook.github.io/imagingbook-public/imagingbook-sample-images/javadoc)
* [**imagingbook-plugins-book**](https://imagingbook.github.io/imagingbook-public/imagingbook-plugins-book/javadoc)
* [**imagingbook-plugins-extras**](https://imagingbook.github.io/imagingbook-public/imagingbook-plugins-extras/javadoc)
* [**imagingbook-plugins-demos**](https://imagingbook.github.io/imagingbook-public/imagingbook-plugins-demos/javadoc)
* [**imagingbook-plugins-tools**](https://imagingbook.github.io/imagingbook-public/imagingbook-plugins-tools/javadoc)

## Use with Maven

Each of the above items is available as a [Maven artifact on Maven Central](https://mvnrepository.com/artifact/com.imagingbook).
For example, to use the `imagingbook-common` library, simply include the following in your project's `pom.xml`file:
```
<dependency>
  <groupId>com.imagingbook</groupId>
  <artifactId>imagingbook-common</artifactId>
  <version>5.0.0</version>
</dependency>
```
Replace `5.0.0` with the most current release version found on [Maven Central](https://mvnrepository.com/artifact/com.imagingbook/imagingbook-common).

### Preconfigured Maven Projects

Two preconfigures Maven projects are available for getting started:

* **[imagingbook-plugins-all](https://github.com/imagingbook/imagingbook-plugins-all)**: ready-to-go Maven project setup that includes the `imagingbook` library and all plugin sets listed above.
* **[imagingbook-maven-demo-project](https://github.com/imagingbook/imagingbook-maven-demo-project)**: minimal Maven setup for ImageJ with the `imagingbook` library only.

These projects are set up to develop and run ImageJ user plugins out of the box. They are based on a special parent POM file 
([**imagingbook-plugins-pom**](https://github.com/imagingbook/imagingbook-public/tree/master/imagingbook-plugins-pom)),
which takes care of ImageJ's particular directory structure.
To use, clone one of the repositories and import it as a Maven project in your favorite IDE (the projects are preconfigured for Eclipse).
If necessary, perform Maven `clean` and `install` to update all dependencies and the runtime setup.

## Use without Maven

In an existing (non-Maven based) ImageJ environvent, you need to manually copy all necessary JAR files from
[imagingbook-plugins-all](https://github.com/imagingbook/imagingbook-plugins-all):

* `ImageJ/jars/*.jar` &rarr; `ImageJ/jars`
* `ImageJ/plugins/*.jar` &rarr;  `ImageJ/plugins`

Then restart ImageJ. Libraries and plugins should be loaded automatically.
Note that the JAR files in [imagingbook-plugins-all](https://github.com/imagingbook/imagingbook-plugins-all)
correspond to the most recent (development) build.
JAR files from stable release versions can be found at [Maven Central](https://mvnrepository.com/artifact/com.imagingbook/imagingbook-common).

## Related Projects

* **[imagingbook-calibrate](https://github.com/imagingbook/imagingbook-calibrate)**: Implementation of Zhang's camera calibration scheme.
* **[imagingbook-violajones](https://github.com/imagingbook/imagingbook-violajones)**: Implementation of Viola-Jones face detection.



<!-- [Markdown tests](docs/markdown-test.md) -->

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

* **[imagingbook-core](imagingbook-core/)**:
    Minimal infrastructure required for building other `imagingbook` library packages.
    Includes code for handling resources and automatically compiling `plugins.config` files
    for ImageJ plugin sets.  
* **[imagingbook-common](imagingbook-common/)**: 
    This is the main `imagingbook` library with implementions of image processing algorithms,
    associated data structures and utility code. Users interested in applying `imagingbook`
    functionality in their own ImageJ plugins only require this package as a Maven dependency.
* **[imagingbook-spectral](imagingbook-spectral/)**: 
    Library code related to spectral image processing (Fourier transforms etc.),
    separated from the main `imagingbook` library to minimize third-party dependencies.
* **[imagingbook-pdf](imagingbook-pdf/)**: 
    PDF-related library code,
    separated from the main `imagingbook` library to minimize third-party dependencies.
* **[imagingbook-sample-images](imagingbook-sample-images/)**: 
    A small set of sample images used for demos and testing.

### ImageJ Plugin Packages

These packages contain sets of `ImageJ` plugins that make use of `imagingbook` library.
Each plugin set is packaged to a `JAR` file that may be imported as a Maven dependency
or copied manually to ImageJ's `plugins/` directory:

* **[imagingbook-plugins-book](imagingbook-plugins-book/)**: 
    A collection of `ImageJ` plugins related to individual book chapters (including 
    materials from previous editions) and tools for working with `ImageJ` (e.g., exact zooming and PDF-export).
* **[imagingbook-plugins-demos](imagingbook-plugins-demos/)**: 
    Various plugins illustrating basic technical concepts in `ImageJ` and the `imagingbook` library..

<!-- [**Change Log**](CHANGES.md) -->

## API Documentation:

* [**imagingbook-public**](https://imagingbook.github.io/imagingbook-public/javadoc) (aggregated JavaDoc)


## Use with Maven

Each of the above items is available as a [Maven artifact on Maven Central](https://mvnrepository.com/artifact/com.imagingbook).
For example, to use the `imagingbook-common` library, simply include the following in your project's `pom.xml`file:
```
<dependency>
  <groupId>com.imagingbook</groupId>
  <artifactId>imagingbook-common</artifactId>
  <version>6.0.0</version>
</dependency>
```
Replace `6.0.0` with the most current release version found on 
Maven Central (https://mvnrepository.com/artifact/com.imagingbook).

### Preconfigured Maven Projects

The following preconfigured projects are available on GitHub for getting started:

* **[imagingbook-plugins-all](https://github.com/imagingbook/imagingbook-plugins-all)**: 
    This is a ready-to-go Maven project that includes the `imagingbook` library, all plugin sets listed above, 
    and a basic `ImageJ` runtime setup. 
* **[imagingbook-maven-demo-project](https://github.com/imagingbook/imagingbook-maven-demo-project)**: This is
    a minimal Maven setup for `ImageJ` with the `imagingbook` library, including sample Java code.

These projects are set up to develop and run `ImageJ` user plugins out of the box. They are based on a special parent POM file 
([**imagingbook-plugins-pom**](https://github.com/imagingbook/imagingbook-public/tree/master/imagingbook-plugins-pom)),
which takes care of ImageJ's particular directory structure during the Maven build.
To use, clone any of these repositories and import it as a Maven project in your favorite IDE.
If necessary, perform Maven `clean` and `install` to update all dependencies and the runtime setup.
These projects may also be used as a good starting point when working without Maven (see below).



## Use without Maven

To use the `imagingbook` library in an existing (non-Maven based) `ImageJ` environment you need to manually copy all necessary JAR files
e.g., from [imagingbook-plugins-all](https://github.com/imagingbook/imagingbook-plugins-all/tree/master/ImageJ/jars):

* `ImageJ/jars/*.jar` &rarr; `ImageJ/jars`
* `ImageJ/plugins/*.jar` &rarr;  `ImageJ/plugins`

Then restart `ImageJ`. Libraries and plugins should be loaded automatically.
Note that the JAR files in [imagingbook-plugins-all](https://github.com/imagingbook/imagingbook-plugins-all/tree/master/ImageJ/jars)
are typically from the most recent development ("snapshot") build.
JAR files from stable release versions can be found on Maven Central (https://mvnrepository.com/artifact/com.imagingbook).

## Related Projects on GitHub

Other projects using the `imagingbook` library include:

* **[imagingbook-calibrate](https://github.com/imagingbook/imagingbook-calibrate)**: Implementation of Zhang's camera calibration scheme.
* **[imagingbook-violajones](https://github.com/imagingbook/imagingbook-violajones)**: Implementation of Viola-Jones face detection.



<!-- [Markdown tests](docs/markdown-test.md) -->

![frankenburg-1918-960x200k.png](docs/img/443892583-frankenburg-1918-960x200k.png)

# 'imagingbook' Main Source Code Repository

This repository contains the **Java source code** accompanying the 
**digital image processing books** by **W. Burger and M. J. Burge**, 
published by Springer. This software is based on **[ImageJ](http://rsbweb.nih.gov/ij/index.html)**.
Please visit the main website **[imagingbook.com](https://imagingbook.com/)** for more information.

**Index terms:** digital image processing, computer algorithms, Java, ImageJ, textbook support.

## Repository Structure

The source code is built as a modular Maven project, which includes the following sub-projects:

* **imagingbook-core**: basic infrastructure required in other imagingbook library packages.
* **imagingbook-common**: the common library of image processing algorithms and utility code.
* **imagingbook-spectral**: library code related to spectral image processing (Fourier transforms etc.).
* **imagingbook-plugins-book**: a collection of ImageJ plugins related to individual book chapters.
* **imagingbook-plugins-extras**: plugins related to chapters in previous book editions or other publications.
* **imagingbook-plugins-demos**: various plugins illustrating basic ImageJ concepts.
* **imagingbook-plugins-tools**: plugins providing tools for working with ImageJ.
* **imagingbook-sample**: a small set of sample images used for demos and testing.

[**Change Log**](CHANGES.md)

## Documentation (JavaDoc):

* [**imagingbook-core**](imagingbook-core/javadoc)
* [**imagingbook-common**](https://imagingbook.github.io/imagingbook-public/imagingbook-common/javadoc)
* [**imagingbook-spectral**](https://imagingbook.github.io/imagingbook-public/imagingbook-spectral/javadoc)
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
  <version>4.0.0</version>
</dependency>
```
Replace `4.0.0` with the most current release version found on [Maven Central](https://mvnrepository.com/artifact/com.imagingbook/imagingbook-common).

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

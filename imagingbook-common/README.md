# imagingbook-common

This repository provides Java source code supplementing
the digital image processing books by W. Burger & M. J. Burge
(see [imagingbook.com](https://imagingbook.com) for details).

**Parent repository:** [imagingbook-public](https://github.com/imagingbook/imagingbook-public)

**JavaDoc:** https://imagingbook.github.io/imagingbook-common/javadoc/

## Maven setup

To use the ``imagingbook-common`` library in your Maven project, add the following lines to your ``pom.xml`` file:
````
<repositories>
    <repository>
	<id>imagingbook-maven-repository</id>
    	<url>https://raw.github.com/imagingbook/imagingbook-maven-repository/master</url>
    </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.imagingbook</groupId>
    <artifactId>imagingbook-common</artifactId>
    <version>3.1</version>
  </dependency>
  <!-- other dependencies ... -->
</dependencies>
````
The above setup refers to release `3.1`. Check the [ImagingBook Maven repository](https://github.com/imagingbook/imagingbook-maven-repository/tree/master/com/imagingbook/) for the most recent version.

A small demo project using this setup can be found in [imagingbook-maven-demo-project](https://github.com/imagingbook/imagingbook-maven-demo-project).

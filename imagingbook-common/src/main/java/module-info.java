/**
 * The central part of {@literal imagingbook} library with implementions of image
 * processing algorithms, associated data structures and utility code.
 */
module imagingbook.common {
    requires java.datatransfer;
    requires transitive ij;
    requires transitive imagingbook.core;
    requires transitive java.desktop;

    requires org.apache.commons.math4.core;
    requires org.apache.commons.math4.legacy;
    requires org.apache.commons.math4.legacy.core;
    requires org.apache.commons.math4.legacy.exception;
    requires org.apache.commons.numbers.complex;
    requires org.apache.commons.numbers.core;

	exports imagingbook.common.color.adapt;
	exports imagingbook.common.color.cie;
	exports imagingbook.common.color.colorspace;
	exports imagingbook.common.color.gamma;
	exports imagingbook.common.color.iterate;
	exports imagingbook.common.color.quantize;
	exports imagingbook.common.color.sets;
	exports imagingbook.common.color.statistics;
	exports imagingbook.common.color;
	exports imagingbook.common.corners;
	exports imagingbook.common.edges;
	exports imagingbook.common.filter.edgepreserving;
	exports imagingbook.common.filter.examples;
	exports imagingbook.common.filter.generic;
	exports imagingbook.common.filter.linear;
	exports imagingbook.common.filter.mask;
	exports imagingbook.common.filter.nonlinear;
	exports imagingbook.common.geometry.basic;
	exports imagingbook.common.geometry.circle;
	exports imagingbook.common.geometry.delaunay.guibas;
	exports imagingbook.common.geometry.delaunay;
	exports imagingbook.common.geometry.ellipse.project;
	exports imagingbook.common.geometry.ellipse;
	exports imagingbook.common.geometry.fd;
	exports imagingbook.common.geometry.fitting.circle.algebraic;
	exports imagingbook.common.geometry.fitting.circle.geometric;
	exports imagingbook.common.geometry.fitting.circle.utils;
	exports imagingbook.common.geometry.fitting.ellipse.algebraic;
	exports imagingbook.common.geometry.fitting.ellipse.geometric;
	exports imagingbook.common.geometry.fitting.ellipse.utils;
	exports imagingbook.common.geometry.fitting.line.utils;
	exports imagingbook.common.geometry.fitting.line;
	exports imagingbook.common.geometry.fitting.points;
	exports imagingbook.common.geometry.hulls;
	exports imagingbook.common.geometry.line;
	exports imagingbook.common.geometry.mappings.linear;
	exports imagingbook.common.geometry.mappings.nonlinear;
	exports imagingbook.common.geometry.mappings;
	exports imagingbook.common.geometry.moments;
	exports imagingbook.common.geometry.shape;
	exports imagingbook.common.histogram;
	exports imagingbook.common.hough;
	exports imagingbook.common.ij.overlay;
	exports imagingbook.common.ij;
	exports imagingbook.common.image.access;
	exports imagingbook.common.image.interpolation;
	exports imagingbook.common.image.matching.lucaskanade;
	exports imagingbook.common.image.matching;
	exports imagingbook.common.image;
	exports imagingbook.common.math.eigen.eispack;
	exports imagingbook.common.math.eigen;
	exports imagingbook.common.math.exception;
	exports imagingbook.common.math.nonlinear;
	exports imagingbook.common.math;
	exports imagingbook.common.morphology;
	exports imagingbook.common.mser.components;
	exports imagingbook.common.mser;
	exports imagingbook.common.noise.hashing;
	exports imagingbook.common.noise.perlin;
	exports imagingbook.common.ransac;
	exports imagingbook.common.regions;
	exports imagingbook.common.sift.scalespace;
	exports imagingbook.common.sift;
	exports imagingbook.common.threshold.adaptive;
	exports imagingbook.common.threshold.global;
	exports imagingbook.common.threshold;
	exports imagingbook.common.util.bits;
	exports imagingbook.common.util.progress;
	exports imagingbook.common.util.random;
	exports imagingbook.common.util.tuples;
	exports imagingbook.common.util;
}
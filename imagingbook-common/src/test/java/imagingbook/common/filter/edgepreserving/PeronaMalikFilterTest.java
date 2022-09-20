/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package imagingbook.common.filter.edgepreserving;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.common.filter.edgepreserving.PeronaMalikF.ColorMode;
import imagingbook.common.filter.edgepreserving.PeronaMalikF.ConductanceFunction;
import imagingbook.common.filter.edgepreserving.PeronaMalikF.ConductanceFunction.Type;
import imagingbook.common.filter.edgepreserving.PeronaMalikF.Parameters;
import imagingbook.common.filter.generic.GenericFilter;
import imagingbook.common.image.OutOfBoundsStrategy;
import imagingbook.common.util.ParameterBundle;
import imagingbook.testimages.FilterTestImage;
import imagingbook.testutils.ImageTestUtils;

public class PeronaMalikFilterTest {
	
	private static final double TOL = 1E-6;
	private static final OutOfBoundsStrategy OBS = OutOfBoundsStrategy.NearestBorder;
	
	private final ImageProcessor monastery, clown;
	private final Parameters params;
	
	public PeronaMalikFilterTest() {
		monastery = FilterTestImage.MonasterySmall.getImage().getProcessor();
		clown = FilterTestImage.Clown.getImage().getProcessor();
		
		assertTrue(monastery instanceof ByteProcessor);
		assertTrue(clown instanceof ColorProcessor);
		
		// use default parameters:
		params = new Parameters();
		params.iterations = 10;
		params.alpha = 0.20; 	
		params.kappa = 25;
		params.conductanceFunType = ConductanceFunction.Type.g1;
		params.colorMode = ColorMode.SeparateChannels;	
	}
	
	// --------------------------------------------------------------------------

	@Test
	public void testConductanceFunctions() {
		float d = 20;
		float kappa = 30;
		// results checked with Mathematica
		Assert.assertEquals(0.641180, ConductanceFunction.get(Type.g1,kappa).eval(d), 1E-6);
		Assert.assertEquals(0.692308, ConductanceFunction.get(Type.g2,kappa).eval(d), 1E-6);
		Assert.assertEquals(0.832050, ConductanceFunction.get(Type.g3,kappa).eval(d), 1E-6);
		Assert.assertEquals(0.790123, ConductanceFunction.get(Type.g4,kappa).eval(d), 1E-6);
	}

	// --------------------------------------------------------------------------
	
	@Test
	public void testGrayScalarG1() {
		ImageProcessor ipA = monastery.duplicate();
		Parameters params2 = ParameterBundle.duplicate(params);
		params2.conductanceFunType = ConductanceFunction.Type.g1;
		GenericFilter filter = new PeronaMalikFilterScalar(params2);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.MonasterySmallPeronaMalikG1.getImage().getProcessor();
		assertTrue(ipB instanceof ByteProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testGrayScalarG2() {
		ImageProcessor ipA = monastery.duplicate();
		Parameters params2 = ParameterBundle.duplicate(params);
		params2.conductanceFunType = ConductanceFunction.Type.g2;
		GenericFilter filter = new PeronaMalikFilterScalar(params2);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.MonasterySmallPeronaMalikG2.getImage().getProcessor();
		assertTrue(ipB instanceof ByteProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testGrayScalarG3() {
		ImageProcessor ipA = monastery.duplicate();
		Parameters params2 = ParameterBundle.duplicate(params);
		params2.conductanceFunType = ConductanceFunction.Type.g3;
		GenericFilter filter = new PeronaMalikFilterScalar(params2);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.MonasterySmallPeronaMalikG3.getImage().getProcessor();
		assertTrue(ipB instanceof ByteProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testGrayScalarG4() {
		ImageProcessor ipA = monastery.duplicate();
		Parameters params2 = ParameterBundle.duplicate(params);
		params2.conductanceFunType = ConductanceFunction.Type.g4;
		GenericFilter filter = new PeronaMalikFilterScalar(params2);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.MonasterySmallPeronaMalikG4.getImage().getProcessor();
		assertTrue(ipB instanceof ByteProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	// --------------------------------------------------------------------------
	
	@Test
	public void testColorScalar() {
		ImageProcessor ipA = clown.duplicate();
		GenericFilter filter = new PeronaMalikFilterScalar(params);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.ClownPeronaMalikSeparateChannels.getImage().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testColorVector1() {
		ImageProcessor ipA = clown.duplicate();
		Parameters params2 = ParameterBundle.duplicate(params);
		params2.colorMode = ColorMode.BrightnessGradient;
		GenericFilter filter = new PeronaMalikFilterVector(params2);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.ClownPeronaMalikBrightnessGradient.getImage().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test
	public void testColorVector2() {
		ImageProcessor ipA = clown.duplicate();
		Parameters params2 = ParameterBundle.duplicate(params);
		params2.colorMode = ColorMode.ColorGradient;
		GenericFilter filter = new PeronaMalikFilterVector(params2);
		filter.applyTo(ipA, OBS);
		ImageProcessor ipB = FilterTestImage.ClownPeronaMalikColorGradient.getImage().getProcessor();
		assertTrue(ipB instanceof ColorProcessor);
		assertTrue(ImageTestUtils.match(ipA, ipB, TOL));
	}
	
	@Test(expected = UnsupportedOperationException.class)	// fails for ColorMode.SeparateChannels
	public void testColorVector3() {
		ImageProcessor ipA = clown.duplicate();
		Parameters params2 = ParameterBundle.duplicate(params);
		params2.colorMode = ColorMode.SeparateChannels;	// not allowed for vector filter
		GenericFilter filter = new PeronaMalikFilterVector(params2);
		filter.applyTo(ipA, OBS);
	}
	
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *******************************************************************************/
package FittingEllipses;

import java.awt.Color;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.io.LogStream;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.fitting.line.LineFit;
import imagingbook.common.geometry.fitting.line.LineSampler;
import imagingbook.common.geometry.fitting.line.LinearRegressionFit;
import imagingbook.common.geometry.fitting.line.OrthogonalLineFitEigen;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.geometry.mappings.linear.Rotation2D;
import imagingbook.common.geometry.mappings.linear.Translation2D;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.common.math.PrintPrecision;

public class Example_Fit_Rotated_Points implements PlugIn {

	private static int W = 400;
	private static int H = 400;
	
	private static final double StrokeWidth = 1.0;
	private static double DashLength = 10;
	
	private static final Color CentroidColor = 		new Color(0,176,80);
	private static final Color RegressionFitColor = 	new Color(255,0,0);
	private static final Color OrthogonalFitColor = 	new Color(0,112,192);
	private static final Color PointColor = Color.blue;
	private static double PointRadius = 2;

	private static Pnt2d p1 = Pnt2d.from(50, 0.5*H);
	private static Pnt2d p2 = Pnt2d.from(W-50, 0.5*H);
	private static int n = 100;
	private static double sigma = 15.0;

	private static boolean DrawSamplePoints = true;
	private static boolean DrawOrthogalFit = true;
	private static boolean DrawRegressionFit = true;
	private static boolean DrawCentroid = true;

	static {
		LogStream.redirectSystem();
		PrintPrecision.set(6);
	}

	@Override
	public void run(String arg) {
		LineSampler sampler = new LineSampler(p1, p2);
		Pnt2d[] pts0 = sampler.getPoints(n, sigma);
		
		Translation2D t1 = new Translation2D(-0.5*W, -0.5*H);
		Translation2D t2 = t1.getInverse();

		for (int theta = 0; theta < 120; theta += 20) {		// step-wise rotation (in degrees)
			Rotation2D rot = new Rotation2D(Math.toRadians(theta));
			AffineMapping2D map = t1.concat(rot).concat(t2);
			Pnt2d[] pts = map.applyTo(pts0);

			LineFit fitO = new OrthogonalLineFitEigen(pts);
			AlgebraicLine lineO = fitO.getLine();

			LineFit fitR = new LinearRegressionFit(pts);
			AlgebraicLine lineR = fitR.getLine();

			ByteProcessor ip = new ByteProcessor(W, H);
			ip.setColor(255);
			ip.fill();

			Overlay oly = new Overlay();
			ShapeOverlayAdapter ola = new ShapeOverlayAdapter(oly);

			// ----- draw sample points ----------------------------------
			if (DrawSamplePoints) {
				ColoredStroke pointStroke = new ColoredStroke(StrokeWidth, PointColor, 0);
				pointStroke.setFillColor(PointColor);
				for (Pnt2d p : pts) {
					ola.addShape(p.getShape(PointRadius), pointStroke);
				}
			}

			if (DrawOrthogalFit) {
				ColoredStroke stroke = new ColoredStroke(StrokeWidth, OrthogonalFitColor, 0);
				ola.addShape(lineO.getShape(W, H), stroke);
			}

			if (DrawRegressionFit) {
				ColoredStroke stroke = new ColoredStroke(StrokeWidth, RegressionFitColor, DashLength);
				ola.addShape(lineR.getShape(W, H), stroke);
			}
			
			if (DrawCentroid) {
				Pnt2d ctr = PntUtils.centroid(pts);
				ColoredStroke pointStroke = new ColoredStroke(StrokeWidth, CentroidColor, 0);
				pointStroke.setFillColor(null);
				ola.addShape(ctr.getShape(PointRadius*2), pointStroke);
			}

			String title = String.format("line-%03d", theta);
			ImagePlus im = new ImagePlus(title, ip);
			im.setOverlay(oly);
			im.show();
		}
	}


}

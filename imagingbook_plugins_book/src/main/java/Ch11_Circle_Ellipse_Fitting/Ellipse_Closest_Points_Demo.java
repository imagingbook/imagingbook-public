/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch11_Circle_Ellipse_Fitting;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.ellipse.GeometricEllipse;
import imagingbook.common.geometry.ellipse.project.ConfocalConicEllipseProjector;
import imagingbook.common.geometry.ellipse.project.EllipseProjector;
import imagingbook.common.geometry.ellipse.project.OrthogonalEllipseProjector;
import imagingbook.common.ij.DialogUtils;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.core.jdoc.JavaDocHelp;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.util.Random;

import static java.lang.Math.PI;

/**
 * <p>
 * This plugin creates a new image with an ellipse and a set of random points. For each point, the closest (contact)
 * point on the ellipse is calculated and a connecting line is drawn to a vector overlay. Two closest-point algorithms
 * are available (see Secs. 11.2.2 and 11.2.3 of [1] for additional details):
 * </p>
 * <ol>
 * <li> orthogonal, exact closest point (exact but iterative) and </li>
 * <li> approximate confocal conic closest point estimation (only approximate but non-iterative).</li>
 * </ol>
 * <p>
 * The random seed may be specified for repeatability of experiments.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic Introduction</em>,
 * 3rd ed, Springer (2022).
 * </p>
 *
 * @author WB
 */
public class Ellipse_Closest_Points_Demo implements PlugIn, JavaDocHelp {
	
	private static int W = 400;
	private static int H = 400;
	private static int N = 250;
	private static int Seed = 0;
	
	private static final double StrokeWidth = 1.0;
	private static final Color EllipseColor = Color.green;
	private static final Color PointColor = Color.blue;
	private static ClosestPointAlgorithm Algorithm = ClosestPointAlgorithm.Orthogonal;
	
	public enum ClosestPointAlgorithm {
		Orthogonal, ConfocalConics
	}
	
	@Override
	public void run(String arg) {
		
		if (!runDialog()) {
			return;
		}
		
		//GeometricEllipse realEllipse = new GeometricEllipse(170, 120, 200, 190, PI/7);		// example 1
		GeometricEllipse realEllipse = new GeometricEllipse(1100, 120, 25, 1200, PI/2 + 0.2);	// example 2
		
		String title = this.getClass().getSimpleName() + " (" + Algorithm.toString() + ")";
		ImagePlus im = NewImage.createByteImage(title, W, H, 1, NewImage.FILL_WHITE);
		
		ShapeOverlayAdapter ola = new ShapeOverlayAdapter();
		
		ColoredStroke ellipseStroke = new ColoredStroke(StrokeWidth, EllipseColor);
		ColoredStroke pointStroke = new ColoredStroke(StrokeWidth, PointColor);
		ola.addShapes(realEllipse.getShapes(), ellipseStroke);
		
		EllipseProjector projector = null;
		switch (Algorithm) {
		case Orthogonal:
			projector = new OrthogonalEllipseProjector(realEllipse);
			break;
		case ConfocalConics:
			projector = new ConfocalConicEllipseProjector(realEllipse);
			break;
		}
		
		Random rd = (Seed == 0) ? new Random() : new Random(Seed);

		for (int i = 0; i < N; i++) {
			Pnt2d p = Pnt2d.from(W * rd.nextDouble(), H* rd.nextDouble());
			Pnt2d p0 = projector.project(p);
			// draw connecting line:
			Path2D path = new Path2D.Double();
			path.moveTo(p.getX(), p.getY());
			path.lineTo(p0.getX(), p0.getY());
			ola.addShape(path);
			// draw point p:
			ola.addShape(p.getShape(), pointStroke);
		}
		
		im.setOverlay(ola.getOverlay());
		im.show();
	}

	private boolean runDialog() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addHelp(getJavaDocUrl());
		gd.addMessage(DialogUtils.formatText(50,
				"This plugin creates a new image with an ellipse and a",
				"set of random points. For each point, the closest (contact)",
				"point on the ellipse is calculated and a connecting line",
				"is drawn to a vector overlay."
				));
		
		gd.addNumericField("Number of random points", N, 0);
		gd.addNumericField("Random seed (0 = none)", Seed, 0);
		gd.addEnumChoice("Closest-point method", Algorithm);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		N = (int) gd.getNextNumber();
		Seed = (int) gd.getNextNumber();
		Algorithm = gd.getNextEnumChoice(ClosestPointAlgorithm.class);
		
		return true;
	}
	
}

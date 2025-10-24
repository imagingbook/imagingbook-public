/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package Ch10_Line_Fitting;

import ij.ImagePlus;
import ij.plugin.ImagesToStack;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import imagingbook.common.geometry.basic.Pnt2d;
import imagingbook.common.geometry.basic.PntUtils;
import imagingbook.common.geometry.fitting.line.LineFit;
import imagingbook.common.geometry.fitting.line.LinearRegressionFit;
import imagingbook.common.geometry.fitting.line.OrthogonalLineFitEigen;
import imagingbook.common.geometry.fitting.line.utils.LineSampler;
import imagingbook.common.geometry.line.AlgebraicLine;
import imagingbook.common.geometry.mappings.linear.AffineMapping2D;
import imagingbook.common.geometry.mappings.linear.Rotation2D;
import imagingbook.common.geometry.mappings.linear.Translation2D;
import imagingbook.common.ij.overlay.ColoredStroke;
import imagingbook.common.ij.overlay.ShapeOverlayAdapter;
import imagingbook.core.jdoc.JavaDocHelp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * ImageJ demo plugin, performs line fitting to a randomly sampled point set
 * that is rotated in uniform steps. The result is shown as a stack of images
 * with graphic overlays.
 * </p>
 * <p>
 * Two fitting methods are employed: (a) linear regression fitting, (b)
 * orthogonal regression fitting. The result of the first varies with rotation,
 * while orthogonal fitting is rotation-invariant. See Sec. 10.2 (Fig. 10.4) of
 * [1] for additional details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An Algorithmic
 * Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/09/30
 * @see imagingbook.common.geometry.fitting.line.LinearRegressionFit
 * @see imagingbook.common.geometry.fitting.line.OrthogonalLineFitEigen
 */
public class Line_Rotation_Demo implements PlugIn, JavaDocHelp {

	public static int W = 400;
	public static int H = 400;
	
	public static final double StrokeWidth = 1.0;
	public static double DashLength = 10;
	
	public static final Color CentroidColor = 		new Color(0,176,80);
	public static final Color RegressionFitColor = 	new Color(255,0,0);
	public static final Color OrthogonalFitColor = 	new Color(0,112,192);
	public static final Color PointColor = Color.blue;
	public static double PointRadius = 2;

	public static Pnt2d P1 = Pnt2d.from(50, 0.5*H);
	public static Pnt2d P2 = Pnt2d.from(W-50, 0.5*H);
	public static int N = 100;
	public static double Sigma = 15.0;

	public static boolean DrawSamplePoints = true;
	public static boolean DrawOrthogalFit = true;
	public static boolean DrawRegressionFit = true;
	public static boolean DrawCentroid = true;

	@Override
	public void run(String arg) {
		Pnt2d[] pts0 = new LineSampler(P1, P2).getPoints(N, Sigma);	
		Translation2D t1 = new Translation2D(-0.5 * W, -0.5 * H);
		Translation2D t2 = t1.getInverse();
		
		List<ImagePlus> imageList = new ArrayList<>();
		
		ColoredStroke pointStroke = new ColoredStroke(StrokeWidth, PointColor, 0);
		pointStroke.setFillColor(PointColor);
		ColoredStroke strokeOrth = new ColoredStroke(StrokeWidth, OrthogonalFitColor, 0);
		ColoredStroke strokeReg = new ColoredStroke(StrokeWidth, RegressionFitColor, DashLength);
		ColoredStroke strokeCtr = new ColoredStroke(StrokeWidth, CentroidColor, 0);

		// step-wise rotation about the image center (in degrees):
		for (int theta = 0; theta < 120; theta += 20) {		
			Rotation2D rot = new Rotation2D(Math.toRadians(theta));
			AffineMapping2D map = t1.concat(rot).concat(t2);
			Pnt2d[] pts = map.applyTo(pts0);

			LineFit fitOrth = new OrthogonalLineFitEigen(pts);
			AlgebraicLine lineOrth = fitOrth.getLine();

			LineFit fitReg = new LinearRegressionFit(pts);
			AlgebraicLine lineReg = fitReg.getLine();

			ByteProcessor ip = new ByteProcessor(W, H);
			ip.setColor(255);
			ip.fill();

			ShapeOverlayAdapter ola = new ShapeOverlayAdapter();

			if (DrawSamplePoints) {				
				for (Pnt2d p : pts) {
					ola.addShape(p.getShape(PointRadius), pointStroke);
				}
			}

			if (DrawOrthogalFit) {
				ola.addShape(lineOrth.getShape(W, H), strokeOrth);
			}

			if (DrawRegressionFit) {
				ola.addShape(lineReg.getShape(W, H), strokeReg);
			}
			
			if (DrawCentroid) {
				Pnt2d ctr = PntUtils.centroid(pts);			
				ola.addShape(ctr.getShape(PointRadius*2), strokeCtr);
			}

			String title = String.format("line-%03d", theta);
			ImagePlus im = new ImagePlus(title, ip);
			im.setOverlay(ola.getOverlay());
			imageList.add(im);
		}
		
		ImagePlus stackIm = ImagesToStack.run(imageList.toArray(new ImagePlus[0]));
		stackIm.setTitle(this.getClass().getSimpleName());
		stackIm.show();
	}

}

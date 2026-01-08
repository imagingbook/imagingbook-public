/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2025 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.common.ij.overlay;

import imagingbook.common.math.Matrix;

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * This is an extension of AWT class {@link BasicStroke} which adds line and fill colors.
 * Instances of this class are immutable, but an internal {@link Builder} class is provided
 * for easy copying and constructing strokes, e.g.
 * <pre>
 * // new stroke from scratch:
 * ColoredStroke stroke1 = new ColoredStroke.Builder()
 * 				.withLineWidth(0.7)
 * 				.withStrokeColor(Color.green)
 * 				.withFillColor(Color.gray)
 * 				.withDashArray(8, 4)
 * 				.build();
 *
 * // same as stroke1 but different color:
 * ColoredStroke stroke2 = new ColoredStroke.Builder(stroke1)
 * 				.withStrokeColor(Color.red)
 * 				.build();
 * </pre>
 * @author WB
 * @version 2026/01/08
 */
public class ColoredStroke extends BasicStroke {
	
	public static final Color DefaultStrokeColor = Color.black;
	public static final Color DefaultFillColor = null;

	private Color strokeColor;
	private Color fillColor;
	
	public ColoredStroke() {
		this(new BasicStroke());
	}
	
	public ColoredStroke(BasicStroke bs) {
		this(bs, DefaultStrokeColor, DefaultFillColor);
	}

	public ColoredStroke(ColoredStroke cs) {
		this((BasicStroke)cs, cs.strokeColor, cs.fillColor);
	}

	public ColoredStroke(BasicStroke bs, Color strokeColor, Color fillColor) {
		this(bs.getLineWidth(),
				bs.getEndCap(),
				bs.getLineJoin(),
				bs.getMiterLimit(),
				bs.getDashArray(),
				bs.getDashPhase(),
				strokeColor,
				fillColor);
	}

	public ColoredStroke(double lineWidth, int endCap, int lineJoin, 
			double miterLimit, float[] dashArray, double dashPhase,
			Color strokeColor, Color fillColor) {
		super((float) lineWidth, endCap, lineJoin, (float) miterLimit, dashArray, (float) dashPhase);
		this.strokeColor = strokeColor;
		this.fillColor = fillColor;
	}

	@Deprecated
	public ColoredStroke(double lineWidth, Color strokeColor) {
		this(instanceFrom(lineWidth, strokeColor));
	}

	private static ColoredStroke instanceFrom(double lineWidth, Color strokeColor) {
		return new Builder()
				.withLineWidth(lineWidth)
				.withStrokeColor(strokeColor)
				.build();
	}

	// -------------------------------------------------------------

	public Color getStrokeColor() {
		return this.strokeColor;
	}

	public Color getFillColor() {
		return this.fillColor;
	}

	// -------------------------------------------------------------------------

	public ColoredStroke duplicate() {
		return new ColoredStroke((BasicStroke)this, strokeColor, fillColor);
	}

	// ---------------------------------------------------------------------------------------

	/**
	 * Builder class for {@link ColoredStroke}.
	 */
	public static class Builder {
		private float lineWidth;
		private int endCap;
		private int lineJoin;
		private float miterLimit;
		private float[] dashArray;
		private float dashPhase;
		private Color strokeColor;
		private Color fillColor;

		/**
		 * Constructs a {@link ColoredStroke} object starting from default values.
		 */
		public Builder() {
			this(new BasicStroke());
			this.strokeColor = DefaultStrokeColor;
			this.fillColor = DefaultFillColor;
		}

		/**
		 * Constructs a {@link ColoredStroke} object starting from the supplied
		 * {@link BasicStroke} or {@link ColoredStroke} instance.
		 * @param str a {@link BasicStroke} or {@link ColoredStroke} instance
		 */
		public Builder(BasicStroke str) {
			this.lineWidth = str.getLineWidth();
			this.endCap = str.getEndCap();
			this.lineJoin = str.getLineJoin();
			this.miterLimit = str.getMiterLimit();
			this.dashArray = str.getDashArray();
			this.dashPhase = str.getDashPhase();

			if (str instanceof ColoredStroke cs) {
				this.strokeColor = cs.strokeColor;
				this.fillColor = cs.fillColor;
			}
			else {
				this.strokeColor = DefaultStrokeColor;
				this.fillColor = DefaultFillColor;
			}
		}

		public ColoredStroke build() {
			return new ColoredStroke(
					this.lineWidth,
					this.endCap,
					this.lineJoin,
					this.miterLimit,
					this.dashArray,
					this.dashPhase,
					this.strokeColor,
					this.fillColor);
		}

		public Builder withLineWidth(double lineWidth) {
			this.lineWidth = (float) lineWidth;
			return this;
		}

		public Builder withEndCap(int endCap) {
			this.endCap = endCap;
			return this;
		}

		public Builder withLineJoin(int lineJoin) {
			this.lineJoin = lineJoin;
			return this;
		}

		public Builder withStrokeColor(Color color) {
			this.strokeColor = color;
			return this;
		}

		public Builder withFillColor(Color color) {
			this.fillColor = color;
			return this;
		}

		/**
		 * Specify the dash pattern. Usage examples:
		 * <pre>
		 * withDashArray(6);      // dashArray = new float[] {6}
		 * withDashArray(6, 4);   // dashArray = new float[] {6, 4}
		 * withDashArray();       // dashArray = null
		 * </pre>
		 * @param dashes a (possibly empty) sequence of dash lengths
		 * @see BasicStroke#getDashArray()
		 */
		public Builder withDashArray(double... dashes) {
			if (dashes == null || dashes.length == 0) {
				this.dashArray = null;
			}
			else {
				this.dashArray = Matrix.toFloat(dashes);
			}
			return this;
		}

		public Builder withDashPhase(double dashPhase) {
			this.dashPhase = (float)dashPhase;
			return this;
		}
	}

}

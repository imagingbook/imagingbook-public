/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2022 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit https://imagingbook.com for additional details.
 *******************************************************************************/
package Ch12_RansacAndHough;

import java.awt.Color;

/**
 * Various display settings used in plugins of this package (only).
 * 
 * @author WB
 *
 */
interface RansacDrawSettings {	
	static double LineStrokeWidth = 0.75;
	static Color InitialFitColor = Color.red;
	static Color FinalFitColor = Color.blue;	
	static Color InlierColor = Color.green.darker();
	static double InlierRadius = 1.5;
	static Color RandomDrawDotColor = Color.red;
	static double RandoDrawDotRadius = 4;
}

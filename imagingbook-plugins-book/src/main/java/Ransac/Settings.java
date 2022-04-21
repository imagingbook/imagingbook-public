package Ransac;

import java.awt.Color;

/**
 * Various display settings used in plugins of this package (only).
 * 
 * @author WB
 *
 */
interface Settings {	
	static double LineStrokeWidth = 0.75;
	static Color InitialFitColor = Color.red;
	static Color FinalFitColor = Color.blue;	
	static Color InlierColor = Color.green.darker();
	static double InlierRadius = 1.5;
	static Color RandomDrawDotColor = Color.red;
	static double RandoDrawDotRadius = 4;
}

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.common.util;

import java.util.Formatter;
import java.util.Locale;

import imagingbook.common.math.PrintPrecision;

/**
 * This class provides some static methods for formatting
 * Java arrays (representing vectors, matrices or measurements)
 * for copy-pasting to Mathematica.
 * @author wilbur
 * @version 2014/12/03
 */
public abstract class MathematicaIO {

	/**
	 * Generates a string holding the named definition
	 * of a 1D double array for Mathematica in the form
	 * name = {A[0], A[1], ...,A[m-1]};
	 * @param name the identifier to be used in Mathematica.
	 * @param A the array to be encoded (of length m).
	 * @return a String holding the Mathematica definition.
	 */
	public static String listArray(String name, double[] A) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		String fs = PrintPrecision.getFormatStringFloat();
		formatter.format(name + " = {");
		for (int i = 0; i < A.length; i++) {
			if (i > 0)
				formatter.format(", ");
			formatter.format(fs, A[i]);
		}
		formatter.format("};\n");
		String result = formatter.toString();
		formatter.close();
		return result;
	}
	
	/**
	 * Generates a string holding the named definition
	 * of a 1D float array for Mathematica in the form
	 * {@code name = {A[0], A[1], ...,A[m-1]};}
	 * 
	 * @param name the name (Mathematica symbol) for the resulting array
	 * @param A the array to be encoded (of length m).
	 * @return a String holding the Mathematica definition.
	 */
	public static String listArray(String name, float[] A) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		String fs = PrintPrecision.getFormatStringFloat();
		formatter.format(name + " = {");
		for (int i = 0; i < A.length; i++) {
			if (i > 0)
				formatter.format(", ");
			formatter.format(fs, A[i]);
		}
		formatter.format("};\n");
		String result = formatter.toString();
		formatter.close();
		return result;
	}
	
	
	/**
	 * Generates a string holding the named definition
	 * of a 1D int array for Mathematica in the form
	 * {@code name = {A[0], A[1], ...,A[m-1]};}
	 * 
	 * @param name the name (Mathematica symbol) for the resulting array
	 * @param A the array to be encoded (of length m).
	 * @return a String holding the Mathematica definition.
	 */
	public static String listArray(String name, int[] A) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(name + " = {");
		for (int i = 0; i < A.length; i++) {
			if (i > 0)
				formatter.format(", ");
			formatter.format("%d", A[i]);
		}
		formatter.format("};\n");
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	// CHECK i/j indices!!!
	/**
	 * Generates a string holding the named definition
	 * of a 2D double array for Mathematica in the form
	 * name = {{A[0][0],...,A[0][m-1]},
	 * {A[1][0],...,A[1][m-1]}, ...,
	 * {A[n-1][0], A[n-1][1], ...,A[n-1][m-1]}};
	 * @param name the identifier to be used in Mathematica.
	 * @param A the array to be encoded (of length m).
	 * @return a String holding the Mathematica definition.
	 */
	public static String listArray(String name, double[][] A) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		String fs = PrintPrecision.getFormatStringFloat();
		formatter.format(name + " = {");
		for (int i = 0; i < A.length; i++) {
			if (i == 0)
				formatter.format("{");
			else
				formatter.format(", \n{");
			for (int j = 0; j < A[i].length; j++) {
				if (j == 0) 
					formatter.format(fs, A[i][j]);
				else
					formatter.format(", " + fs, A[i][j]);
			}
			formatter.format("}");
		}
		formatter.format("};\n");
		String result = formatter.toString();
		formatter.close();
		return result;
	}



	/**
	 * Generates a string holding the named definition
	 * of a 2D float array for Mathematica in the form
	 * name = {{A[0][0],...,A[0][m-1]},
	 * {A[1][0],...,A[1][m-1]}, ...,
	 * {A[n-1][0], A[n-1][1], ...,A[n-1][m-1]}};
	 * @param name the identifier to be used in Mathematica.
	 * @param A the array to be encoded (of length m).
	 * @return a String holding the Mathematica definition.
	 */
	public static String listArray(String name, float[][] A) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		String fs = PrintPrecision.getFormatStringFloat();
		formatter.format(name + " = {");
		for (int i = 0; i < A.length; i++) {
			if (i == 0)
				formatter.format("{");
			else
				formatter.format(", \n{");
			for (int j = 0; j < A[i].length; j++) {
				if (j == 0) 
					formatter.format(fs, A[j][i]);
				else
					formatter.format(", " + fs, A[j][i]);
			}
			formatter.format("}");
		}
		formatter.format("};\n");
		String result = formatter.toString();
		formatter.close();
		return result;
	}
	
// ------------------------------------------------------
	
//	public static void main(String[] args) {
//		double[][] A = new double[10][2];
//		for (int i = 0; i < A.length; i++) {
//			A[i][0] = i * 1.0/3 - 1.5;
//			A[i][1] = Math.random();
//		}
//		String str = listArray("A", A);
//		System.out.println(str);
//
//	}

}

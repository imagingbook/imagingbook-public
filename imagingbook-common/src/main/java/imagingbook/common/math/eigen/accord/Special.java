package imagingbook.common.math.eigen.accord;


// ported from https://github.com/accord-net/framework/blob/development/Sources/Accord.Math/Special.cs
public abstract class Special {
	
	
	/// <summary>
    ///   Returns <paramref name="a"/> with the sign of <paramref name="b"/>. 
    /// </summary>
    /// 
    /// <remarks>
    ///   This is a port of the sign transfer function from EISPACK,
    ///   and is is equivalent to C++'s std::copysign function.
    /// </remarks>
    /// 
    /// <returns>If B > 0 then the result is ABS(A), else it is -ABS(A).</returns>
    /// 
	public static double Sign(double a, double b) {
		double x = (a >= 0 ? a : -a);
		return (b >= 0 ? x : -x);
	}
	
	
	   /// <summary>
    ///   Estimates unit round-off in quantities of size x.
    /// </summary>
    /// <remarks>
    ///   This is a port of the epslon function from EISPACK.
    /// </remarks>
    /// 
	public static double Epslon(double x)
	{
		double a, b, c, eps;
		a = 1.3333333333333333;

		L10: do {
			b = a - 1.0;
			c = b + b + b;
			eps = Math.abs(c - 1.0);

			if (eps == 0.0) {
				//goto L10;
				break L10;
			}
		} while (true);

		return eps * Math.abs(x);
	}
	
//    public static double Epslon(double x)
//    {
//        double a, b, c, eps;
//
//        a = 1.3333333333333333;
//
//    L10:
//        b = a - 1.0;
//        c = b + b + b;
//        eps = System.Math.Abs(c - 1.0);
//
//        if (eps == 0.0)
//            goto L10;
//
//        return eps * System.Math.Abs(x);
//    }


}

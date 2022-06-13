package imagingbook.common.math.eigen;


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


}

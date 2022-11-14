package imagingbook.common.color.colorspace;

/**
 * <p>
 * Defines the "modified gamma correction" used for converting linear to
 * non-linear color component values. The mapping function consists of a linear
 * and non-linear part. In the forward mapping {@link #applyFwd(double)}), the
 * linear part covers input values between a = 0,...,a0, while values a =
 * a0,...,1 are mapped non-linearly. For the inverse mapping
 * {@link #applyInv(double)}), the linear part is b = 0,...,b0, the non-linear
 * part is b = b0,...,1. Theoretically all mapping parameters can be derived
 * from parameters {@code gamma} and {@code a0} (the linear to non-linear
 * transition point) only. Note that {@code gamma} specifies the nominal gamma
 * value for the <em>forward</em> (i.e., linear to non-linear) mapping, e.g.,
 * gamma = 1/2.4 for sRGB. See Sec.3.7.6 of [1] for more details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 *
 */
public class GammaMappingFunction {
	
	/** Gamma mapping function for ITU-R BT.709 (see Table 3.1 of [1]). */
	public static GammaMappingFunction ITU709 = new GammaMappingFunction(1/2.222, 0.018); //, 4.5, 0.099);
	
	/** Gamma mapping function for sRGB (see Table 3.1 of [1]). 
	 * Note that we need to specify parameters s and d too to comply 
	 * strictly with the sRGB standard. */
	public static GammaMappingFunction sRGB = new GammaMappingFunction(1/2.4, 0.0031308, 12.92, 0.055);
	
	private final double gamma;
	private final double igamma;
	private final double a0;
	private final double b0;
	private final double s;
	private final double d;
	
	/**
	 * Constructor, derives parameters s and d from gamma and a0
	 * (see Eq. 3.35 in [1]).
	 * @param gamma &in; [0,1], the nominal gamma value for the forward mapping (e.g., gamma = 1/2.4 for sRGB)
	 * @param a0 &in; [0,1], the linear to non-linear transition point (e.g., a0 = 1/2.4 for sRGB)
	 */
	public GammaMappingFunction(double gamma, double a0) {
		this(gamma, a0, s(gamma, a0), d(gamma, a0));
	}
	
	/**
	 * Constructor, accepts explicit parameters s and d to allow for
	 * minor inaccuracies in published standards.
	 * 
	 * @param gamma &in; [0,1], the nominal gamma value for the forward mapping (e.g., gamma = 1/2.4 for sRGB)
	 * @param a0 &in; [0,1], the linear to non-linear transition point (e.g., a0 = 1/2.4 for sRGB)
	 * @param s &gt 0, the slope s of the linear section (e.g., s = 12.92 for sRGB)
	 * @param d &ge; 0 the offset d of the non-linear section (e.g., s = 0.055 for sRGB)
	 */
	public GammaMappingFunction(double gamma, double a0, double s, double d) {
		this.a0 = a0;
		this.b0 = s * a0;
		this.gamma = gamma;
		this.igamma = 1.0 / gamma;
		this.s = s;
		this.d = d;
	}

	/**
	 * Function s(gamma, a0) derives the slope s of the linear section
	 * from parameters gamma and a0 (see Eq. 3.35 in [1]).
	 * 
	 * @param gamma the nominal gamma value for the forward mapping
	 * @param a0 the linear to non-linear transition point
	 * @return the slope s of the linear section
	 */
	protected static double s(double gamma, double a0) {
		return gamma / (a0 * (gamma - 1) + Math.pow(a0, 1 - gamma));
	}
	
	/**
	 * Function d(gamma, a0) derives the offset d of the non-linear section
	 * from parameters gamma and a0 (see Eq. 3.35 in [1]).
	 *  
	 * @param gamma the nominal gamma value for the forward mapping
	 * @param a0 the linear to non-linear transition point
	 * @return the offset d of the non-linear section
	 */
	protected static double d(double gamma, double a0) {
		return 1.0 / (Math.pow(a0, gamma) * (gamma - 1) + 1) - 1;
	}
	
	/**
	 * Forward Gamma mapping (from linear to non-linear component values).
	 * 
	 * @param a linear component value in [0,1]
	 * @return the gamma-corrected (non-linear) component value
	 */
    public double applyFwd(double a) {
		return (a <= a0) ?
			s * a :
			(1 + d) * Math.pow(a, gamma) - d;
    }
    
    /**
     * Float version of {@link #applyFwd(double)}
     * @param a linear component value in [0,1]
     * @return the gamma-corrected (non-linear) component value
     */
    public float applyFwd(float a) {
		return (float) applyFwd((double) a);
    }
    
    /**
	 * Inverse Gamma mapping (from non-linear to linear component values).
	 * 
	 * @param b non-linear (Gamma-corrected) component value in [0,1]
	 * @return the linear component value
	 */
    public double applyInv(double b) {
    	return (b <= b0) ?
    		(b / s) :
			Math.pow((b + d) / (1 + d), igamma);
    }
    
    /**
     * Float version of {@link #applyInv(double)}
     * @param b non-linear (Gamma-corrected) component value in [0,1]
     * @return the linear component value
     */
    public float applyInv(float b) {
    	return (float) applyInv((double) b);
    }

}

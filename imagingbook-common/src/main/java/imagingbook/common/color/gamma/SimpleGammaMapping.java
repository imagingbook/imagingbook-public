package imagingbook.common.color.gamma;

/**
 * <p>
 * Defines the basic "gamma correction" used for converting linear to
 * non-linear color component values. The mapping function consists of a 
 * non-linear part only. Note that {@code gamma} specifies the nominal &gamma;
 * parameter for the <em>forward</em> (i.e., linear to non-linear) mapping, e.g.,
 * &gamma; ~ 1/2.2 for Adobe RGB. See Sec. 3.7.2 of [1] for more details.
 * </p>
 * <p>
 * [1] W. Burger, M.J. Burge, <em>Digital Image Processing &ndash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 * 
 * @author WB
 * @version 2022/11/14
 */
public class SimpleGammaMapping implements GammaMapping {
	
	private final double gamma;
	private final double igamma;
	
	public SimpleGammaMapping(double gamma) {
		this.gamma = gamma;
		this.igamma = 1.0 / gamma;
	}

	@Override
	public double applyFwd(double a) {
		return Math.pow(a, gamma);
	}

	@Override
	public double applyInv(double b) {
		return Math.pow(b, igamma);
	}

}

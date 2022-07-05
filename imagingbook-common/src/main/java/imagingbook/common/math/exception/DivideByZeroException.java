package imagingbook.common.math.exception;

public class DivideByZeroException extends ArithmeticException {
	private static final long serialVersionUID = 1L;
	private static String DefaultMessage = "zero denominator in division";
	
	public DivideByZeroException() {
		super(DefaultMessage);
	}
}
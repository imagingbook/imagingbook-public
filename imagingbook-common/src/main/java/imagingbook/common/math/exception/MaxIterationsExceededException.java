package imagingbook.common.math.exception;

public class MaxIterationsExceededException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public MaxIterationsExceededException(int maxIterations) {
		super(String.format("max. number of iterations (%d) exceeded", maxIterations));
	}
	
	public MaxIterationsExceededException() {
		super("max. number of iterations exceeded");
	}

}

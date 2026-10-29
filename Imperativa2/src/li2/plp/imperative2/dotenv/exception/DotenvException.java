package li2.plp.imperative2.dotenv.exception;

public class DotenvException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DotenvException(String message) {
		super(message);
	}

	public DotenvException(String message, Throwable cause) {
		super(message, cause);
	}
}


package exceptions;

public class SessionDoesNotExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1786672854484275957L;

	public SessionDoesNotExistException() {
		
	}

	public SessionDoesNotExistException(String message) {
		super(message);
	}

}

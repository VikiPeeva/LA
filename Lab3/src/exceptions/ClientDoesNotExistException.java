package exceptions;

public class ClientDoesNotExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5180512272705559802L;

	public ClientDoesNotExistException() {
		super();
	}

	public ClientDoesNotExistException(String message) {
		super(message);
	}

	
}

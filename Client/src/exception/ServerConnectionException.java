package exception;

/**
 * The exception for server connection error
 * 
 * @author Arnon  Ruangthanawes arua663
 */
public class ServerConnectionException extends Exception {
	private static final long serialVersionUID = -2073697167952512947L;

	public ServerConnectionException() {
	}

	public ServerConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerConnectionException(String message) {
		super(message);
	}

	public ServerConnectionException(Throwable cause) {
		super(cause);
	}
}
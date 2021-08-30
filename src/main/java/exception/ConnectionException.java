package exception;

public class ConnectionException extends RuntimeException {

    public ConnectionException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public ConnectionException(String msg) {
        super(msg);
    }
}

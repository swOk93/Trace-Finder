package exception;

public class RulebookFormatException extends Exception {

    public RulebookFormatException(final String msg) {
        super(msg);
    }

    public RulebookFormatException(final Throwable cause, final String msg) {
        super(msg, cause);
    }
}

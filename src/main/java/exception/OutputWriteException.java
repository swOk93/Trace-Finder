package exception;

public class OutputWriteException extends Exception {
    public OutputWriteException(final String path, final Throwable cause) {
        super("Could not write report to '%s'".formatted(path), cause);
    }
}

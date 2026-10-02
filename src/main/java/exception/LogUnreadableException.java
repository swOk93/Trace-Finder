package exception;

public class LogUnreadableException extends Exception {
    private final String path;

    public LogUnreadableException(final Throwable cause, final String path) {
        super(cause);
        this.path = path;
    }

    @Override
    public String toString() {
        return "Log file unreadable: '%s'".formatted(path);
    }
}

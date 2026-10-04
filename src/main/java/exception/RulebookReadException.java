package exception;

public class RulebookReadException extends Exception {
    private final String path;

    public RulebookReadException(final Throwable cause, final String path) {
        super(cause);
        this.path = path;
    }

    @Override 
    public  String toString() {
        return "Rulebook file unreadable: '%s'".formatted(path);
    }
}

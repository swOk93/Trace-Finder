package exception;

public class RulebookFormatException extends Exception {
    private final String path;

    public RulebookFormatException(final Throwable cause, final String path) {
        super(cause);
        this.path = path;
    }

    @Override 
    public  String toString() {
        return "Rulebook format unmatched: '%s'".formatted(path);
    }
}

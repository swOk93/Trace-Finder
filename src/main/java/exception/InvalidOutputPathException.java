package exception;

public class InvalidOutputPathException extends Exception {
    public InvalidOutputPathException(final String path) {
        super("Invalid output path: '%s'".formatted(path));
    }

    public InvalidOutputPathException(final String path, final Throwable cause) {
        super("Invalid output path: '%s'".formatted(path), cause);
    }
}

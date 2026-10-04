package model;

public class MalformedEntry {
    private final long line;
    private final String entry;

    public MalformedEntry(final long line,
                            final String entry) {
        this.line = line;
        this.entry = entry;
    }

    public long getLine() {
        return line;
    }

    public String getEntry() {
        return entry;
    }
}

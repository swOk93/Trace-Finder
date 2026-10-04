package model;

public class FlaggedEntry {
    private final String raw;
    private final int severity;

    public FlaggedEntry(final String raw, final int severity) {
        this.raw = raw;
        this.severity = severity;
    }

    public String getRaw() {
        return raw;
    }

    public int getSeverity() {
        return severity;
    }
}

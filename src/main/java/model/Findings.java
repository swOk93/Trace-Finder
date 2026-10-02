package model;

import java.util.HashMap;
import java.net.Inet4Address;

public class Findings {
    private final Map<String, Integer> levelToOccurrences;
    private final List<String> flaggedEntries;
    private final Map<Inet4Address, Integer> ipToOccurrences;
    private final List<UnknownPattern> unknownPatterns;

    public Findings() {
        levelToOccurrences = new HashMap<>();
        flaggedEntries = new ArrayList<>();
        ipToOccurrences = new HashMap<>();
        unknownPatterns = new ArrayList<>();
    }

    public Map<String, Integer> getLevelToOccurrences() {
        return levelToOccurrences;
    }

    public List<String> getFlaggedEntries() {
        return flaggedEntries;
    }

    public Map<Inet4Address, Integer> getIpToOccurrences() {
        return ipToOccurrences;
    }

    public List<UnknownPattern> getUnknownPatterns() {
        return unknownPatterns;
    }

    public static class UnknownPattern {
        private final long line;
        private final String entry;

        public UnknownPattern(final long line,
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
}

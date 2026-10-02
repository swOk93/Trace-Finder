package model;

import java.net.Inet4Address;
import java.time.LocalDateTime;

import Action;
import Level;
import Path;

public class Log {
    private final List<Entry> entries;
    private final List<MalformedEntry> malformedEntries;

    public Log() {
        entries = new ArrayList<>();
        malformedEntries = new ArrayList<>();
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public List<MalformedEntry> getMalformedEntries() {
        return malformedEntries;
    }

    public static class Entry {
        private final long line;
        private final String raw;

        private final LocalDateTime timestamp;
        private final Level level;
        private final Inet4Address address;
        private final Path path;
        private final Action action;

        public Entry(final long line,
                     final String raw,
                     final LocalDateTime timestamp,
                     final Level level,
                     final Inet4Address address,
                     final Path path,
                     final Action action) {
            this.line = line;
            this.raw = raw;
            this.timestamp = timestamp;
            this.level = level;
            this.address = address;
            this.path = path;
            this.action = action;
        }

        public long getLine() {
            return line;
        }

        public String getRaw() {
            return raw;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public Level getLevel() {
            return level;
        }

        public Inet4Address getAddress() {
            return address;
        }

        public Path getPath() {
            return path;
        }

        public Action getAction() {
            return action;
        }
    }

    public static class MalformedEntry {
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
}

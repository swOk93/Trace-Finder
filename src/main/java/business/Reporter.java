package business;

import java.io.IOException;
import java.net.Inet4Address;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;

import exception.InvalidOutputPathException;
import exception.OutputWriteException;
import model.Findings;
import model.FlaggedEntry;

public class Reporter {
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String LOG_HEADER        = "=== Log Analysis Report ===%nGenerated: %s%n";
    private static final String ACTIVITY_HEADER   = System.lineSeparator() + "--- Activity Summary ---" + System.lineSeparator();
    private static final String FLAGGED_HEADER    = System.lineSeparator() + "--- Flagged Entries ---" + System.lineSeparator();
    private static final String SUSPICIOUS_HEADER = System.lineSeparator() + "--- Suspicious Activity by IP ---" + System.lineSeparator();
    private static final String UNKNOWN_HEADER    = System.lineSeparator() + "--- Unknown Patterns ---" + System.lineSeparator();
    private static final String MALFORMED_HEADER  = System.lineSeparator() + "--- Malformed Lines ---" + System.lineSeparator();
    private static final String NONE_WERE_FOUND   = "none were found" + System.lineSeparator();

    public static void writeReport(final String filepath, final Findings findings) throws InvalidOutputPathException, OutputWriteException {
        final StringBuilder output = new StringBuilder();
        output.append(LOG_HEADER.formatted(LocalDateTime.now().format(TIMESTAMP_FORMAT)));
        
        appendActivitySummary(output, findings);
        appendFlaggedEntries(output, findings);
        appendSuspiciousActivity(output, findings);
        appendUnknownPatterns(output, findings);
        appendMalformedLines(output, findings);

        final Path path;
        try {
            path = Path.of(filepath);
        } catch (InvalidPathException e) {
            throw new InvalidOutputPathException(filepath, e);
        }
        try {
            Files.writeString(path, output.toString());
        } catch (IOException e) {
            throw new OutputWriteException(filepath, e);
        }
    }

    private static void appendActivitySummary(final StringBuilder output, final Findings findings) {
        output.append(ACTIVITY_HEADER);

        if (findings.getLevelToOccurrences().isEmpty()) {
            output.append(NONE_WERE_FOUND);
            return;
        }

        final int activityMax = findings.getLevelToOccurrences().values().stream()
                    .max(Integer::compareTo)
                    .orElse(0);
        final int activityLength = String.valueOf(activityMax).length();
        final String activityFormat = "%5s: %" + activityLength + "d%n";
        findings.getLevelToOccurrences().entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(entry -> output.append(activityFormat.formatted(entry.getKey(), entry.getValue())));
    }

    private static void appendFlaggedEntries(final StringBuilder output, final Findings findings) {
        output.append(FLAGGED_HEADER);

        if (findings.getFlaggedEntries().isEmpty()) {
            output.append(NONE_WERE_FOUND);
            return;
        }

        findings.getFlaggedEntries().stream()
            .sorted(Comparator.comparingInt(FlaggedEntry::getSeverity).reversed())
            .forEach(entry -> {
                output.append(entry.getRaw());
                output.append(System.lineSeparator());
            });
    }

    private static void appendSuspiciousActivity(final StringBuilder output, final Findings findings) {
        output.append(SUSPICIOUS_HEADER);

        if (findings.getIpToOccurrences().isEmpty()) {
            output.append(NONE_WERE_FOUND);
            return;
        }

        final int suspiciousMax = findings.getIpToOccurrences().values().stream()
                .max(Integer::compareTo)
                .orElse(0);
        final int suspiciousLength = String.valueOf(suspiciousMax).length();
        final String suspiciousFormat = "%15s: %" + suspiciousLength + "d entries%n";
        findings.getIpToOccurrences().entrySet().stream()
            .sorted(Map.Entry.<Inet4Address, Integer>comparingByValue().reversed())
            .forEach(entry -> output.append(suspiciousFormat.formatted(entry.getKey().getHostAddress(), entry.getValue())));
    }

    private static void appendUnknownPatterns(final StringBuilder output, final Findings findings) {
        output.append(UNKNOWN_HEADER);

        if (findings.getUnknownPatterns().isEmpty()) {
            output.append(NONE_WERE_FOUND);
            return;
        }

        findings.getUnknownPatterns().forEach(unknownPattern ->
            output.append("Line %s: %s%n".formatted(unknownPattern.getLine(), unknownPattern.getEntry()))
        );
    }

    private static void appendMalformedLines(final StringBuilder output, final Findings findings) {
        output.append(MALFORMED_HEADER);

        if (findings.getMalformedEntries().isEmpty()) {
            output.append(NONE_WERE_FOUND);
            return;
        }

        findings.getMalformedEntries().forEach(malformedEntry ->
            output.append("Line %s: %s%n".formatted(malformedEntry.getLine(), malformedEntry.getEntry()))
        );
    }
}

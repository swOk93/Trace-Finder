package business;

import java.net.Inet4Address;
import java.nio.file.InvalidPathException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;

import model.Findings;
import model.FlaggedEntry;
import model.MalformedEntry;

public class Reporter {
    final private static String LOG_HEADER =        "=== Log Analysis Report ===%nGenerated: %s%n";
    final private static String ACTIVITY_HEADER =   System.lineSeparator() + "--- Activity Summary ---" + System.lineSeparator();
    final private static String FLAGGED_HEADER =    System.lineSeparator() + "--- Flagged Entries ---" + System.lineSeparator();
    final private static String SUSPICIOUS_HEADER = System.lineSeparator() + "--- Suspicious Activity by IP ---" + System.lineSeparator();
    final private static String UNKNOWN_HEADER =    System.lineSeparator() + "--- Unknown Patterns ---" + System.lineSeparator();
    final private static String MALFORMED_HEADER =  System.lineSeparator() + "--- Malformed Lines ---" + System.lineSeparator();
    final private static String NONE_WERE_FOUND =   "none were found" + System.lineSeparator();

    public static void writeReport(final String filepath, final Findings findings) {
        final StringBuilder output = new StringBuilder();
        output.append(LOG_HEADER.formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        
        output.append(ACTIVITY_HEADER);
        final int activityMax = findings.getLevelToOccurrences().values().stream()
                .max(Integer::compareTo)
                .orElse(0);
        final int activityLength = String.valueOf(activityMax).length();
        final String activityFormat = "%5s: %" + activityLength + "d";
        findings.getLevelToOccurrences().entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(entry -> {
                output.append(activityFormat.formatted(
                        entry.getKey(),
                        entry.getValue()
                ));
                output.append(System.lineSeparator());
            });

        output.append(FLAGGED_HEADER);
        findings.getFlaggedEntries().stream()
            .sorted(Comparator.comparingInt(FlaggedEntry::getSeverity).reversed())
            .forEach(entry -> {
                output.append(entry.getRaw());
                output.append(System.lineSeparator());
            });

        output.append(SUSPICIOUS_HEADER);
        final int suspiciousMax = findings.getIpToOccurrences().values().stream()
                .max(Integer::compareTo)
                .orElse(0);
        final int suspiciousLength = String.valueOf(suspiciousMax).length();
        final String suspiciousFormat = "%15s: %" + suspiciousLength + "d entries";
        findings.getIpToOccurrences().entrySet().stream()
            .sorted(Map.Entry.<Inet4Address, Integer>comparingByValue().reversed())
            .forEach(entry -> {
                output.append(suspiciousFormat.formatted(
                        entry.getKey().getHostAddress(),
                        entry.getValue()
                ));
                output.append(System.lineSeparator());
            });
            
        output.append(UNKNOWN_HEADER);
        findings.getUnknownPatterns().forEach(unknownPattern ->
            output.append("Line %s: %s%n".formatted(unknownPattern.getLine(), unknownPattern.getEntry()))
        );

        output.append(MALFORMED_HEADER);
        findings.getMalformedEntries().forEach(malformedEntry ->
            output.append("Line %s: %s%n".formatted(malformedEntry.getLine(), malformedEntry.getEntry()))
        );

        System.out.print(output.toString());  
    }
}

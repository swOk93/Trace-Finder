package business;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import model.Action;
import model.Findings;
import model.Level;
import model.Log;
import model.MalformedEntry;
import model.Path;
import model.Rulebook;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnalyzerTest {
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void analyzeKnownLevelsCountsOccurrences() {
        final Rulebook rulebook = new Rulebook();
        rulebook.getLevelToSeverity().put("INFO", 1);
        rulebook.getLevelToSeverity().put("WARN", 1);

        final Log log = new Log();
        log.getEntries().add(entry(1, "2024-03-15 02:14:08", "INFO", 192, 168, 1, 45, "/login", "success"));
        log.getEntries().add(entry(2, "2024-03-15 02:14:33", "WARN", 203, 0, 113, 42, "/login", "failed"));
        log.getEntries().add(entry(3, "2024-03-15 02:14:35", "WARN", 203, 0, 113, 42, "/login", "failed"));

        final Findings findings = Analyzer.analyzeLog(log, rulebook);

        assertEquals(1, findings.getLevelToOccurrences().get("INFO"));
        assertEquals(2, findings.getLevelToOccurrences().get("WARN"));
    }

    @Test
    void analyzeUnknownLevelRecordsUnknownPattern() {
        final Rulebook rulebook = new Rulebook();
        rulebook.getLevelToSeverity().put("INFO", 1);

        final Log log = new Log();
        log.getEntries().add(entry(1, "2024-03-15 03:42:11", "CRIT", 203, 0, 113, 42, "/admin", "escalated"));

        final Findings findings = Analyzer.analyzeLog(log, rulebook);

        assertEquals(1, findings.getUnknownPatterns().size());
        assertEquals(1, findings.getUnknownPatterns().get(0).getLine());
        assertEquals("2024-03-15 03:42:11 | CRIT | 203.0.113.42 | /admin | escalated", findings.getUnknownPatterns().get(0).getEntry());
    }

    @Test
    void analyzeDifferentCaseRecordsUnknownPattern() {
        final Rulebook rulebook = new Rulebook();
        rulebook.getLevelToSeverity().put("INFO", 1);

        final Log log = new Log();

        log.getEntries().add(entry(1, "2024-03-15 08:20:00", "info", 192, 168, 1, 20, "/dashboard", "accessed"));

        final Findings findings = Analyzer.analyzeLog(log, rulebook);

        assertEquals(1, findings.getUnknownPatterns().size());
        assertEquals(1, findings.getUnknownPatterns().get(0).getLine());
        assertEquals("2024-03-15 08:20:00 | info | 192.168.1.20 | /dashboard | accessed", findings.getUnknownPatterns().get(0).getEntry());
    }

    @Test
    void analyzeSeverityBelowThreeDoesNotFlag() {
        final Rulebook rulebook = new Rulebook();
        rulebook.getLevelToSeverity().put("INFO", 1);

        final Log log = new Log();

        log.getEntries().add(entry(1, "2024-03-15 02:14:08", "INFO", 192, 168, 1, 45, "/login", "success"));

        final Findings findings = Analyzer.analyzeLog(log, rulebook);

        assertEquals(0, findings.getFlaggedEntries().size());
    }

    @Test
    void analyzeSeverityThreeFlags() {
        final Rulebook rulebook = new Rulebook();
        rulebook.getLevelToSeverity().put("INFO", 3);

        final Log log = new Log();

        log.getEntries().add(entry(1, "2024-03-15 02:14:08", "INFO", 192, 168, 1, 45, "/login", "success"));

        final Findings findings = Analyzer.analyzeLog(log, rulebook);

        assertEquals(1, findings.getLevelToOccurrences().get("INFO"));
        assertEquals(1, findings.getFlaggedEntries().size());
        assertEquals("2024-03-15 02:14:08 | INFO | 192.168.1.45 | /login | success", findings.getFlaggedEntries().get(0).getRaw());
        assertEquals(3, findings.getFlaggedEntries().get(0).getSeverity());
        assertEquals(0, findings.getUnknownPatterns().size());
        assertEquals(1, findings.getIpToOccurrences().get(ip(192, 168, 1, 45)));
    }

    @Test
    void analyzeSuspiciousIpUsesTotalOccurrenceCount() {
        final Rulebook rulebook = new Rulebook();
        rulebook.getLevelToSeverity().put("INFO", 1);
        rulebook.getLevelToSeverity().put("WARN", 3);

        final Log log = new Log();

        log.getEntries().add(entry(1, "2024-03-15 02:14:08", "INFO", 192, 168, 1, 45, "/login", "success"));
        log.getEntries().add(entry(2, "2024-03-15 02:14:09", "WARN", 192, 168, 1, 45, "/login", "failed"));

        final Findings findings = Analyzer.analyzeLog(log, rulebook);

        assertEquals(2, findings.getIpToOccurrences().get(ip(192, 168, 1, 45)));
    }

    @Test
    void analyzeCopiesMalformedEntries() {
        final Rulebook rulebook = new Rulebook();
        final Log log = new Log();

        log.getMalformedEntries().add(new MalformedEntry(1, "test"));
        log.getMalformedEntries().add(new MalformedEntry(2, "test2"));

        final Findings findings = Analyzer.analyzeLog(log, rulebook);

        assertEquals(2, findings.getMalformedEntries().size());

        assertEquals(1, findings.getMalformedEntries().get(0).getLine());
        assertEquals("test", findings.getMalformedEntries().get(0).getEntry());

        assertEquals(2, findings.getMalformedEntries().get(1).getLine());
        assertEquals("test2", findings.getMalformedEntries().get(1).getEntry());
    }

    private static Log.Entry entry(final long line,
            final String timestamp,
            final String level,
            final int octet1,
            final int octet2,
            final int octet3,
            final int octet4,
            final String path,
            final String action) {

        final String ip = octet1 + "." + octet2 + "." + octet3 + "." + octet4;
        
        return new Log.Entry(
                line,
                String.join(" | ", timestamp, level, ip, path, action),
                LocalDateTime.parse(timestamp, TIMESTAMP_FORMAT),
                new Level(level),
                ip(octet1, octet2, octet3, octet4),
                new Path(path),
                new Action(action)
        );
    }

    private static Inet4Address ip(final int a, final int b, final int c, final int d) {
        try {
            return (Inet4Address) InetAddress.getByAddress(new byte[] {(byte) a, (byte) b, (byte) c, (byte) d});
        } catch (UnknownHostException e) {
            throw new AssertionError(e);
        }
    }
}

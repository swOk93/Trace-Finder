package business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import exception.InvalidOutputPathException;
import model.Findings;
import model.FlaggedEntry;
import model.MalformedEntry;

public class ReporterTest {
    
    @Test 
    void writeReportToStringHappyPath() {
        final Findings findings = new Findings();

        findings.getLevelToOccurrences().put("ERROR", 6);
        findings.getLevelToOccurrences().put("INFO", 56);

        findings.getFlaggedEntries().add(new FlaggedEntry("2024-03-15 02:14:33 | WARN | 203.0.113.42 | /login | failed", 2));
        findings.getFlaggedEntries().add(new FlaggedEntry("2024-03-15 02:16:02 | ALERT | 203.0.113.42 | /etc/passwd | read", 4));

        findings.getIpToOccurrences().put(ip(10, 0, 0, 5), 9);
        findings.getIpToOccurrences().put(ip(203, 0, 113, 42), 15);

        findings.getUnknownPatterns().add(new Findings.UnknownPattern(16, "2024-03-15 03:42:11 | CRIT | 203.0.113.42 | /admin | escalated"));

        findings.getMalformedEntries().add(new MalformedEntry(25, "2024-03-15 05:00:00 | WARN | 203.0.113.42 | /login"));

        final String actual = Reporter.writeReportToString(findings, LocalDateTime.of(2026, 10, 7, 8, 36, 51));

        final String expected = """
            === Log Analysis Report ===
            Generated: 2026-10-07 08:36:51

            --- Activity Summary ---
             INFO: 56
            ERROR:  6

            --- Flagged Entries ---
            2024-03-15 02:16:02 | ALERT | 203.0.113.42 | /etc/passwd | read
            2024-03-15 02:14:33 | WARN | 203.0.113.42 | /login | failed

            --- Suspicious Activity by IP ---
               203.0.113.42: 15 entries
                   10.0.0.5:  9 entries

            --- Unknown Patterns ---
            Line 16: 2024-03-15 03:42:11 | CRIT | 203.0.113.42 | /admin | escalated

            --- Malformed Lines ---
            Line 25: 2024-03-15 05:00:00 | WARN | 203.0.113.42 | /login
            """;

        assertEquals(expected, actual);
    }

    @Test 
    void writeReportToStringEmptyFindings() {
        final Findings findings = new Findings();

        final String actual = Reporter.writeReportToString(findings, LocalDateTime.of(2026, 10, 7, 8, 36, 51));
        
        final String expected = """
            === Log Analysis Report ===
            Generated: 2026-10-07 08:36:51

            --- Activity Summary ---
            none were found

            --- Flagged Entries ---
            none were found

            --- Suspicious Activity by IP ---
            none were found

            --- Unknown Patterns ---
            none were found

            --- Malformed Lines ---
            none were found
            """;

        assertEquals(expected, actual);
    }

    @Test 
    void writeReportToStringPartiallyPopulated() {
        final Findings findings = new Findings();

        findings.getLevelToOccurrences().put("INFO", 1);
        findings.getIpToOccurrences().put(ip(10, 0, 0, 5), 1);
        findings.getMalformedEntries().add(new MalformedEntry(25, "2024-03-15 05:00:00 | WARN | 203.0.113.42 | /login"));

        final String actual = Reporter.writeReportToString(findings, LocalDateTime.of(2026, 10, 7, 8, 36, 51));

        final String expected = """
            === Log Analysis Report ===
            Generated: 2026-10-07 08:36:51

            --- Activity Summary ---
             INFO: 1

            --- Flagged Entries ---
            none were found

            --- Suspicious Activity by IP ---
                   10.0.0.5: 1 entries

            --- Unknown Patterns ---
            none were found

            --- Malformed Lines ---
            Line 25: 2024-03-15 05:00:00 | WARN | 203.0.113.42 | /login
            """;
        
        assertEquals(expected, actual);
    }

    @Test
    void writeReportToFileInvalidPath() {
        final Findings findings = new Findings();

        assertThrows(
            InvalidOutputPathException.class,
            () -> Reporter.writeReportToFile("\0invalid", findings)
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

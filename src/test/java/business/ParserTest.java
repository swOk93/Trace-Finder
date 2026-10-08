package business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.RulebookFormatException;
import model.Log;
import model.Rulebook;

public class ParserTest {

    private Log log;
    private Rulebook rulebook;

    @BeforeEach 
    void setUp() {
        log = new Log();
        rulebook = new Rulebook();
    }

    // Log tests

    @Test
    void parsesValidLogLine() {
        Parser.parseLogLine("2024-03-15 02:14:08 | INFO  | 192.168.1.45  | /login        | success", 2, log);

        assertEquals(1, log.getEntries().size());
        assertEquals(0, log.getMalformedEntries().size());
        assertEquals("2024-03-15 02:14:08 | INFO  | 192.168.1.45  | /login        | success", log.getEntries().get(0).getRaw());
        assertEquals("INFO", log.getEntries().get(0).getLevel().getValue());
        assertEquals("192.168.1.45", log.getEntries().get(0).getAddress().getHostAddress());
        assertEquals("/login", log.getEntries().get(0).getPath().getValue());
        assertEquals("success", log.getEntries().get(0).getAction().getValue());
    }

    @Test
    void rejectsLogLineWithBadTimestamp() {
        Parser.parseLogLine("not-a-timestamp | INFO | 192.168.1.45 | /home | accessed", 2, log);
        assertEquals("not-a-timestamp | INFO | 192.168.1.45 | /home | accessed", log.getMalformedEntries().get(0).getEntry());
    }

    @Test
    void rejectsLogLineWithBadTimestamp2() {
        Parser.parseLogLine("2024-33-15 02:17:00 | INFO | 192.168.1.45 | /home | accessed", 2, log);
        assertEquals("2024-33-15 02:17:00 | INFO | 192.168.1.45 | /home | accessed", log.getMalformedEntries().get(0).getEntry());
    }

    @Test
    void rejectsLogLineWithBadTimestamp3() {
        Parser.parseLogLine("2024-03-15 02:17 | INFO | 192.168.1.45 | /home | accessed", 2, log);
        assertEquals("2024-03-15 02:17 | INFO | 192.168.1.45 | /home | accessed", log.getMalformedEntries().get(0).getEntry());
    }

    @Test
    void rejectsLogLineWithBadTimestamp4() {
        Parser.parseLogLine("2024-03 02:17:00 | INFO | 192.168.1.45 | /home | accessed", 2, log);
        assertEquals("2024-03 02:17:00 | INFO | 192.168.1.45 | /home | accessed", log.getMalformedEntries().get(0).getEntry());
    }

    @Test
    void rejectsLogLineWithMissingField() {
        Parser.parseLogLine("2024-03-15 02:17:00 | WARN | 203.0.113.42 | /login", 2, log);
        assertEquals("2024-03-15 02:17:00 | WARN | 203.0.113.42 | /login", log.getMalformedEntries().get(0).getEntry()); // need to understand how it works
    }

    @Test
    void rejectsBlankLogLine() {
        Parser.parseLogLine("", 5, log);

        assertEquals("", log.getMalformedEntries().get(0).getEntry());
        assertEquals(5, log.getMalformedEntries().get(0).getLine());
    }

    @Test
    void rejectsLogLineWithTooManyFields() {
        Parser.parseLogLine("2024-03-15 02:14:08 | INFO | 192.168.1.45 | /login | success | extra", 1, log);

        assertEquals("2024-03-15 02:14:08 | INFO | 192.168.1.45 | /login | success | extra", log.getMalformedEntries().get(0).getEntry());
    }

    @Test
    void rejectsLogLineWithWrongDelimiter() {
        Parser.parseLogLine("2024-03-15 02:17:00 | WARN ? 203.0.113.42  /login", 0, log);

        assertEquals("2024-03-15 02:17:00 | WARN ? 203.0.113.42  /login", log.getMalformedEntries().get(0).getEntry());
    }

    @Test
    void rejectsLogLineWithInvalidIp() {
        String line = "2024-03-15 02:14:08 | INFO | not-an-ip | /login | success";
        Parser.parseLogLine(line, 0, log);

        assertEquals(line, log.getMalformedEntries().get(0).getEntry());
    }

    @Test
    void rejectsIpv6Address() {
        String line = "2024-03-15 02:14:08 | INFO | 2001:db8::1 | /login | success";
        Parser.parseLogLine(line, 0, log);

        assertEquals(line, log.getMalformedEntries().get(0).getEntry());
    }

    // Rulebook tests

    @Test 
    void parseValidRulebookLine() throws RulebookFormatException {

        Parser.parseRulebookLine("WARN,3", rulebook);

        assertEquals(1, rulebook.getLevelToSeverity().size());
        assertEquals(3, rulebook.getLevelToSeverity().get("WARN"));
    }

    @Test 
    void parseValidRulebook() throws RulebookFormatException {

        Parser.parseRulebookLine("INFO,1", rulebook);
        Parser.parseRulebookLine("WARN,3", rulebook);
        Parser.parseRulebookLine("ERROR,5", rulebook);
        Parser.parseRulebookLine("ALERT,9", rulebook);

        assertEquals(4, rulebook.getLevelToSeverity().size());
        assertEquals(1, rulebook.getLevelToSeverity().get("INFO"));
        assertEquals(3, rulebook.getLevelToSeverity().get("WARN"));
        assertEquals(5, rulebook.getLevelToSeverity().get("ERROR"));
        assertEquals(9, rulebook.getLevelToSeverity().get("ALERT"));
        assertEquals(null, rulebook.getLevelToSeverity().get("Alert"));
        assertEquals(null, rulebook.getLevelToSeverity().get("info"));

    }

    @Test 
    void missingSeverityRulebook() throws RulebookFormatException {

        assertThrows(
            RulebookFormatException.class, 
            () -> Parser.parseRulebookLine("WARN,", rulebook)
        );
    }

    @Test
    void rejectsMissingLevelRulebook() throws RulebookFormatException {

        assertThrows(
            RulebookFormatException.class, 
            () -> Parser.parseRulebookLine(",3", rulebook)
        );
    }

    @Test
    void rejectsNonNumericSeverityRulebook() throws RulebookFormatException {

        assertThrows(
            RulebookFormatException.class, 
            () -> Parser.parseRulebookLine("WARN, abs", rulebook)
        );
    }

    @Test
    void rejectsTooManyPartsRulebook() throws RulebookFormatException {
        
        assertThrows(
            RulebookFormatException.class, 
            () -> Parser.parseRulebookLine("WARN, 3, 4", rulebook)
        );
        
        assertThrows(
            RulebookFormatException.class, 
            () -> Parser.parseRulebookLine("WARN, 3, 4, Warn, abc, 5", rulebook)
        );

        
        assertThrows(
            RulebookFormatException.class, 
            () -> Parser.parseRulebookLine("WARN, 3, 4, 5, 6, 7", rulebook)
        );
    }

    @Test
    void rejectsBlankLineRulebook() throws RulebookFormatException {
        
        assertThrows(
            RulebookFormatException.class, 
            () -> Parser.parseRulebookLine("", rulebook)
        );
    }

    @Test 
    void rejectsOnlyCommaRulebook() throws RulebookFormatException {
        
        assertThrows(
            RulebookFormatException.class, 
            () -> Parser.parseRulebookLine(",", rulebook)
        );
    }
}
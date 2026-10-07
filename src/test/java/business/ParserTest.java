package business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.RulebookFormatException;

import model.Rulebook;

public class ParserTest {
    private Rulebook rulebook;

    @BeforeEach 
    void setUp() {
        rulebook = new Rulebook();
    }

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
    void rejectsMissingLevel() throws RulebookFormatException {

        assertThrows(
            RulebookFormatException.class, 
            () -> Parser.parseRulebookLine(",3", rulebook)
        );
    }

    @Test
    void rejectsNonNumericSeverity() throws RulebookFormatException {

        assertThrows(
            RulebookFormatException.class, 
            () -> Parser.parseRulebookLine("WARN, abs", rulebook)
        );
    }

    @Test
    void rejectsTooManyParts() throws RulebookFormatException {
        
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
    void rejectsBlankLine() throws RulebookFormatException {
        
        assertThrows(
            RulebookFormatException.class, 
            () -> Parser.parseRulebookLine("", rulebook)
        );
    }

    @Test 
    void rejectsOnlyComma() throws RulebookFormatException {
        
        assertThrows(
            RulebookFormatException.class, 
            () -> Parser.parseRulebookLine(",", rulebook)
        );
    }
}
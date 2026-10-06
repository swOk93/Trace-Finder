package business;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import exception.RulebookFormatException;

import model.Rulebook;

public class ParserTest {

    @Test 
    void parseRulebook() throws RulebookFormatException {

        Rulebook rulebook = new Rulebook();

        Parser.parseRulebookLine("WARN,3", rulebook);

        assertEquals(3, rulebook.getLevelToSeverity().get("WARN"));
    }
}
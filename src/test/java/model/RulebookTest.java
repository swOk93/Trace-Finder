package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RulebookTest {
    
    @Test 
    void ruleBookStoresSeverityForLevel() {
        Rulebook rulebook = new Rulebook();

        rulebook.getLevelToSeverity().put("WARN", 3);

        assertEquals(3, rulebook.getLevelToSeverity().get("WARN"));
        assertEquals(null, rulebook.getLevelToSeverity().get("Warn"));

    }
}

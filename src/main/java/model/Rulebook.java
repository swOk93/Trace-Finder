package model;

import java.util.HashMap;
import java.util.Map;

public class Rulebook {
    private final Map<String, Integer> levelToSeverity;

    public Rulebook() {
        levelToSeverity = new HashMap<>();
    }

    public Map<String, Integer> getLevelToSeverity() {
        return levelToSeverity;
    }
}

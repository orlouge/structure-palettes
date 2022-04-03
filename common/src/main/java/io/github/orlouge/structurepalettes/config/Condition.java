package io.github.orlouge.structurepalettes.config;

public class Condition {
    public final String key, value;
    public final boolean negate;

    public Condition(String key, String value, boolean negate) {
        this.key = key;
        this.value = value;
        this.negate = negate;
    }
}

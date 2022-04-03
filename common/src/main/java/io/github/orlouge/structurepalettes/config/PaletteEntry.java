package io.github.orlouge.structurepalettes.config;

import net.minecraft.util.Pair;

import java.util.List;

public class PaletteEntry {
    public final List<Pair<Double, String>> groups;
    public final List<Mapping> mappings;

    public PaletteEntry(List<Pair<Double, String>> groups, List<Mapping> mappings) {
        this.groups = groups;
        this.mappings = mappings;
    }

    public static class Mapping {
        public final String source;
        public final TargetEntry target;
        public final List<Condition> conditions;

        public Mapping(String source, TargetEntry target, List<Condition> conditions) {
            this.source = source;
            this.target = target;
            this.conditions = conditions;
        }
    }
}

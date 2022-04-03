package io.github.orlouge.structurepalettes.config;

import net.minecraft.util.Pair;

import java.util.List;

public class TargetEntry {
    public final List<Pair<Double, String>> targets;

    public TargetEntry(List<Pair<Double, String>> targets) {
        this.targets = targets;
    }
}

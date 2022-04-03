package io.github.orlouge.structurepalettes.palettes;

import io.github.orlouge.structurepalettes.config.PaletteEntry;
import net.minecraft.util.Pair;

import java.util.*;

public class PaletteManager {
    private static final Map<String, PaletteGroup> groups = new HashMap<>();

    public static void load(Collection<PaletteEntry> entries) {
        Map<String, List<Pair<Double, Palette>>> tmpGroups = new HashMap<>();

        for (PaletteEntry entry : entries) {
            for (Pair<Double, String> groupEntry : entry.groups) {
                tmpGroups.computeIfAbsent(groupEntry.getRight(), g -> new LinkedList<>())
                        .add(new Pair<>(groupEntry.getLeft(), Palette.fromEntry(entry, groupEntry.getRight())));
            }
        }

        tmpGroups.forEach((g, list) -> groups.put(g, new PaletteGroup(list)));
    }

    public static PaletteGroup getGroup(String paletteGroup) {
        return groups.get(paletteGroup);
    }

    public static Palette samplePalette(String rootGroup) {
        return PaletteGroup.samplePalette(rootGroup, groups);
    }
}

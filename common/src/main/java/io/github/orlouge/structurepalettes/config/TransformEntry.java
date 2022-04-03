package io.github.orlouge.structurepalettes.config;

import net.minecraft.util.Identifier;

public class TransformEntry {
    public final String transformGroup;
    public final String paletteGroup;
    public final double chance;
    public final int order;
    public final String structure, biome;
    public final boolean addToBiomeList;

    public TransformEntry(String transformGroup, String paletteGroup, double chance, int order, String structure, String biome, boolean addToBiomeList) {
        this.transformGroup = transformGroup;
        this.paletteGroup = paletteGroup;
        this.chance = chance;
        this.order = order;
        this.structure = structure;
        this.biome = biome;
        this.addToBiomeList = addToBiomeList;
    }
}

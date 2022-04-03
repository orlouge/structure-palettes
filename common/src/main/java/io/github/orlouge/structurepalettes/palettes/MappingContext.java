package io.github.orlouge.structurepalettes.palettes;

import net.minecraft.util.Identifier;

public class MappingContext {
    public Identifier feature = null;
    public Identifier piece = null;
    public Identifier biome = null;
    public Identifier poolelement = null;

    public MappingContext() {}

    public MappingContext(MappingContext ctx) {
        this.feature = ctx.feature;
        this.piece = ctx.piece;
        this.biome = ctx.biome;
        this.poolelement = ctx.poolelement;
    }
}

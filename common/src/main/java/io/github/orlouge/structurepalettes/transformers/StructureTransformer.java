package io.github.orlouge.structurepalettes.transformers;

import io.github.orlouge.structurepalettes.palettes.MappingContext;
import io.github.orlouge.structurepalettes.palettes.Palette;
import io.github.orlouge.structurepalettes.palettes.PaletteManager;
import io.github.orlouge.structurepalettes.config.TransformEntry;
import net.minecraft.block.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class StructureTransformer {
    public static StructureTransformer fromEntry(TransformEntry entry, Random rng) {
        Palette palette = PaletteManager.samplePalette(entry.paletteGroup);
        if (palette != null) {
            return new Simple(palette, new MappingContext(), rng);
        } else {
            return new Chain();
        }
    }

    public static StructureTransformer chain(Collection<StructureTransformer> transformer) {
        return new Chain(transformer);
    }

    public abstract BlockState transform(BlockState state);

    public abstract StructureTransformer withContext(Consumer<MappingContext> f);

    public abstract boolean isNop();

    private static class Simple extends StructureTransformer {
        private final Palette palette;
        private final Random rng;
        private final MappingContext ctx;

        private Simple(Palette palette, MappingContext ctx, Random rng) {
            this.palette = palette;
            this.rng = rng;
            this.ctx = ctx;
        }

        public BlockState transform(BlockState state) {
            return state.getBlock().getRegistryEntry().getKey()
                        .flatMap(k -> Optional.ofNullable(this.palette.transform(k.getValue(), this.ctx, this.rng)))
                        .flatMap(Registry.BLOCK::getOrEmpty)
                        .map(newBlock -> newBlock.getStateWithProperties(state))
                        .orElse(state);
        }

        @Override
        public StructureTransformer withContext(Consumer<MappingContext> f) {
            MappingContext newCtx = new MappingContext(this.ctx);
            f.accept(newCtx);
            return new Simple(this.palette, newCtx, this.rng);
        }

        @Override
        public boolean isNop() {
            return false;
        }
    }

    private static class Chain extends StructureTransformer {
        private final Collection<StructureTransformer> transformers;

        private Chain() {
            this.transformers = Collections.emptyList();
        }

        private Chain(Collection<StructureTransformer> transformers) {
            this.transformers = transformers;
        }

        public BlockState transform(BlockState state) {
            for (StructureTransformer transformer : this.transformers) {
                state = transformer.transform(state);
            }
            return state;
        }

        @Override
        public StructureTransformer withContext(Consumer<MappingContext> f) {
            return new Chain(transformers.stream().map(t -> t.withContext(f)).collect(Collectors.toList()));
        }

        @Override
        public boolean isNop() {
            return this.transformers.isEmpty();
        }
    }
}

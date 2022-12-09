package io.github.orlouge.structurepalettes.mixin;

import io.github.orlouge.structurepalettes.transformers.StructureTransformer;
import io.github.orlouge.structurepalettes.transformers.StructureTransformerManager;
import io.github.orlouge.structurepalettes.transformers.StructureTransformerProvider;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ConfiguredFeature.class)
public abstract class ConfiguredFeatureMixin implements StructureTransformerProvider {
    private StructureTransformerManager.Pool structureTransformerPool = null;

    @Override
    public StructureTransformer getStructureTransformer(Identifier featureIdentifier, RegistryEntry<Biome> biome, Random rng) {
        if (this.structureTransformerPool == null) {
            this.structureTransformerPool = StructureTransformerManager.getPool(featureIdentifier);
        }
        StructureTransformer transformer = this.structureTransformerPool.sample(biome, rng);
        if (biome != null) {
            return biome.getKey().map(k -> transformer.withContext(ctx -> { ctx.biome = k.getValue(); }))
                    .orElse(transformer);
        }
        return transformer;
    }
}

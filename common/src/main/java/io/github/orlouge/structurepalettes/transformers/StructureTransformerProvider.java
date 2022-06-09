package io.github.orlouge.structurepalettes.transformers;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public interface StructureTransformerProvider {
    StructureTransformer getStructureTransformer(Identifier structureIdentifier, RegistryEntry<Biome> biome, Random rng);
}

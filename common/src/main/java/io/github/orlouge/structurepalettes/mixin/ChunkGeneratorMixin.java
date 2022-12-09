package io.github.orlouge.structurepalettes.mixin;

import io.github.orlouge.structurepalettes.interfaces.HasModifiedBiomeList;
import io.github.orlouge.structurepalettes.transformers.StructureTransformerManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.LinkedList;
import java.util.List;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @ModifyVariable(
            method = "trySetStructureStart(Lnet/minecraft/structure/StructureSet$WeightedEntry;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/structure/StructureTemplateManager;JLnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/ChunkSectionPos;)Z",
            at = @At("STORE")
    )
    public RegistryEntryList<Biome> modifyStructureBiomes(RegistryEntryList<Biome> biomes, StructureSet.WeightedEntry weightedEntry, StructureAccessor structureAccessor, DynamicRegistryManager dynamicRegistryManager) {
        Structure structure = weightedEntry.structure().value();
        if (structure instanceof HasModifiedBiomeList modifiedBiomes && modifiedBiomes.getModifiedBiomeList() == null) {
            Identifier structureId = weightedEntry.structure().getKey().get().getValue();
            List<RegistryEntry<Biome>> entries = new LinkedList<>();
            boolean[] modified = {false};
            for (Identifier biomeId : StructureTransformerManager.getAdditionalBiomes(structureId)) {
                RegistryKey<Biome> biomeKey = RegistryKey.of(RegistryKeys.BIOME, biomeId);
                dynamicRegistryManager.get(RegistryKeys.BIOME).getEntry(biomeKey).ifPresent(entry -> {
                    entries.add(entry);
                    modified[0] = true;
                });
            }
            RegistryEntryList<Biome> entryList;
            if (modified[0]) {
                entries.addAll(biomes.stream().toList());
                entryList = RegistryEntryList.of(entries);
            } else {
                entryList = biomes;
            }
            modifiedBiomes.setModifiedBiomeList(entryList);
            return entryList;
        }
        return biomes;
    }
}

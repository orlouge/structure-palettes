package io.github.orlouge.structurepalettes.mixin;

import io.github.orlouge.structurepalettes.StructurePalettesMod;
import io.github.orlouge.structurepalettes.interfaces.HasModifiedBiomeList;
import io.github.orlouge.structurepalettes.proxy.RegistryEntryListProxy;
import io.github.orlouge.structurepalettes.transformers.StructureTransformerManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.LinkedList;
import java.util.List;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @ModifyVariable(
            method = "method_41044(Lnet/minecraft/structure/StructureSet$WeightedEntry;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/structure/StructureManager;JLnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/ChunkSectionPos;)Z",
            at = @At("STORE")
    )
    public RegistryEntryList<Biome> modifyStructureBiomes(RegistryEntryList<Biome> biomes, StructureSet.WeightedEntry weightedEntry, StructureAccessor structureAccessor, DynamicRegistryManager dynamicRegistryManager) {
        ConfiguredStructureFeature<?, ?> structure = weightedEntry.structure().value();
        if (structure instanceof HasModifiedBiomeList modifiedBiomes && modifiedBiomes.getModifiedBiomeList() == null) {
            Identifier structureId = weightedEntry.structure().getKey().get().getValue();
            List<RegistryEntry<Biome>> entries = new LinkedList<>();
            boolean[] modified = {false};
            for (Identifier biomeId : StructureTransformerManager.getAdditionalBiomes(structureId)) {
                RegistryKey<Biome> biomeKey = RegistryKey.of(Registry.BIOME_KEY, biomeId);
                dynamicRegistryManager.get(Registry.BIOME_KEY).getEntry(biomeKey).ifPresent(entry -> {
                    entries.add(entry);
                    modified[0] = true;
                    StructurePalettesMod.LOGGER.info("Added " + structureId + " to " + biomeId);
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

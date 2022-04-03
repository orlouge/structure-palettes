package io.github.orlouge.structurepalettes.transformers;

import io.github.orlouge.structurepalettes.StructurePalettesMod;
import io.github.orlouge.structurepalettes.config.TransformEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;

import java.util.*;
import java.util.stream.Collectors;

public class StructureTransformerManager {
    private static final Collection<TransformEntry> transformEntries = new LinkedList<>();

    public static void load(Collection<TransformEntry> entries) {
        transformEntries.addAll(entries);
    }

    public static Pool getPool(Identifier structureIdentifier) {
        return new Pool(
                transformEntries
                        .stream()
                        .filter(entry -> entry.structure == null || new Identifier(entry.structure).equals(structureIdentifier))
                        .sorted(Comparator.comparing(entry -> entry.order))
                        .collect(Collectors.toList())
        );
    }

    public static Collection<Identifier> getAdditionalBiomes(Identifier structureIdentifier) {
        return transformEntries
                .stream()
                .filter(entry -> entry.addToBiomeList &&
                                 entry.structure != null &&
                                 new Identifier(entry.structure).equals(structureIdentifier))
                .map(entry -> new Identifier(entry.biome))
                .collect(Collectors.toList());
    }

    public static class Pool {
        private final List<List<TransformEntry>> groups;

        public Pool(List<TransformEntry> entries) {
            this.groups = new LinkedList<>();
            if (entries.size() > 0) {
                int currentOrder = entries.get(0).order;
                for (int i = 0; i < entries.size();) {
                    Map<String, List<TransformEntry>> namedGroups = new TreeMap<>();
                    TransformEntry entry;
                    do {
                        entry = entries.get(i++);
                        if (entry.transformGroup == null || entry.transformGroup.length() == 0) {
                            this.groups.add(List.of(entry));
                        } else {
                            StructurePalettesMod.LOGGER.info("Added " + entry.transformGroup + " to " + entry.structure);
                            namedGroups.computeIfAbsent(
                                    entry.transformGroup,
                                    g -> new LinkedList<>()
                            ).add(entry);
                        }
                    } while (entry.order == currentOrder && i < entries.size());
                    this.groups.addAll(namedGroups.values());
                    currentOrder = entry.order;
                }
            }
        }

        public StructureTransformer sample(RegistryEntry<Biome> biome, Random rng) {
            return StructureTransformer.chain(
                    groups.stream()
                    .map(g -> this.sampleTransformGroup(g, biome, rng))
                    .filter(entry -> entry != null && rng.nextFloat() * 100 < entry.chance)
                    .map(entry -> StructureTransformer.fromEntry(entry, rng))
                    .collect(Collectors.toList())
            );
        }

        private TransformEntry sampleTransformGroup(List<TransformEntry> entries, RegistryEntry<Biome> biome, Random rng) {
            StructurePalettesMod.LOGGER.info("Sampling from " + entries.size() + " entries");
            for (TransformEntry entry : entries) {
                if ((entry.biome == null || biome.matchesId(new Identifier(entry.biome))) && rng.nextFloat() * 100 < entry.chance) {
                    StructurePalettesMod.LOGGER.info("Accepted entry of group " + (entry.transformGroup == null ? "(no group)" : entry.transformGroup) + " with chance " + entry.chance + " and palette group " + entry.paletteGroup);
                    return entry;
                }
                StructurePalettesMod.LOGGER.info("Rejected entry of group " + (entry.transformGroup == null ? "(no group)" : entry.transformGroup) + " with chance " + entry.chance);
            }
            return null;
        }
    }
}
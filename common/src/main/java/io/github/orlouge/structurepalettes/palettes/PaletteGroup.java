package io.github.orlouge.structurepalettes.palettes;

import io.github.orlouge.structurepalettes.StructurePalettesMod;
import io.github.orlouge.structurepalettes.utils.WeightedRandomList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class PaletteGroup {
    private final WeightedRandomList<Palette> palettes;

    public PaletteGroup(Collection<Pair<Double, Palette>> palettes) {
        this.palettes = new WeightedRandomList<>();
        for (Pair<Double, Palette> palette : palettes) {
            this.palettes.add(palette.getLeft(), palette.getRight());
        }
    }

    /**
     * Sample a palette from a group of palettes. Virtual identifiers for other groups
     * can't be used.
     */
    private Palette samplePalette() {
        return new PaletteGroupSampler(this);
    }


    /**
     * Sample a palette from a group of palettes, eventually sampling virtual blocks
     * from the relevant palette groups.
     */
    public static Palette samplePalette(String rootGroup, Map<String, PaletteGroup> groups) {
        return new PaletteSampler(rootGroup, groups);
    }

    /**
     * A combined palette built by sampling disjoint palettes from a group and merging them.
     */
    private static class PaletteGroupSampler extends Palette {
        private final WeightedRandomList<Palette> unusedPalettes;

        public PaletteGroupSampler(PaletteGroup group) {
            super(new HashMap<>(), new HashMap<>());
            this.unusedPalettes = new WeightedRandomList<Palette>(group.palettes);
        }

        private Palette sampleMerge(Random rng) {
            Palette newSample = this.unusedPalettes.popSample(rng);
            if (newSample == null) {
                return null;
            } else {
                StructurePalettesMod.LOGGER.info("Current palette:" + super.toString());
                StructurePalettesMod.LOGGER.info("Sampling palette:" + newSample);
                if (this.disjoint(newSample)) {
                    StructurePalettesMod.LOGGER.info("Sampled.");
                    this.merge(newSample);
                } else {
                    StructurePalettesMod.LOGGER.info("Not disjoint.");
                }
                return newSample;
            }
        }

        @Override
        public Identifier transform(Identifier id, MappingContext context, Random rng) {
            //ExampleMod.LOGGER.info("Transforming: " + id);
            Identifier target = super.transform(id, context, rng);
            if (target == null) {
                return sampleMerge(rng) == null ? null : this.transform(id, context, rng);
            } else {
                return target;
            }
        }

        @Override
        public Identifier locate(VirtualIdentifier id, MappingContext context, Random rng) {
            //ExampleMod.LOGGER.info("Locating: " + id);
            Identifier target = super.locate(id, context, rng);
            if (target == null) {
                return sampleMerge(rng) == null ? null : this.locate(id, context, rng);
            } else {
                return target;
            }
        }
    }

    /**
     * A palette sampled from a root group that eventually samples from other groups
     * if virtual identifiers are used.
     */
    private static class PaletteSampler extends PaletteGroupSampler {
        private final Map<String, PaletteGroup> groups;
        private final String rootGroup;
        private final Map<String, Palette> sampledPalettes;

        private PaletteSampler(String rootGroup, Map<String, PaletteGroup> groups) {
            super(groups.get(rootGroup));
            this.groups = groups;
            this.rootGroup = rootGroup;
            this.sampledPalettes = new HashMap<>();
        }

        @Override
        public Identifier transform(Identifier id, MappingContext context, Random rng) {
            return super.transform(id, context, rng);
        }

        @Override
        public Identifier locate(VirtualIdentifier id, MappingContext context, Random rng) {
            if (id.group().equals(this.rootGroup)) {
                return super.locate(id, context, rng);
            } else {
                Palette palette = sampledPalettes.get(id.group());
                if (palette == null) {
                    palette = groups.get(id.group()).samplePalette();
                    sampledPalettes.put(id.group(), palette);
                }
                return palette.locate(id, context, rng);
            }
        }
    }
}

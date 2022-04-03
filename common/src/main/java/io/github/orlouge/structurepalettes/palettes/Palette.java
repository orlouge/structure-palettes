package io.github.orlouge.structurepalettes.palettes;

import io.github.orlouge.structurepalettes.config.Condition;
import io.github.orlouge.structurepalettes.config.PaletteEntry;
import net.minecraft.util.Identifier;

import java.util.*;

public class Palette {
    private final Map<Identifier, List<Mapping>> replacements;
    private final Map<VirtualIdentifier, List<Mapping>> references;

    public Palette(Map<Identifier, List<Mapping>> replacements, Map<VirtualIdentifier, List<Mapping>> references) {
        this.replacements = replacements;
        this.references = references;
    }

    public static Palette fromEntry(PaletteEntry paletteEntry, String group) {
        Palette palette = new Palette(new HashMap<>(), new HashMap<>());
        for (PaletteEntry.Mapping entry : paletteEntry.mappings) {
            TargetBlock target = TargetBlock.fromEntry(entry.target);
            Mapping mapping = new Mapping(target, entry.conditions);
            if (VirtualIdentifier.isVirtual(entry.source)) {
                palette.references.computeIfAbsent(new VirtualIdentifier(group, entry.source), k -> new LinkedList<>())
                        .add(mapping);
            } else {
                palette.replacements.computeIfAbsent(new Identifier(entry.source), k -> new LinkedList<>())
                        .add(mapping);
            }
        }
        return palette;
    }

    public Identifier transform(Identifier id, MappingContext context, Random rng) {
        List<Mapping> mappings = replacements.getOrDefault(id, Collections.emptyList());
        for (Mapping mapping : mappings) {
            if (mapping.matches(context)) {
                return mapping.target.sample(rng).map(
                        rid -> rid,
                        vid -> this.locate(vid, context, rng)
                );
            }
        }
        return null;
    }

    public Identifier locate(VirtualIdentifier id, MappingContext context, Random rng) {
        List<Mapping> mappings = references.getOrDefault(id, Collections.emptyList());
        for (Mapping mapping : mappings) {
            if (mapping.matches(context)) {
                return mapping.target.sample(rng).map(
                        rid -> rid,
                        vid -> this.locate(vid, context, rng)
                );
            }
        }
        return null;
    }

    public boolean disjoint(Palette other) {
        return Collections.disjoint(this.replacements.keySet(), other.replacements.keySet()) &&
               Collections.disjoint(this.references.keySet(), other.references.keySet());
    }

    public void merge(Palette other) {
        this.references.putAll(other.references);
        this.replacements.putAll(other.replacements);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Identifier, List<Mapping>> entry : this.replacements.entrySet()) {
            for (Mapping mapping : entry.getValue()) {
                builder.append(entry.getKey().toString());
                builder.append(" -> ");
                builder.append(mapping.toString());
                builder.append("\n");
            }
        }
        for (Map.Entry<VirtualIdentifier, List<Mapping>> entry : this.references.entrySet()) {
            for (Mapping mapping : entry.getValue()) {
                builder.append(entry.getKey().toString());
                builder.append(" -> ");
                builder.append(mapping.toString());
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    private static class Mapping {
        private final TargetBlock target;

        private boolean mustBeFeature = false, mustNotBeFeature = false;
        private Identifier featureId = null;

        private boolean mustBePiece = false, mustNotBePiece = false;
        private Identifier pieceId = null;

        private boolean mustBePoolElement = false, mustNotBePoolElement = false;
        private Identifier poolElementId = null;

        private Identifier biomeId = null;


        public Mapping(TargetBlock target, Collection<Condition> conditions) {
            this.target = target;

            for (Condition condition : conditions) {
                if (condition.key.equals("feature")) {
                    if (condition.negate) {
                        this.mustNotBeFeature = true;
                    } else {
                        this.mustBeFeature = true;
                        this.featureId = condition.value == null ? null : new Identifier(condition.value);
                    }
                } else if (condition.key.equals("piece")) {
                    if (condition.negate) {
                        this.mustNotBePiece = true;
                    } else {
                        this.mustBePiece = true;
                        this.pieceId = condition.value == null ? null : new Identifier(condition.value);
                    }
                } else if (condition.key.equals("biome")) {
                    this.biomeId = new Identifier(condition.value);
                } else if (condition.key.equals("poolelement")) {
                    if (condition.negate) {
                        this.mustNotBePoolElement = true;
                    } else {
                        this.mustBePoolElement = true;
                        this.poolElementId = condition.value == null ? null : new Identifier(condition.value);
                    }
                }
            }
        }

        public boolean matches(MappingContext context) {
            if (mustBeFeature) {
                if (context.feature == null) {
                    return false;
                }
                if (featureId != null && !context.feature.equals(featureId)) {
                    return false;
                }
            }
            if (mustNotBeFeature && context.feature != null) {
                return false;
            }

            if (mustBePiece) {
                if (context.piece == null) {
                    return false;
                }
                if (pieceId != null && !context.piece.equals(pieceId)) {
                    return false;
                }
            }
            if (mustNotBePiece && context.piece != null) {
                return false;
            }

            if (biomeId != null && (context.biome == null || !context.biome.equals(biomeId))) {
                return false;
            }

            if (mustBePoolElement) {
                if (context.poolelement == null) {
                    return false;
                }
                if (poolElementId != null && !context.poolelement.equals(poolElementId)) {
                    return false;
                }
            }
            if (mustNotBePoolElement && context.poolelement != null) {
                return false;
            }

            return true;
        }

        @Override
        public String toString() {
            return this.target.toString();
        }
    }
}

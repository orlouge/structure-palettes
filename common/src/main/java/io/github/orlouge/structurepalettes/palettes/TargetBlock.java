package io.github.orlouge.structurepalettes.palettes;

import com.mojang.datafixers.util.Either;
import io.github.orlouge.structurepalettes.config.TargetEntry;
import io.github.orlouge.structurepalettes.utils.WeightedRandomList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Collection;
import java.util.Random;

public class TargetBlock {
    private final WeightedRandomList<Either<Identifier, VirtualIdentifier>> targetBlocks = new WeightedRandomList<>();

    private TargetBlock() {}

    public TargetBlock(Collection<Pair<Double, Identifier>> blocks) {
        for (Pair<Double, Identifier> entry : blocks) {
            targetBlocks.add(entry.getLeft(), Either.left(entry.getRight()));
        }
    }

    public static TargetBlock fromEntry(TargetEntry entry) {
        TargetBlock targetBlock = new TargetBlock();
        for (Pair<Double, String> target : entry.targets) {
            if (target.getRight().equals("?")) {
                targetBlock.targetBlocks.add(target.getLeft(), Either.left(null));
            } else if (VirtualIdentifier.isVirtual(target.getRight())) {
                targetBlock.targetBlocks.add(target.getLeft(), Either.right(new VirtualIdentifier(target.getRight())));
            } else {
                targetBlock.targetBlocks.add(target.getLeft(), Either.left(new Identifier(target.getRight())));
            }
        }
        return targetBlock;
    }

    public Either<Identifier, VirtualIdentifier> sample(Random rng) {
        return targetBlocks.sample(rng);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Either<Identifier, VirtualIdentifier> entry : this.targetBlocks) {
            if (!first) {
                builder.append(", ");
            }
            first = false;
            builder.append((String) entry.map(
                        id -> id == null ? "?" : id.toString(),
                        id -> id == null ? "?" : id.toString()));
        }
        return builder.toString();
    }
}

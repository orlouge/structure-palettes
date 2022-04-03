package io.github.orlouge.structurepalettes.mixin;

import io.github.orlouge.structurepalettes.transformers.StructureTransformer;
import io.github.orlouge.structurepalettes.transformers.StructureTransformerProvider;
import io.github.orlouge.structurepalettes.transformers.StructureTransformerReceiver;
import io.github.orlouge.structurepalettes.proxy.StructureWorldAccessProxy;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.Random;

@Mixin(StructureStart.class)
public abstract class StructureStartMixin implements StructureTransformerReceiver {
    private StructureTransformer structureTransformer = null;

    @Shadow public abstract List<StructurePiece> getChildren();

    @Shadow @Final private ConfiguredStructureFeature<?, ?> feature;

    @Shadow @Final private ChunkPos pos;

    @Override
    public void setStructureTransformer(StructureTransformer transformer) {
        if (transformer == null || transformer.isNop()) {
            this.structureTransformer = null;
        } else {
            this.structureTransformer = transformer;
        }
    }

    @ModifyVariable(
            method = "place(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;)V",
            at = @At("LOAD")
    )
    public StructureWorldAccess onPlace(StructureWorldAccess world, StructureWorldAccess world2, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos) {
        if (this.structureTransformer == null) {
            world.getRegistryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY).getKey((ConfiguredStructureFeature<?, ?>) this.feature).ifPresent(
                    k -> {
                        if (this.feature instanceof StructureTransformerProvider stProvider) {
                            int x = this.pos.getCenterX();
                            int z = this.pos.getCenterZ();
                            int y = chunkGenerator.getHeightInGround(x, z, Heightmap.Type.WORLD_SURFACE_WG, world);
                            RegistryEntry<Biome> biome = chunkGenerator.getBiomeForNoiseGen(BiomeCoords.fromBlock(x), BiomeCoords.fromBlock(y), BiomeCoords.fromBlock(z));
                        /*
                        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
                        chunkRandom.setCarverSeed(world.getSeed(), chunkPos.x, chunkPos.z);
                         */
                            this.setStructureTransformer(
                                    stProvider.getStructureTransformer(k.getValue(), biome, random)
                            );
                        }
                    });
        }
        if (this.structureTransformer == null || this.structureTransformer.isNop()) {
            return world;
        } else {
            /* // TODO
                world.getRegistryManager().get(Registry.STRUCTURE_PIECE_KEY).getKey(structurePiece.getType()).ifPresent(
                        k -> structureTransformer[0] = structureTransformer[0].withContext(ctx -> { ctx.piece = k.getValue();})
                );
             */
            return new StructureWorldAccessProxy(world, this.structureTransformer);
        }
    }
}

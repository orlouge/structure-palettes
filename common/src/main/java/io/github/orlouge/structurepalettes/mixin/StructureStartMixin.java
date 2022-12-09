package io.github.orlouge.structurepalettes.mixin;

import io.github.orlouge.structurepalettes.transformers.StructureTransformer;
import io.github.orlouge.structurepalettes.transformers.StructureTransformerProvider;
import io.github.orlouge.structurepalettes.transformers.StructureTransformerReceiver;
import io.github.orlouge.structurepalettes.proxy.StructureWorldAccessProxy;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(StructureStart.class)
public abstract class StructureStartMixin implements StructureTransformerReceiver {
    private StructureTransformer structureTransformer = null;

    @Shadow public abstract List<StructurePiece> getChildren();
    
    @Shadow @Final private ChunkPos pos;

    @Shadow @Final private Structure structure;

    @Override
    public void setStructureTransformer(StructureTransformer transformer) {
        if (transformer == null || transformer.isNop()) {
            this.structureTransformer = null;
        } else {
            this.structureTransformer = transformer;
        }
    }

    @ModifyVariable(
            method = "place(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;)V",
            at = @At("LOAD")
    )
    public StructureWorldAccess onPlace(StructureWorldAccess world, StructureWorldAccess world2, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos) {
        if (this.structureTransformer == null) {
            world.getRegistryManager().get(RegistryKeys.STRUCTURE).getKey((Structure) this.structure).ifPresent(
                    k -> {
                        if (this.structure instanceof StructureTransformerProvider stProvider) {
                            int x = this.pos.getCenterX();
                            int z = this.pos.getCenterZ();
                            int y;
                            if (world.getChunkManager() instanceof ServerChunkManager serverChunkManager) {
                                y = chunkGenerator.getHeightInGround(x, z, Heightmap.Type.WORLD_SURFACE_WG, world, serverChunkManager.getNoiseConfig());
                            } else {
                                y = 64;
                            }
                            
                            RegistryEntry<Biome> biome = world.getBiomeForNoiseGen(BiomeCoords.fromBlock(x), BiomeCoords.fromBlock(y), BiomeCoords.fromBlock(z));
                            
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

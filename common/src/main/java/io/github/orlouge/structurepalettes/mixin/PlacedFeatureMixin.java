package io.github.orlouge.structurepalettes.mixin;

import io.github.orlouge.structurepalettes.transformers.StructureTransformer;
import io.github.orlouge.structurepalettes.transformers.StructureTransformerProvider;
import io.github.orlouge.structurepalettes.proxy.StructureWorldAccessProxy;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlacedFeature.class)
public class PlacedFeatureMixin {
    @ModifyVariable(
            method = "generate(Lnet/minecraft/world/gen/feature/FeaturePlacementContext;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At(value="LOAD", ordinal=1)
    )
    public FeaturePlacementContext generate(FeaturePlacementContext context, FeaturePlacementContext context2, Random random, BlockPos pos) {
        RegistryEntry<ConfiguredFeature<?, ?>> feature = ((PlacedFeature) (Object) this).feature();
        if (feature.getKey().isPresent()) {
            RegistryKey<ConfiguredFeature<?, ?>> key = feature.getKey().get();
            if (context.getWorld() instanceof StructureWorldAccessProxy) {
                    return new FeaturePlacementContext(
                            ((StructureWorldAccessProxy) context.getWorld()).withContext(
                                ctx -> { ctx.feature = key.getValue(); }),
                            context.getChunkGenerator(),
                            context.getPlacedFeature()
                    );
            } else if ((Object) feature.value() instanceof StructureTransformerProvider stProvider) {
                RegistryEntry<Biome> biome =  context.getWorld().getBiomeForNoiseGen(BiomeCoords.fromBlock(pos.getX()), BiomeCoords.fromBlock(pos.getY()), BiomeCoords.fromBlock(pos.getZ()));
                StructureTransformer transformer = stProvider.getStructureTransformer(key.getValue(), biome, random);
                if (transformer != null && !transformer.isNop()) {
                    transformer = transformer.withContext(ctx -> { ctx.feature = key.getValue(); });
                    return new FeaturePlacementContext(
                            new StructureWorldAccessProxy(context.getWorld(), transformer),
                            context.getChunkGenerator(),
                            context.getPlacedFeature()
                    );
                }
            }
        }
        return context;
    }
}

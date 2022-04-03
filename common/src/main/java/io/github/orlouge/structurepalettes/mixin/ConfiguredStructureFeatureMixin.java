package io.github.orlouge.structurepalettes.mixin;

import io.github.orlouge.structurepalettes.interfaces.HasModifiedBiomeList;
import io.github.orlouge.structurepalettes.transformers.StructureTransformer;
import io.github.orlouge.structurepalettes.transformers.StructureTransformerManager;
import io.github.orlouge.structurepalettes.transformers.StructureTransformerProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ConfiguredStructureFeature.class)
public class ConfiguredStructureFeatureMixin implements StructureTransformerProvider, HasModifiedBiomeList {
    private StructureTransformerManager.Pool structureTransformerPool = null;
    private RegistryEntryList<Biome> modifiedBiomeList = null;

    @Override
    public StructureTransformer getStructureTransformer(Identifier structureIdentifier, RegistryEntry<Biome> biome, Random rng) {
        if (structureTransformerPool == null) {
            structureTransformerPool = StructureTransformerManager.getPool(structureIdentifier);
        }
        StructureTransformer transformer = structureTransformerPool.sample(biome, rng);
        if (biome != null) {
            return biome.getKey().map(k -> transformer.withContext(ctx -> { ctx.biome = k.getValue(); }))
                    .orElse(transformer);
        }
        return transformer;
    }

    @Override
    public RegistryEntryList<Biome> getModifiedBiomeList() {
        return modifiedBiomeList;
    }

    @Override
    public void setModifiedBiomeList(RegistryEntryList<Biome> modifiedBiomeList) {
        this.modifiedBiomeList = modifiedBiomeList;
    }

    @Inject(
            method = "getBiomes()Lnet/minecraft/util/registry/RegistryEntryList;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onGetBiomes(CallbackInfoReturnable<RegistryEntryList<Biome>> cir) {
        if (this.modifiedBiomeList != null) {
            cir.setReturnValue(this.modifiedBiomeList);
            cir.cancel();
        }
    }
}

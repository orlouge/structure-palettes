package io.github.orlouge.structurepalettes.mixin;

import com.mojang.datafixers.util.Either;
import io.github.orlouge.structurepalettes.StructurePalettesMod;
import io.github.orlouge.structurepalettes.interfaces.HasLocation;
import io.github.orlouge.structurepalettes.proxy.StructureWorldAccessProxy;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PoolStructurePiece.class)
public class PoolStructurePieceMixin {
    @Shadow @Final protected StructurePoolElement poolElement;

    @ModifyVariable(
            method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/BlockPos;Z)V",
            at = @At("LOAD")
    )
    public StructureWorldAccess addPoolElementContext(StructureWorldAccess world) {
        if (world instanceof StructureWorldAccessProxy proxy && (Object) this.poolElement instanceof HasLocation elem) {
            Either<Identifier, Structure> loc = elem.getLocation();
            if (loc.left().isPresent()) {
                return proxy.withContext(ctx -> {
                    StructurePalettesMod.LOGGER.info("POOL ELEMENT " + loc.left().get());
                    ctx.poolelement = loc.left().get();
                });
            }
        }
        return world;
    }
}

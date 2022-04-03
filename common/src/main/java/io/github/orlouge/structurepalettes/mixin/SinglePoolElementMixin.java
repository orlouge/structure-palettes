package io.github.orlouge.structurepalettes.mixin;

import com.mojang.datafixers.util.Either;
import io.github.orlouge.structurepalettes.interfaces.HasLocation;
import net.minecraft.structure.Structure;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementMixin implements HasLocation {
    @Override
    @Accessor("location")
    public abstract Either<Identifier, Structure> getLocation();
}

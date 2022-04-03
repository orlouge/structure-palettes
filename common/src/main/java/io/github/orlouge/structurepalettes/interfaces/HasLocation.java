package io.github.orlouge.structurepalettes.interfaces;

import com.mojang.datafixers.util.Either;
import net.minecraft.structure.Structure;
import net.minecraft.util.Identifier;

public interface HasLocation {
    Either<Identifier, Structure> getLocation();
}
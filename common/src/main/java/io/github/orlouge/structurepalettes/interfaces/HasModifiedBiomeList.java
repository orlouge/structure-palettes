package io.github.orlouge.structurepalettes.interfaces;

import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.biome.Biome;

public interface HasModifiedBiomeList {
    void setModifiedBiomeList(RegistryEntryList<Biome> list);

    RegistryEntryList<Biome> getModifiedBiomeList();
}

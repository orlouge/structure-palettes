package io.github.orlouge.structurepalettes.fabric;

import io.github.orlouge.structurepalettes.StructurePalettesMod;
import net.fabricmc.api.ModInitializer;

public class StructurePalettesModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        StructurePalettesMod.init();
    }
}

package io.github.orlouge.structurepalettes;

import io.github.orlouge.structurepalettes.config.Config;
import io.github.orlouge.structurepalettes.palettes.PaletteManager;
import io.github.orlouge.structurepalettes.transformers.StructureTransformerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StructurePalettesMod {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "structurepalettes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void init() {
		Config config = null;
		try {
			config = Config.load(ExampleExpectPlatform.getConfigDirectory().resolve(MOD_ID));
			PaletteManager.load(config.getPalettes());
			StructureTransformerManager.load(config.getTransformations());
		} catch (IOException | Config.ConfigParseException e) {
			e.printStackTrace();
		}
	}
}

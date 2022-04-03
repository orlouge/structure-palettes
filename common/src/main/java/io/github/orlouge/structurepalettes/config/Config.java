package io.github.orlouge.structurepalettes.config;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.Pair;

public class Config {
    private final Collection<TransformEntry> transformations;
    private final Collection<PaletteEntry> palettes;

    public Config(Collection<TransformEntry> transformations, Collection<PaletteEntry> palettes) {
        this.transformations = transformations;
        this.palettes = palettes;
    }

    public static Config load(Path folder) throws IOException, ConfigParseException {
        Parser parser = new Parser();

        if (Files.exists(folder)) {
            Iterator<Path> iter = Files.walk(folder, FileVisitOption.FOLLOW_LINKS).iterator();
            while (iter.hasNext()) {
                Path path = iter.next();
                if (path.toString().toLowerCase().endsWith(".txt")) {
                    parser.parse(Files.readAllLines(path));
                }
            }
        } else {
            Files.createDirectory(folder);
        }

        return parser.getConfig();
    }

    public Collection<TransformEntry> getTransformations() {
        return transformations;
    }

    public Collection<PaletteEntry> getPalettes() {
        return palettes;
    }

    private static class Parser {
        private final LinkedList<TransformEntry> transformations = new LinkedList<>();
        private final LinkedList<PaletteEntry> palettes = new LinkedList<>();
        private List<Pair<Double, String>> currentPaletteGroups = null;
        private List<PaletteEntry.Mapping> currentPaletteMappings = new LinkedList<>();

        private final static String
                paletteDefinitionGroup = "(([0-9.]+)\\s+)?([A-z_][A-z_0-9]*)",
                paletteDefinitionHead = paletteDefinitionGroup + "(\\s*,\\s*" + paletteDefinitionGroup + ")*\\s*:",
                condition = "(!)?([A-z_\\-/.][A-z_0-9\\-/.]*)(\\s*=\\s*([A-z_0-9\\-/.:]*))?",
                conditions = condition + "(\\s*,\\s*" + condition + ")*\\s*",
                sourceBlock = "(?<sourceblock>([A-z_\\-/.][A-z_0-9\\-/.]*:)?[A-z_\\-/.][A-z_0-9\\-/.]*|%[A-z_][A-z_0-9]*)",
                targetBlock = "(([0-9.]+)%?\\s+)?(([A-z_\\-/.][A-z_0-9\\-/.]*:)?[A-z_\\-/.][A-z_0-9\\-/.]*|%[A-z_][A-z_0-9]*:[A-z_][A-z_0-9]*|\\?)",
                targetBlocks = "(?<targetblocks>" + targetBlock + "(\\s*,\\s*" + targetBlock + ")*)\\s*",
                paletteDefinitionBody = sourceBlock + "\\s*(?<mappingconditions>\\(\\s*" + conditions + "\\s*\\))?\\s*" + "\\s*->\\s*" + targetBlocks,
                targetStructure = "((?<structure>([A-z_\\-/.][A-z_0-9\\-/.]*:)?[A-z_\\-/.]+)|\\*)",
                targetBiome = "(?<biome>([A-z_\\-/.][A-z_0-9\\-/.]*:)?[A-z_\\-/.]+|\\*)(\\s*(?<biomeadd>\\+))?",
                paletteGroupUsage = "(?<palettegroup>[A-z_][A-z_0-9]*)",
                structureTransformation =
                        "((?<chance>[0-9.]+)%\\s)?\\s*" +
                        "((?<transformgroup>[A-z_][A-z_0-9]*)?:(?<order>[0-9]+)?\\s)?\\s*" +
                        paletteGroupUsage + "\\s*=>\\s*" +
                        targetStructure + "\\s*(\\(\\s*" +
                        targetBiome + "\\s*\\))?",
                root =
                        "\\s*(?<palettehead>" + paletteDefinitionHead +
                        "\\s*(//.*)?)|(?<palettebody>" + paletteDefinitionBody +
                        "\\s*(//.*)?)|(?<transformation>" + structureTransformation +
                        "\\s*(//.*)?)|(?<empty>\\s*)\\s*(//.*)?";
        private final static Pattern
                rootPattern = Pattern.compile(root),
                conditionPattern = Pattern.compile(condition),
                paletteGroupPattern = Pattern.compile(paletteDefinitionGroup),
                targetBlockPattern = Pattern.compile(targetBlock);

        public void parse(List<String> lines) throws ConfigParseException {
            for (String line : lines) {
                Matcher matcher = rootPattern.matcher(line);
                if (matcher.matches()) {
                    if (matcher.group("palettehead") != null) {
                        this.parsePaletteHead(matcher.group("palettehead"));
                    } else if (matcher.group("palettebody") != null) {
                        this.parsePaletteBody(matcher);
                    } else if (matcher.group("transformation") != null) {
                        this.parseTransformation(matcher);
                    }
                } else {
                    throw new ConfigParseException(line);
                }
            }

            this.addPalette();
        }

        private void parseTransformation(Matcher matcher) {
            this.addPalette();

            this.transformations.add(new TransformEntry(
                    matcher.group("transformgroup"),
                    matcher.group("palettegroup"),
                    matcher.group("chance") != null ? Double.parseDouble(matcher.group("chance")) : 100,
                    matcher.group("order") != null ? Integer.parseInt(matcher.group("order")) : 0,
                    matcher.group("structure"),
                    matcher.group("biome"),
                    matcher.group("biomeadd") != null
            ));
        }

        private void parsePaletteBody(Matcher matcher) {
            this.currentPaletteMappings.add(new PaletteEntry.Mapping(
                    matcher.group("sourceblock"),
                    parseTargetBlocks(matcher.group("targetblocks")),
                    parseConditions(matcher.group("mappingconditions"))
            ));
        }

        private TargetEntry parseTargetBlocks(String targetBlocks) {
            Matcher matcher = targetBlockPattern.matcher(targetBlocks);
            LinkedList<Pair<Double, String>> targetList = new LinkedList<>();
            while (matcher.find()) {
                targetList.add(new Pair<>(
                        matcher.group(2) != null ? Double.parseDouble(matcher.group(2)) : 1000,
                        matcher.group(3)
                ));
            }
            return new TargetEntry(targetList);
        }

        private List<Condition> parseConditions(String conditions) {
            if (conditions == null) {
                return Collections.emptyList();
            }
            Matcher matcher = conditionPattern.matcher(conditions);
            LinkedList<Condition> conditionList = new LinkedList<>();
            while (matcher.find()) {
                conditionList.add(new Condition(
                        matcher.group(2),
                        matcher.group(4),
                        matcher.group(1) != null
                ));
            }
            return conditionList;
        }

        private void parsePaletteHead(String palettehead) {
            this.addPalette();

            Matcher matcher = paletteGroupPattern.matcher(palettehead);
            this.currentPaletteGroups = new LinkedList<>();
            while (matcher.find()) {
                this.currentPaletteGroups.add(new Pair<>(
                        matcher.group(2) != null ? Double.parseDouble(matcher.group(2)) : 1000,
                        matcher.group(3)
                ));
            }
        }

        private void addPalette() {
            if (this.currentPaletteGroups != null) {
                palettes.add(new PaletteEntry(
                        this.currentPaletteGroups,
                        this.currentPaletteMappings
                ));
                this.currentPaletteGroups = null;
                this.currentPaletteMappings = new LinkedList<>();
            }
        }

        public Config getConfig() {
            return new Config(transformations, palettes);
        }
    }

    public static class ConfigParseException extends Exception {
        public ConfigParseException(String line) {
            super("Invalid config line: " + line);
        }
    }
}

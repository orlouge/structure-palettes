package io.github.orlouge.structurepalettes.palettes;

import net.minecraft.util.Identifier;

public class VirtualIdentifier {
    private final Identifier pseudoIdentifier;

    public VirtualIdentifier(String id) {
        this.pseudoIdentifier = new Identifier(id.substring(1));
    }

    public VirtualIdentifier(String group, String name) {
        this.pseudoIdentifier = new Identifier(group, name.substring(1));
    }

    public String group() {
        return this.pseudoIdentifier.getNamespace();
    }

    public String name() {
        return this.pseudoIdentifier.getPath();
    }

    public static boolean isVirtual(String source) {
        return source.startsWith("%");
    }

    @Override
    public int hashCode() {
        return this.pseudoIdentifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VirtualIdentifier)) {
            return false;
        } else {
            return this.pseudoIdentifier.equals(((VirtualIdentifier) obj).pseudoIdentifier);
        }
    }

    @Override
    public String toString() {
        return "%" + this.pseudoIdentifier.toString();
    }
}

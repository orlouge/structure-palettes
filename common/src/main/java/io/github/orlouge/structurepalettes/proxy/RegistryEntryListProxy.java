package io.github.orlouge.structurepalettes.proxy;

import com.mojang.datafixers.util.Either;
import io.github.orlouge.structurepalettes.StructurePalettesMod;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;

import java.util.List;

public class RegistryEntryListProxy<T> extends RegistryEntryList.ListBacked<T> {
    private final List<RegistryEntry<T>> entries;
    private RegistryEntryList<T> original = null;

    public RegistryEntryListProxy(List<RegistryEntry<T>> entries, RegistryEntryList<T> original) {
        this.entries = entries;
        Either<TagKey<T>, List<RegistryEntry<T>>> storage = original.getStorage();
        storage.ifRight(this.entries::addAll);
        storage.ifLeft(tag -> { this.original = original; });
    }

    @Override
    protected List<RegistryEntry<T>> getEntries() {
        this.update();
        return this.entries;
    }

    @Override
    public Either<TagKey<T>, List<RegistryEntry<T>>> getStorage() {
        this.update();
        if (this.original == null) {
            return Either.right(this.entries);
        } else {
            return this.original.getStorage();
        }
    }

    @Override
    public boolean contains(RegistryEntry<T> entry) {
        this.update();
        if (this.original == null) {
            return this.entries.contains(entry);
        } else {
            return this.entries.contains(entry) || this.original.contains(entry);
        }
    }

    @Override
    public int size() {
        return this.entries.size();
    }

    private void update() {
        if (this.original != null) {
            List<RegistryEntry<T>> originalEntries = this.original.stream().toList();
            if (originalEntries.size() > 0) {
                this.entries.addAll(originalEntries);
                StructurePalettesMod.LOGGER.info("Updated " + originalEntries.size());
                this.original = null;
            }
        }
    }
}

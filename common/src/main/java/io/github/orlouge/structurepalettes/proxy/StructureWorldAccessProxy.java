package io.github.orlouge.structurepalettes.proxy;

import io.github.orlouge.structurepalettes.palettes.MappingContext;
import io.github.orlouge.structurepalettes.transformers.StructureTransformer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.QueryableTickScheduler;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class StructureWorldAccessProxy implements StructureWorldAccess {
    private final StructureWorldAccess world;
    private final StructureTransformer structureTransformer;

    public StructureWorldAccessProxy(StructureWorldAccess world, StructureTransformer structureTransformer) {
        this.world = world;
        this.structureTransformer = structureTransformer;
    }

    public StructureWorldAccessProxy withContext(Consumer<MappingContext> f) {
        return new StructureWorldAccessProxy(this.world, this.structureTransformer.withContext(f));
    }


    @Override
    public long getSeed() {
        return this.world.getSeed();
    }

    @Override
    public ServerWorld toServerWorld() {
        return this.world.toServerWorld();
    }

    @Override
    public long getTickOrder() {
        return this.world.getTickOrder();
    }

    @Override
    public QueryableTickScheduler<Block> getBlockTickScheduler() {
        return this.world.getBlockTickScheduler();
    }

    @Override
    public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
        return this.world.getFluidTickScheduler();
    }

    @Override
    public WorldProperties getLevelProperties() {
        return this.world.getLevelProperties();
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos pos) {
        return this.world.getLocalDifficulty(pos);
    }

    @Override
    public MinecraftServer getServer() {
        return this.getServer();
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.world.getChunkManager();
    }

    @Override
    public Random getRandom() {
        return this.world.getRandom();
    }

    @Override
    public void playSound(PlayerEntity player, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        this.world.playSound(player, pos, sound, category, volume, pitch);
    }

    @Override
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.world.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Override
    public void syncWorldEvent(PlayerEntity player, int eventId, BlockPos pos, int data) {
        this.world.syncWorldEvent(player, eventId, pos, data);
    }

    @Override
    public void emitGameEvent(Entity entity, GameEvent event, BlockPos pos) {
        this.world.emitGameEvent(entity, event, pos);
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        return this.world.getRegistryManager();
    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return this.world.getBrightness(direction, shaded);
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.world.getLightingProvider();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.getWorldBorder();
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.world.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.world.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.world.getFluidState(pos);
    }

    @Override
    public List<Entity> getOtherEntities(Entity except, Box box, Predicate<? super Entity> predicate) {
        return this.world.getOtherEntities(except, box, predicate);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate) {
        return this.world.getEntitiesByType(filter, box, predicate);
    }

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return this.world.getPlayers();
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        if (this.structureTransformer != null) {
            return this.world.setBlockState(pos, this.structureTransformer.transform(state), flags, maxUpdateDepth);
        } else {
            return this.world.setBlockState(pos, state, flags, maxUpdateDepth);
        }
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean move) {
        return this.world.removeBlock(pos, move);
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean drop, Entity breakingEntity, int maxUpdateDepth) {
        return this.world.breakBlock(pos, drop, breakingEntity, maxUpdateDepth);
    }

    @Override
    public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
        return this.world.testBlockState(pos, state);
    }

    @Override
    public boolean testFluidState(BlockPos pos, Predicate<FluidState> state) {
        return this.world.testFluidState(pos, state);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        return this.world.getChunk(chunkX, chunkZ, leastStatus, create);
    }

    @Override
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        return this.world.getTopY(heightmap, x, z);
    }

    @Override
    public int getAmbientDarkness() {
        return this.world.getAmbientDarkness();
    }

    @Override
    public BiomeAccess getBiomeAccess() {
        return this.world.getBiomeAccess();
    }

    @Override
    public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return this.world.getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
    }

    @Override
    public boolean isClient() {
        return this.world.isClient();
    }

    @Override
    public int getSeaLevel() {
        return this.world.getSeaLevel();
    }

    @Override
    public DimensionType getDimension() {
        return this.world.getDimension();
    }
}

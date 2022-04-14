package io.github.orlouge.structurepalettes.proxy;

import io.github.orlouge.structurepalettes.palettes.MappingContext;
import io.github.orlouge.structurepalettes.transformers.StructureTransformer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.tick.QueryableTickScheduler;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

    @Override
    public boolean isValidForSetBlock(BlockPos pos) {
        return this.world.isValidForSetBlock(pos);
    }

    @Override
    public void setCurrentlyGeneratingStructureName(Supplier<String> structureName) {
        this.world.setCurrentlyGeneratingStructureName(structureName);
    }

    @Override
    public void spawnEntityAndPassengers(Entity entity) {
        this.world.spawnEntityAndPassengers(entity);
    }

    @Override
    public long getLunarTime() {
        return this.world.getLunarTime();
    }

    @Override
    public void createAndScheduleBlockTick(BlockPos pos, Block block, int delay, TickPriority priority) {
        this.world.createAndScheduleBlockTick(pos, block, delay, priority);
    }

    @Override
    public void createAndScheduleBlockTick(BlockPos pos, Block block, int delay) {
        this.world.createAndScheduleBlockTick(pos, block, delay);
    }

    @Override
    public void createAndScheduleFluidTick(BlockPos pos, Fluid fluid, int delay, TickPriority priority) {
        this.world.createAndScheduleFluidTick(pos, fluid, delay, priority);
    }

    @Override
    public void createAndScheduleFluidTick(BlockPos pos, Fluid fluid, int delay) {
        this.world.createAndScheduleFluidTick(pos, fluid, delay);
    }

    @Override
    public Difficulty getDifficulty() {
        return this.world.getDifficulty();
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return this.world.isChunkLoaded(chunkX, chunkZ);
    }

    @Override
    public void updateNeighbors(BlockPos pos, Block block) {
        this.world.updateNeighbors(pos, block);
    }

    @Override
    public void syncWorldEvent(int eventId, BlockPos pos, int data) {
        this.world.syncWorldEvent(eventId, pos, data);
    }

    @Override
    public void emitGameEvent(GameEvent event, BlockPos pos) {
        this.world.emitGameEvent(event, pos);
    }

    @Override
    public void emitGameEvent(GameEvent event, Entity emitter) {
        this.world.emitGameEvent(event, emitter);
    }

    @Override
    public void emitGameEvent(Entity entity, GameEvent event, Entity emitter) {
        this.world.emitGameEvent(entity, event, emitter);
    }

    @Override
    public float getMoonSize() {
        return this.world.getMoonSize();
    }

    @Override
    public float getSkyAngle(float tickDelta) {
        return this.world.getSkyAngle(tickDelta);
    }

    @Override
    public int getMoonPhase() {
        return this.world.getMoonPhase();
    }

    @Override
    public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos pos, BlockEntityType<T> type) {
        return this.world.getBlockEntity(pos, type);
    }

    @Override
    public List<VoxelShape> getEntityCollisions(Entity entity, Box box) {
        return this.world.getEntityCollisions(entity, box);
    }

    @Override
    public boolean doesNotIntersectEntities(Entity except, VoxelShape shape) {
        return this.world.doesNotIntersectEntities(except, shape);
    }

    @Override
    public BlockPos getTopPosition(Heightmap.Type heightmap, BlockPos pos) {
        return this.world.getTopPosition(heightmap, pos);
    }

    @Override
    public int getLightLevel(LightType type, BlockPos pos) {
        return this.world.getLightLevel(type, pos);
    }

    @Override
    public int getBaseLightLevel(BlockPos pos, int ambientDarkness) {
        return this.world.getBaseLightLevel(pos, ambientDarkness);
    }

    @Override
    public boolean isSkyVisible(BlockPos pos) {
        return this.world.isSkyVisible(pos);
    }

    @Override
    public boolean canPlace(BlockState state, BlockPos pos, ShapeContext context) {
        return this.world.canPlace(state, pos, context);
    }

    @Override
    public boolean doesNotIntersectEntities(Entity entity) {
        return this.world.doesNotIntersectEntities(entity);
    }

    @Override
    public boolean isSpaceEmpty(Box box) {
        return this.world.isSpaceEmpty(box);
    }

    @Override
    public boolean isSpaceEmpty(Entity entity) {
        return this.world.isSpaceEmpty(entity);
    }

    @Override
    public boolean isSpaceEmpty(Entity entity, Box box) {
        return this.world.isSpaceEmpty(entity, box);
    }

    @Override
    public Iterable<VoxelShape> getCollisions(Entity entity, Box box) {
        return this.world.getCollisions(entity, box);
    }

    @Override
    public Iterable<VoxelShape> getBlockCollisions(Entity entity, Box box) {
        return this.world.getBlockCollisions(entity, box);
    }

    @Override
    public boolean canCollide(Entity entity, Box box) {
        return this.world.canCollide(entity, box);
    }

    @Override
    public Optional<Vec3d> findClosestCollision(Entity entity, VoxelShape shape, Vec3d target, double x, double y, double z) {
        return this.world.findClosestCollision(entity, shape, target, x, y, z);
    }

    @Override
    public int getLuminance(BlockPos pos) {
        return this.world.getLuminance(pos);
    }

    @Override
    public int getMaxLightLevel() {
        return this.world.getMaxLightLevel();
    }

    @Override
    public Stream<BlockState> getStatesInBox(Box box) {
        return this.world.getStatesInBox(box);
    }

    @Override
    public BlockHitResult raycast(BlockStateRaycastContext context) {
        return this.world.raycast(context);
    }

    @Override
    public BlockHitResult raycast(RaycastContext context) {
        return this.world.raycast(context);
    }

    @Override
    public BlockHitResult raycastBlock(Vec3d start, Vec3d end, BlockPos pos, VoxelShape shape, BlockState state) {
        return this.world.raycastBlock(start, end, pos, shape, state);
    }

    @Override
    public double getDismountHeight(VoxelShape blockCollisionShape, Supplier<VoxelShape> belowBlockCollisionShapeGetter) {
        return this.world.getDismountHeight(blockCollisionShape, belowBlockCollisionShapeGetter);
    }

    @Override
    public double getDismountHeight(BlockPos pos) {
        return this.world.getDismountHeight(pos);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesByClass(Class<T> entityClass, Box box, Predicate<? super T> predicate) {
        return this.world.getEntitiesByClass(entityClass, box, predicate);
    }

    @Override
    public List<Entity> getOtherEntities(Entity except, Box box) {
        return this.world.getOtherEntities(except, box);
    }

    @Override
    public <T extends Entity> List<T> getNonSpectatingEntities(Class<T> entityClass, Box box) {
        return this.world.getNonSpectatingEntities(entityClass, box);
    }

    @Override
    public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, Predicate<Entity> targetPredicate) {
        return this.world.getClosestPlayer(x, y, z, maxDistance, targetPredicate);
    }

    @Override
    public PlayerEntity getClosestPlayer(Entity entity, double maxDistance) {
        return this.world.getClosestPlayer(entity, maxDistance);
    }

    @Override
    public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, boolean ignoreCreative) {
        return this.world.getClosestPlayer(x, y, z, maxDistance, ignoreCreative);
    }

    @Override
    public boolean isPlayerInRange(double x, double y, double z, double range) {
        return this.world.isPlayerInRange(x, y, z, range);
    }

    @Override
    public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity) {
        return this.world.getClosestPlayer(targetPredicate, entity);
    }

    @Override
    public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity, double x, double y, double z) {
        return this.world.getClosestPlayer(targetPredicate, entity, x, y, z);
    }

    @Override
    public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, double x, double y, double z) {
        return this.world.getClosestPlayer(targetPredicate, x, y, z);
    }

    @Override
    public <T extends LivingEntity> T getClosestEntity(Class<? extends T> entityClass, TargetPredicate targetPredicate, LivingEntity entity, double x, double y, double z, Box box) {
        return this.world.getClosestEntity(entityClass, targetPredicate, entity, x, y, z, box);
    }

    @Override
    public <T extends LivingEntity> T getClosestEntity(List<? extends T> entityList, TargetPredicate targetPredicate, LivingEntity entity, double x, double y, double z) {
        return this.world.getClosestEntity(entityList, targetPredicate, entity, x, y, z);
    }

    @Override
    public List<PlayerEntity> getPlayers(TargetPredicate targetPredicate, LivingEntity entity, Box box) {
        return this.world.getPlayers(targetPredicate, entity, box);
    }

    @Override
    public <T extends LivingEntity> List<T> getTargets(Class<T> entityClass, TargetPredicate targetPredicate, LivingEntity targetingEntity, Box box) {
        return this.world.getTargets(entityClass, targetPredicate, targetingEntity, box);
    }

    @Override
    public PlayerEntity getPlayerByUuid(UUID uuid) {
        return this.world.getPlayerByUuid(uuid);
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
        if (this.structureTransformer != null) {
            return this.world.setBlockState(pos, this.structureTransformer.transform(state), flags);
        } else {
            return this.world.setBlockState(pos, state, flags);
        }
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean drop) {
        return this.world.breakBlock(pos, drop);
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean drop, Entity breakingEntity) {
        return this.world.breakBlock(pos, drop, breakingEntity);
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        return this.world.spawnEntity(entity);
    }

    @Override
    public RegistryEntry<Biome> getBiome(BlockPos pos) {
        return this.world.getBiome(pos);
    }

    @Override
    public Stream<BlockState> getStatesInBoxIfLoaded(Box box) {
        return this.world.getStatesInBoxIfLoaded(box);
    }

    @Override
    public int getColor(BlockPos pos, ColorResolver colorResolver) {
        return this.world.getColor(pos, colorResolver);
    }

    @Override
    public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.world.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }

    @Override
    public int getBottomY() {
        return this.world.getBottomY();
    }

    @Override
    public int getHeight() {
        return this.world.getHeight();
    }

    @Override
    public boolean isAir(BlockPos pos) {
        return this.world.isAir(pos);
    }

    @Override
    public boolean isSkyVisibleAllowingSea(BlockPos pos) {
        return this.world.isSkyVisibleAllowingSea(pos);
    }

    @Override
    public float getBrightness(BlockPos pos) {
        return this.world.getBrightness(pos);
    }

    @Override
    public int getStrongRedstonePower(BlockPos pos, Direction direction) {
        return this.world.getStrongRedstonePower(pos, direction);
    }

    @Override
    public Chunk getChunk(BlockPos pos) {
        return this.world.getChunk(pos);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        return this.world.getChunk(chunkX, chunkZ);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus status) {
        return this.world.getChunk(chunkX, chunkZ, status);
    }

    @Override
    public BlockView getChunkAsView(int chunkX, int chunkZ) {
        return this.world.getChunkAsView(chunkX, chunkZ);
    }

    @Override
    public boolean isWater(BlockPos pos) {
        return this.world.isWater(pos);
    }

    @Override
    public boolean containsFluid(Box box) {
        return this.world.containsFluid(box);
    }

    @Override
    public int getLightLevel(BlockPos pos) {
        return this.world.getLightLevel(pos);
    }

    @Override
    public int getLightLevel(BlockPos pos, int ambientDarkness) {
        return this.world.getLightLevel(pos, ambientDarkness);
    }

    @Override
    public boolean isPosLoaded(int x, int z) {
        return this.world.isPosLoaded(x, z);
    }

    @Override
    public boolean isChunkLoaded(BlockPos pos) {
        return this.world.isChunkLoaded(pos);
    }

    @Override
    public boolean isRegionLoaded(BlockPos min, BlockPos max) {
        return this.world.isRegionLoaded(min, max);
    }

    @Override
    public boolean isRegionLoaded(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return this.world.isRegionLoaded(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public boolean isRegionLoaded(int minX, int minZ, int maxX, int maxZ) {
        return this.world.isRegionLoaded(minX, minZ, maxX, maxZ);
    }

    @Override
    public int getTopY() {
        return this.world.getTopY();
    }

    @Override
    public int countVerticalSections() {
        return this.world.countVerticalSections();
    }

    @Override
    public int getBottomSectionCoord() {
        return this.world.getBottomSectionCoord();
    }

    @Override
    public int getTopSectionCoord() {
        return this.world.getTopSectionCoord();
    }

    @Override
    public boolean isOutOfHeightLimit(BlockPos pos) {
        return this.world.isOutOfHeightLimit(pos);
    }

    @Override
    public boolean isOutOfHeightLimit(int y) {
        return this.world.isOutOfHeightLimit(y);
    }

    @Override
    public int getSectionIndex(int y) {
        return this.world.getSectionIndex(y);
    }

    @Override
    public int sectionCoordToIndex(int coord) {
        return this.world.sectionCoordToIndex(coord);
    }

    @Override
    public int sectionIndexToCoord(int index) {
        return this.world.sectionIndexToCoord(index);
    }
}

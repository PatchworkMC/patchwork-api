/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2020, 2019-2020
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.Event;
import org.apache.commons.lang3.NotImplementedException;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.PlayerSaveHandler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.level.LevelInfo;

import net.patchworkmc.impl.capability.CapabilityEvents;
import net.patchworkmc.impl.event.entity.EntityEvents;
import net.patchworkmc.impl.event.entity.PlayerEvents;
import net.patchworkmc.impl.event.loot.LootEvents;
import net.patchworkmc.impl.event.world.WorldEvents;
import net.patchworkmc.impl.extensions.block.BlockHarvestManager;
import net.patchworkmc.annotations.Stubbed;

/**
 * A stubbed out copy of Forge's ForgeEventFactory, intended for use by Forge mods only.
 * For methods that you are implementing, don't keep implementation details here.
 * Elements should be thin wrappers around methods in other modules.
 * Do not depend on this class in other modules.
 */
public class ForgeEventFactory {
	/*
	@Stubbed
	public static boolean onMultiBlockPlace(@Nullable Entity entity, List<BlockSnapshot> blockSnapshots, Direction direction) {
		throw new NotImplementedException("ForgeEventFactory stub");
	} */

	/*
	@Stubbed
	public static boolean onBlockPlace(@Nullable Entity entity, @NotNull BlockSnapshot blockSnapshot, @NotNull Direction direction) {
		throw new NotImplementedException("ForgeEventFactory stub");
	} */

	/*
	@Stubbed
	public static NeighborNotifyEvent onNeighborNotify(World world, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate) {
		throw new NotImplementedException("ForgeEventFactory stub");
	} */

	@Stubbed
	public static boolean doPlayerHarvestCheck(PlayerEntity player, BlockState state, boolean success) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static float getBreakSpeed(PlayerEntity player, BlockState state, float original, BlockPos pos) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static void onPlayerDestroyItem(PlayerEntity player, @NotNull ItemStack stack, @Nullable Hand hand) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	public static Event.Result canEntitySpawn(MobEntity entity, WorldAccess world, double x, double y, double z, MobSpawnerLogic spawner, SpawnReason spawnReason) {
		return EntityEvents.canEntitySpawn(entity, world, x, y, z, spawner, spawnReason);
	}

	public static boolean canEntitySpawnSpawner(MobEntity entity, World world, float x, float y, float z, MobSpawnerLogic spawner) {
		return EntityEvents.canEntitySpawnFromSpawner(entity, world, x, y, z, spawner);
	}

	public static boolean doSpecialSpawn(MobEntity entity, World world, float x, float y, float z, MobSpawnerLogic spawner, SpawnReason spawnReason) {
		return EntityEvents.doSpecialSpawn(entity, world, x, y, z, spawner, spawnReason);
	}

	@Stubbed
	public static Event.Result canEntityDespawn(MobEntity entity) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static int getItemBurnTime(@NotNull ItemStack itemStack, int burnTime) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static int getExperienceDrop(LivingEntity entity, PlayerEntity attackingPlayer, int originalExperience) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	@Nullable
	public static List<Biome.SpawnEntry> getPotentialSpawns(WorldAccess world, SpawnGroup type, BlockPos pos, List<Biome.SpawnEntry> oldList) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static int getMaxSpawnPackSize(MobEntity entity) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static String getPlayerDisplayName(PlayerEntity player, String username) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	// Forge might remove BlockEvent.HarvestDropsEvent, which is replaced by the new loot modifier.
	@Deprecated
	public static float fireBlockHarvesting(DefaultedList<ItemStack> drops, World world, BlockPos pos, BlockState state, int fortune, float dropChance, boolean silkTouch, PlayerEntity player) {
		return BlockHarvestManager.fireBlockHarvesting(drops, world, pos, state, fortune, dropChance, silkTouch, player);
	}

	@Stubbed
	public static BlockState fireFluidPlaceBlockEvent(WorldAccess world, BlockPos pos, BlockPos liquidPos, BlockState state) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	/*
	@Stubbed
	public static ItemTooltipEvent onItemTooltip(ItemStack itemStack, @Nullable PlayerEntity entityPlayer, List<Text> list, TooltipContext flags) {
		throw new NotImplementedException("ForgeEventFactory stub");
	} */

	/*
	@Stubbed
	public static SummonAidEvent fireZombieSummonAid(ZombieEntity zombie, World world, int x, int y, int z, LivingEntity attacker, double summonChance) {
		throw new NotImplementedException("ForgeEventFactory stub");
	} */

	@Stubbed
	public static boolean onEntityStruckByLightning(Entity entity, LightningEntity bolt) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static int onItemUseStart(LivingEntity entity, ItemStack item, int duration) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static int onItemUseTick(LivingEntity entity, ItemStack item, int duration) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean onUseItemStop(LivingEntity entity, ItemStack item, int duration) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static ItemStack onItemUseFinish(LivingEntity entity, ItemStack item, int duration, ItemStack result) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static void onStartEntityTracking(Entity entity, PlayerEntity player) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static void onStopEntityTracking(Entity entity, PlayerEntity player) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static void firePlayerLoadingEvent(PlayerEntity player, File playerDirectory, String uuidString) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static void firePlayerSavingEvent(PlayerEntity player, File playerDirectory, String uuidString) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static void firePlayerLoadingEvent(PlayerEntity player, PlayerSaveHandler playerFileData, String uuidString) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	@Nullable
	public static Text onClientChat(MessageType type, Text message) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	@NotNull
	public static String onClientSendMessage(String message) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static int onHoeUse(ItemUsageContext context) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static int onApplyBonemeal(@NotNull PlayerEntity player, @NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ItemStack stack) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	@Nullable
	public static TypedActionResult<ItemStack> onBucketUse(@NotNull PlayerEntity player, @NotNull World world, @NotNull ItemStack stack, @Nullable HitResult target) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean canEntityUpdate(Entity entity) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	/*
	@Stubbed
	public static PlaySoundAtEntityEvent onPlaySoundAtEntity(Entity entity, SoundEvent name, SoundCategory category, float volume, float pitch) {
		throw new NotImplementedException("ForgeEventFactory stub");
	} */

	public static int onItemExpire(ItemEntity entity, @NotNull ItemStack item) {
		return EntityEvents.onItemExpire(entity, item);
	}

	public static int onItemPickup(ItemEntity item, PlayerEntity player) {
		return PlayerEvents.onItemPickup(player, item);
	}

	@Stubbed
	public static boolean canMountEntity(Entity entityMounting, Entity entityBeingMounted, boolean isMounting) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	public static boolean onAnimalTame(AnimalEntity animal, PlayerEntity tamer) {
		return EntityEvents.onAnimalTame(animal, tamer);
	}

	@Stubbed
	public static PlayerEntity.SleepFailureReason onPlayerSleepInBed(PlayerEntity player, Optional<BlockPos> pos) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static void onPlayerWakeup(PlayerEntity player, boolean wakeImmediately, boolean updateWorldFlag, boolean setSpawn) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	public static void onPlayerFall(PlayerEntity player, float distance, float multiplier) {
		EntityEvents.onFlyablePlayerFall(player, distance, multiplier);
	}

	@Stubbed
	public static boolean onPlayerSpawnSet(PlayerEntity player, BlockPos pos, boolean forced) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static void onPlayerClone(PlayerEntity player, PlayerEntity oldPlayer, boolean wasDeath) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean onExplosionStart(World world, Explosion explosion) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static void onExplosionDetonate(World world, Explosion explosion, List<Entity> list, double diameter) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean onCreateWorldSpawn(World world, LevelInfo settings) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static float onLivingHeal(LivingEntity entity, float amount) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean onPotionAttemptBrew(DefaultedList<ItemStack> stacks) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static void onPotionBrewed(DefaultedList<ItemStack> brewingItemStacks) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static void onPlayerBrewedPotion(PlayerEntity player, ItemStack stack) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean renderFireOverlay(PlayerEntity player, float renderPartialTicks) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean renderWaterOverlay(PlayerEntity player, float renderPartialTicks) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	/*
	@Stubbed
	public static boolean renderBlockOverlay(PlayerEntity player, float renderPartialTicks, OverlayType type, BlockState block, BlockPos pos) {
		throw new NotImplementedException("ForgeEventFactory stub");
	} */

	@Nullable
	public static <T> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider) {
		return gatherCapabilities(type, provider, null);
	}

	@Nullable
	public static <T> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider, @Nullable ICapabilityProvider parent) {
		return CapabilityEvents.gatherCapabilities(type, provider, parent);
	}

	/*
	@Stubbed
	@Nullable
	private static CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent<?> event, @Nullable ICapabilityProvider parent) {
		throw new NotImplementedException("ForgeEventFactory stub");
	} */

	@Stubbed
	public static boolean fireSleepingLocationCheck(LivingEntity player, BlockPos sleepingLocation) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean fireSleepingTimeCheck(PlayerEntity player, Optional<BlockPos> sleepingLocation) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static TypedActionResult<ItemStack> onArrowNock(ItemStack item, World world, PlayerEntity player, Hand hand, boolean hasAmmo) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static int onArrowLoose(ItemStack stack, World world, PlayerEntity player, int charge, boolean hasAmmo) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	public static boolean onProjectileImpact(Entity entity, HitResult ray) {
		return EntityEvents.onProjectileImpact(entity, ray);
	}

	public static boolean onProjectileImpact(PersistentProjectileEntity arrow, HitResult ray) {
		return EntityEvents.onProjectileImpact(arrow, ray);
	}

	public static boolean onProjectileImpact(ExplosiveProjectileEntity fireball, HitResult ray) {
		return EntityEvents.onProjectileImpact(fireball, ray);
	}

	public static boolean onProjectileImpact(ThrownEntity throwable, HitResult ray) {
		return EntityEvents.onProjectileImpact(throwable, ray);
	}

	public static LootTable loadLootTable(Identifier name, LootTable table, LootManager lootTableManager) {
		return LootEvents.loadLootTable(name, table, lootTableManager);
	}

	@Stubbed
	public static boolean canCreateFluidSource(World world, BlockPos pos, BlockState state, boolean def) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	/*
	@Stubbed
	public static boolean onTrySpawnPortal(IWorld world, BlockPos pos, PortalBlock.AreaHelper size) {
		throw new NotImplementedException("ForgeEventFactory stub");
	} */

	@Stubbed
	public static int onEnchantmentLevelSet(World world, BlockPos pos, int enchantRow, int power, ItemStack itemStack, int level) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean onEntityDestroyBlock(LivingEntity entity, BlockPos pos, BlockState state) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean gatherCollisionBoxes(World world, Entity entity, Box aabb, List<Box> outList) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean getMobGriefingEvent(World world, Entity entity) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	public static boolean saplingGrowTree(WorldAccess world, Random rand, BlockPos pos) {
		return WorldEvents.onSaplingGrowTree(world, rand, pos);
	}

	@Stubbed
	public static void fireChunkWatch(boolean watch, ServerPlayerEntity entity, ChunkPos chunkpos, ServerWorld world) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean onPistonMovePre(World world, BlockPos pos, Direction direction, boolean extending) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static boolean onPistonMovePost(World world, BlockPos pos, Direction direction, boolean extending) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}

	@Stubbed
	public static long onSleepFinished(ServerWorld world, long newTime, long minTime) {
		throw new NotImplementedException("ForgeEventFactory stub");
	}
}

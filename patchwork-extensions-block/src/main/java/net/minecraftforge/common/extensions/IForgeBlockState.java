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

package net.minecraftforge.common.extensions;

import java.util.Optional;

import net.minecraftforge.common.ToolType;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnRestriction.Location;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.patchworkmc.annotations.Stubbed;

// Remember to remove @Stubbed from the IForgeBlock method too
public interface IForgeBlockState {
	default BlockState getBlockState() {
		return (BlockState) this;
	}

	default IForgeBlock patchwork$getForgeBlock() {
		return (IForgeBlock) getBlockState().getBlock();
	}

	/**
	 * Gets the slipperiness at the given location at the given state. Normally
	 * between 0 and 1.
	 *
	 * <p>Note that entities may reduce slipperiness by a certain factor of their own;
	 * for {@link LivingEntity}, this is {@code .91}.
	 * {@link net.minecraft.entity.ItemEntity} uses {@code .98}, and
	 * {@link net.minecraft.entity.projectile.FishingBobberEntity} uses {@code .92}.
	 *
	 * @param world  the world
	 * @param pos    the position in the world
	 * @param entity the entity in question
	 * @return the factor by which the entity's motion should be multiplied
	 */
	@Stubbed
	default float getSlipperiness(WorldView world, BlockPos pos, @Nullable Entity entity) {
		return patchwork$getForgeBlock().getSlipperiness(getBlockState(), world, pos, entity);
	}

	/**
	 * Get a light value for this block, taking into account the given state and coordinates, normal ranges are between 0 and 15
	 */
	@Stubbed
	default int getLightValue(BlockView world, BlockPos pos) {
		return patchwork$getForgeBlock().getLightValue(getBlockState(), world, pos);
	}

	/**
	 * Checks if a player or entity can use this block to 'climb' like a ladder.
	 *
	 * @param world  The current world
	 * @param pos    Block position in world
	 * @param entity The entity trying to use the ladder, CAN be null.
	 * @return True if the block should act like a ladder
	 */
	@Stubbed
	default boolean isLadder(WorldView world, BlockPos pos, LivingEntity entity) {
		return patchwork$getForgeBlock().isLadder(getBlockState(), world, pos, entity);
	}

	/**
	 * Called throughout the code as a replacement for block instanceof BlockContainer
	 * Moving this to the Block base class allows for mods that wish to extend vanilla
	 * blocks, and also want to have a tile entity on that block, may.
	 * <p>
	 * Return true from this function to specify this block has a tile entity.
	 *
	 * @return True if block has a tile entity, false otherwise
	 */
	// Implemented through mass asm
	default boolean hasTileEntity() {
		return patchwork$getForgeBlock().hasTileEntity(getBlockState());
	}

	/**
	 * Called throughout the code as a replacement for ITileEntityProvider.createNewTileEntity
	 * Return the same thing you would from that function.
	 * This will fall back to ITileEntityProvider.createNewTileEntity(World) if this block is a ITileEntityProvider
	 *
	 * @param world The world to create the TE in
	 * @return A instance of a class extending TileEntity
	 */
	@Nullable
	// Implemented through mass asm
	default BlockEntity createTileEntity(BlockView world) {
		return patchwork$getForgeBlock().createTileEntity(getBlockState(), world);
	}

	/**
	 * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
	 *
	 * @param world  The current world
	 * @param pos    The block's current position
	 * @param player The player damaging the block
	 * @return True to spawn the drops
	 */
	@Stubbed
	default boolean canHarvestBlock(BlockView world, BlockPos pos, PlayerEntity player) {
		return patchwork$getForgeBlock().canHarvestBlock(getBlockState(), world, pos, player);
	}

	/**
	 * Called when a player removes a block.  This is responsible for
	 * actually destroying the block, and the block is intact at time of call.
	 * This is called regardless of whether the player can harvest the block or
	 * not.
	 * <p>
	 * Return true if the block is actually destroyed.
	 * <p>
	 * Note: When used in multiplayer, this is called on both client and
	 * server sides!
	 *
	 * @param world       The current world
	 * @param player      The player damaging the block, may be null
	 * @param pos         Block position in world
	 * @param willHarvest True if Block.harvestBlock will be called after this, if the return in true.
	 *                    Can be useful to delay the destruction of tile entities till after harvestBlock
	 * @param fluid       The current fluid and block state for the position in the world.
	 * @return True if the block is actually destroyed.
	 */
	@Stubbed
	default boolean removedByPlayer(World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
		return patchwork$getForgeBlock().removedByPlayer(getBlockState(), world, pos, player, willHarvest, fluid);
	}

	/**
	 * Determines if this block is classified as a Bed, Allowing
	 * players to sleep in it, though the block has to specifically
	 * perform the sleeping functionality in it's activated event.
	 *
	 * @param world   The current world
	 * @param pos     Block position in world
	 * @param sleeper The sleeper or camera entity, null in some cases.
	 * @return True to treat this as a bed
	 */
	@Stubbed
	default boolean isBed(BlockView world, BlockPos pos, @Nullable LivingEntity sleeper) {
		return patchwork$getForgeBlock().isBed(getBlockState(), world, pos, sleeper);
	}

	/**
	 * Determines if a specified mob type can spawn on this block, returning false will
	 * prevent any mob from spawning on the block.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @param type  The Mob Category Type
	 * @return True to allow a mob of the specified category to spawn, false to prevent it.
	 */
	@Stubbed
	default boolean canCreatureSpawn(WorldView world, BlockPos pos, Location type, EntityType<?> entityType) {
		return patchwork$getForgeBlock().canCreatureSpawn(getBlockState(), world, pos, type, entityType);
	}

	/**
	 * Returns the position that the sleeper is moved to upon
	 * waking up, or respawning at the bed.
	 *
	 * @param world       The current world
	 * @param pos         Block position in world
	 * @param orientation The direction the entity is facing while getting into bed.
	 * @param sleeper     The sleeper or camera entity, null in some cases.
	 * @return The spawn position
	 */
	@Stubbed
	default Optional<Vec3d> getBedSpawnPosition(EntityType<?> type, WorldView world, BlockPos pos, float orientation, @Nullable LivingEntity sleeper) {
		return patchwork$getForgeBlock().getBedSpawnPosition(type, getBlockState(), world, pos, orientation, sleeper);
	}

	/**
	 * Called when a user either starts or stops sleeping in the bed.
	 *
	 * @param world    The current world
	 * @param pos      Block position in world
	 * @param sleeper  The sleeper or camera entity, null in some cases.
	 * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
	 */
	@Stubbed
	default void setBedOccupied(World world, BlockPos pos, LivingEntity sleeper, boolean occupied) {
		patchwork$getForgeBlock().setBedOccupied(getBlockState(), world, pos, sleeper, occupied);
	}

	/**
	 * Returns the direction of the block. Same values that
	 * are returned by BlockDirectional
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return Bed direction
	 */
	@Stubbed
	default Direction getBedDirection(WorldView world, BlockPos pos) {
		return patchwork$getForgeBlock().getBedDirection(getBlockState(), world, pos);
	}

	/**
	 * Determines this block should be treated as an air block
	 * by the rest of the code. This method is primarily
	 * useful for creating pure logic-blocks that will be invisible
	 * to the player and otherwise interact as air would.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return True if the block considered air
	 * @deprecated TODO: Remove in 1.17, in favor of state only version. This is a old hook from before
	 * block states were unlimited and people used TileEntities. If you still use the location
	 * information in your TileEntity please explain why and how you can't use BlockState only version
	 * here: https://github.com/MinecraftForge/MinecraftForge/issues/7409
	 */
	@Deprecated
	// Don't bother implementing, getting removed in 1.17
	@Stubbed
	default boolean isAir(BlockView world, BlockPos pos) {
		return patchwork$getForgeBlock().isAir(getBlockState(), world, pos);
	}

	/**
	 * Used during tree growth to determine if newly generated leaves can replace this block.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return true if this block can be replaced by growing leaves.
	 */
	@Stubbed
	default boolean canBeReplacedByLeaves(WorldView world, BlockPos pos) {
		return patchwork$getForgeBlock().canBeReplacedByLeaves(getBlockState(), world, pos);
	}

	/**
	 * Used during tree growth to determine if newly generated logs can replace this block.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return true if this block can be replaced by growing leaves.
	 */
	@Stubbed
	default boolean canBeReplacedByLogs(WorldView world, BlockPos pos) {
		return patchwork$getForgeBlock().canBeReplacedByLogs(getBlockState(), world, pos);
	}

	/**
	 * Location sensitive version of getExplosionResistance
	 *
	 * @param world     The current world
	 * @param pos       Block position in world
	 * @param explosion The explosion
	 * @return The amount of the explosion absorbed.
	 */
	@Stubbed
	default float getExplosionResistance(BlockView world, BlockPos pos, Explosion explosion) {
		return patchwork$getForgeBlock().getExplosionResistance(getBlockState(), world, pos, explosion);
	}

	/**
	 * Determine if this block can make a redstone connection on the side provided,
	 * Useful to control which sides are inputs and outputs for redstone wires.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @param side  The side that is trying to make the connection, CAN BE NULL
	 * @return True to make the connection
	 */
	@Stubbed
	default boolean canConnectRedstone(BlockView world, BlockPos pos, @Nullable Direction side) {
		return patchwork$getForgeBlock().canConnectRedstone(getBlockState(), world, pos, side);
	}

	/**
	 * Called when A user uses the creative pick block button on this block
	 *
	 * @param target The full target the player is looking at
	 * @return A ItemStack to add to the player's inventory, empty itemstack if nothing should be added.
	 */
	@Stubbed
	default ItemStack getPickBlock(HitResult target, BlockView world, BlockPos pos, PlayerEntity player) {
		return patchwork$getForgeBlock().getPickBlock(getBlockState(), target, world, pos, player);
	}

	/**
	 * Allows a block to override the standard EntityLivingBase.updateFallState
	 * particles, this is a server side method that spawns particles with
	 * WorldServer.spawnParticle.
	 *
	 * @param worldserver       The current Server World
	 * @param pos               The position of the block.
	 * @param state2            The state at the specific world/pos
	 * @param entity            The entity that hit landed on the block
	 * @param numberOfParticles That vanilla world have spawned
	 * @return True to prevent vanilla landing particles from spawning
	 */
	@Stubbed
	default boolean addLandingEffects(ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return patchwork$getForgeBlock().addLandingEffects(getBlockState(), worldserver, pos, state2, entity, numberOfParticles);
	}

	/**
	 * Allows a block to override the standard vanilla running particles.
	 * This is called from {@link Entity#spawnRunningParticles} and is called both,
	 * Client and server side, it's up to the implementor to client check / server check.
	 * By default vanilla spawns particles only on the client and the server methods no-op.
	 *
	 * @param world  The world.
	 * @param pos    The position at the entities feet.
	 * @param entity The entity running on the block.
	 * @return True to prevent vanilla running particles from spawning.
	 */
	@Stubbed
	default boolean addRunningEffects(World world, BlockPos pos, Entity entity) {
		return patchwork$getForgeBlock().addRunningEffects(getBlockState(), world, pos, entity);
	}

	/**
	 * Spawn a digging particle effect in the world, this is a wrapper
	 * around EffectRenderer.addBlockHitEffects to allow the block more
	 * control over the particles. Useful when you have entirely different
	 * texture sheets for different sides/locations in the world.
	 *
	 * @param world   The current world
	 * @param target  The target the player is looking at {x/y/z/side/sub}
	 * @param manager A reference to the current particle manager.
	 * @return True to prevent vanilla digging particles form spawning.
	 */
	@Environment(EnvType.CLIENT)
	@Stubbed
	default boolean addHitEffects(World world, HitResult target, ParticleManager manager) {
		return patchwork$getForgeBlock().addHitEffects(getBlockState(), world, target, manager);
	}

	/**
	 * Spawn particles for when the block is destroyed. Due to the nature
	 * of how this is invoked, the x/y/z locations are not always guaranteed
	 * to host your block. So be sure to do proper sanity checks before assuming
	 * that the location is this block.
	 *
	 * @param world   The current world
	 * @param pos     Position to spawn the particle
	 * @param manager A reference to the current particle manager.
	 * @return True to prevent vanilla break particles from spawning.
	 */
	@Environment(EnvType.CLIENT)
	@Stubbed
	default boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
		return patchwork$getForgeBlock().addDestroyEffects(getBlockState(), world, pos, manager);
	}

	/**
	 * Determines if this block can support the passed in plant, allowing it to be planted and grow.
	 * Some examples:
	 * Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water
	 * Cacti checks if its a cacti, or if its sand
	 * Nether types check for soul sand
	 * Crops check for tilled soil
	 * Caves check if it's a solid surface
	 * Plains check if its grass or dirt
	 * Water check if its still water
	 *
	 * @param world     The current world
	 * @param facing    The direction relative to the given position the plant wants to be, typically its UP
	 * @param plantable The plant that wants to check
	 * @return True to allow the plant to be planted/stay.
	 */
	//@Stubbed
	//default boolean canSustainPlant(BlockView world, BlockPos pos, Direction facing, IPlantable plantable) {
	//	return patchwork$getForgeBlock().canSustainPlant(getBlockState(), world, pos, facing, plantable);
	//}

	/**
	 * Checks if this soil is fertile, typically this means that growth rates
	 * of plants on this soil will be slightly sped up.
	 * Only vanilla case is tilledField when it is within range of water.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return True if the soil should be considered fertile.
	 */
	@Stubbed
	default boolean isFertile(BlockView world, BlockPos pos) {
		return patchwork$getForgeBlock().isFertile(getBlockState(), world, pos);
	}

	/**
	 * Determines if this block can be used as the frame of a conduit.
	 *
	 * @param world   The current world
	 * @param pos     Block position in world
	 * @param conduit Conduit position in world
	 * @return True, to support the conduit, and make it active with this block.
	 */
	@Stubbed
	default boolean isConduitFrame(WorldView world, BlockPos pos, BlockPos conduit) {
		return patchwork$getForgeBlock().isConduitFrame(getBlockState(), world, pos, conduit);
	}

	/**
	 * Determines if this block can be used as part of a frame of a nether portal.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return True, to support being part of a nether portal frame, false otherwise.
	 */
	@Stubbed
	default boolean isPortalFrame(BlockView world, BlockPos pos) {
		return patchwork$getForgeBlock().isPortalFrame(getBlockState(), world, pos);
	}

	/**
	 * Gathers how much experience this block drops when broken.
	 *
	 * @param world   The world
	 * @param pos     Block position
	 * @param fortune
	 * @return Amount of XP from breaking this block.
	 */
	@Stubbed
	default int getExpDrop(WorldView world, BlockPos pos, int fortune, int silktouch) {
		return patchwork$getForgeBlock().getExpDrop(getBlockState(), world, pos, fortune, silktouch);
	}

	@Stubbed
	default BlockState rotate(WorldAccess world, BlockPos pos, BlockRotation direction) {
		return patchwork$getForgeBlock().rotate(getBlockState(), world, pos, direction);
	}

	/**
	 * Determines the amount of enchanting power this block can provide to an enchanting table.
	 *
	 * @param world The World
	 * @param pos   Block position in world
	 * @return The amount of enchanting power this block produces.
	 */
	@Stubbed
	default float getEnchantPowerBonus(WorldView world, BlockPos pos) {
		return patchwork$getForgeBlock().getEnchantPowerBonus(getBlockState(), world, pos);
	}

	/**
	 * Called when a tile entity on a side of this block changes is created or is destroyed.
	 *
	 * @param world    The world
	 * @param pos      Block position in world
	 * @param neighbor Block position of neighbor
	 */
	@Stubbed
	default void onNeighborChange(WorldView world, BlockPos pos, BlockPos neighbor) {
		patchwork$getForgeBlock().onNeighborChange(getBlockState(), world, pos, neighbor);
	}

	/**
	 * Called on an Observer block whenever an update for an Observer is received.
	 *
	 * @param observerState The Observer block's state.
	 * @param world         The current world.
	 * @param pos           The Observer block's position.
	 * @param changed       The updated block.
	 * @param changedPos    The updated block's position.
	 */
	@Stubbed
	default void observedNeighborChange(World world, BlockPos pos, Block changed, BlockPos changedPos) {
		patchwork$getForgeBlock().observedNeighborChange(getBlockState(), world, pos, changed, changedPos);
	}

	/**
	 * Called to determine whether to allow the a block to handle its own indirect power rather than using the default rules.
	 *
	 * @param world The world
	 * @param pos   Block position in world
	 * @param side  The INPUT side of the block to be powered - ie the opposite of this block's output side
	 * @return Whether Block#isProvidingWeakPower should be called when determining indirect power
	 */
	@Stubbed
	default boolean shouldCheckWeakPower(WorldView world, BlockPos pos, Direction side) {
		return patchwork$getForgeBlock().shouldCheckWeakPower(getBlockState(), world, pos, side);
	}

	/**
	 * If this block should be notified of weak changes.
	 * Weak changes are changes 1 block away through a solid block.
	 * Similar to comparators.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return true To be notified of changes
	 */
	@Stubbed
	default boolean getWeakChanges(WorldView world, BlockPos pos) {
		return patchwork$getForgeBlock().getWeakChanges(getBlockState(), world, pos);
	}

	/**
	 * Queries the class of tool required to harvest this block, if null is returned
	 * we assume that anything can harvest this block.
	 */
	@Stubbed
	default ToolType getHarvestTool() {
		return patchwork$getForgeBlock().getHarvestTool(getBlockState());
	}

	@Stubbed
	default int getHarvestLevel() {
		return patchwork$getForgeBlock().getHarvestLevel(getBlockState());
	}

	/**
	 * Checks if the specified tool type is efficient on this block,
	 * meaning that it digs at full speed.
	 */
	@Stubbed
	default boolean isToolEffective(ToolType tool) {
		return patchwork$getForgeBlock().isToolEffective(getBlockState(), tool);
	}

	/**
	 * Sensitive version of getSoundType
	 *
	 * @param world  The world
	 * @param pos    The position. Note that the world may not necessarily have {@code state} here!
	 * @param entity The entity that is breaking/stepping on/placing/hitting/falling on this block, or null if no entity is in this context
	 * @return A SoundType to use
	 */
	@Stubbed
	default BlockSoundGroup getSoundType(WorldView world, BlockPos pos, @Nullable Entity entity) {
		return patchwork$getForgeBlock().getSoundType(getBlockState(), world, pos, entity);
	}

	/**
	 * @param world     The world
	 * @param pos       The position of this state
	 * @param beaconPos The position of the beacon
	 * @return A float RGB [0.0, 1.0] array to be averaged with a beacon's existing beam color, or null to do nothing to the beam
	 */
	@Nullable
	@Stubbed
	default float[] getBeaconColorMultiplier(WorldView world, BlockPos pos, BlockPos beacon) {
		return patchwork$getForgeBlock().getBeaconColorMultiplier(getBlockState(), world, pos, beacon);
	}

	/**
	 * Use this to change the fog color used when the entity is "inside" a material.
	 * Vec3d is used here as "r/g/b" 0 - 1 values.
	 *
	 * @param world         The world.
	 * @param pos           The position at the entity viewport.
	 * @param entity        the entity
	 * @param originalColor The current fog color, You are not expected to use this, Return as the default if applicable.
	 * @return The new fog color.
	 */
	@Environment(EnvType.CLIENT)
	@Stubbed
	default Vec3d getFogColor(WorldView world, BlockPos pos, Entity entity, Vec3d originalColor, float partialTicks) {
		return patchwork$getForgeBlock().getFogColor(getBlockState(), world, pos, entity, originalColor, partialTicks);
	}

	/**
	 * Used to determine the state 'viewed' by an entity (see
	 * {@link ActiveRenderInfo#getBlockStateAtEntityViewpoint(World, Entity, float)}).
	 * Can be used by fluid blocks to determine if the viewpoint is within the fluid or not.
	 *
	 * @param world     the world
	 * @param pos       the position
	 * @param viewpoint the viewpoint
	 * @return the block state that should be 'seen'
	 */
	@Stubbed
	default BlockState getStateAtViewpoint(BlockView world, BlockPos pos, Vec3d viewpoint) {
		return patchwork$getForgeBlock().getStateAtViewpoint(getBlockState(), world, pos, viewpoint);
	}

	/**
	 * @param state The state
	 * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
	 */
	@Stubbed
	default boolean isSlimeBlock() {
		return patchwork$getForgeBlock().isSlimeBlock(getBlockState());
	}

	/**
	 * @param state The state
	 * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
	 */
	@Stubbed
	default boolean isStickyBlock() {
		return patchwork$getForgeBlock().isStickyBlock(getBlockState());
	}

	/**
	 * Determines if this block can stick to another block when pushed by a piston.
	 *
	 * @param other Other block
	 * @return True to link blocks
	 */
	@Stubbed
	default boolean canStickTo(BlockState other) {
		return patchwork$getForgeBlock().canStickTo(getBlockState(), other);
	}

	/**
	 * Chance that fire will spread and consume this block.
	 * 300 being a 100% chance, 0, being a 0% chance.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @param face  The face that the fire is coming from
	 * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
	 */
	@Stubbed
	default int getFlammability(BlockView world, BlockPos pos, Direction face) {
		return patchwork$getForgeBlock().getFlammability(getBlockState(), world, pos, face);
	}

	/**
	 * Called when fire is updating, checks if a block face can catch fire.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @param face  The face that the fire is coming from
	 * @return True if the face can be on fire, false otherwise.
	 */
	@Stubbed
	default boolean isFlammable(BlockView world, BlockPos pos, Direction face) {
		return patchwork$getForgeBlock().isFlammable(getBlockState(), world, pos, face);
	}

	/**
	 * If the block is flammable, this is called when it gets lit on fire.
	 *
	 * @param world   The current world
	 * @param pos     Block position in world
	 * @param face    The face that the fire is coming from
	 * @param igniter The entity that lit the fire
	 */
	@Stubbed
	default void catchFire(World world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
		patchwork$getForgeBlock().catchFire(getBlockState(), world, pos, face, igniter);
	}

	/**
	 * Called when fire is updating on a neighbor block.
	 * The higher the number returned, the faster fire will spread around this block.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @param face  The face that the fire is coming from
	 * @return A number that is used to determine the speed of fire growth around the block
	 */
	@Stubbed
	default int getFireSpreadSpeed(BlockView world, BlockPos pos, Direction face) {
		return patchwork$getForgeBlock().getFireSpreadSpeed(getBlockState(), world, pos, face);
	}

	/**
	 * Currently only called by fire when it is on top of this block.
	 * Returning true will prevent the fire from naturally dying during updating.
	 * Also prevents firing from dying from rain.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @param side  The face that the fire is coming from
	 * @return True if this block sustains fire, meaning it will never go out.
	 */
	@Stubbed
	default boolean isFireSource(WorldView world, BlockPos pos, Direction side) {
		return patchwork$getForgeBlock().isFireSource(getBlockState(), world, pos, side);
	}

	/**
	 * Determines if this block is can be destroyed by the specified entities normal behavior.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return True to allow the ender dragon to destroy this block
	 */
	@Stubbed
	default boolean canEntityDestroy(BlockView world, BlockPos pos, Entity entity) {
		return patchwork$getForgeBlock().canEntityDestroy(getBlockState(), world, pos, entity);
	}

	/**
	 * Determines if this block should set fire and deal fire damage
	 * to entities coming into contact with it.
	 *
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return True if the block should deal damage
	 */
	@Stubbed
	default boolean isBurning(BlockView world, BlockPos pos) {
		return patchwork$getForgeBlock().isBurning(getBlockState(), world, pos);
	}

	/**
	 * Get the {@code PathNodeType} for this block. Return {@code null} for vanilla behavior.
	 *
	 * @return the PathNodeType
	 */
	@Nullable
	@Stubbed
	default PathNodeType getAiPathNodeType(BlockView world, BlockPos pos) {
		return getAiPathNodeType(world, pos, null);
	}

	/**
	 * Get the {@code PathNodeType} for this block. Return {@code null} for vanilla behavior.
	 *
	 * @return the PathNodeType
	 */
	@Nullable
	@Stubbed
	default PathNodeType getAiPathNodeType(BlockView world, BlockPos pos, @Nullable MobEntity entity) {
		return patchwork$getForgeBlock().getAiPathNodeType(getBlockState(), world, pos, entity);
	}

	/**
	 * Determines if this block should drop loot when exploded.
	 */
	@Stubbed
	default boolean canDropFromExplosion(BlockView world, BlockPos pos, Explosion explosion) {
		return patchwork$getForgeBlock().canDropFromExplosion(getBlockState(), world, pos, explosion);
	}

	/**
	 * Called when the block is destroyed by an explosion.
	 * Useful for allowing the block to take into account tile entities,
	 * state, etc. when exploded, before it is removed.
	 *
	 * @param world     The current world
	 * @param pos       Block position in world
	 * @param explosion The explosion instance affecting the block
	 */
	@Stubbed
	default void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		patchwork$getForgeBlock().onBlockExploded(getBlockState(), world, pos, explosion);
	}

	/**
	 * Determines if this block's collision box should be treated as though it can extend above its block space.
	 * This can be used to replicate fence and wall behavior.
	 */
	@Stubbed
	default boolean collisionExtendsVertically(BlockView world, BlockPos pos, Entity collidingEntity) {
		return patchwork$getForgeBlock().collisionExtendsVertically(getBlockState(), world, pos, collidingEntity);
	}

	/**
	 * Called to determine whether this block should use the fluid overlay texture or flowing texture when it is placed under the fluid.
	 *
	 * @param world      The world
	 * @param pos        Block position in world
	 * @param fluidState The state of the fluid
	 * @return Whether the fluid overlay texture should be used
	 */
	@Stubbed
	default boolean shouldDisplayFluidOverlay(BlockRenderView world, BlockPos pos, FluidState fluidState) {
		return patchwork$getForgeBlock().shouldDisplayFluidOverlay(getBlockState(), world, pos, fluidState);
	}

	/**
	 * Returns the state that this block should transform into when right clicked by a tool.
	 * For example: Used to determine if an axe can strip, a shovel can path, or a hoe can till.
	 * Return null if vanilla behavior should be disabled.
	 *
	 * @param world     The world
	 * @param pos       The block position in world
	 * @param player    The player clicking the block
	 * @param stack     The stack being used by the player
	 * @param toolTypes The tool types to be considered when performing the action
	 * @return The resulting state after the action has been performed
	 */
	@Nullable
	@Stubbed
	default BlockState getToolModifiedState(World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		throw new NotImplementedException("getToolModifiedState not implemented");
		//BlockState eventState = net.minecraftforge.event.ForgeEventFactory.onToolUse(getBlockState(), world, pos, player, stack, toolType);
		//return eventState != getBlockState() ? eventState : patchwork$getForgeBlock().getToolModifiedState(getBlockState(), world, pos, player, stack, toolType);
	}

	/**
	 * Checks if a player or entity handles movement on this block like scaffolding.
	 *
	 * @param entity The entity on the scaffolding
	 * @return True if the block should act like scaffolding
	 */
	@Stubbed
	default boolean isScaffolding(LivingEntity entity) {
		return patchwork$getForgeBlock().isScaffolding(getBlockState(), entity.world, entity.getBlockPos(), entity);
	}
}

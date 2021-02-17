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
import java.util.Set;

import net.minecraftforge.common.ToolType;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.block.Stainable;
import net.minecraft.block.TransparentBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
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
import net.patchworkmc.api.block.PatchworkAxeItem;
import net.patchworkmc.api.block.PatchworkHoeItem;
import net.patchworkmc.api.block.PatchworkShovelItem;

@SuppressWarnings("EqualsBetweenInconvertibleTypes")
public interface IForgeBlock {
	default Block getBlock() {
		return (Block) this;
	}

	// TODO: move this out of here?
	default BlockEntity patchwork$createTileEntityIntermediate(BlockView world) {
		return createTileEntity(getBlock().getDefaultState(), world);
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
	 * @param state  state of the block
	 * @param world  the world
	 * @param pos    the position in the world
	 * @param entity the entity in question
	 * @return the factor by which the entity's motion should be multiplied
	 */
	@Stubbed
	float getSlipperiness(BlockState state, WorldView world, BlockPos pos, @Nullable Entity entity);

	/**
	 * Get a light value for this block, taking into account the given state and coordinates, normal ranges are between 0 and 15
	 *
	 * @param state
	 * @param world
	 * @param pos
	 * @return The light value
	 */
	@Stubbed
	default int getLightValue(BlockState state, BlockView world, BlockPos pos) {
		return state.getLuminance();
	}

	/**
	 * Checks if a player or entity can use this block to 'climb' like a ladder.
	 *
	 * @param state  The current state
	 * @param world  The current world
	 * @param pos    Block position in world
	 * @param entity The entity trying to use the ladder, CAN be null.
	 * @return True if the block should act like a ladder
	 */
	@Stubbed
	default boolean isLadder(BlockState state, WorldView world, BlockPos pos, LivingEntity entity) {
		return state.getBlock().isIn(BlockTags.CLIMBABLE);
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
	default boolean isBurning(BlockState state, BlockView world, BlockPos pos) {
		return this == Blocks.FIRE || this == Blocks.LAVA;
	}

	/**
	 * Called throughout the code as a replacement for {@code block instanceof} {@link BlockEntityProvider}.
	 * Allows for blocks to have a block entity conditionally based on block state.
	 *
	 * <p>Return true from this function to specify this block has a block entity.
	 *
	 * @param state State of the current block
	 * @return True if block has a tile entity, false otherwise
	 */
	// Implemented through mass asm
	default boolean hasTileEntity(BlockState state) {
		return this instanceof BlockEntityProvider;
	}

	/**
	 * Called throughout the code as a replacement for {@link BlockEntityProvider#createBlockEntity(BlockView)}
	 * Return the same thing you would from that function.
	 * This will fall back to {@link BlockEntityProvider#createBlockEntity(BlockView)} if this block is a {@link BlockEntityProvider}
	 *
	 * @param state The state of the current block
	 * @param world The world to create the TE in
	 * @return A instance of a class extending TileEntity
	 */
	@Nullable
	// Implemented through mass asm
	default BlockEntity createTileEntity(BlockState state, BlockView world) {
		if (getBlock() instanceof BlockEntityProvider) {
			return ((BlockEntityProvider) getBlock()).createBlockEntity(world);
		}

		return null;
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
	default boolean canHarvestBlock(BlockState state, BlockView world, BlockPos pos, PlayerEntity player) {
		throw new NotImplementedException("canharvestblock not implemented");
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
	 * @param state       The current state.
	 * @param world       The current world
	 * @param player      The player damaging the block, may be null
	 * @param pos         Block position in world
	 * @param willHarvest True if Block.harvestBlock will be called after this, if the return in true.
	 *                    Can be useful to delay the destruction of tile entities till after harvestBlock
	 * @param fluid       The current fluid state at current position
	 * @return True if the block is actually destroyed.
	 */
	@Stubbed
	default boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
		getBlock().onBreak(world, pos, state, player);
		return world.setBlockState(pos, fluid.getBlockState(), world.isClient ? 11 : 3);
	}

	/**
	 * Determines if this block is classified as a Bed, Allowing
	 * players to sleep in it, though the block has to specifically
	 * perform the sleeping functionality in it's activated event.
	 *
	 * @param state  The current state
	 * @param world  The current world
	 * @param pos    Block position in world
	 * @param player The player or camera entity, null in some cases.
	 * @return True to treat this as a bed
	 */
	@Stubbed
	default boolean isBed(BlockState state, BlockView world, BlockPos pos, @Nullable Entity player) {
		return this.getBlock() instanceof BedBlock; //TODO: Forge: Keep isBed function?
	}

	/**
	 * Determines if a specified mob type can spawn on this block, returning false will
	 * prevent any mob from spawning on the block.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos   Block position in world
	 * @param type  The Mob Category Type
	 * @return True to allow a mob of the specified category to spawn, false to prevent it.
	 */
	@Stubbed
	default boolean canCreatureSpawn(BlockState state, BlockView world, BlockPos pos, SpawnRestriction.Location type, @Nullable EntityType<?> entityType) {
		return state.allowsSpawning(world, pos, entityType);
	}

	/**
	 * Returns the position that the sleeper is moved to upon
	 * waking up, or respawning at the bed.
	 *
	 * @param state       The current state
	 * @param world       The current world
	 * @param pos         Block position in world
	 * @param orientation The direction the entity is facing while getting into bed.
	 * @param sleeper     The sleeper or camera entity, null in some cases.
	 * @return The spawn position
	 */
	@Stubbed
	default Optional<Vec3d> getBedSpawnPosition(EntityType<?> entityType, BlockState state, WorldView world, BlockPos pos, float orientation, @Nullable LivingEntity sleeper) {
		if (world instanceof World) {
			return BedBlock.findWakeUpPosition(entityType, world, pos, orientation);
		}

		return Optional.empty();
	}

	/**
	 * Called when a user either starts or stops sleeping in the bed.
	 *
	 * @param state
	 * @param world    The current world
	 * @param pos      Block position in world
	 * @param sleeper  The sleeper or camera entity, null in some cases.
	 * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
	 */
	@Stubbed
	default void setBedOccupied(BlockState state, World world, BlockPos pos, LivingEntity sleeper, boolean occupied) {
		world.setBlockState(pos, state.with(BedBlock.OCCUPIED, occupied), 3);
	}

	/**
	 * Returns the direction of the block. Same values that
	 * are returned by BlockDirectional. Called every frame tick for every living entity. Be VERY fast.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return Bed direction
	 */
	@Stubbed
	default Direction getBedDirection(BlockState state, WorldView world, BlockPos pos) {
		return state.get(HorizontalFacingBlock.FACING);
	}

	/**
	 * Determines this block should be treated as an air block
	 * by the rest of the code. This method is primarily
	 * useful for creating pure logic-blocks that will be invisible
	 * to the player and otherwise interact as air would.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return True if the block considered air
	 */
	@Stubbed
	// Don't bother implementing--getting removed in 1.17
	default boolean isAir(BlockState state, BlockView world, BlockPos pos) {
		return state.getMaterial() == Material.AIR;
	}

	/**
	 * Used during tree growth to determine if newly generated leaves can replace this block.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return true if this block can be replaced by growing leaves.
	 */
	@Stubbed
	default boolean canBeReplacedByLeaves(BlockState state, WorldView world, BlockPos pos) {
		return isAir(state, world, pos) || state.isIn(BlockTags.LEAVES);
	}

	/**
	 * Used during tree growth to determine if newly generated logs can replace this block.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return true if this block can be replaced by growing leaves.
	 */
	@Stubbed
	default boolean canBeReplacedByLogs(BlockState state, WorldView world, BlockPos pos) {
		throw new NotImplementedException("canBeReplacedByLogs needs the forge Tags class");
		//return (isAir(state, world, pos) || state.isIn(BlockTags.LEAVES)) || this == Blocks.GRASS_BLOCK || state.isIn(Tags.Blocks.DIRT)
		//|| getBlock().isIn(BlockTags.LOGS) || getBlock().isIn(BlockTags.SAPLINGS) || this == Blocks.VINE;
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
	default float getExplosionResistance(BlockState state, BlockView world, BlockPos pos, Explosion explosion) {
		return this.getBlock().getBlastResistance();
	}

	/**
	 * Determine if this block can make a redstone connection on the side provided,
	 * Useful to control which sides are inputs and outputs for redstone wires.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos   Block position in world
	 * @param side  The side that is trying to make the connection, CAN BE NULL
	 * @return True to make the connection
	 */
	@Stubbed
	default boolean canConnectRedstone(BlockState state, BlockView world, BlockPos pos, @Nullable Direction side) {
		return state.emitsRedstonePower() && side != null;
	}

	/**
	 * Called when A user uses the creative pick block button on this block
	 *
	 * @param target The full target the player is looking at
	 * @return A ItemStack to add to the player's inventory, empty itemstack if nothing should be added.
	 */
	@Stubbed
	default ItemStack getPickBlock(BlockState state, HitResult target, BlockView world, BlockPos pos, PlayerEntity player) {
		return this.getBlock().getPickStack(world, pos, state);
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
	default boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return false;
	}

	/**
	 * Allows a block to override the standard vanilla running particles.
	 * This is called from {@link Entity#spawnRunningParticles} and is called both,
	 * Client and server side, it's up to the implementor to client check / server check.
	 * By default vanilla spawns particles only on the client and the server methods no-op.
	 *
	 * @param state  The BlockState the entity is running on.
	 * @param world  The world.
	 * @param pos    The position at the entities feet.
	 * @param entity The entity running on the block.
	 * @return True to prevent vanilla running particles from spawning.
	 */
	@Stubbed
	default boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
		return false;
	}

	/**
	 * Spawn a digging particle effect in the world, this is a wrapper
	 * around EffectRenderer.addBlockHitEffects to allow the block more
	 * control over the particles. Useful when you have entirely different
	 * texture sheets for different sides/locations in the world.
	 *
	 * @param state   The current state
	 * @param world   The current world
	 * @param target  The target the player is looking at {x/y/z/side/sub}
	 * @param manager A reference to the current particle manager.
	 * @return True to prevent vanilla digging particles form spawning.
	 */
	@Stubbed
	@Environment(EnvType.CLIENT)
	default boolean addHitEffects(BlockState state, World worldObj, HitResult target, ParticleManager manager) {
		return false;
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
	@Stubbed
	@Environment(EnvType.CLIENT)
	default boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
		return false;
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
	 * @param state     The Current state
	 * @param world     The current world
	 * @param facing    The direction relative to the given position the plant wants to be, typically its UP
	 * @param plantable The plant that wants to check
	 * @return True to allow the plant to be planted/stay.
	 */
	//@Stubbed
	//boolean canSustainPlant(BlockState state, BlockView world, BlockPos pos, Direction facing, IPlantable plantable);

	/**
	 * Called when a plant grows on this block, only implemented for saplings using the WorldGen*Trees classes right now.
	 * Modder may implement this for custom plants.
	 * This does not use ForgeDirection, because large/huge trees can be located in non-representable direction,
	 * so the source location is specified.
	 * Currently this just changes the block to dirt if it was grass.
	 * <p>
	 * Note: This happens DURING the generation, the generation may not be complete when this is called.
	 *
	 * @param state  The current state
	 * @param world  Current world
	 * @param pos    Block position in world
	 * @param source Source plant's position in world
	 */
	//@Stubbed
	//default void onPlantGrow(BlockState state, WorldAccess world, BlockPos pos, BlockPos source) {
	//if (state.isIn(Tags.Blocks.DIRT)) {
	//world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
	//}
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
	default boolean isFertile(BlockState state, BlockView world, BlockPos pos) {
		if (state.isOf(Blocks.FARMLAND)) {
			return state.get(FarmlandBlock.MOISTURE) > 0;
		}

		return false;
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
	default boolean isConduitFrame(BlockState state, WorldView world, BlockPos pos, BlockPos conduit) {
		return state.getBlock() == Blocks.PRISMARINE
				|| state.getBlock() == Blocks.PRISMARINE_BRICKS
				|| state.getBlock() == Blocks.SEA_LANTERN
				|| state.getBlock() == Blocks.DARK_PRISMARINE;
	}

	/**
	 * Determines if this block can be used as part of a frame of a nether portal.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return True, to support being part of a nether portal frame, false otherwise.
	 */
	@Stubbed
	default boolean isPortalFrame(BlockState state, BlockView world, BlockPos pos) {
		return state.isOf(Blocks.OBSIDIAN);
	}

	/**
	 * Gathers how much experience this block drops when broken.
	 *
	 * @param state   The current state
	 * @param world   The world
	 * @param pos     Block position
	 * @param fortune
	 * @return Amount of XP from breaking this block.
	 */
	@Stubbed
	default int getExpDrop(BlockState state, WorldView world, BlockPos pos, int fortune, int silktouch) {
		return 0;
	}

	@Stubbed
	default BlockState rotate(BlockState state, WorldAccess world, BlockPos pos, BlockRotation direction) {
		return state.rotate(direction);
	}

	/**
	 * Determines the amount of enchanting power this block can provide to an enchanting table.
	 *
	 * @param world The World
	 * @param pos   Block position in world
	 * @return The amount of enchanting power this block produces.
	 */
	@Stubbed
	default float getEnchantPowerBonus(BlockState state, WorldView world, BlockPos pos) {
		return state.isOf(Blocks.BOOKSHELF) ? 1 : 0;
	}

	/**
	 * Called when a tile entity on a side of this block changes is created or is destroyed.
	 *
	 * @param world    The world
	 * @param pos      Block position in world
	 * @param neighbor Block position of neighbor
	 */
	@Stubbed
	default void onNeighborChange(BlockState state, WorldView world, BlockPos pos, BlockPos neighbor) {
		//
	}

	/**
	 * Called on an Observer block whenever an update for an Observer is received.
	 *
	 * @param observerState   The Observer block's state.
	 * @param world           The current world.
	 * @param observerPos     The Observer block's position.
	 * @param changedBlock    The updated block.
	 * @param changedBlockPos The updated block's position.
	 */
	@Stubbed
	default void observedNeighborChange(BlockState observerState, World world, BlockPos observerPos, Block changedBlock, BlockPos changedBlockPos) {
		//
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
	default boolean shouldCheckWeakPower(BlockState state, WorldView world, BlockPos pos, Direction side) {
		return state.isSolidBlock(world, pos);
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
	default boolean getWeakChanges(BlockState state, WorldView world, BlockPos pos) {
		return false;
	}

	/**
	 * Queries the class of tool required to harvest this block, if null is returned
	 * we assume that anything can harvest this block.
	 */
	ToolType getHarvestTool(BlockState state);

	/**
	 * Queries the harvest level of this item stack for the specified tool class,
	 * Returns -1 if this tool is not of the specified type
	 *
	 * @return Harvest level, or -1 if not the specified tool type.
	 */
	int getHarvestLevel(BlockState state);

	/**
	 * Checks if the specified tool type is efficient on this block,
	 * meaning that it digs at full speed.
	 */
	@Stubbed
	default boolean isToolEffective(BlockState state, ToolType tool) {
		if (tool == ToolType.PICKAXE && (this.getBlock() == Blocks.REDSTONE_ORE || this.getBlock() == Blocks.REDSTONE_LAMP || this.getBlock() == Blocks.OBSIDIAN)) {
			return false;
		}

		return tool == getHarvestTool(state);
	}

	/**
	 * Sensitive version of getSoundType
	 *
	 * @param state  The state
	 * @param world  The world
	 * @param pos    The position. Note that the world may not necessarily have {@code state} here!
	 * @param entity The entity that is breaking/stepping on/placing/hitting/falling on this block, or null if no entity is in this context
	 * @return A SoundType to use
	 */
	@Stubbed
	default BlockSoundGroup getSoundType(BlockState state, WorldView world, BlockPos pos, @Nullable Entity entity) {
		return this.getBlock().getSoundGroup(state);
	}

	/**
	 * @param state     The state
	 * @param world     The world
	 * @param pos       The position of this state
	 * @param beaconPos The position of the beacon
	 * @return A float RGB [0.0, 1.0] array to be averaged with a beacon's existing beam color, or null to do nothing to the beam
	 */
	@Nullable
	@Stubbed
	default float[] getBeaconColorMultiplier(BlockState state, WorldView world, BlockPos pos, BlockPos beaconPos) {
		if (getBlock() instanceof Stainable) {
			return ((Stainable) getBlock()).getColor().getColorComponents();
		}

		return null;
	}

	/**
	 * Use this to change the fog color used when the entity is "inside" a material.
	 * Vec3d is used here as "r/g/b" 0 - 1 values.
	 *
	 * @param world         The world.
	 * @param pos           The position at the entity viewport.
	 * @param state         The state at the entity viewport.
	 * @param entity        the entity
	 * @param originalColor The current fog color, You are not expected to use this, Return as the default if applicable.
	 * @return The new fog color.
	 */
	@Environment(EnvType.CLIENT)
	@Stubbed
	default Vec3d getFogColor(BlockState state, WorldView world, BlockPos pos, Entity entity, Vec3d originalColor, float partialTicks) {
		if (state.getMaterial() == Material.WATER) {
			float f12 = 0.0F;

			if (entity instanceof LivingEntity) {
				LivingEntity ent = (LivingEntity) entity;
				f12 = (float) EnchantmentHelper.getRespiration(ent) * 0.2F;

				if (ent.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
					f12 = f12 * 0.3F + 0.6F;
				}
			}

			return new Vec3d(0.02F + f12, 0.02F + f12, 0.2F + f12);
		} else if (state.getMaterial() == Material.LAVA) {
			return new Vec3d(0.6F, 0.1F, 0.0F);
		}

		return originalColor;
	}

	/**
	 * Used to determine the state 'viewed' by an entity (see
	 * {@link ActiveRenderInfo#getBlockStateAtEntityViewpoint(World, Entity, float)}).
	 * Can be used by fluid blocks to determine if the viewpoint is within the fluid or not.
	 *
	 * @param state     the state
	 * @param world     the world
	 * @param pos       the position
	 * @param viewpoint the viewpoint
	 * @return the block state that should be 'seen'
	 */
	@Stubbed
	default BlockState getStateAtViewpoint(BlockState state, BlockView world, BlockPos pos, Vec3d viewpoint) {
		return state;
	}

	/**
	 * Get the {@code PathNodeType} for this block. Return {@code null} for vanilla behavior.
	 *
	 * @return the PathNodeType
	 */
	@Nullable
	default PathNodeType getAiPathNodeType(BlockState state, BlockView world, BlockPos pos, @Nullable MobEntity entity) {
		return state.getBlock() == Blocks.LAVA ? PathNodeType.LAVA : ((IForgeBlockState) state).isBurning(world, pos) ? PathNodeType.DAMAGE_FIRE : null;
	}

	/**
	 * @param state The state
	 * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
	 */
	@Stubbed
	default boolean isSlimeBlock(BlockState state) {
		return state.getBlock() == Blocks.SLIME_BLOCK;
	}

	/**
	 * @param state The state
	 * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
	 */
	@Stubbed
	default boolean isStickyBlock(BlockState state) {
		return state.getBlock() == Blocks.SLIME_BLOCK || state.getBlock() == Blocks.HONEY_BLOCK;
	}

	/**
	 * Determines if this block can stick to another block when pushed by a piston.
	 *
	 * @param state My state
	 * @param other Other block
	 * @return True to link blocks
	 */
	@Stubbed
	default boolean canStickTo(BlockState state, BlockState other) {
		if (state.getBlock() == Blocks.HONEY_BLOCK && other.getBlock() == Blocks.SLIME_BLOCK) {
			return false;
		}

		if (state.getBlock() == Blocks.SLIME_BLOCK && other.getBlock() == Blocks.HONEY_BLOCK) {
			return false;
		}

		return ((IForgeBlockState) state).isStickyBlock() || ((IForgeBlockState) other).isStickyBlock();
	}

	/**
	 * Chance that fire will spread and consume this block.
	 * 300 being a 100% chance, 0, being a 0% chance.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos   Block position in world
	 * @param face  The face that the fire is coming from
	 * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
	 */
	@Stubbed
	default int getFlammability(BlockState state, BlockView world, BlockPos pos, Direction face) {
		return ((FireBlock) Blocks.FIRE).getSpreadChance(state);
	}

	/**
	 * Called when fire is updating, checks if a block face can catch fire.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos   Block position in world
	 * @param face  The face that the fire is coming from
	 * @return True if the face can be on fire, false otherwise.
	 */
	@Stubbed
	default boolean isFlammable(BlockState state, BlockView world, BlockPos pos, Direction face) {
		return ((IForgeBlockState) state).getFlammability(world, pos, face) > 0;
	}

	/**
	 * If the block is flammable, this is called when it gets lit on fire.
	 *
	 * @param state   The current state
	 * @param world   The current world
	 * @param pos     Block position in world
	 * @param face    The face that the fire is coming from
	 * @param igniter The entity that lit the fire
	 */
	@Stubbed
	default void catchFire(BlockState state, World world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
		//
	}

	/**
	 * Called when fire is updating on a neighbor block.
	 * The higher the number returned, the faster fire will spread around this block.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos   Block position in world
	 * @param face  The face that the fire is coming from
	 * @return A number that is used to determine the speed of fire growth around the block
	 */
	@Stubbed
	default int getFireSpreadSpeed(BlockState state, BlockView world, BlockPos pos, Direction face) {
		return ((FireBlock) Blocks.FIRE).getBurnChance(state);
	}

	/**
	 * Currently only called by fire when it is on top of this block.
	 * Returning true will prevent the fire from naturally dying during updating.
	 * Also prevents firing from dying from rain.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos   Block position in world
	 * @param side  The face that the fire is coming from
	 * @return True if this block sustains fire, meaning it will never go out.
	 */
	@Stubbed
	default boolean isFireSource(BlockState state, WorldView world, BlockPos pos, Direction side) {
		return state.isIn(world.getDimension().getInfiniburnBlocks());
	}

	/**
	 * Determines if this block is can be destroyed by the specified entities normal behavior.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos   Block position in world
	 * @return True to allow the ender dragon to destroy this block
	 */
	@Stubbed
	default boolean canEntityDestroy(BlockState state, BlockView world, BlockPos pos, Entity entity) {
		if (entity instanceof EnderDragonEntity) {
			return !BlockTags.DRAGON_IMMUNE.contains(this.getBlock());
		} else if ((entity instanceof WitherEntity) || (entity instanceof WitherSkullEntity)) {
			return ((IForgeBlockState) state).isAir(world, pos) || WitherEntity.canDestroy(state);
		}

		return true;
	}

	/**
	 * Determines if this block should drop loot when exploded.
	 */
	@Stubbed
	default boolean canDropFromExplosion(BlockState state, BlockView world, BlockPos pos, Explosion explosion) {
		return state.getBlock().shouldDropItemsOnExplosion(explosion);
	}

	/**
	 * Retrieves a list of tags names this is known to be associated with.
	 * This should be used in favor of TagCollection.getOwningTags, as this caches the result and automatically updates when the TagCollection changes.
	 */
	@Stubbed // needs call locations
	Set<Identifier> getTags();

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
	default void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		getBlock().onDestroyedByExplosion(world, pos, explosion);
	}

	/**
	 * Determines if this block's collision box should be treated as though it can extend above its block space.
	 * Use this to replicate fence and wall behavior.
	 */
	@Stubbed
	default boolean collisionExtendsVertically(BlockState state, BlockView world, BlockPos pos, Entity collidingEntity) {
		return getBlock().isIn(BlockTags.FENCES) || getBlock().isIn(BlockTags.WALLS) || getBlock() instanceof FenceGateBlock;
	}

	/**
	 * Called to determine whether this block should use the fluid overlay texture or flowing texture when it is placed under the fluid.
	 *
	 * @param state      The current state
	 * @param world      The world
	 * @param pos        Block position in world
	 * @param fluidState The state of the fluid
	 * @return Whether the fluid overlay texture should be used
	 */
	@Stubbed
	default boolean shouldDisplayFluidOverlay(BlockState state, BlockRenderView world, BlockPos pos, FluidState fluidState) {
		return state.getBlock() instanceof TransparentBlock || state.getBlock() instanceof LeavesBlock;
	}

	/**
	 * Returns the state that this block should transform into when right clicked by a tool.
	 * For example: Used to determine if an axe can strip, a shovel can path, or a hoe can till.
	 * Return null if vanilla behavior should be disabled.
	 *
	 * @param state  The current state
	 * @param world  The world
	 * @param pos    The block position in world
	 * @param player The player clicking the block
	 * @param stack  The stack being used by the player
	 * @return The resulting state after the action has been performed
	 */
	@Stubbed
	@Nullable
	default BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		if (toolType == ToolType.AXE) {
			return PatchworkAxeItem.getAxeStrippingState(state);
		} else if (toolType == ToolType.HOE) {
			return PatchworkHoeItem.getHoeTillingState(state);
		} else {
			return toolType == ToolType.SHOVEL ? PatchworkShovelItem.getShovelPathingState(state) : null;
		}
	}

	/**
	 * Checks if a player or entity handles movement on this block like scaffolding.
	 *
	 * @param state  The current state
	 * @param world  The current world
	 * @param pos    The block position in world
	 * @param entity The entity on the scaffolding
	 * @return True if the block should act like scaffolding
	 */
	@Stubbed
	default boolean isScaffolding(BlockState state, WorldView world, BlockPos pos, LivingEntity entity) {
		return state.isOf(Blocks.SCAFFOLDING);
	}
}

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

package net.patchworkmc.impl.event.entity;

import java.util.List;
import java.util.Collection;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

import net.patchworkmc.mixin.event.entity.old.StorageMinecartEntityAccessor;

public class EntityEventsOld implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger("patchwork-events-entity");

	public static ActionResult onInteractEntityAt(PlayerEntity player, Entity entity, HitResult ray, Hand hand) {
		Vec3d vec3d = new Vec3d(ray.getPos().x - entity.x, ray.getPos().y - entity.y, ray.getPos().z - entity.z);

		return onInteractEntityAt(player, entity, vec3d, hand);
	}

	public static ActionResult onInteractEntityAt(PlayerEntity player, Entity target, Vec3d localPos, Hand hand) {
		PlayerInteractEvent event = new PlayerInteractEvent.EntityInteractSpecific(player, hand, target, localPos);

		MinecraftForge.EVENT_BUS.post(event);

		return event.isCanceled() ? event.getCancellationResult() : null;
	}

	public static ActionResult onInteractEntity(PlayerEntity player, Entity entity, Hand hand) {
		PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(player, hand, entity);

		MinecraftForge.EVENT_BUS.post(event);

		return event.isCanceled() ? event.getCancellationResult() : null;
	}

	public static PlayerInteractEvent.RightClickItem onItemRightClick(PlayerEntity player, Hand hand) {
		PlayerInteractEvent.RightClickItem event = new PlayerInteractEvent.RightClickItem(player, hand);

		MinecraftForge.EVENT_BUS.post(event);

		return event;
	}

	public static PlayerInteractEvent.RightClickBlock onBlockRightClick(PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
		PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(player, hand, pos, face);

		MinecraftForge.EVENT_BUS.post(event);

		return event;
	}

	public static void onEmptyRightClick(PlayerEntity player, Hand hand) {
		MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickEmpty(player, hand));
	}

	public static PlayerInteractEvent.LeftClickBlock onBlockLeftClick(PlayerEntity player, BlockPos pos, Direction face) {
		PlayerInteractEvent.LeftClickBlock event = new PlayerInteractEvent.LeftClickBlock(player, pos, face);

		MinecraftForge.EVENT_BUS.post(event);

		return event;
	}

	public static void onEmptyLeftClick(PlayerEntity player) {
		MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.LeftClickEmpty(player));
	}

	public static boolean onLivingDeath(LivingEntity entity, DamageSource src) {
		return MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
	}

	public static boolean onLivingUpdateEvent(LivingEntity entity) {
		return MinecraftForge.EVENT_BUS.post(new LivingEvent.LivingUpdateEvent(entity));
	}

	public static boolean onEntityJoinWorld(Entity entity, World world) {
		return MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(entity, world));
	}

	public static void onEntityConstruct(Entity entity) {
		MinecraftForge.EVENT_BUS.post(new EntityEvent.EntityConstructing(entity));
	}

	public static void onEnteringChunk(Entity entity, int newChunkX, int newChunkZ, int oldChunkX, int oldChunkZ) {
		MinecraftForge.EVENT_BUS.post(new EntityEvent.EnteringChunk(entity, newChunkX, newChunkZ, oldChunkX, oldChunkZ));
	}

	public static boolean onLivingAttack(LivingEntity entity, DamageSource src, float damage) {
		return MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, damage));
	}

	public static void onLivingSetAttackTarget(LivingEntity entity, LivingEntity target) {
		MinecraftForge.EVENT_BUS.post(new LivingSetAttackTargetEvent(entity, target));
	}

	public static float onLivingHurt(LivingEntity entity, DamageSource src, float damage) {
		LivingHurtEvent event = new LivingHurtEvent(entity, src, damage);
		return MinecraftForge.EVENT_BUS.post(event) ? 0 : event.getAmount();
	}

	public static float[] onLivingFall(LivingEntity entity, float distance, float damageMultiplier) {
		LivingFallEvent event = new LivingFallEvent(entity, distance, damageMultiplier);
		return MinecraftForge.EVENT_BUS.post(event) ? null : new float[]{ event.getDistance(), event.getDamageMultiplier() };
	}

	public static void onFlyablePlayerFall(PlayerEntity player, float distance, float damageMultiplier) {
		MinecraftForge.EVENT_BUS.post(new PlayerFlyableFallEvent(player, distance, damageMultiplier));
	}

	public static float onLivingDamage(LivingEntity entity, DamageSource src, float damage) {
		LivingDamageEvent event = new LivingDamageEvent(entity, src, damage);
		return MinecraftForge.EVENT_BUS.post(event) ? 0 : event.getAmount();
	}

	public static boolean onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) {
		return MinecraftForge.EVENT_BUS.post(new LivingDropsEvent(entity, source, drops, lootingLevel, recentlyHit));
	}

	public static float getEyeHeight(Entity entity, EntityPose pose, EntityDimensions size, float defaultHeight) {
		EntityEvent.EyeHeight event = new EntityEvent.EyeHeight(entity, pose, size, defaultHeight);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getNewHeight();
	}

	public static Result canEntitySpawn(MobEntity entity, WorldAccess world, double x, double y, double z, MobSpawnerLogic spawner, SpawnReason spawnType) {
		if (entity == null) {
			return Result.DEFAULT;
		}

		LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn(entity, world, x, y, z, spawner, spawnType);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getResult();
	}

	public static boolean canEntitySpawnFromSpawner(MobEntity entity, World world, double x, double y, double z, MobSpawnerLogic spawner) {
		Result result = canEntitySpawn(entity, world, x, y, z, spawner, SpawnReason.SPAWNER);

		if (result == Result.DEFAULT) {
			// Vanilla logic, but inverted since we're checking if it CAN spawn instead of if it CAN'T
			return entity.canSpawn(world, SpawnReason.SPAWNER) && entity.canSpawn(world);
		} else {
			return result == Result.ALLOW;
		}
	}

	public static boolean canEntitySpawnNaturally(MobEntity entity, WorldAccess world, double x, double y, double z, MobSpawnerLogic spawner, SpawnReason spawnType, double sqDistanceFromPlayer) {
		Result result = canEntitySpawn(entity, world, x, y, z, spawner, spawnType);

		if (result == Result.DEFAULT) {
			// Vanilla logic, but inverted since we're checking if it CAN spawn instead of if it CAN'T
			return !(sqDistanceFromPlayer > 16384.0D && entity.canImmediatelyDespawn(sqDistanceFromPlayer)) && entity.canSpawn(world, SpawnReason.NATURAL) && entity.canSpawn(world);
		} else {
			return result == Result.ALLOW;
		}
	}

	public static boolean doSpecialSpawn(MobEntity entity, WorldAccess world, double x, double y, double z, MobSpawnerLogic spawner, SpawnReason spawnType) {
		return MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn(entity, world, x, y, z, spawner, spawnType));
	}

	public static boolean attackEntity(PlayerEntity player, Entity target) {
		if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) {
			return false;
		}

		ItemStack stack = player.getMainHandStack();

		if (stack.isEmpty()) {
			return true;
		}

		IForgeItem item = (IForgeItem) stack.getItem();

		return !item.onLeftClickEntity(stack, player, target);
	}

	public static void onItemTooltip(ItemStack itemStack, PlayerEntity entityPlayer, List<Text> list, TooltipContext flags) {
		MinecraftForge.EVENT_BUS.post(new ItemTooltipEvent(itemStack, entityPlayer, list, flags));
	}

	public static boolean onAnimalTame(AnimalEntity animal, PlayerEntity tamer) {
		return MinecraftForge.EVENT_BUS.post(new AnimalTameEvent(animal, tamer));
	}

	public static int onItemExpire(ItemEntity entity, ItemStack item) {
		if (item.isEmpty()) return -1;

		ItemExpireEvent event = new ItemExpireEvent(entity, ((IForgeItem) item.getItem()).getEntityLifespan(item, entity.world));

		return MinecraftForge.EVENT_BUS.post(event) ? event.getExtraLife() : -1;
	}

	public static boolean onPlayerTossEvent(PlayerEntity player, ItemEntity itemEntity) {
		return MinecraftForge.EVENT_BUS.post(new ItemTossEvent(itemEntity, player));
	}

	public static boolean onProjectileImpact(Entity entity, HitResult ray) {
		return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent(entity, ray));
	}

	public static boolean onProjectileImpact(PersistentProjectileEntity arrow, HitResult ray) {
		return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Arrow(arrow, ray));
	}

	public static boolean onProjectileImpact(ExplosiveProjectileEntity fireball, HitResult ray) {
		return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Fireball(fireball, ray));
	}

	public static boolean onProjectileImpact(ThrownEntity throwable, HitResult ray) {
		return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Throwable(throwable, ray));
	}

	public static boolean onTravelToDimension(Entity entity, DimensionType dimensionType) {
		EntityTravelToDimensionEvent event = new EntityTravelToDimensionEvent(entity, dimensionType);
		boolean result = !MinecraftForge.EVENT_BUS.post(event);

		if (!result) {
			// Revert variable back to true as it would have been set to false

			if (entity instanceof StorageMinecartEntity) {
				((StorageMinecartEntityAccessor) entity).dropContentsWhenDead(true);
			}
		}

		return result;
	}

	@Override
	public void onInitialize() {
		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (player.isSpectator()) {
				return ActionResult.PASS;
			}

			if (player.getItemCooldownManager().isCoolingDown(player.getStackInHand(hand).getItem())) {
				return ActionResult.PASS;
			}

			PlayerInteractEvent.RightClickItem event = EntityEventsOld.onItemRightClick(player, hand);

			if (event.isCanceled() && event.getCancellationResult() == ActionResult.PASS) {
				// TODO: Fabric API doesn't have a way to express "cancelled, but return PASS"

				LOGGER.error("[patchwork-events-entity] RightClickItem: Cannot cancel with a result of PASS yet, assuming SUCCESS");

				return ActionResult.SUCCESS;
			}

			return event.getCancellationResult();
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (player.isSpectator()) {
				return ActionResult.PASS;
			}

			PlayerInteractEvent.RightClickBlock event = EntityEventsOld.onBlockRightClick(player, hand, hitResult.getBlockPos(), hitResult.getSide());

			if (event.isCanceled()) {
				if (event.getCancellationResult() == ActionResult.PASS) {
					// TODO: Fabric API doesn't have a way to express "cancelled, but return PASS"
					LOGGER.error("[patchwork-events-entity] RightClickBlock: Cannot cancel with a result of PASS yet, assuming SUCCESS");

					return ActionResult.SUCCESS;
				} else {
					return event.getCancellationResult();
				}
			}

			// Not cancelled entirely, but a single behavior is cancelled.

			if (event.getUseBlock() == Event.Result.DENY || event.getUseItem() == Event.Result.DENY) {
				// TODO: Handle Result.DENY -> ActionResult.PASS

				throw new UnsupportedOperationException("Cannot handle partial RightClickBlock cancellation yet");
			}

			return ActionResult.PASS;
		});

		UseEntityCallback.EVENT.register(((playerEntity, world, hand, entity, entityHitResult) -> {
			if (playerEntity.isSpectator()) {
				return ActionResult.PASS;
			}

			ActionResult result = EntityEventsOld.onInteractEntityAt(playerEntity, entity, entityHitResult, hand);

			if (result == null) {
				return ActionResult.PASS;
			}

			return result;
		}));

		AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
			PlayerInteractEvent.LeftClickBlock event = EntityEventsOld.onBlockLeftClick(playerEntity, blockPos, direction);

			if (event.isCanceled() || (!playerEntity.isCreative() && event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY)) {
				return ActionResult.SUCCESS;
			} else {
				return ActionResult.PASS;
			}
		});
	}
}

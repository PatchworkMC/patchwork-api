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

package net.minecraftforge.common;

import java.util.Collection;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.IWorld;
import net.minecraft.world.MobSpawnerLogic;

import net.patchworkmc.impl.event.entity.EntityEvents;
import net.patchworkmc.impl.loot.LootHooks;

/*
 * Note: this class is intended for mod use only, to dispatch to the implementations kept in their own modules.
 * Do not keep implementation details here, methods should be thin wrappers around methods in other modules.
 */
public class ForgeHooks {
	//---------------\\
	// ENTITY EVENTS ||
	//---------------//

	// COMBAT & DAMAGE //

	// TODO: forge calls the equivilant to this in LivingEntity, but patchwork only calls the equivilant to onPlayerAttack
	public static boolean onLivingAttack(LivingEntity entity, DamageSource src, float amount) {
		return entity instanceof PlayerEntity || onPlayerAttack(entity, src, amount);
	}

	public static boolean onPlayerAttack(LivingEntity entity, DamageSource src, float amount) {
		return !EntityEvents.onLivingAttack(entity, src, amount);
	}

	// optifine wants this? O.o
	public static void onLivingSetAttackTarget(LivingEntity entity, LivingEntity target) {
		EntityEvents.onLivingSetAttackTarget(entity, target);
	}

	public static boolean onPlayerAttackTarget(PlayerEntity player, Entity target) {
		return EntityEvents.attackEntity(player, target);
	}

	public static float onLivingHurt(LivingEntity entity, DamageSource src, float amount) {
		return EntityEvents.onLivingHurt(entity, src, amount);
	}

	@Nullable
	public static float[] onLivingFall(LivingEntity entity, float distance, float damageMultiplier) {
		return EntityEvents.onLivingFall(entity, distance, damageMultiplier);
	}

	public static float onLivingDamage(LivingEntity entity, DamageSource src, float amount) {
		return EntityEvents.onLivingDamage(entity, src, amount);
	}

	public static boolean onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) {
		return EntityEvents.onLivingDrops(entity, source, drops, lootingLevel, recentlyHit);
	}

	public static boolean onLivingDeath(LivingEntity entity, DamageSource src) {
		return EntityEvents.onLivingDeath(entity, src);
	}

	// ENTITY INTERACTION //

	// TODO: onInteractEntityAt

	public static ActionResult onInteractEntity(PlayerEntity player, Entity entity, Hand hand) {
		return EntityEvents.onInteractEntity(player, entity, hand);
	}

	// OTHER //

	public static int canEntitySpawn(MobEntity entity, IWorld world, double x, double y, double z, MobSpawnerLogic spawner, SpawnType spawnReason) {
		Event.Result res = ForgeEventFactory.canEntitySpawn(entity, world, x, y, z, null, spawnReason);
		return res == Event.Result.DEFAULT ? 0 : res == Event.Result.DENY ? -1 : 1;
	}

	public static boolean onLivingUpdate(LivingEntity entity) {
		return EntityEvents.onLivingUpdateEvent(entity);
	}

	//-------------\\
	// CHESTS/LOOT ||
	//-------------//

	@SuppressWarnings({ "rawtypes", "unused" })
	private static ThreadLocal<?> lootContext = LootHooks.lootContext;

	// Need to have the class here to make some mod hacks work
	public static class LootTableContext extends LootHooks.LootTableContext {
		private LootTableContext(Identifier name, boolean custom) {
			super(name, custom);
		}
	}

	@Nullable
	public static LootTable loadLootTable(Gson gson, Identifier name, JsonObject data, boolean custom, LootManager lootTableManager) {
		return LootHooks.loadLootTable(gson, name, data, custom, lootTableManager);
	}

	public static String readPoolName(JsonObject json) {
		return LootHooks.readPoolName(json);
	}
}

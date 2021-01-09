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

package net.patchworkmc.mixin.extensions.entity;

import java.util.Iterator;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.math.GravityField;
import net.minecraft.world.SpawnHelper;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
	/**
	 * Stores the latest entity processed in {@link SpawnHelper#setupSpawn(int, Iterable, SpawnHelper.ChunkSource)} for each thread.
	 */
	@Unique
	private static final ThreadLocal<IForgeEntity> latestCalculatedEntity = new ThreadLocal<>();

	/**
	 * When an entity is iterated through in {@link SpawnHelper#setupSpawn(int, Iterable, SpawnHelper.ChunkSource)},
	 * assign it to {@link #latestCalculatedEntity} (Assuming that all entities implement {@link IForgeEntity}).
	 */
	@Inject(method = "setupSpawn",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;getSpawnGroup()Lnet/minecraft/entity/SpawnGroup;", ordinal = 0))
	private static void onEntitySetup(int spawningChunkCount,
										Iterable<Entity> entities,
										SpawnHelper.ChunkSource chunkSource,
										CallbackInfoReturnable<SpawnHelper.Info> cir,
										GravityField gravityField,
										Object2IntOpenHashMap<SpawnGroup> object2IntOpenHashMap,
										Iterator<Entity> var5,
										Entity entity) {
		latestCalculatedEntity.set((IForgeEntity) entity);
	}

	/**
	 * Use the entity stored in {@link #latestCalculatedEntity}'s {@link IForgeEntity#getClassification(boolean)} to
	 * replace the original {@code getType().getSpawnGroup()}.
	 *
	 * <p>The default implementation for getClassification is identical to the original, but forge mods may replace it.</p>
	 */
	@Redirect(method = "setupSpawn(ILjava/lang/Iterable;Lnet/minecraft/world/SpawnHelper$ChunkSource;)Lnet/minecraft/world/SpawnHelper$Info;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;getSpawnGroup()Lnet/minecraft/entity/SpawnGroup;", ordinal = 0))
	private static SpawnGroup onClassification(EntityType<?> entityType) {
		return latestCalculatedEntity.get().getClassification(true);
	}

	/**
	 * At the end of {@link SpawnHelper#setupSpawn(int, Iterable, SpawnHelper.ChunkSource)}, clear the value of
	 * {@link #latestCalculatedEntity} to prevent any memory leaks.
	 */
	@Inject(method = "setupSpawn", at = @At("TAIL"))
	private static void onEntitySetupComplete(CallbackInfoReturnable<SpawnHelper.Info> cir) {
		latestCalculatedEntity.remove();
	}
}

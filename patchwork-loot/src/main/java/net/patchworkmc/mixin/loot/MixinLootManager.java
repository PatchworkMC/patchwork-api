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

package net.patchworkmc.mixin.loot;

import java.util.Map;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.patchworkmc.impl.loot.LootHooks;

@Mixin(LootManager.class)
public abstract class MixinLootManager extends MixinJsonDataLoader {
	@Shadow
	@Final
	private static Gson GSON;

	@Shadow
	@Final
	private static Logger LOGGER;

	@Redirect(method = "apply", at = @At(value = "INVOKE", target = "java/util/Map.forEach (Ljava/util/function/BiConsumer;)V", ordinal = 0))
	private void cancel_forEach(Map<Identifier, JsonObject> map, BiConsumer<Identifier, JsonObject> consumer) {
		// ignore this call, we're gonna reintroduce it but with capturing locals
	}

	@Inject(method = "apply", at = @At(value = "INVOKE", target = "java/util/Map.forEach (Ljava/util/function/BiConsumer;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void reintroduce_forEach(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo info, ImmutableMap.Builder<Identifier, LootTable> builder) {
		map.forEach((id, jsonObject) -> {
			try {
				LootManager lootManager = (LootManager) (Object) this;
				Resource res = resourceManager.getResource(this.getPreparedPath(id));
				LootTable lootTable = LootHooks.loadLootTable(GSON, id, jsonObject, res == null || !res.getResourcePackName().equals("Default"), lootManager);
				builder.put(id, lootTable);
			} catch (Exception ex) {
				LOGGER.error("Couldn't parse loot table {}", id, ex);
			}
		});
	}

	@Overwrite
	private static void method_20711(ImmutableMap.Builder<Identifier, LootTable> builder, Identifier id, JsonObject obj) {
		// We are effectively overwriting this lambda with our own, so let's make that explicit by actually overwriting it.
	}
}

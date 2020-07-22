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

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.patchworkmc.impl.event.loot.LootEvents;
import net.patchworkmc.impl.loot.LootHooks;
import net.patchworkmc.impl.loot.WrappedImmutableMapBuilder;

@Mixin(LootManager.class)
public abstract class MixinLootManager extends MixinJsonDataLoader {
	@Shadow
	@Final
	private static Gson GSON;

	@Shadow
	@Final
	private static Logger LOGGER;

	// NOTE: this could also be a Redirect of forEach that just wraps the existing lambda, instead of an additional forEach
	@Inject(method = "apply", at = @At(value = "INVOKE", target = "java/util/Map.forEach (Ljava/util/function/BiConsumer;)V", ordinal = 0))
	private void prepareJson(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo info) {
		map.forEach((id, lootTableObj) -> {
			try {
				boolean custom = isLootTableCustom(resourceManager, id);
				LootManager lootManager = (LootManager) (Object) this;
				LootHooks.prepareLootTable(id, lootTableObj, custom, lootManager);
			} catch (IOException ex) {
				LOGGER.error("Couldn't parse loot table {}", id, ex);
			}
		});
	}

	@Redirect(method = "apply", at = @At(value = "INVOKE", target = "com/google/common/collect/ImmutableMap.builder ()Lcom/google/common/collect/ImmutableMap$Builder;"))
	private ImmutableMap.Builder<Identifier, LootTable> wrapBuilder(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
		LootManager lootManager = (LootManager) (Object) this;
		return new WrappedImmutableMapBuilder<Identifier, LootTable>((id, table) -> {
			// TODO: handle exception?
			if (!isLootTableCustom(resourceManager, id)) {
				table = LootEvents.loadLootTable(id, table, lootManager);
			}

			// if (table != null) {
			// 	table.freeze();
			// }
			return table;
		});
	}

	@Unique
	private boolean isLootTableCustom(ResourceManager resourceManager, Identifier id) throws IOException {
		Resource res = resourceManager.getResource(this.getPreparedPath(id));
		return res == null || !res.getResourcePackName().equals("Default");
	}
}

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
import java.util.Deque;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Queues;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.loot.LootManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.patchworkmc.impl.loot.LootHooks;

@Mixin(LootManager.class)
public abstract class MixinLootManager extends MixinJsonDataLoader {
	// should this also be part of the static threadlocal?
	@Unique
	private ResourceManager resourceManager;

	@Unique
	private static ThreadLocal<Deque<LootManager>> lootContext = new ThreadLocal<Deque<LootManager>>();

	// TODO: is reentrancy necessary?
	@Inject(method = "apply", at = @At("HEAD"))
	private void getResourceManager(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo info) {
		this.resourceManager = resourceManager;
		Deque<LootManager> que = lootContext.get();

		if (que == null) {
			que = Queues.newArrayDeque();
			lootContext.set(que);
		}

		que.push((LootManager) (Object) this);
	}

	@Inject(method = "apply", at = @At("RETURN"))
	private void delResourceManager(CallbackInfo info) {
		// TODO: what if an exception is thrown?
		resourceManager = null;
		lootContext.get().pop();
	}

	@Redirect(method = "method_20711", at = @At(value = "INVOKE", target = "com/google/gson/Gson.fromJson (Lcom/google/gson/JsonElement;Ljava/lang/Class;)Ljava/lang/Object;"))
	private static Object loadLootTable(Gson GSON, JsonElement elem, Class cls, ImmutableMap.Builder _bld, Identifier id, JsonObject obj) throws IOException {
		LootManager lootManager = lootContext.get().peek();

		if (lootManager == null) {
			throw new JsonParseException("Invalid call stack, could not grab loot manager!"); // Should I throw this? Do we care about custom deserializers outside the manager?
		}

		ResourceManager resourceManager = ((MixinLootManager) (Object) lootManager).resourceManager;
		Resource res = resourceManager.getResource(((MixinLootManager) (Object) lootManager).getPreparedPath(id));
		return LootHooks.loadLootTable(GSON, id, elem, res == null || !res.getResourcePackName().equals("Default"), lootManager);
	}
}

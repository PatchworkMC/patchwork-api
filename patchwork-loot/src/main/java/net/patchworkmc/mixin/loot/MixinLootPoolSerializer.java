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

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.loot.LootPool;

import net.patchworkmc.api.loot.ForgeLootPool;
import net.patchworkmc.impl.loot.LootHooks;
import net.patchworkmc.impl.loot.PatchworkLootPool;

@Mixin(LootPool.Serializer.class)
public class MixinLootPoolSerializer {
	@Inject(method = "deserialize", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void addNameToConstructor(JsonElement elem, Type ty, JsonDeserializationContext ctx, CallbackInfoReturnable<LootPool> cir, JsonObject obj) {
		LootPool ret = cir.getReturnValue();
		((PatchworkLootPool) ret).patchwork$setName(LootHooks.readPoolName(obj));

		// is this necessary?
		cir.setReturnValue(ret);
	}

	@Redirect(method = "serialize", at = @At(value = "NEW", args = "class=com/google/gson/JsonObject"))
	private static JsonObject serializeName(LootPool pool, Type type, JsonSerializationContext ctx) {
		JsonObject ret = new JsonObject();

		String name = ((ForgeLootPool) pool).getName();

		if (name != null && !name.startsWith("custom#")) {
			ret.add("name", ctx.serialize(name));
		}

		return ret;
	}
}

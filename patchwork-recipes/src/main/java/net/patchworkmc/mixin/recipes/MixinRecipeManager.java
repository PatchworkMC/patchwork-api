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

package net.patchworkmc.mixin.recipes;

import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraftforge.common.crafting.CraftingHelper;

import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.resource.ResourceManager;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Inject(method = "apply", at = @At("HEAD"))
	private void filterRecipeMap(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
		Iterator<Map.Entry<Identifier, JsonObject>> iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<Identifier, JsonObject> entry = iterator.next();
			Identifier key = entry.getKey();

			if (!CraftingHelper.processConditions(entry.getValue(), "conditions")) {
				LOGGER.info("Skipping loading recipe {} as it's conditions were not met", key);
				iterator.remove();
			}
		}
	}
}

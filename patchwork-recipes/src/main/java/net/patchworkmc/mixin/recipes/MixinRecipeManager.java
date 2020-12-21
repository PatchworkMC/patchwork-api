package net.patchworkmc.mixin.recipes;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Inject(method = "apply", at = @At(value = "HEAD"))
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

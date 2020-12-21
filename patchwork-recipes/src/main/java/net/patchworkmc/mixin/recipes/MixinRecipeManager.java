package net.patchworkmc.mixin.recipes;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {
	@Redirect(method = "apply", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"))
	private Iterator patchwork_filterRecipeSet(Set<Map.Entry<Identifier, JsonObject>> set) {
		return set.stream().filter(item -> CraftingHelper.processConditions(item.getValue(), "conditions")).iterator();
	}
}

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
//	@ModifyVariable(method = "apply", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;"))
//	private Map<Identifier, JsonObject> patchwork_filterRecipeMap(Map<Identifier, JsonObject> map) {
//		return map.entrySet().stream().filter(item -> {
//			if (CraftingHelper.processConditions(item.getValue(), "conditions")) {
//				System.out.println("conditions not met");
//			}
//
//			return false;
//		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
////	}
//	@Redirect(method = "apply", at = @At(value = "INVOKE",target = "Ljava/util/Map;entrySet()Ljava/util/Set;"))
//	private Set patchwork_filterRecipeSet(Map<Identifier, JsonObject> map) {
//		return map.entrySet().stream().filter(item -> {
//			return false;
//		});
//	}

	@Redirect(method = "apply", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"))
	private Iterator patchwork_filterRecipeSet(Set<Map.Entry<Identifier, JsonObject>> set) {
		return set.stream().filter(item -> CraftingHelper.processConditions(item.getValue(), "conditions")).iterator();
	}
}

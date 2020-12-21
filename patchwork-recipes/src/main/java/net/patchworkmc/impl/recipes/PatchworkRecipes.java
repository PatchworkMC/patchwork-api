package net.patchworkmc.impl.recipes;

import net.fabricmc.api.ModInitializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

public class PatchworkRecipes implements ModInitializer {
	@Override
	public void onInitialize() {
		CraftingHelper.register(ModLoadedCondition.Serializer.INSTANCE);
	}
}

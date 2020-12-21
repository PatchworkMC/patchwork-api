package net.patchworkmc.impl.recipes;

import net.fabricmc.api.ModInitializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.FalseCondition;
import net.minecraftforge.common.crafting.conditions.ItemExistsCondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

public class PatchworkRecipes implements ModInitializer {
	@Override
	public void onInitialize() {
		CraftingHelper.register(AndCondition.Serializer.INSTANCE);
		CraftingHelper.register(FalseCondition.Serializer.INSTANCE);
		CraftingHelper.register(ItemExistsCondition.Serializer.INSTANCE);
		CraftingHelper.register(ModLoadedCondition.Serializer.INSTANCE);
		CraftingHelper.register(NotCondition.Serializer.INSTANCE);
		CraftingHelper.register(OrCondition.Serializer.INSTANCE);
		CraftingHelper.register(TrueCondition.Serializer.INSTANCE);
		CraftingHelper.register(TagEmptyCondition.Serializer.INSTANCE);
	}
}

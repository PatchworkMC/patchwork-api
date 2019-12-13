package net.coderbot.patchwork.itemgroup;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public abstract class PatchworkItemGroup extends ItemGroup {
	public PatchworkItemGroup(String name) {
		super(getNewArrayIndex(), name);
	}

	private static int getNewArrayIndex() {
		// Get a new slot in the array

		FabricItemGroupBuilder.create(new Identifier("patchwork", "dummy")).build();

		return GROUPS.length - 1;
	}

	// Note: uncomment this in dev
	/*public net.minecraft.item.ItemStack createIcon() {
		return method_7750();
	}

	// TODO: Missing required classpath information in remapper!
	public abstract net.minecraft.item.ItemStack method_7750();*/
}

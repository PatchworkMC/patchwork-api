package com.patchworkmc.mixin.registries;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.patchworkmc.impl.registries.ExtendedForgeRegistryEntry;

@Mixin(Item.class)
public class MixinItem implements ExtendedForgeRegistryEntry<Item> {
	@Unique
	private Identifier registryName;

	@Override
	public IForgeRegistryEntry setRegistryName(Identifier name) {
		this.registryName = name;

		return this;
	}

	public Identifier getRegistryName() {
		return registryName;
	}

	public Class<Item> getRegistryType() {
		return Item.class;
	}
}

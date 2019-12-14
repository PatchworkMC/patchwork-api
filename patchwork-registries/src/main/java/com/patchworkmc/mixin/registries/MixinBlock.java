package com.patchworkmc.mixin.registries;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.patchworkmc.impl.registries.ExtendedForgeRegistryEntry;

@Mixin(Block.class)
public class MixinBlock implements ExtendedForgeRegistryEntry<Block> {
	@Unique
	private Identifier registryName;

	@Override
	public IForgeRegistryEntry setRegistryName(Identifier name) {
		this.registryName = name;

		return this;
	}

	public Identifier getRegistryName() {
		Identifier current = Registry.BLOCK.getId((Block)(Object)this);
		Identifier set = registryName;

		if(set == null) {
			set = Registry.BLOCK.getDefaultId();
		}

		return current != Registry.BLOCK.getDefaultId() ? current : set;
	}

	public Class<Block> getRegistryType() {
		return Block.class;
	}
}

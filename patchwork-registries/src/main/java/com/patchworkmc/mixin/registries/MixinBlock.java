package com.patchworkmc.mixin.registries;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;

import com.patchworkmc.impl.registries.ExtendedForgeRegistryEntry;

@Mixin(Block.class)
public class MixinBlock implements ExtendedForgeRegistryEntry<Block> {
	private Identifier registryName;

	@Override
	public IForgeRegistryEntry setRegistryName(Identifier name) {
		this.registryName = name;

		return this;
	}

	public Identifier getRegistryName() {
		return registryName;
	}

	public Class<Block> getRegistryType() {
		return Block.class;
	}
}

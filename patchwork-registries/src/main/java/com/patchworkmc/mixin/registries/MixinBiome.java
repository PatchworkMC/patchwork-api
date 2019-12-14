package com.patchworkmc.mixin.registries;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.patchworkmc.impl.registries.ExtendedForgeRegistryEntry;

@Mixin(Biome.class)
public class MixinBiome implements ExtendedForgeRegistryEntry<Biome> {
	@Unique
	private Identifier registryName;

	@Override
	public IForgeRegistryEntry setRegistryName(Identifier name) {
		this.registryName = name;

		return this;
	}

	public Identifier getRegistryName() {
		Identifier current = Registry.BIOME.getId((Biome)(Object)this);
		Identifier set = registryName;

		return current != null ? current : set;
	}

	public Class<Biome> getRegistryType() {
		return Biome.class;
	}
}

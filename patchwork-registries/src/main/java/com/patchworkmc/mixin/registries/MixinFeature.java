package com.patchworkmc.mixin.registries;

import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.patchworkmc.impl.registries.ExtendedForgeRegistryEntry;

@Mixin(Feature.class)
public class MixinFeature implements ExtendedForgeRegistryEntry<Feature> {
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

	public Class<Feature> getRegistryType() {
		return Feature.class;
	}
}

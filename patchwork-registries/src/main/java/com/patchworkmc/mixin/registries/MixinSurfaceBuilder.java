package com.patchworkmc.mixin.registries;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.patchworkmc.impl.registries.ExtendedForgeRegistryEntry;

@Mixin(SurfaceBuilder.class)
public class MixinSurfaceBuilder implements ExtendedForgeRegistryEntry<SurfaceBuilder> {
	@Unique
	private Identifier registryName;

	@Override
	public IForgeRegistryEntry setRegistryName(Identifier name) {
		this.registryName = name;

		return this;
	}

	public Identifier getRegistryName() {
		Identifier current = Registry.SURFACE_BUILDER.getId((SurfaceBuilder)(Object)this);
		Identifier set = registryName;

		return current != null ? current : set;
	}

	public Class<SurfaceBuilder> getRegistryType() {
		return SurfaceBuilder.class;
	}
}

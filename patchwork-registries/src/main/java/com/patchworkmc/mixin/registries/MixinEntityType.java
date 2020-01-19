package com.patchworkmc.mixin.registries;

import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.patchworkmc.impl.registries.ExtendedForgeRegistryEntry;

@Mixin(EntityType.class)
public class MixinEntityType implements ExtendedForgeRegistryEntry<EntityType> {
	@Unique
	private Identifier registryName;

	@Override
	public Identifier getRegistryName() {
		Identifier current = Registry.ENTITY_TYPE.getId((EntityType) (Object) this);
		Identifier set = registryName;

		if (set == null) {
			set = Registry.ENTITY_TYPE.getDefaultId();
		}

		return current != Registry.ENTITY_TYPE.getDefaultId() ? current : set;
	}

	@Override
	public IForgeRegistryEntry setRegistryName(Identifier name) {
		this.registryName = name;
		return this;
	}

	@Override
	public Class<EntityType> getRegistryType() {
		return EntityType.class;
	}
}

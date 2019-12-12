package net.minecraftforge.registries;

import net.minecraft.util.Identifier;

public abstract class ForgeRegistryEntry<V> implements IForgeRegistryEntry<V> {
	private Identifier name;

	@Override
	public final IForgeRegistryEntry setRegistryName(Identifier name) {
		this.name = name;

		return this;
	}

	@Override
	public final Identifier getRegistryName() {
		return this.name;
	}

	@Override
	public Class<V> getRegistryType() {
		return (Class<V>)getClass();
	}
}

package com.patchworkmc.impl.registries;

import net.minecraft.util.Identifier;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface ExtendedForgeRegistryEntry<V> extends IForgeRegistryEntry<V> {
	default IForgeRegistryEntry setRegistryName(String full) {
		String activeNamespace = ModLoadingContext.get().getActiveNamespace();

		if(activeNamespace == null || activeNamespace.equals("minecraft")) {
			System.err.println("Currently active namespace is minecraft while registering item: " + full);
		}

		Identifier identifier;

		if(full.contains(":")) {
			identifier = new Identifier(full);
		} else {
			identifier = new Identifier(activeNamespace, full);
		}

		if(!identifier.getNamespace().equals(activeNamespace)) {
			System.err.printf("Potentially Dangerous alternative prefix `%s` for name `%s`, expected `%s`. This could be a intended override, but in most cases indicates a broken mod.\n", identifier.getNamespace(), identifier.getPath(), activeNamespace);
		}

		return this.setRegistryName(identifier);
	}

	default IForgeRegistryEntry setRegistryName(String namespace, String name) {
		return this.setRegistryName(new Identifier(namespace, name));
	}
}

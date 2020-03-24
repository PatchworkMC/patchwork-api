package net.minecraftforge.registries;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import net.minecraftforge.fml.ModLoadingContext;

import net.minecraft.util.Identifier;

public class GameData {
	public static Identifier checkPrefix(String name, boolean warnOverrides) {
		int index = name.lastIndexOf(':');
		String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase(Locale.ROOT);
		name = index == -1 ? name : name.substring(index + 1);
		String prefix = ModLoadingContext.get().getActiveNamespace();

		if (warnOverrides && !oldPrefix.equals(prefix) && oldPrefix.length() > 0) {
			LogManager.getLogger().info(
					"Potentially Dangerous alternative prefix `{}` for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.",
					oldPrefix, name, prefix
			);
			prefix = oldPrefix;
		}

		return new Identifier(prefix, name);
	}
}

package net.patchworkmc.impl.mappings;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.srg.tsrg.TSrgWriter;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.MethodMapping;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.FabricLoader;

public class PatchworkMappings implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(PatchworkMappings.class);
	private static MappingSet mappings;
	private static final Map<String, String> fieldMappings = new HashMap<>();
	private static final Map<String, String> methodMappings = new HashMap<>();

	@Override
	public void onInitialize() {
		try {
			mappings = MappingGenerator.getOrGenerateSrgToIntMappings(FabricLoader.INSTANCE.getGameProvider().getRawGameVersion());

			try (TSrgWriter writer = new TSrgWriter(new FileWriter("./patchwork/data/generated.tsrg"))) {
				writer.write(mappings);
			}

			LinkedList<ClassMapping<?, ?>> q = new LinkedList<>(mappings.getTopLevelClassMappings());

			for (ClassMapping<?,?> classMapping = q.poll(); classMapping != null; classMapping = q.poll()) {
				for (FieldMapping fieldMapping : classMapping.getFieldMappings()) {
					fieldMappings.put(fieldMapping.getObfuscatedName(), fieldMapping.getDeobfuscatedName());
				}

				for (MethodMapping methodMapping : classMapping.getMethodMappings()) {
					fieldMappings.put(methodMapping.getObfuscatedName(), methodMapping.getDeobfuscatedName());
				}

				q.addAll(classMapping.getInnerClassMappings());
			}
		} catch (IOException e) {
			LOGGER.error("Error while generating srg -> intermediary mappings", e);
		}

	}

	public static MappingSet getMappings() {
		return mappings;
	}

	public static Map<String, String> getFieldMappings() {
		return fieldMappings;
	}

	public static Map<String, String> getMethodMappings() {
		return methodMappings;
	}
}

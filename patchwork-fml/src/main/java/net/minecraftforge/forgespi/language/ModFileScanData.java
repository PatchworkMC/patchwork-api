package net.minecraftforge.forgespi.language;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

public class ModFileScanData {
	private static final Logger LOGGER = LogManager.getLogger();

	private String modid;
	private boolean initialized = false;
	private AnnotationStorage annotationStorage;
	private Set<AnnotationData> annotationData;

	public ModFileScanData(String modid) {
		this.modid = modid;
	}

	private void init() {
		initialized = true;

		ModContainer modContainer = FabricLoader.INSTANCE.getModContainer(modid)
				.orElseThrow(
						() -> new RuntimeException("Cannot get mod container for " + modid)
				);
		CustomValue customValue = FabricLoader.INSTANCE.getModContainer(modid)
				.orElseThrow(() -> new RuntimeException())
				.getMetadata()
				.getCustomValue("patchwork:annotations");
		if (customValue == null) {
			LOGGER.error("ModFileScanData is being accessed but cannot find annotation storage");
			return;
		}

		String annotationJsonLocation = customValue.getAsString();
		Path annotationJsonPath = modContainer.getPath(annotationJsonLocation);

		try {
			InputStream outputStream = Files.newInputStream(annotationJsonPath);
			Gson gson = new Gson();
			this.annotationStorage = gson.fromJson(
					new InputStreamReader(outputStream), AnnotationStorage.class
			);
			annotationData = this.annotationStorage.entries.stream()
					.map(ModFileScanData::getAnnotationData)
					.collect(Collectors.toSet());
		} catch (IOException e) {
			LOGGER.error(String.format(
					"Could not read annotations from %s (loaded from %s)",
					modid, modContainer.getRootPath()
			));
			e.printStackTrace();
		}
	}

	private void initIfNeeded() {
		if (!initialized) {
			init();
		}
	}

	public Set<AnnotationData> getAnnotations() {
		initIfNeeded();
		return annotationData;
	}

	private static AnnotationData getAnnotationData(AnnotationStorage.Entry entry) {
		Type annotationType = Type.getType(entry.annotationType);
		Type targetInType = Type.getType("L" + entry.targetInClass + ";");
		return new AnnotationData(
				annotationType, entry.targetType, targetInType, entry.target
		);
	}

	public static class AnnotationData {
		private final Type annotationType;
		private final ElementType targetType;
		private final Type clazz;
		private final String memberName;

		//lazy evaluated
		private Map<String, Object> annotationData;

		public AnnotationData(
				final Type annotationType, final ElementType targetType,
				final Type clazz, final String memberName
		) {
			this.annotationType = annotationType;
			this.targetType = targetType;
			this.clazz = clazz;
			this.memberName = memberName;
		}

		public Type getAnnotationType() {
			return annotationType;
		}

		public ElementType getTargetType() {
			return targetType;
		}

		public Type getClassType() {
			return clazz;
		}

		public String getMemberName() {
			return memberName;
		}

		public Map<String, Object> getAnnotationData() {
			if (annotationData == null) {
				initAnnotationData();
			}

			return annotationData;
		}

		private void initAnnotationData() {
			annotationData = new HashMap<>();

			try {
				// TODO: This *may* load classes in the wrong order, but it shouldn't be an issue
				Class<?> clazzObj = Class.forName(clazz.getClassName());
				Class annotationType = Class.forName(this.annotationType.getClassName());
				Annotation annotationObject = getAnnotationObject(clazzObj, annotationType);

				if (annotationObject == null) {
					LOGGER.error(String.format("Cannot fetch annotation object %s %s %s %s",
							annotationType, targetType, clazz, memberName
					));
					return;
				}

				Method[] argMethods = annotationObject.getClass().getDeclaredMethods();

				for (Method argMethod : argMethods) {
					if (isArgumentMethod(argMethod)) {
						annotationData.put(
								argMethod.getName(),
								argMethod.invoke(annotationObject)
						);
					}
				}
			} catch (Throwable e) {
				LOGGER.error(e);
			}
		}

		private static boolean isArgumentMethod(Method method) {
			String name = method.getName();
			if (name.equals("toString")) return false;
			if (name.equals("hashCode")) return false;
			if (name.equals("getClass")) return false;

			return true;
		}

		private Annotation getAnnotationObject(Class<?> clazzObj, Class annotationType) {
			try {
				switch (targetType) {
				case TYPE:
					return clazzObj.getAnnotation(annotationType);
				case FIELD:
					return clazzObj.getField(memberName)
							.getAnnotation(annotationType);
				case METHOD:
					Type methodType = Type.getType(memberName);
					Method declaredMethod = clazzObj.getDeclaredMethod(
							memberName.substring(memberName.indexOf('(')),
							Arrays.stream(methodType.getArgumentTypes())
									.map(type -> {
										try {
											return Class.forName(type.getClassName());
										} catch (ClassNotFoundException e) {
											throw new RuntimeException(e);
										}
									}).toArray(Class[]::new)
					);
					return declaredMethod.getAnnotation(annotationType);
				default:
					return null;
				}
			} catch (NoSuchFieldException | NoSuchMethodException e) {
				return null;
			}
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof AnnotationData)) {
				return false;
			}

			AnnotationData dat = (AnnotationData) obj;
			return Objects.equals(annotationType, dat.annotationType)
					&& Objects.equals(targetType, dat.targetType)
					&& Objects.equals(clazz, dat.clazz)
					&& Objects.equals(memberName, dat.memberName);
		}

		@Override
		public int hashCode() {
			return Objects.hash(annotationType, targetType, clazz, memberName);
		}
	}
}

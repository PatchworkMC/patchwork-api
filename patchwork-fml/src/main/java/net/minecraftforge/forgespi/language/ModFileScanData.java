package net.minecraftforge.forgespi.language;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import org.objectweb.asm.Type;

import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ModFileScanData {
	private Runnable initFunc;
	private AnnotationStorage annotationStorage;
	private Set<AnnotationData> annotationData;

	public ModFileScanData(String modid) {
		initFunc = ()
				-> {
			ModContainer modContainer = FabricLoader.INSTANCE.getModContainer(modid)
					.orElseThrow(
							() -> new RuntimeException("Cannot get mod container for " + modid)
					);
			Path annotationJsonPath = modContainer.getPath("annotations.json");

			try {
				FileReader fileReader = new FileReader(annotationJsonPath.toFile());
				Gson gson = new Gson();
				annotationStorage = gson.fromJson(
						fileReader, AnnotationStorage.class
				);
				annotationStorage.entries.stream()
						.map(entry -> getAnnotationData(entry))
						.collect(Collectors.toSet());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		};
	}

	private void initIfNeeded() {
		if (initFunc != null) {
			initFunc.run();
			initFunc = null;
		}
	}

	public Set<AnnotationData> getAnnotations() {
		initIfNeeded();
		return annotationData;
	}

	private static AnnotationData getAnnotationData(AnnotationStorage.Entry entry) {
		return new AnnotationData(
				Type.getType(entry.annotationType),
				entry.targetType,
				Type.getType(entry.targetInClass),
				entry.target
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
				final Type annotationType,
				final ElementType targetType,
				final Type clazz,
				final String memberName
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
			try {
				Class<?> clazzObj = Class.forName(clazz.getClassName());
				Class annotationType = Class.forName(this.annotationType.getClassName());
				Annotation annotationObject = getAnnotationObject(clazzObj, annotationType);
				Method[] argMethods = annotationObject.getClass().getDeclaredMethods();

				annotationData = new HashMap<>();

				for (Method argMethod : argMethods) {
					if (isArgumentMethod(argMethod)) {
						annotationData.put(
								argMethod.getName(),
								argMethod.invoke(annotationObject)
						);
					}
				}
			} catch (Throwable e) {
				throw new RuntimeException(e);
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
					System.out.println("Not being supported");
					return null;
				default:
					return null;
				}
			} catch (NoSuchFieldException e) {
				return null;
			}
		}

		@Override
		public boolean equals(final Object obj) {
			try {
				AnnotationData dat = (AnnotationData) obj;
				return (!Objects.isNull(dat))
						&& Objects.equals(annotationType, dat.annotationType)
						&& Objects.equals(targetType, dat.targetType)
						&& Objects.equals(clazz, dat.clazz)
						&& Objects.equals(memberName, dat.memberName);
			} catch (ClassCastException e) {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(annotationType, targetType, clazz, memberName);
		}
	}
}

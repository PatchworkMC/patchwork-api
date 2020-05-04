/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2020, 2019-2020
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
import java.util.HashSet;
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

		ModContainer modContainer = getModContainer(modid);
		CustomValue customValue = modContainer.getMetadata().getCustomValue("patchwork:annotations");

		if (customValue == null) {
			LOGGER.error("ModFileScanData: Tried to access the scanned annotation data for " + modid + ", but it is missing");
			annotationData = new HashSet<>();
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
					"Could not read annotations from %s %s (loaded from %s)",
					modid, annotationJsonPath, modContainer.getRootPath()
			));
			e.printStackTrace();
		}
	}

	public Set<AnnotationData> getAnnotations() {
		if (!initialized) {
			init();
		}

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
			if (name.equals("equals")) return false;
			if (name.equals("annotationType")) return false;

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
					//TODO handle overloaded methods
					String methodName = memberName.substring(0, memberName.indexOf('('));
					return Arrays.stream(clazzObj.getDeclaredMethods())
							.filter(method -> method.getName().equals(methodName))
							.findFirst()
							.orElseThrow(() -> new RuntimeException("Cannot get method " + memberName))
							.getAnnotation(annotationType);
				default:
					return null;
				}
			} catch (NoSuchFieldException e) {
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

	//if it's a jij mod, return parent mod's container
	private static ModContainer getModContainer(String modid) {
		ModContainer modContainer = FabricLoader.INSTANCE.getModContainer(modid)
				.orElseThrow(() -> new RuntimeException("Cannot get mod container " + modid));

		CustomValue customValue = modContainer.getMetadata().getCustomValue("modmenu:parent");

		if (customValue == null) {
			//it's not a jij mod
			return modContainer;
		}

		String parentModid = customValue.getAsString();

		ModContainer parentModContainer = FabricLoader.INSTANCE.getModContainer(parentModid)
				.orElseThrow(() -> new RuntimeException("Cannot get mod container " + parentModid));
		return parentModContainer;
	}
}

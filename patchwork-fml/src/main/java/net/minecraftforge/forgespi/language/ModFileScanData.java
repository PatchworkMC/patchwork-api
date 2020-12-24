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

import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.objectweb.asm.Type;

import net.fabricmc.loader.api.ModContainer;

// TODO: mega stub
public class ModFileScanData {
	public static final ModFileScanData EMPTY = new ModFileScanData();

	public ModFileScanData(ModContainer modContainer, String annotationJsonLocation) {
		//
	}

	public ModFileScanData() {
		//
	}

	public Set<AnnotationData> getAnnotations() {
		return Collections.emptySet();
	}

	public static class AnnotationData {
		private final Type annotationType;
		private final ElementType targetType;
		private final Type clazz;
		private final String memberName;
		private final Map<String, Object> annotationData;

		public AnnotationData(final Type annotationType, final ElementType targetType, final Type clazz, final String memberName, final Map<String, Object> annotationData) {
			this.annotationType = annotationType;
			this.targetType = targetType;
			this.clazz = clazz;
			this.memberName = memberName;
			this.annotationData = annotationData;
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
			return annotationData;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj == this) {
				return true;
			}

			if (obj.getClass() != getClass()) {
				return false;
			}

			AnnotationData dat = (AnnotationData) obj;

			return Objects.equals(annotationType, dat.annotationType)
					&& Objects.equals(targetType, dat.targetType)
					&& Objects.equals(clazz, dat.clazz)
					&& Objects.equals(memberName, dat.memberName)
					&& Objects.equals(annotationData, dat.annotationData);
		}

		@Override
		public int hashCode() {
			return Objects.hash(annotationType, targetType, clazz, memberName, annotationData);
		}
	}
}

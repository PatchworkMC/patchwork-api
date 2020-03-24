package net.minecraftforge.forgespi.language;

import java.lang.annotation.ElementType;
import java.util.ArrayList;

public class AnnotationStorage {
	public static class Entry {
		public String annotationType;
		public ElementType targetType;
		public String targetInClass;
		public String target;

		public Entry(
				String annotationType,
				ElementType targetType,
				String targetInClass,
				String target
		) {
			this.annotationType = annotationType;
			this.targetType = targetType;
			this.targetInClass = targetInClass;
			this.target = target;
		}
	}

	public ArrayList<Entry> entries = new ArrayList<>();

	public AnnotationStorage() {
	}
}

package net.patchworkmc.processor.god_classes;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.google.gson.reflect.TypeToken;

// Remember to update copy in buildSrc when modified!
public class SourceElement {
	public SourceElement() {}

	// fully qualified name to class
	String source_class;
	// short name relative to source class
	String source_name;
	// short name relative to generated class
	String target_name;
	// element modifiers
	// TODO: rework?
	Modifier[] modifiers;

	List<List<Arg>> constructors;

	// type for field, return type for method
	Ty type;
	// argument types for method
	List<Arg> args;
	List<TypeArg> type_args;

	public static class Arg {
		Arg() {}
		Arg(Ty ty, String n) {
			type = ty;
			name = n;
		}
		Arg(VariableElement var) {
			type = new SourceElement.Ty(var.asType());
			name = var.getSimpleName().toString();
		}

		Ty type;
		String name;
	}

	public static class TypeArg {
		TypeArg() {}
		TypeArg(String n, List<Ty> b) {
			name = n;
			bounds = b;
		}

		TypeArg(TypeParameterElement elem) {
			name = elem.getSimpleName().toString();
			bounds = elem.getBounds()
				.stream()
				.map(Ty::new)
				.collect(Collectors.toList());
		}


		String name;
		List<Ty> bounds;
	}

	public static class Ty {
		Ty() {}

		Ty(String n, List<TypeArg> g) {
			name = n;
			generics = g;
		}

		Ty(TypeMirror tm) {
			if (tm.getKind() == TypeKind.DECLARED) {
				TypeElement ty = (TypeElement)((DeclaredType)tm).asElement();
				name = ty.getQualifiedName().toString();
				//name = ty.getSimpleName().toString();
				generics = ty.getTypeParameters()
					.stream()
					.map(TypeArg::new).collect(Collectors.toList());
			} else {
				name = tm.toString();
			}

			isPrimitive = tm.getKind().isPrimitive();
		}

		String name;
		List<TypeArg> generics;
		boolean isPrimitive;
	}

	public static final Type serialized_type = new TypeToken<HashMap<String, List<SourceElement>>>(){}.getType();
}

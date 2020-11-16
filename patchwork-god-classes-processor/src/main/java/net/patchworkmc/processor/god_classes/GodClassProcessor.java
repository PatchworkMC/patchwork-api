package net.patchworkmc.processor.god_classes;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

import com.google.gson.Gson;

import net.patchworkmc.annotations.GodClass;

@SupportedAnnotationTypes("net.patchworkmc.annotations.GodClass")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class GodClassProcessor extends AbstractProcessor {
	public GodClassProcessor() {
		super();
	}

	//	static class TargetFile {
//		List<SourceElementTmp> elements;
//	}

	//JsonElement annotated_items;
	private HashMap<String, List<SourceElement>> annotated_items;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnvironment) {
		super.init(processingEnvironment);

		annotated_items = new HashMap<>();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Gson gson = new Gson();
		for ( Element element : roundEnv.getElementsAnnotatedWith(GodClass.class) ) {
			if (!element.getModifiers().containsAll(Arrays.asList(Modifier.STATIC, Modifier.PUBLIC))) {
				processingEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					"Annotated item must be public static",
					element
				);

				continue;
			}

			GodClass target = element.getAnnotation(GodClass.class);
			SourceElement sourceElement = new SourceElement();

			ElementKind kind = element.getKind();
			if (kind.isClass()) {
				TypeElement type_element = ((TypeElement)element);
				sourceElement.source_class = type_element
					.getQualifiedName().toString();

				sourceElement.constructors = type_element.getEnclosedElements().stream()
					.filter(e -> e.getKind() == ElementKind.CONSTRUCTOR)
					.map(e -> ((ExecutableElement)e).getParameters().stream().map(SourceElement.Arg::new).collect(Collectors.toList()))
					.collect(Collectors.toList());
			} else {
				sourceElement.source_name = element.getSimpleName().toString();
				try {
					sourceElement.source_class = ((TypeElement)element.getEnclosingElement())
						.getQualifiedName().toString();
				} catch (ClassCastException ex) {
					processingEnv.getMessager().printMessage(
						Diagnostic.Kind.ERROR,
						"Annotated item must be a direct member of a type",
						element
					);
					continue;
				}
			}

			if (!target.name().isEmpty()) {
				sourceElement.target_name = target.name();
			} else {
				sourceElement.target_name = element.getSimpleName().toString();
			}

			sourceElement.modifiers = target.modifiers();

			if (kind.isField()) {
//				Types.direct
//				element.asType().getKind();
				sourceElement.type = new SourceElement.Ty(element.asType());
			} else if (kind == ElementKind.METHOD) {
				ExecutableElement exec_element = (ExecutableElement) element;
				sourceElement.type = new SourceElement.Ty(exec_element.getReturnType());
				sourceElement.args = exec_element
					.getParameters()
					.stream()
					.map(SourceElement.Arg::new)
					.collect(Collectors.toList());

				sourceElement.type_args = exec_element
					.getTypeParameters()
					.stream()
					.map(SourceElement.TypeArg::new)
					.collect(Collectors.toList());
			} else if (!kind.isClass()) {
				processingEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					"Annotated item must be a method, field, or class",
					element
				);
			}

			annotated_items
				.computeIfAbsent(target.value(), k -> new ArrayList<>())
				.add(sourceElement);

			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "found @GodClass at " + element);
		}

		if (roundEnv.processingOver()) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Writing god class info to json");
			try {
				Writer writer = processingEnv.getFiler()
					.createResource(StandardLocation.CLASS_OUTPUT, "", "generated/god_classes.json")
					.openWriter();

				gson.toJson(annotated_items, SourceElement.serialized_type, writer);
				writer.close();
			} catch (IOException ex) {
				processingEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					"Failed to write json: " + ex
				);
			}
		}

		return true;
	}
}

package net.patchworkmc.processor.god_classes;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import net.patchworkmc.annotations.GodClass;

@SupportedAnnotationTypes({ "net.patchworkmc.annotations.GodClass", "net.patchworkmc.annotations.GodClass.Constructor" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class GodClassProcessor extends AbstractProcessor {
	public GodClassProcessor() {
		super();
	}

	private HashMap<ClassName, TypeSpec.Builder> generated_classes;

	private Messager messager;
	private Filer filer;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnvironment) {
		super.init(processingEnvironment);

		generated_classes = new HashMap<>();
		messager = processingEnvironment.getMessager();
		filer = processingEnvironment.getFiler();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for ( Element element : roundEnv.getElementsAnnotatedWith(GodClass.class) ) {
			messager.printMessage(Diagnostic.Kind.NOTE, "found @GodClass at " + element);
			if (!element.getModifiers().containsAll(Arrays.asList(Modifier.STATIC, Modifier.PUBLIC))) {
				messager.printMessage(
					Diagnostic.Kind.ERROR,
					"Annotated item must be public static",
					element
				);

				continue;
			}

			// TODO: getAnnotationsByType? aka, supporting multiple annotations on the same element
			GodClass target = element.getAnnotation(GodClass.class);
			String[] split = target.value().split(":");
			if (split.length < 2) {
				messager.printMessage(
					Diagnostic.Kind.ERROR,
					"GodClass annotation missing element name",
					element
				);
				continue;
			} else if (split.length > 2) {
				messager.printMessage(
					Diagnostic.Kind.ERROR,
					"GodClass annotation has too many seperators indicating element name",
					element
				);
				continue;
			}

			// This is fine unless we ever stop following java conventions for package and class names.
			ClassName target_class_name = ClassName.bestGuess(split[0]);
			String target_item_name = split[1];

			TypeSpec.Builder target_class = generated_classes.computeIfAbsent(target_class_name, TypeSpec::classBuilder);
			target_class.addOriginatingElement(element);

			// TODO: no annotations are currently passed through, pass (some of them?) through.
			ElementKind kind = element.getKind();
			if (kind.isField()) {
				ClassName source_class = ClassName.get((TypeElement) element.getEnclosingElement());
				target_class.addField(
					FieldSpec.builder(TypeName.get(element.asType()), target_item_name, Modifier.STATIC)
						.addModifiers(target.modifiers())
						.initializer(
							CodeBlock.of("$T.$N", source_class, element.getSimpleName())
						).build()
				);
			} else if (kind == ElementKind.METHOD) {
				ExecutableElement exec_element = (ExecutableElement) element;
				ClassName source_class = ClassName.get((TypeElement) element.getEnclosingElement());

				TypeName return_type = TypeName.get(exec_element.getReturnType());
				List<ParameterSpec> parameters = exec_element.getParameters().stream()
					.map(ParameterSpec::get)
					.collect(Collectors.toList());
				List<TypeVariableName> typeVars = exec_element.getTypeParameters().stream()
					.map(TypeVariableName::get)
					.collect(Collectors.toList());

				CodeBlock.Builder body = CodeBlock.builder();

				if (return_type != TypeName.VOID) {
					body.add("return ");
				}

				// I would specify type parameters as part of this but java always infers them, so nevermind.
				body.add(
					"$T.$N($L)",
					source_class,
					exec_element.getSimpleName(),
					parameters.stream()
						.map(param -> CodeBlock.of("$N", param))
						.collect(CodeBlock.joining(", "))
				);

				target_class.addMethod(
					MethodSpec.methodBuilder(target_item_name)
						.addModifiers(Modifier.STATIC)
						.addModifiers(target.modifiers())
						.addTypeVariables(typeVars)
						.addParameters(parameters)
						.returns(return_type)
						.addStatement(body.build())
						.build()
				);
			} else if (kind.isClass()) {
				TypeElement typeElement = (TypeElement)element;
				target_class.addType(
					TypeSpec.classBuilder(target_class_name.nestedClass(target_item_name))
						.addModifiers(Modifier.STATIC)
						.addModifiers(target.modifiers())
						.superclass(typeElement.asType())
						.addMethods(
							typeElement.getEnclosedElements().stream()
								.filter(e -> e.getKind() == ElementKind.CONSTRUCTOR && e.getAnnotation(GodClass.Constructor.class) != null)
								.map(e -> (ExecutableElement)e)
								.map(e -> {
									List<ParameterSpec> params = e.getParameters().stream()
										.map(ParameterSpec::get)
										.collect(Collectors.toList());

									return MethodSpec.constructorBuilder()
										.addModifiers(e.getAnnotation(GodClass.Constructor.class).value())
										.addParameters(params)
										.addStatement(
											"super$L",
											params.stream()
												.map(p -> CodeBlock.of("$N", p))
												.collect(CodeBlock.joining(", ", "(", ")"))
										).build();
								}).collect(Collectors.toList())
						).build()
				);
			} else {
				messager.printMessage(
					Diagnostic.Kind.ERROR,
					"Annotated item must be a method, field, or class",
					element
				);
			}
		}

		if (roundEnv.processingOver()) {
			Filer filer = processingEnv.getFiler();

			for (Map.Entry<ClassName, TypeSpec.Builder> entry: generated_classes.entrySet()) {
				messager.printMessage(
					Diagnostic.Kind.NOTE,
					"Writing generated source for: " + entry.getKey().canonicalName()
				);

				try {
					TypeSpec clazz = entry.getValue()
						//.addModifiers(Modifier.PUBLIC)
						.addAnnotation(AnnotationSpec.builder(Generated.class)
							.addMember("value", "$S", "net.patchworkmc.processor.god_classes.GodClassProcessor")
							.build()
						).build();
					JavaFile.builder(entry.getKey().packageName(), clazz)
						// I would add the license here, but the style wouldn't be correct so meh
						//.addFileComment()
						.build()
						.writeTo(filer);
				} catch (IOException e) {
					messager.printMessage(
						Diagnostic.Kind.ERROR,
						"Failed to write generated source: " + e
					);
				}
			}
		}

		return true;
	}
}

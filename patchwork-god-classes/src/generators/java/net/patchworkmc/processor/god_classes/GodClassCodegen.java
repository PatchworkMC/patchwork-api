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

package net.patchworkmc.processor.god_classes;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.codemodel.writer.FilterCodeWriter;
import com.sun.codemodel.writer.ProgressCodeWriter;

public class GodClassCodegen {
	private static final String[] docstring = {
		"Generated \"God Class\" stub intended for use by Forge mods only.",
		"Members of this class are thin generated shims for methods in other modules."
	};

	// TODO: replicate annotations on generated method
	public void doGenerate(File dir, Map.Entry<Stream<Reader>, String> input) throws IOException, JClassAlreadyExistsException, ClassNotFoundException {
		//new AbstractMap.SimpleImmutableEntry<Stream<Reader>, File>(input, null);
		Gson gson = new Gson();
		HashMap<String, List<SourceElement>> classes = input.getKey().map(reader -> {
			HashMap<String, List<SourceElement>> elem = gson.fromJson(reader, SourceElement.serialized_type);
			return elem;
		}).reduce(new HashMap<>(), (acc, map) -> {
			map.forEach((key, val) -> acc.merge(key, val, (a, b) -> {
				a.addAll(b); return a;
			}));
			return acc;
		});

		JCodeModel cm = new JCodeModel();

		for (Map.Entry<String, List<SourceElement>> e: classes.entrySet()) {
			JDefinedClass gen_class = cm._class(e.getKey());

			gen_class.javadoc().append(String.join("\n", docstring));

			for (SourceElement elem: e.getValue()) {
				if (elem.args != null) {
					// method
					// TODO: modifiers

					JType returnType = getType(cm, elem.type);

					JMethod gen_method = gen_class.method(JMod.PUBLIC | JMod.STATIC, returnType, elem.target_name);

					for (SourceElement.TypeArg typeArg: elem.type_args) {
						// NOTE: I can't deal with more than one type bound!

						try {
							gen_method.generify(typeArg.name, (JClass) getType(cm, typeArg.bounds.get(0)));
						} catch (IndexOutOfBoundsException ex) {
							gen_method.generify(typeArg.name);
						}
					}

					JInvocation invoke = cm.directClass(elem.source_class).staticInvoke(elem.source_name);

					for (SourceElement.Arg arg: elem.args) {
						JVar gen_param = gen_method.param(getType(cm, arg.type), arg.name);

						invoke.arg(gen_param);
					}

					if (elem.type.name.equals("void")) {
						gen_method.body().add(invoke);
					} else {
						gen_method.body()._return(invoke);
					}
				} else if (elem.type != null) {
					// field
					// TODO: modifiers

					JFieldVar gen_field = gen_class.field(JMod.PUBLIC | JMod.STATIC, getType(cm, elem.type), elem.target_name);

					gen_field.assign(cm.directClass(elem.source_class).staticRef(elem.source_name));
				} else {
					// inner class
					// TODO: modifiers
					JDefinedClass inner = gen_class._class(JMod.PUBLIC | JMod.STATIC, elem.target_name);
					inner._extends(cm.directClass(elem.source_class));
					// TODO: modifiers
					// TODO: how to decide which constructor(s)
					for (List<SourceElement.Arg> constructor: elem.constructors) {
						JMethod gen_init = inner.constructor(JMod.PUBLIC);
						JInvocation call_super = JExpr.invoke("super");

						for (SourceElement.Arg arg: constructor) {
							JVar gen_param = gen_init.param(getType(cm, arg.type), arg.name);

							call_super.arg(gen_param);
						}

						gen_init.body().add(call_super);
					}
				}
			}
		}

		CodeWriter src = new LicenseCodeWriter(new ProgressCodeWriter(new FileCodeWriter(dir), System.out), input.getValue());

		cm.build(src);
	}

	JType getType(JCodeModel cm, SourceElement.Ty type) {
		if (type.isPrimitive) {
			return JType.parse(cm, type.name);
		} else {
			// i'd use parseType for everything but it keeps throwing NullPointerException iirc so...
			JClass ref = cm.directClass(type.name);

			if (type.generics != null) {
				for (SourceElement.TypeArg arg: type.generics) {
					ref.narrow(getTypeArg(cm, arg));
				}
			}

			return ref;
		}
	}

	JClass getTypeArg(JCodeModel cm, SourceElement.TypeArg arg) {
		try {
			return (JClass) getType(cm, arg.bounds.get(0));
		} catch (IndexOutOfBoundsException ex) {
			return cm.wildcard();
		}
	}

	// https://github.com/javaee/jaxb-codemodel/blob/5e0fd4234b180c88fbc808bfadc6d75e34d76e5e/codemodel/codemodel/src/main/java/com/sun/codemodel/writer/PrologCodeWriter.java
	static class LicenseCodeWriter extends FilterCodeWriter {
		private final String license;
		LicenseCodeWriter(CodeWriter core, String license) {
			super(core);
			this.license = license;
		}

		@Override
		public Writer openSource(JPackage pkg, String fileName) throws IOException {
			Writer w = super.openSource(pkg, fileName);

			if (license != null) {
				w.write("/*\n");

				for (String line: license.split("\n")) {
					w.write(" *");

					if (!line.isEmpty()) {
						w.write(" ");
						w.write(line);
					}

					w.write("\n");
				}

				w.write(" */\n");
			}

			return w;
		}
	}
}

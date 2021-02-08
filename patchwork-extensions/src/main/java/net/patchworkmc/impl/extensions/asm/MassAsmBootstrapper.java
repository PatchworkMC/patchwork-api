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

package net.patchworkmc.impl.extensions.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.FabricMixinTransformerProxy;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

// thank HalfOfTwo for this monstrosity
public class MassAsmBootstrapper implements PreLaunchEntrypoint {
	private static final Field ACTIVE_TRANSFORMER, MIXIN_TRANSFORMER;

	static {
		Transformer.init();

		Transformer.LOGGER.info("I am become Mass ASM, destroyer of coderbot16");

		try {
			ACTIVE_TRANSFORMER = MixinEnvironment.class.getDeclaredField("transformer");
			ACTIVE_TRANSFORMER.setAccessible(true);
			MIXIN_TRANSFORMER = Class.forName("net.fabricmc.loader.launch.knot.KnotClassDelegate").getDeclaredField("mixinTransformer");
			MIXIN_TRANSFORMER.setAccessible(true);
		} catch (ReflectiveOperationException arg) {
			throw new RuntimeException(arg);
		}
	}

	public class FabricMixinTransformerProxyProxy extends FabricMixinTransformerProxy {
		private final FabricMixinTransformerProxy proxy;

		public FabricMixinTransformerProxyProxy(FabricMixinTransformerProxy proxy) {
			this.proxy = proxy;
		}

		@Override
		public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
			if (basicClass == null) {
				return this.proxy.transformClassBytes(name, transformedName, basicClass);
			}

			byte[] transformed = this.proxy.transformClassBytes(name, transformedName, basicClass);

			try {
				return Transformer.transform(transformedName, transformed);
			} catch (AnalyzerException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	@Override
	public void onPreLaunch() {
		try {
			// first we need knot's delegate
			Object delegate = getDelegate();
			// then we get the current mixin transformer
			FabricMixinTransformerProxy proxy = (FabricMixinTransformerProxy) MIXIN_TRANSFORMER.get(delegate);

			Field active = FabricMixinTransformerProxy.class.getDeclaredField("transformer");
			active.setAccessible(true);

			// we need to delete mixin's current mixin transformer to get around it's constructor check
			Object currentTransformer = active.get(proxy);

			// we remove the active transformer
			ACTIVE_TRANSFORMER.set(null, null);
			// this action overrides the current transformer
			FabricMixinTransformerProxyProxy proxyProxy = new FabricMixinTransformerProxyProxy(proxy);
			// so we return the old one
			ACTIVE_TRANSFORMER.set(null, currentTransformer);

			MIXIN_TRANSFORMER.set(delegate, proxyProxy);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private static Object getDelegate() throws ReflectiveOperationException {
		ClassLoader loader = MassAsmBootstrapper.class.getClassLoader();
		Class<?> knotInterface = Class.forName("net.fabricmc.loader.launch.knot.KnotClassLoaderInterface");

		while (loader != null && !knotInterface.isInstance(loader)) {
			loader = loader.getParent();
		}

		Method method = knotInterface.getMethod("getDelegate");
		method.setAccessible(true);
		return method.invoke(loader);
	}
}

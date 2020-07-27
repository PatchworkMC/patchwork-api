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

package net.patchworkmc.mixin.resource;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraftforge.resource.VanillaResourceType;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;

import net.patchworkmc.impl.resource.TypedResourceLoader;

@Mixin(LanguageOptionsScreen.class)
public abstract class MixinLanguageOptionsScreen {
	@SuppressWarnings("rawtypes")
	@Redirect(method = "method_19820", at = @At(value = "INVOKE", target = "net/minecraft/client/MinecraftClient.reloadResources()Ljava/util/concurrent/CompletableFuture;"))
	protected CompletableFuture patchwork_init_reloadResources(MinecraftClient mc) {
		return TypedResourceLoader.patchwork$refreshResources(mc, VanillaResourceType.LANGUAGES);
	}
}

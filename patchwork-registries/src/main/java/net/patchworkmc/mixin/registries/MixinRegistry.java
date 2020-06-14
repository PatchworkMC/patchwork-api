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

package net.patchworkmc.mixin.registries;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraftforge.registries.GameData;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

@Mixin(Registry.class)
public class MixinRegistry {
	@Inject(at = @At("TAIL"), method = "putDefaultEntry(Ljava/lang/String;Lnet/minecraft/util/registry/MutableRegistry;Ljava/util/function/Supplier;)Lnet/minecraft/util/registry/MutableRegistry;")
	private static <T, R extends MutableRegistry<T>> void handleNewVanillaRegistry(String name, R reg, Supplier<T> supplier, CallbackInfoReturnable<R> info) {
		Identifier id = new Identifier(name);
		GameData.wrapVanilla(id, reg);
	}
}

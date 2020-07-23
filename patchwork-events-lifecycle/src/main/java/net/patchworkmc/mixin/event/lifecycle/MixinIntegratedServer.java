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

package net.patchworkmc.mixin.event.lifecycle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;

import net.patchworkmc.impl.event.lifecycle.LifecycleEvents;

@Mixin(IntegratedServer.class)
public class MixinIntegratedServer {
	@Inject(method = "setupServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;loadWorld(Ljava/lang/String;Ljava/lang/String;JLnet/minecraft/world/level/LevelGeneratorType;Lcom/google/gson/JsonElement;)V"))
	priv​ate v​oid onSer​verAboutToStart(CallbackI​nfoReturnable<B​oolean> ci​r)​ {​
		LifecycleEven​ts.handleS​erverAboutToStart((Minec​raftServer​) ​(Obje​ct)​ th​is)​;​
	​}​
}

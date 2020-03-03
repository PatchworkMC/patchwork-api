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

package com.patchworkmc.mixin.extensions.keybinds;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import net.minecraftforge.client.extensions.IForgeKeybinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.CompoundTag;

@Mixin(GameOptions.class)
public class MixinGameOptions {
	@Shadow
	@Final
	public KeyBinding keyForward;

	@Shadow
	@Final
	public KeyBinding keyLeft;

	@Shadow
	@Final
	public KeyBinding keyBack;

	@Shadow
	@Final
	public KeyBinding keyRight;

	@Shadow
	@Final
	public KeyBinding keyJump;

	@Shadow
	@Final
	public KeyBinding keySneak;

	@Shadow
	@Final
	public KeyBinding keySprint;

	@Shadow
	@Final
	public KeyBinding keyAttack;

	@Shadow
	@Final
	public KeyBinding keyChat;

	@Shadow
	@Final
	public KeyBinding keyPlayerList;

	@Shadow
	@Final
	public KeyBinding keyCommand;

	@Shadow
	@Final
	public KeyBinding keyTogglePerspective;

	@Shadow
	@Final
	public KeyBinding keySmoothCamera;

	@Shadow
	@Final
	public KeyBinding keySwapHands;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(MinecraftClient client, File optionsFile, CallbackInfo ci) {
		KeyConflictContext inGame = KeyConflictContext.IN_GAME;
		((IForgeKeybinding) keyForward).setKeyConflictContext(inGame);
		((IForgeKeybinding) keyLeft).setKeyConflictContext(inGame);
		((IForgeKeybinding) keyBack).setKeyConflictContext(inGame);
		((IForgeKeybinding) keyRight).setKeyConflictContext(inGame);
		((IForgeKeybinding) keyJump).setKeyConflictContext(inGame);
		((IForgeKeybinding) keySneak).setKeyConflictContext(inGame);
		((IForgeKeybinding) keySprint).setKeyConflictContext(inGame);
		((IForgeKeybinding) keyAttack).setKeyConflictContext(inGame);
		((IForgeKeybinding) keyChat).setKeyConflictContext(inGame);
		((IForgeKeybinding) keyPlayerList).setKeyConflictContext(inGame);
		((IForgeKeybinding) keyCommand).setKeyConflictContext(inGame);
		((IForgeKeybinding) keyTogglePerspective).setKeyConflictContext(inGame);
		((IForgeKeybinding) keySmoothCamera).setKeyConflictContext(inGame);
		((IForgeKeybinding) keySwapHands).setKeyConflictContext(inGame);
	}

	@Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;fromName(Ljava/lang/String;)Lnet/minecraft/client/util/InputUtil$KeyCode;"))
	private InputUtil.KeyCode setKeyCodeAndDoNothingAndPleaseDoNotCrash(String s) {
		return null;
	}

	@Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/KeyBinding;setKeyCode(Lnet/minecraft/client/util/InputUtil$KeyCode;)V"))
	private void setKeyCodeAndDoNothing(KeyBinding keyBinding, InputUtil.KeyCode keyCode) {
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;fromName(Ljava/lang/String;)Lnet/minecraft/client/util/InputUtil$KeyCode;"),
					locals = LocalCapture.CAPTURE_FAILHARD)
	private void setKeyCodeAndActuallyDoStuff(CallbackInfo ci, List list, CompoundTag compoundTag, Iterator var3, String string2, String string3, KeyBinding[] var6, int var7, int var8, KeyBinding keyBinding, String var11) {
		if (string3.indexOf(':') != -1) {
			String[] pts = string3.split(":");
			((IForgeKeybinding) keyBinding).setKeyModifierAndCode(KeyModifier.valueFromString(pts[1]), InputUtil.fromName(pts[0]));
		} else {
			((IForgeKeybinding) keyBinding).setKeyModifierAndCode(KeyModifier.NONE, InputUtil.fromName(string3));
		}
	}

	@Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/KeyBinding;getName()Ljava/lang/String;"))
	private String getNameWithModifiers(KeyBinding keyBinding) {
		return keyBinding.getName() + (((IForgeKeybinding) keyBinding).getKeyModifier() != KeyModifier.NONE ? ":" + ((IForgeKeybinding) keyBinding).getKeyModifier() : "");
	}
}

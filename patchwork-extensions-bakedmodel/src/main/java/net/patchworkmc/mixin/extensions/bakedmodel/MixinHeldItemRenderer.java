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

package net.patchworkmc.mixin.extensions.bakedmodel;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import net.patchworkmc.impl.extensions.bakedmodel.ForgeBlockModels;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {
	@Shadow
	@Final
	private MinecraftClient client;

	private static final ThreadLocal<BlockPos> renderOverlays_PlayerPos = new ThreadLocal<>();

	@ModifyArg(method = "renderOverlays", at = @At(value = "INVOKE", ordinal = 0, target =
			"Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockPos collectLocalVarWithInitVal(BlockPos pos) {
		if (renderOverlays_PlayerPos.get() == null) {
			renderOverlays_PlayerPos.set(pos);
			return pos;
		} else {
			throw new IllegalStateException("renderOverlays_PlayerPos is not clean!");
		}
	}

	//	if (blockState2.canSuffocate(this.client.world, blockPos)) {
	//+		This injection
	//		blockState = blockState2;
	@Inject(method = "renderOverlays", locals = LocalCapture.CAPTURE_FAILHARD,
			slice = @Slice(from = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/block/BlockState;canSuffocate(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z")),
			at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = Shift.AFTER, ordinal = 0))
	private void hook_canSuffocate(float f, CallbackInfo ci, BlockState blockState, PlayerEntity playerEntity, int i, double d, double e, double g, BlockPos blockPos, BlockState blockState2) {
		renderOverlays_PlayerPos.set(blockPos);
	}

	@Redirect(method = "renderOverlays", at = @At(value = "INVOKE", ordinal = 0, target =
			"Lnet/minecraft/client/render/block/BlockModels;getSprite(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/texture/Sprite;"))
	private Sprite redirect_getSprite(BlockModels blockModels, BlockState blockState) {
		BlockPos pos = renderOverlays_PlayerPos.get();
		return ((ForgeBlockModels) blockModels).getTexture(blockState, client.world, pos);
	}

	@Inject(method = "renderOverlays", at = @At("RETURN"))
	private void resetLocalVar(CallbackInfo ci) {
		renderOverlays_PlayerPos.remove();
	}
}

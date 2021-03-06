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

package net.patchworkmc.mixin.extensions.item.client;

import net.minecraftforge.common.extensions.IForgeItem;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(BlockRenderManager.class)
public class MixinBlockRenderManager {
	@Redirect(method = "renderBlockAsEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/item/BuiltinModelItemRenderer;INSTANCE:Lnet/minecraft/client/render/item/BuiltinModelItemRenderer;",
			opcode = Opcodes.GETSTATIC, ordinal = 0))
	private BuiltinModelItemRenderer patchwork$useForgeISTER(BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, int overlay) {
		return ((IForgeItem) state.getBlock().asItem()).getItemStackTileEntityRenderer();
	}
}

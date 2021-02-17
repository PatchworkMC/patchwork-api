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

package net.patchworkmc.impl.event.render;

import java.util.Set;

import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoader;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class RenderEvents implements ClientModInitializer {
	public static void onBlockColorsInit(BlockColors blockColors) {
		ModLoader.get().postEvent(new ColorHandlerEvent.Block(blockColors));
	}

	public static void onItemColorsInit(ItemColors itemColors, BlockColors blockColors) {
		ModLoader.get().postEvent(new ColorHandlerEvent.Item(itemColors, blockColors));
	}

	public static void onTextureStitchPre(SpriteAtlasTexture spriteAtlasTexture, Set<Identifier> set) {
		ModLoader.get().postEvent(new TextureStitchEvent.Pre(spriteAtlasTexture, set));
	}

	public static void onTextureStitchPost(SpriteAtlasTexture spriteAtlasTexture) {
		ModLoader.get().postEvent(new TextureStitchEvent.Post(spriteAtlasTexture));
	}

	/**
	 * Called by ForgeHooksClient and in WorldRenderEvents callback.
	 * @return true if the bounding box rendering is cancelled.
	 */
	public static boolean onDrawHighlightEvent(WorldRenderer context, Camera info, HitResult target, float partialTicks, MatrixStack matrix, VertexConsumerProvider buffers) {
		switch (target.getType()) {
			case BLOCK:
				if (!(target instanceof BlockHitResult)) {
					return false;
				}

				return MinecraftForge.EVENT_BUS.post(new DrawHighlightEvent.HighlightBlock(context, info, target, partialTicks, matrix, buffers));
			case ENTITY:
				if (!(target instanceof EntityHitResult)) {
					return false;
				}

				return MinecraftForge.EVENT_BUS.post(new DrawHighlightEvent.HighlightEntity(context, info, target, partialTicks, matrix, buffers));
			default:
				return MinecraftForge.EVENT_BUS.post(new DrawHighlightEvent(context, info, target, partialTicks, matrix, buffers));
		}
	}

	public static void onRenderWorldLast(WorldRenderer context, MatrixStack matrixStack, float tickDelta, Matrix4f projectionMatrix, long limitTime) {
		MinecraftForge.EVENT_BUS.post(new RenderWorldLastEvent(context, matrixStack, tickDelta, projectionMatrix, limitTime));
	}

	public static boolean onRenderHand(Hand hand, MatrixStack mat, VertexConsumerProvider buffers, int light, float partialTicks, float interpPitch, float swingProgress, float equipProgress, ItemStack stack) {
		return MinecraftForge.EVENT_BUS.post(new RenderHandEvent(hand, mat, buffers, light, partialTicks, interpPitch, swingProgress, equipProgress, stack));
	}

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(((worldRenderContext, hitResult) -> !onDrawHighlightEvent(
				worldRenderContext.worldRenderer(),
				worldRenderContext.camera(),
				hitResult,
				worldRenderContext.tickDelta(),
				worldRenderContext.matrixStack(),
				worldRenderContext.consumers()
		)));

		WorldRenderEvents.END.register((end) -> onRenderWorldLast(end.worldRenderer(), end.matrixStack(), end.tickDelta(), end.projectionMatrix(), end.limitTime()));
	}
}

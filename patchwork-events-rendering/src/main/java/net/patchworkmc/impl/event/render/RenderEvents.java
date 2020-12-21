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
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoader;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class RenderEvents {
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
	 * Called by ForgeHooksClient and MixinWorldRenderer.
	 * @return true if the bounding box rendering is cancelled.
	 */
	public static boolean onDrawHighlightEvent(WorldRenderer context, Camera info, HitResult target, int subID, float partialTicks) {
		switch (target.getType()) {
		case BLOCK:
			if (!(target instanceof BlockHitResult)) return false;
			return MinecraftForge.EVENT_BUS.post(new DrawBlockHighlightEvent.HighlightBlock(context, info, target, subID, partialTicks));
		case ENTITY:
			if (!(target instanceof EntityHitResult)) return false;
			return MinecraftForge.EVENT_BUS.post(new DrawBlockHighlightEvent.HighlightEntity(context, info, target, subID, partialTicks));
		default:
			return MinecraftForge.EVENT_BUS.post(new DrawBlockHighlightEvent(context, info, target, subID, partialTicks));
		}
	}

	public static void onRenderWorldLast(WorldRenderer context, float tickDelta) {
		MinecraftForge.EVENT_BUS.post(new RenderWorldLastEvent(context, tickDelta));
	}

	public static boolean onRenderHand(WorldRenderer context, float tickDelta) {
		return MinecraftForge.EVENT_BUS.post(new RenderHandEvent(context, tickDelta));
	}

	public static boolean onRenderSpecificHand(Hand hand, float tickDelta, float pitch, float swingProgress, float equipProgress, ItemStack stack) {
		return MinecraftForge.EVENT_BUS.post(new RenderSpecificHandEvent(hand, tickDelta, pitch, swingProgress, equipProgress, stack));
	}
}

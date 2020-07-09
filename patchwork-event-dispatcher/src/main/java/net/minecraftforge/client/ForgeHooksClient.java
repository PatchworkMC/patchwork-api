package net.minecraftforge.client;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.patchworkmc.impl.event.render.RenderEvents;

import java.util.Set;

public class ForgeHooksClient {
	public static void onBlockColorsInit(BlockColors blockColors) {
		RenderEvents.onBlockColorsInit(blockColors);
	}

	public static void onItemColorsInit(ItemColors itemColors, BlockColors blockColors) {
		RenderEvents.onItemColorsInit(itemColors, blockColors);
	}

	public static void onTextureStitchedPre(SpriteAtlasTexture map, Set<Identifier> resourceLocations) {
		RenderEvents.onTextureStitchPre(map, resourceLocations);
	}

	public static void onTextureStitchedPost(SpriteAtlasTexture map) {
		RenderEvents.onTextureStitchPost(map);
	}
}

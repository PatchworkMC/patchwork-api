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

package net.minecraftforge.client.event;

import java.util.Set;

import net.minecraftforge.eventbus.api.Event;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

// TODO: This is supposed to implement IModBusEvent, which is not yet implemented
public class TextureStitchEvent extends Event {
	private final SpriteAtlasTexture map;

	public TextureStitchEvent(SpriteAtlasTexture map) {
		this.map = map;
	}

	public SpriteAtlasTexture getMap() {
		return map;
	}

	/**
	 * Fired when the {@link SpriteAtlasTexture} is told to refresh its stitched texture.
	 * Called before the {@link SpriteAtlasTexture} is loaded.
	 */
	public static class Pre extends TextureStitchEvent {
		private final Set<Identifier> sprites;

		public Pre(SpriteAtlasTexture map, Set<Identifier> sprites) {
			super(map);
			this.sprites = sprites;
		}

		/**
		 * Add a sprite to be stitched into the texture atlas.
		 */
		public boolean addSprite(Identifier sprite) {
			return this.sprites.add(sprite);
		}
	}

	/**
	 * This event is fired once the texture map has loaded all textures and
	 * stitched them together. All sprites should have there locations defined
	 * by the time this is fired.
	 */
	public static class Post extends TextureStitchEvent {
		public Post(SpriteAtlasTexture map) {
			super(map);
		}
	}
}

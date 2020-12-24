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

package net.minecraftforge.client.model.animation;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.block.entity.BlockEntity;

import net.patchworkmc.impl.extensions.blockentity.ForgeBlockEntityRenderer;

/**
 * TODO: The FastTESR is removed in 1.15.
 * The new vanilla BlockEntityRender is identical to Forge's FastTESR, see the "Disadvantages" section.
 * Patchwork API will not implement proper batch rendering for FastTESR,
 * however, FastTESRs can still be displayed.
 *
 * <p>A special case which can be batched with other
 * renderers that are also instances of this class.
 *
 * <p>Advantages:
 * <ul>
 * <li>All batched renderers are drawn with a single draw call</li>
 * <li>Renderers have their vertices depth sorted for better translucency
 * support</li>
 * </ul>
 *
 * <p>Disadvantages:
 * <ul>
 * <li>OpenGL operations are not permitted</li>
 * <li>All renderers must use the same {@link VertexFormat}
 * ({@link VertexFormats#POSITION_COLOR_TEXTURE_LIGHT_NORMAL})</li>
 * </ul>
 *
 * @param <T> The type of {@link BlockEntity} being rendered.
 */
@Deprecated
public abstract class TileEntityRendererFast<T extends BlockEntity> extends BlockEntityRenderer<T> implements ForgeBlockEntityRenderer<T> {
	@Override
	public final void render(T te, double x, double y, double z, float partialTicks, int destroyStage) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
		DiffuseLighting.disable();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();

		if (MinecraftClient.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		} else {
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_UV_LMAP);

		renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, buffer);
		buffer.setOffset(0, 0, 0);

		tessellator.draw();

		DiffuseLighting.enableForLevel();
	}

	/**
	 * Draw this renderer to the passed {@link BufferBuilder}. <strong>DO
	 * NOT</strong> draw to any buffers other than the one passed, or use any OpenGL
	 * operations as they will not be applied when this renderer is batched.
	 */
	@Override
	public abstract void renderTileEntityFast(T te, double x, double y, double z, float partialTicks, int destroyStage, BufferBuilder buffer);
}

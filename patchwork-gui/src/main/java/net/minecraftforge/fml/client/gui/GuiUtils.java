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

package net.minecraftforge.fml.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

/**
 * This class provides several methods and constants used by the Config GUI classes.
 *
 * @author bspkrs
 */
public class GuiUtils {
	/**
	 * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
	 * and filler. It is assumed that the desired texture Identifier object has been bound using
	 * MinecraftClient.getInstance().getTextureManager().bindTexture(identifier).
	 *
	 * @param x x axis offset
	 * @param y y axis offset
	 * @param u bound identifier image x offset
	 * @param v bound identifier image y offset
	 * @param width the desired box width
	 * @param height the desired box height
	 * @param textureWidth the width of the box texture in the identifier image
	 * @param textureHeight the height of the box texture in the identifier image
	 * @param borderSize the size of the box's borders
	 * @param zLevel the zLevel to draw at
	 */
	@Deprecated // Use matrix stack version TODO remove 1.17
	public static void drawContinuousTexturedBox(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize, float zLevel) {
		drawContinuousTexturedBox(x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize, zLevel);
	}

	/**
	 * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
	 * and filler. The provided Identifier object will be bound using
	 * MinecraftClient.getInstance().getTextureManager().bindTexture(identifier).
	 *
	 * @param res the Identifier object that contains the desired image
	 * @param x x axis offset
	 * @param y y axis offset
	 * @param u bound identifier image x offset
	 * @param v bound identifier image y offset
	 * @param width the desired box width
	 * @param height the desired box height
	 * @param textureWidth the width of the box texture in the identifier image
	 * @param textureHeight the height of the box texture in the identifier image
	 * @param borderSize the size of the box's borders
	 * @param zLevel the zLevel to draw at
	 */
	@Deprecated // Use matrix stack version TODO remove 1.17
	public static void drawContinuousTexturedBox(Identifier res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize, float zLevel) {
		drawContinuousTexturedBox(res, x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize, zLevel);
	}

	/**
	 * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
	 * and filler. The provided Identifier object will be bound using
	 * MinecraftClient.getInstance().getTextureManager().bindTexture(identifier).
	 *
	 * @param res the Identifier object that contains the desired image
	 * @param x x axis offset
	 * @param y y axis offset
	 * @param u bound identifier image x offset
	 * @param v bound identifier image y offset
	 * @param width the desired box width
	 * @param height the desired box height
	 * @param textureWidth the width of the box texture in the identifier image
	 * @param textureHeight the height of the box texture in the identifier image
	 * @param topBorder the size of the box's top border
	 * @param bottomBorder the size of the box's bottom border
	 * @param leftBorder the size of the box's left border
	 * @param rightBorder the size of the box's right border
	 * @param zLevel the zLevel to draw at
	 */
	@Deprecated // Use matrix stack version TODO remove 1.17
	public static void drawContinuousTexturedBox(Identifier res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(res);
		drawContinuousTexturedBox(x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
	}

	/**
	 * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
	 * and filler. It is assumed that the desired texture Identifier object has been bound using
	 * MinecraftClient.getInstance().getTextureManager().bindTexture(identifier).
	 *
	 * @param x x axis offset
	 * @param y y axis offset
	 * @param u bound identifier image x offset
	 * @param v bound identifier image y offset
	 * @param width the desired box width
	 * @param height the desired box height
	 * @param textureWidth the width of the box texture in the identifier image
	 * @param textureHeight the height of the box texture in the identifier image
	 * @param topBorder the size of the box's top border
	 * @param bottomBorder the size of the box's bottom border
	 * @param leftBorder the size of the box's left border
	 * @param rightBorder the size of the box's right border
	 * @param zLevel the zLevel to draw at
	 */
	@Deprecated // Use matrix stack version TODO remove 1.17
	public static void drawContinuousTexturedBox(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

		int fillerWidth = textureWidth - leftBorder - rightBorder;
		int fillerHeight = textureHeight - topBorder - bottomBorder;
		int canvasWidth = width - leftBorder - rightBorder;
		int canvasHeight = height - topBorder - bottomBorder;
		int xPasses = canvasWidth / fillerWidth;
		int remainderWidth = canvasWidth % fillerWidth;
		int yPasses = canvasHeight / fillerHeight;
		int remainderHeight = canvasHeight % fillerHeight;

		// Draw Border
		// Top Left
		drawTexturedModalRect(x, y, u, v, leftBorder, topBorder, zLevel);
		// Top Right
		drawTexturedModalRect(x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel);
		// Bottom Left
		drawTexturedModalRect(x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel);
		// Bottom Right
		drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);

		for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++) {
			// Top Border
			drawTexturedModalRect(x + leftBorder + (i * fillerWidth), y, u + leftBorder, v, (i == xPasses ? remainderWidth : fillerWidth), topBorder, zLevel);
			// Bottom Border
			drawTexturedModalRect(x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bottomBorder, zLevel);

			// Throw in some filler for good measure
			for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++) {
				drawTexturedModalRect(x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder, (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight), zLevel);
			}
		}

		// Side Borders
		for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++) {
			// Left Border
			drawTexturedModalRect(x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
			// Right Border
			drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight), u + leftBorder + fillerWidth, v + topBorder, rightBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
		}
	}

	@Deprecated // Use matrix stack version TODO remove 1.17
	public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float zLevel) {
		final float uScale = 1f / 0x100;
		final float vScale = 1f / 0x100;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder wr = tessellator.getBuffer();
		wr.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
		wr.vertex(x, y + height, zLevel).texture(u * uScale, ((v + height) * vScale)).next();
		wr.vertex(x + width, y + height, zLevel).texture((u + width) * uScale, ((v + height) * vScale)).next();
		wr.vertex(x + width, y, zLevel).texture((u + width) * uScale, (v * vScale)).next();
		wr.vertex(x, y, zLevel).texture(u * uScale, (v * vScale)).next();
		tessellator.draw();
	}

	/**
	 * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
	 * and filler. It is assumed that the desired texture Identifier object has been bound using
	 * MinecraftClient.getInstance().getTextureManager().bindTexture(identifier).
	 *
	 * @param matrixStack the gui matrix stack
	 * @param x x axis offset
	 * @param y y axis offset
	 * @param u bound identifier image x offset
	 * @param v bound identifier image y offset
	 * @param width the desired box width
	 * @param height the desired box height
	 * @param textureWidth the width of the box texture in the identifier image
	 * @param textureHeight the height of the box texture in the identifier image
	 * @param borderSize the size of the box's borders
	 * @param zLevel the zLevel to draw at
	 */
	public static void drawContinuousTexturedBox(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize, float zLevel) {
		drawContinuousTexturedBox(matrixStack, x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize, zLevel);
	}

	/**
	 * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
	 * and filler. The provided Identifier object will be bound using
	 * MinecraftClient.getInstance().getTextureManager().bindTexture(identifier).
	 *
	 * @param matrixStack the gui matrix stack
	 * @param res the Identifier object that contains the desired image
	 * @param x x axis offset
	 * @param y y axis offset
	 * @param u bound identifier image x offset
	 * @param v bound identifier image y offset
	 * @param width the desired box width
	 * @param height the desired box height
	 * @param textureWidth the width of the box texture in the identifier image
	 * @param textureHeight the height of the box texture in the identifier image
	 * @param borderSize the size of the box's borders
	 * @param zLevel the zLevel to draw at
	 */
	public static void drawContinuousTexturedBox(MatrixStack matrixStack, Identifier res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize, float zLevel) {
		drawContinuousTexturedBox(matrixStack, res, x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize, zLevel);
	}

	/**
	 * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
	 * and filler. The provided Identifier object will be bound using
	 * MinecraftClient.getInstance().getTextureManager().bindTexture(identifier).
	 *
	 * @param matrixStack the gui matrix stack
	 * @param res the Identifier object that contains the desired image
	 * @param x x axis offset
	 * @param y y axis offset
	 * @param u bound identifier image x offset
	 * @param v bound identifier image y offset
	 * @param width the desired box width
	 * @param height the desired box height
	 * @param textureWidth the width of the box texture in the identifier image
	 * @param textureHeight the height of the box texture in the identifier image
	 * @param topBorder the size of the box's top border
	 * @param bottomBorder the size of the box's bottom border
	 * @param leftBorder the size of the box's left border
	 * @param rightBorder the size of the box's right border
	 * @param zLevel the zLevel to draw at
	 */
	public static void drawContinuousTexturedBox(MatrixStack matrixStack, Identifier res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(res);
		drawContinuousTexturedBox(matrixStack, x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
	}

	/**
	 * Draws a textured box of any size (smallest size is borderSize * 2 square) based on a fixed size textured box with continuous borders
	 * and filler. It is assumed that the desired texture Identifier object has been bound using
	 * MinecraftClient.getInstance().getTextureManager().bindTexture(identifier).
	 *
	 * @param matrixStack the gui matrix stack
	 * @param x x axis offset
	 * @param y y axis offset
	 * @param u bound identifier image x offset
	 * @param v bound identifier image y offset
	 * @param width the desired box width
	 * @param height the desired box height
	 * @param textureWidth the width of the box texture in the identifier image
	 * @param textureHeight the height of the box texture in the identifier image
	 * @param topBorder the size of the box's top border
	 * @param bottomBorder the size of the box's bottom border
	 * @param leftBorder the size of the box's left border
	 * @param rightBorder the size of the box's right border
	 * @param zLevel the zLevel to draw at
	 */
	@SuppressWarnings("deprecation")
	public static void drawContinuousTexturedBox(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

		int fillerWidth = textureWidth - leftBorder - rightBorder;
		int fillerHeight = textureHeight - topBorder - bottomBorder;
		int canvasWidth = width - leftBorder - rightBorder;
		int canvasHeight = height - topBorder - bottomBorder;
		int xPasses = canvasWidth / fillerWidth;
		int remainderWidth = canvasWidth % fillerWidth;
		int yPasses = canvasHeight / fillerHeight;
		int remainderHeight = canvasHeight % fillerHeight;

		// Draw Border
		// Top Left
		drawTexturedModalRect(matrixStack, x, y, u, v, leftBorder, topBorder, zLevel);

		// Top Right
		drawTexturedModalRect(matrixStack, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel);

		// Bottom Left
		drawTexturedModalRect(matrixStack, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel);

		// Bottom Right
		drawTexturedModalRect(matrixStack, x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);

		for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++) {
			// Top Border
			drawTexturedModalRect(matrixStack, x + leftBorder + (i * fillerWidth), y, u + leftBorder, v, (i == xPasses ? remainderWidth : fillerWidth), topBorder, zLevel);

			// Bottom Border
			drawTexturedModalRect(matrixStack, x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, (i == xPasses ? remainderWidth
					: fillerWidth), bottomBorder, zLevel);

			// Throw in some filler for good measure
			for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++) {
				drawTexturedModalRect(matrixStack, x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder, (i == xPasses ? remainderWidth
						: fillerWidth), (j == yPasses
						? remainderHeight
						: fillerHeight), zLevel);
			}
		}

		// Side Borders
		for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++) {
			// Left Border
			drawTexturedModalRect(matrixStack, x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
			// Right Border
			drawTexturedModalRect(matrixStack, x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight), u + leftBorder + fillerWidth, v + topBorder, rightBorder, (j == yPasses
					? remainderHeight
					: fillerHeight), zLevel);
		}
	}

	public static void drawTexturedModalRect(MatrixStack matrixStack, int x, int y, int u, int v, int width, int height, float zLevel) {
		final float uScale = 1f / 0x100;
		final float vScale = 1f / 0x100;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder wr = tessellator.getBuffer();
		wr.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
		Matrix4f matrix = matrixStack.peek().getModel();
		wr.vertex(matrix, x, y + height, zLevel).texture(u * uScale, ((v + height) * vScale)).next();
		wr.vertex(matrix, x + width, y + height, zLevel).texture((u + width) * uScale, ((v + height) * vScale)).next();
		wr.vertex(matrix, x + width, y, zLevel).texture((u + width) * uScale, (v * vScale)).next();
		wr.vertex(matrix, x, y, zLevel).texture(u * uScale, (v * vScale)).next();
		tessellator.draw();
	}
}

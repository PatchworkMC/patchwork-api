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

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class RenderWorldLastEvent extends net.minecraftforge.eventbus.api.Event {
	private final WorldRenderer context;
	private final MatrixStack mat;
	private final float partialTicks;
	private final Matrix4f projectionMatrix;
	private final long finishTimeNano;

	public RenderWorldLastEvent(WorldRenderer context, MatrixStack mat, float partialTicks, Matrix4f projectionMatrix, long finishTimeNano) {
		this.context = context;
		this.mat = mat;
		this.partialTicks = partialTicks;
		this.projectionMatrix = projectionMatrix;
		this.finishTimeNano = finishTimeNano;
	}

	public WorldRenderer getContext() {
		return context;
	}

	public MatrixStack getMatrixStack() {
		return mat;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public long getFinishTimeNano() {
		return finishTimeNano;
	}
}

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

package net.minecraftforge.common.util;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.PersistentState;

public class WorldCapabilityData extends PersistentState {
	public static final String ID = "capabilities";

	private INBTSerializable<CompoundTag> serializable;
	private CompoundTag capNBT = null;

	public WorldCapabilityData(String name) {
		super(name);
	}

	public WorldCapabilityData(@Nullable INBTSerializable<CompoundTag> serializable) {
		super(ID);
		this.serializable = serializable;
	}

	@Override
	public void fromTag(CompoundTag nbt) {
		this.capNBT = nbt;

		if (serializable != null) {
			serializable.deserializeNBT(this.capNBT);
			this.capNBT = null;
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag nbt) {
		if (serializable != null) {
			nbt = serializable.serializeNBT();
		}

		return nbt;
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	public void setCapabilities(INBTSerializable<CompoundTag> capabilities) {
		this.serializable = capabilities;

		if (this.capNBT != null && serializable != null) {
			serializable.deserializeNBT(this.capNBT);
			this.capNBT = null;
		}
	}
}

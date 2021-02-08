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

package net.minecraftforge.energy;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.Direction;

import net.patchworkmc.api.capability.CapabilityRegisteredCallback;

public class CapabilityEnergy {
	public static Capability<IEnergyStorage> ENERGY = null;

	public static void register() {
		// TODO: This might not register before it's needed, double check that
		CapabilityRegisteredCallback.event(IEnergyStorage.class).register(cap -> ENERGY = cap);

		CapabilityManager.INSTANCE.register(IEnergyStorage.class, new IStorage<IEnergyStorage>() {
					@Override
					public Tag writeNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, Direction side) {
						return IntTag.of(instance.getEnergyStored());
					}

					@Override
					public void readNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, Direction side, Tag nbt) {
						if (!(instance instanceof EnergyStorage)) {
							throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
						}

						((EnergyStorage) instance).energy = ((IntTag) nbt).getInt();
					}
				},
				() -> new EnergyStorage(1000)
		);
	}
}


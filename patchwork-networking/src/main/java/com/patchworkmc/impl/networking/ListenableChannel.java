/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2019, 2019
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

package com.patchworkmc.impl.networking;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;

public interface ListenableChannel {
	void setPacketListener(BiConsumer<ICustomPacket<?>, NetworkEvent.Context> listener);
	void setRegistrationChangeListener(Consumer<NetworkEvent.ChannelRegistrationChangeEvent> listener);
	void setGatherLoginPayloadsListener(BiConsumer<List<NetworkRegistry.LoginPayload>, Boolean> listener);
}

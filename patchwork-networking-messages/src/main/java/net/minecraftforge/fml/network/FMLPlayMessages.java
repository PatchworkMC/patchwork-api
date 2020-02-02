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

package net.minecraftforge.fml.network;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.network.PacketContext;

import com.patchworkmc.impl.networking.ClientEntitySpawner;
import com.patchworkmc.impl.networking.PatchworkNetworking;

public class FMLPlayMessages {
	/**
	 * Used to spawn a custom entity without the same restrictions as
	 * {@link net.minecraft.client.network.packet.EntitySpawnS2CPacket} or {@link net.minecraft.client.network.packet.MobSpawnS2CPacket}
	 * <p>
	 * To customize how your entity is created clientside (instead of using the default factory provided to the {@link EntityType})
	 * see {@link com.patchworkmc.mixin.networking.MixinEntityTypeBuilder#setCustomClientFactory}.
	 */
	public static class SpawnEntity {
		private final Entity entity;
		private final int typeId;
		private final int entityId;
		private final UUID uuid;
		private final double posX, posY, posZ;
		private final byte pitch, yaw, headYaw;
		private final int velX, velY, velZ;
		private final PacketByteBuf buf;

		// Note: package-private on Forge
		public SpawnEntity(Entity e) {
			this.entity = e;
			this.typeId = Registry.ENTITY_TYPE.getRawId(e.getType());
			this.entityId = e.getEntityId();
			this.uuid = e.getUuid();
			this.posX = e.x;
			this.posY = e.y;
			this.posZ = e.z;
			this.pitch = (byte) MathHelper.floor(e.pitch * 256.0F / 360.0F);
			this.yaw = (byte) MathHelper.floor(e.yaw * 256.0F / 360.0F);
			this.headYaw = (byte) (e.getHeadYaw() * 256.0F / 360.0F);
			Vec3d vec3d = e.getVelocity();
			double d1 = MathHelper.clamp(vec3d.x, -3.9D, 3.9D);
			double d2 = MathHelper.clamp(vec3d.y, -3.9D, 3.9D);
			double d3 = MathHelper.clamp(vec3d.z, -3.9D, 3.9D);
			this.velX = (int) (d1 * 8000.0D);
			this.velY = (int) (d2 * 8000.0D);
			this.velZ = (int) (d3 * 8000.0D);
			this.buf = null;
		}

		private SpawnEntity(int typeId, int entityId, UUID uuid, double posX, double posY, double posZ,
							byte pitch, byte yaw, byte headYaw, int velX, int velY, int velZ, PacketByteBuf buf) {
			this.entity = null;
			this.typeId = typeId;
			this.entityId = entityId;
			this.uuid = uuid;
			this.posX = posX;
			this.posY = posY;
			this.posZ = posZ;
			this.pitch = pitch;
			this.yaw = yaw;
			this.headYaw = headYaw;
			this.velX = velX;
			this.velY = velY;
			this.velZ = velZ;
			this.buf = buf;
		}

		public static void encode(SpawnEntity msg, PacketByteBuf buf) {
			buf.writeVarInt(msg.typeId);
			buf.writeInt(msg.entityId);
			buf.writeLong(msg.uuid.getMostSignificantBits());
			buf.writeLong(msg.uuid.getLeastSignificantBits());
			buf.writeDouble(msg.posX);
			buf.writeDouble(msg.posY);
			buf.writeDouble(msg.posZ);
			buf.writeByte(msg.pitch);
			buf.writeByte(msg.yaw);
			buf.writeByte(msg.headYaw);
			buf.writeShort(msg.velX);
			buf.writeShort(msg.velY);
			buf.writeShort(msg.velZ);

			if (msg.entity instanceof IEntityAdditionalSpawnData) {
				((IEntityAdditionalSpawnData) msg.entity).writeSpawnData(buf);
			}
		}

		public static SpawnEntity decode(PacketByteBuf buf) {
			return new SpawnEntity(
					buf.readVarInt(),
					buf.readInt(),
					new UUID(buf.readLong(), buf.readLong()),
					buf.readDouble(), buf.readDouble(), buf.readDouble(),
					buf.readByte(), buf.readByte(), buf.readByte(),
					buf.readShort(), buf.readShort(), buf.readShort(),
					buf
			);
		}

		public static void handle(SpawnEntity msg, PacketContext context) {
			PatchworkNetworking.enqueueWork(context.getTaskQueue(), () -> {
				EntityType<?> type = Registry.ENTITY_TYPE.get(msg.typeId);

				if (type.equals(Registry.ENTITY_TYPE.get(Registry.ENTITY_TYPE.getDefaultId()))) {
					throw new RuntimeException(String.format("Could not spawn entity (id %d) with unknown type at (%f, %f, %f)", msg.entityId, msg.posX, msg.posY, msg.posZ));
				}

				ClientWorld world = MinecraftClient.getInstance().world;

				Entity e = ((ClientEntitySpawner<?>) type).customClientSpawn(msg, world);

				if (e == null) {
					return;
				}

				e.updateTrackedPosition(msg.posX, msg.posY, msg.posZ);
				e.setPositionAnglesAndUpdate(msg.posX, msg.posY, msg.posZ, (msg.yaw * 360) / 256.0F, (msg.pitch * 360) / 256.0F);
				e.setHeadYaw((msg.headYaw * 360) / 256.0F);
				e.setYaw((msg.headYaw * 360) / 256.0F);

				e.setEntityId(msg.entityId);
				e.setUuid(msg.uuid);
				world.addEntity(msg.entityId, e);
				e.setVelocity(msg.velX / 8000.0, msg.velY / 8000.0, msg.velZ / 8000.0);

				if (e instanceof IEntityAdditionalSpawnData) {
					((IEntityAdditionalSpawnData) e).readSpawnData(msg.buf);
				}
			});
		}

		public static void handle(SpawnEntity msg, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();

			if (context.getDirection().getReceptionSide() != LogicalSide.CLIENT) {
				return;
			}

			handle(msg, context);

			context.setPacketHandled(true);
		}

		public Entity getEntity() {
			return entity;
		}

		public int getTypeId() {
			return typeId;
		}

		public int getEntityId() {
			return entityId;
		}

		public UUID getUuid() {
			return uuid;
		}

		public double getPosX() {
			return posX;
		}

		public double getPosY() {
			return posY;
		}

		public double getPosZ() {
			return posZ;
		}

		public byte getPitch() {
			return pitch;
		}

		public byte getYaw() {
			return yaw;
		}

		public byte getHeadYaw() {
			return headYaw;
		}

		public int getVelX() {
			return velX;
		}

		public int getVelY() {
			return velY;
		}

		public int getVelZ() {
			return velZ;
		}

		public PacketByteBuf getAdditionalData() {
			return buf;
		}
	}

	// TODO: OpenContainer
	// TODO: DimensionInfoMessage
}

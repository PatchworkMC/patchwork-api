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

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

// Preliminary, simple Fake Player class
public class FakePlayer extends ServerPlayerEntity {
	public FakePlayer(ServerWorld world, GameProfile profile) {
		super(world.getServer(), world, profile, new ServerPlayerInteractionManager(world));
	}

	@Override
	public Vec3d getPosVector() {
		return new Vec3d(0, 0, 0);
	}

	@Override
	public void sendMessage(Text text, boolean actionBar) {
	}

	@Override
	public void sendSystemMessage(Text component) {
	}

	@Override
	public void increaseStat(Stat stat, int num) {
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return true;
	}

	@Override
	public boolean shouldDamagePlayer(PlayerEntity player) {
		return false;
	}

	@Override
	public void onDeath(DamageSource source) {
	}

	@Override
	public void tick() {
	}

	@Override
	public void setClientSettings(ClientSettingsC2SPacket packet) {
	}

	// Forge also has this here:
	// @Override @Nullable public MinecraftServer getServer() { return ServerLifecycleHooks.getCurrentServer(); }
}

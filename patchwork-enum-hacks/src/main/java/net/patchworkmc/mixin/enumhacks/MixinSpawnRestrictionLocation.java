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

package net.patchworkmc.mixin.enumhacks;

import net.minecraftforge.common.util.TriPredicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;

import net.patchworkmc.impl.enumhacks.HackableEnum;
import net.patchworkmc.impl.enumhacks.PatchworkSpawnRestrictionLocation;

@Mixin(SpawnRestriction.Location.class)
public class MixinSpawnRestrictionLocation implements PatchworkSpawnRestrictionLocation, HackableEnum<SpawnRestriction.Location> {
	@Unique
	private TriPredicate<CollisionView, BlockPos, EntityType<?>> predicate;

	@Shadow
	@Final
	@Mutable
	private static SpawnRestriction.Location[] field_6319;

	public boolean canSpawnAt(CollisionView world, BlockPos pos, EntityType<?> type) {
		return predicate.test(world, pos, type);
	}

	@Override
	public boolean patchwork_useVanillaBehavior() {
		return predicate == null;
	}

	@Override
	public void patchwork_setPredicate(TriPredicate<CollisionView, BlockPos, EntityType<?>> predicate) {
		this.predicate = predicate;
	}

	@Override
	public void patchwork_setValues(SpawnRestriction.Location[] values) {
		field_6319 = values;
	}
}

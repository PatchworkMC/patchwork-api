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

package net.patchworkmc.mixin.extensions.shearing;

import java.util.List;
import java.util.Map;

import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * Patches {@link SheepEntity} to allow using {@link IShearable} for dropping wool.
 *
 * @author SuperCoder79
 */
@Mixin(SheepEntity.class)
public abstract class MixinSheepEntity extends AnimalEntity implements IShearable {
	@Shadow
	public abstract DyeColor getColor();

	@Shadow
	@Final
	private static Map<DyeColor, ItemConvertible> DROPS;

	@Shadow
	public abstract boolean isSheared();

	@Shadow
	public abstract void setSheared(boolean bl);

	protected MixinSheepEntity(EntityType<? extends AnimalEntity> type, World world) {
		super(type, world);
	}

	@Override
	public boolean isShearable(ItemStack item, CollisionView world, BlockPos pos) {
		return !this.isSheared() && !this.isBaby();
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, WorldAccess world, BlockPos pos, int fortune) {
		List<ItemStack> drops = new java.util.ArrayList<>();

		if (!this.world.isClient) {
			this.setSheared(true);

			int count = 1 + this.random.nextInt(3);
			ItemConvertible wool = DROPS.get(this.getColor());

			for (int i = 0; i < count; i++) {
				drops.add(new ItemStack(wool));
			}
		}

		this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
		return drops;
	}
}

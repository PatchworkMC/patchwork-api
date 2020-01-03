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

package com.patchworkmc.mixin.extensions.shearing;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

/**
 * Patches MooshroomEntity to allow shearing when the mooshroom is an adult, dropping mushrooms. This patch cancels the vanilla shearing code.
 *
 * @author SuperCoder79
 */
@Mixin(MooshroomEntity.class)
public abstract class MixinMooshroomEntity extends CowEntity implements IShearable {
	@Shadow
	public abstract MooshroomEntity.Type getMooshroomType();

	public MixinMooshroomEntity(EntityType<? extends CowEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean isShearable(ItemStack item, ViewableWorld world, BlockPos pos) {
		return this.getBreedingAge() >= 0;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IWorld world, BlockPos pos, int fortune) {
		List<ItemStack> drops = new ArrayList<>();
		this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y + (double) (this.getHeight() / 2.0F), this.z, 0.0D, 0.0D, 0.0D);

		if (!this.world.isClient) {
			this.remove();

			CowEntity cow = EntityType.COW.create(this.world);
			cow.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
			cow.setHealth(this.getHealth());
			cow.field_6283 = this.field_6283;

			if (this.hasCustomName()) {
				cow.setCustomName(this.getCustomName());
			}

			this.world.spawnEntity(cow);
			Block mushroom = this.getMooshroomType().getMushroomState().getBlock();

			// TODO: Fixes forge bug where shearing brown mooshrooms always drop red mushrooms (Fixed in 1.15)
			for (int i = 0; i < 5; ++i) {
				drops.add(new ItemStack(mushroom));
			}

			this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
		}

		return drops;
	}
}

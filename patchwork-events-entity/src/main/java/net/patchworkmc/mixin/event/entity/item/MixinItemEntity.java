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

package net.patchworkmc.mixin.event.entity.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.patchworkmc.api.extensions.item.ItemEntityLifespanAccess;
import net.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity implements ItemEntityLifespanAccess {
	@Shadow
	public abstract ItemStack getStack();

	public MixinItemEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@ModifyConstant(method = "tick()V", constant = @Constant(intValue = 6000))
	private int patchwork$useLifespan(int in) {
		return patchwork$getLifespan();
	}

	@Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V", ordinal = 1))
	private void patchwork$onItemExpire(ItemEntity itemEntity) {
		int hook = EntityEvents.onItemExpire((ItemEntity) (Object) this, this.getStack());

		if (hook < 0) {
			this.remove();
		} else {
			this.patchwork$setLifespan(this.patchwork$getLifespan() + hook);
		}
	}
}

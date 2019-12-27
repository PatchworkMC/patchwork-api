package com.patchworkmc.mixin.extensions.shearing;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
	public boolean isShearable(ItemStack item, ViewableWorld world, net.minecraft.util.math.BlockPos pos) {
		return this.getBreedingAge() >= 0;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IWorld world, BlockPos pos, int fortune) {
		List<ItemStack> ret = new ArrayList<>();
		this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y + (double)(this.getHeight() / 2.0F), this.z, 0.0D, 0.0D, 0.0D);
		if (!this.world.isClient) {
			this.remove();

			CowEntity entityCow = EntityType.COW.create(this.world);
			entityCow.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
			entityCow.setHealth(this.getHealth());
			entityCow.field_6283 = this.field_6283;
			if (this.hasCustomName()) {
				entityCow.setCustomName(this.getCustomName());
			}

			((ServerWorld)this.world).loadEntity(entityCow);

			for(int i = 0; i < 5; ++i) { //Fixes forge bug where shearing brown mooshrooms always drop red mushrooms
				ret.add(new ItemStack(this.getMooshroomType().getMushroomState().getBlock()));
			}

			this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
		}
		return ret;
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity mate) {
		return null;
	}
}

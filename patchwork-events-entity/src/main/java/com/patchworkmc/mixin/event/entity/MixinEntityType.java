package com.patchworkmc.mixin.event.entity;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(EntityType.class)
public class MixinEntityType {
	private static final String SPAWN = "spawn(Lnet/minecraft/world/World;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/text/Text;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/SpawnType;ZZ)Lnet/minecraft/entity/Entity;";

	@Inject(method = SPAWN, at = @At(value = "INVOKE", target = "net/minecraft/world/World.spawnEntity(Lnet/minecraft/entity/Entity;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void hookMobSpawns(World world, @Nullable CompoundTag itemTag, @Nullable Text name, @Nullable PlayerEntity player, BlockPos pos, SpawnType type, boolean alignPosition, boolean bl, CallbackInfoReturnable<Entity> callback, Entity entity) {
		if (!(entity instanceof MobEntity)) {
			return;
		}

		MobEntity mob = (MobEntity) entity;

		if (EntityEvents.doSpecialSpawn(mob, world, pos.getX(), pos.getY(), pos.getZ(), null, type)) {
			callback.setReturnValue(null);
		}
	}
}

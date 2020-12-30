package net.patchworkmc.mixin.extensions.entity;

import net.minecraft.client.render.entity.LivingEntityRenderer;

import net.minecraft.entity.LivingEntity;

import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {
	/**
	 * Handles the {@link IForgeEntity#shouldRiderSit()} hook for determining if riders should be rendered as sitting.
	 *
	 * <p>This method intentionally targets every invocation of {@link LivingEntity#hasVehicle()}.</p>
	 *
	 * @param entity The "rider" entity being rendered
	 * @return If the entity should be rendered as sitting
	 */
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasVehicle()Z"))
	private boolean redirectHasVehicle(LivingEntity entity) {
		return entity.hasVehicle() && ((IForgeEntity) entity.getVehicle()).shouldRiderSit();
	}
}

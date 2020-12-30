package net.patchworkmc.mixin.extensions.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;

import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProjectileUtil.class)
public class MixinProjectileUtil {
	/**
	 * Targets an if condition that determines if a shooter is riding a target, not allowing interactions if so.
	 *
	 * <p>As an added condition of this check, we see if the target allows {@link IForgeEntity#canRiderInteract()}.
	 * If it does, we intentionally cause the if condition to fail by making {@code rider.getRootVehicle()} return null,
	 * allowing the two entities to interact.</p>
	 *
	 * @param targetEntity The entity targeted in the raycast
	 * @return The root entity of the target, or null if the rider is allowed to interact with the target.
	 */
	@Redirect(method = "raycast", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRootVehicle()Lnet/minecraft/entity/Entity;", ordinal = 0))
	private static Entity onRiderCheck(Entity targetEntity) {
		if (((IForgeEntity)targetEntity).canRiderInteract()) {
			return null;
		} else {
			return targetEntity.getRootVehicle();
		}
	}
}

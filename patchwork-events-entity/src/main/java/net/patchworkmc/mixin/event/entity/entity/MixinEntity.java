package net.patchworkmc.mixin.event.entity.entity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.patchworkmc.impl.event.entity.EntityEvents;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@Shadow
	private EntityDimensions dimensions;

	@Shadow
	private float standingEyeHeight;

	@Shadow
	public abstract EntityDimensions getDimensions(EntityPose pose);

	@Shadow
	public abstract EntityPose getPose();

	@Shadow
	protected abstract float getEyeHeight(EntityPose pose, EntityDimensions dimensions);

	@Shadow
	public abstract void setBoundingBox(Box boundingBox);

	@Shadow
	public abstract double getX();

	@Shadow
	public abstract double getY();

	@Shadow
	public abstract double getZ();

	@Shadow
	public abstract Box getBoundingBox();

	@Shadow
	protected boolean firstUpdate;

	@Shadow
	public World world;

	@Shadow
	public abstract void move(MovementType type, Vec3d movement);

	@Inject(method = "<init>", at = @At("TAIL"))
	private void patchwork$fireEntityConstructionEvents(EntityType<?> type, World world, CallbackInfo ci) {
		EntityEvent.Size sizeEvent = EntityEvents.getEntitySizeForge((Entity) (Object) this, EntityPose.STANDING, this.dimensions, this.standingEyeHeight);
		this.dimensions = sizeEvent.getNewSize();
		this.standingEyeHeight = sizeEvent.getNewEyeHeight();
		MinecraftForge.EVENT_BUS.post(new EntityEvent.EntityConstructing((Entity) (Object) this));
	}

	/**
	 * The alternative of redirects and ThreadLocal caching is extremely fragile
	 * (see: the entitydimensions being stored from this.dimensions, etc); let's just fail hard if someone
	 * tinkers with this.
	 * @author glitch
	 */
	@Overwrite
	public void calculateDimensions() {
		EntityDimensions entityDimensions = this.dimensions;
		EntityPose entityPose = this.getPose();
		// forge start
		net.minecraftforge.event.entity.EntityEvent.Size sizeEvent = EntityEvents.getEntitySizeForge((Entity) (Object) this, entityPose, this.getDimensions(entityPose), this.getEyeHeight(entityPose, entityDimensions));
		EntityDimensions entityDimensions2 = sizeEvent.getNewSize();
		this.dimensions = entityDimensions;
		this.standingEyeHeight = sizeEvent.getNewEyeHeight();
		// forge end
		if (entityDimensions2.width < entityDimensions.width) {
			double d = (double)entityDimensions2.width / 2.0D;
			this.setBoundingBox(new Box(this.getX() - d, this.getY(), this.getZ() - d, this.getX() + d, this.getY() + (double)entityDimensions2.height, this.getZ() + d));
		} else {
			Box box = this.getBoundingBox();
			this.setBoundingBox(new Box(box.minX, box.minY, box.minZ, box.minX + (double)entityDimensions2.width, box.minY + (double)entityDimensions2.height, box.minZ + (double)entityDimensions2.width));
			if (entityDimensions2.width > entityDimensions.width && !this.firstUpdate && !this.world.isClient) {
				float f = entityDimensions.width - entityDimensions2.width;
				this.move(MovementType.SELF, new Vec3d((double)f, 0.0D, (double)f));
			}

		}
	}

}

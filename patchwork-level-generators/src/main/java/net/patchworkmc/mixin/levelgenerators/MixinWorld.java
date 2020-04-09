package net.patchworkmc.mixin.levelgenerators;

import net.minecraftforge.common.extensions.IForgeWorldType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.World;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;

import net.patchworkmc.impl.levelgenerators.PatchworkGeneratorType;

@Mixin(World.class)
public class MixinWorld {
	@Shadow
	@Final
	private LevelProperties properties;

	@Inject(at = @At("HEAD"), method = "getHorizonHeight", cancellable = true)
	private void getHorizonHeight(CallbackInfoReturnable<Double> info) { // TODO: use IForgeDimension
		LevelGeneratorType generatorType = this.properties.getGeneratorType();

		if (generatorType instanceof PatchworkGeneratorType) {
			info.setReturnValue(((IForgeWorldType) generatorType).getHorizon((World) (Object) this));
		}
	}
}

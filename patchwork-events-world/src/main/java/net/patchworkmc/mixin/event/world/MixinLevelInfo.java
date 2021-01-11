package net.patchworkmc.mixin.event.world;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.Difficulty;
import net.minecraft.world.level.LevelInfo;

import net.patchworkmc.impl.event.world.WorldEvents;

@Mixin(LevelInfo.class)
public abstract class MixinLevelInfo {
	@Shadow
	@Final
	private Difficulty difficulty;

	@Inject(method = "withDifficulty", at = @At("HEAD"))
	private void onDifficultyChange(Difficulty difficulty, CallbackInfoReturnable<LevelInfo> info) {
		WorldEvents.onDifficultyChange(difficulty, this.difficulty);
	}
}

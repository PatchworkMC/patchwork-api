package net.patchworkmc.mixin.extensions.entity;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.SpawnHelper;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
	@ModifyVariable(method = "setupSpawn",
			ordinal = 0,
			index = 7,
			name = "spawnGroup",
			at = @At(value = "JUMP",
					opcode = Opcodes.IF_ACMPNE,
					ordinal = 0))
	private static SpawnGroup onClassification(SpawnGroup spawnGroup) {
		// TODO: :ohno:
		return spawnGroup;
	}
}

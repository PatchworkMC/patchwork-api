package com.patchworkmc.mixin.extension;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraftforge.common.extensions.IForgeEffect;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StatusEffect.class)
public class MixinStatusEffect implements IForgeEffect {
}

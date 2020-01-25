package com.patchworkmc.mixin.biomes;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.biome.Biome;

import com.patchworkmc.impl.biomes.ForgeBiomeExt;
import com.patchworkmc.impl.biomes.PatchworkBiomes;

@Mixin(Biome.class)
public class MixinBiome implements ForgeBiomeExt {
	public Biome getRiver() {
		Biome self = (Biome) (Object) this;
		return PatchworkBiomes.getDefaultRiver(self);
	}
}

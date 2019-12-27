package com.patchworkmc.mixin.extensions.shearing;

import net.minecraft.block.*;
import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Patches blocks to extend IShearable.
 *
 * @author SuperCoder79
 */
@Mixin({SeagrassBlock.class, VineBlock.class, LeavesBlock.class, DeadBushBlock.class, FernBlock.class, ReplaceableTallPlantBlock.class, CobwebBlock.class})
public class MixinShearableBlock implements IShearable {
}

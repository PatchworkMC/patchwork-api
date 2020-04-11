package net.patchworkmc.mixin.extensions.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.tag.BlockTags;

@Mixin(BlockTags.class)
public interface BlockTagsAccessor {
	@Accessor
	static int getLatestVersion() {
		throw new UnsupportedOperationException();
	}
}

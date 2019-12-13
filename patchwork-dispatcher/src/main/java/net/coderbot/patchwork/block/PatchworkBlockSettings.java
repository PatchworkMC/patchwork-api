package net.coderbot.patchwork.block;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.sound.BlockSoundGroup;

public class PatchworkBlockSettings {
	public static Block.Settings sounds(Block.Settings settings, BlockSoundGroup sounds) {
		return FabricBlockSettings.copyOf(settings).sounds(sounds).build();
	}

	public static Block.Settings lightLevel(Block.Settings settings, int level) {
		return FabricBlockSettings.copyOf(settings).lightLevel(level).build();
	}

	public static Block.Settings breakInstantly(Block.Settings settings) {
		return settings.strength(0.0F, 0.0F);
	}

	public static Block.Settings strength(Block.Settings settings, float strength) {
		return settings.strength(strength, strength);
	}

	public static Block.Settings ticksRandomly(Block.Settings settings) {
		return FabricBlockSettings.copyOf(settings).ticksRandomly().build();
	}

	public static Block.Settings hasDynamicBounds(Block.Settings settings) {
		return FabricBlockSettings.copyOf(settings).dynamicBounds().build();
	}

	public static Block.Settings dropsNothing(Block.Settings settings) {
		return FabricBlockSettings.copyOf(settings).dropsNothing().build();
	}
}

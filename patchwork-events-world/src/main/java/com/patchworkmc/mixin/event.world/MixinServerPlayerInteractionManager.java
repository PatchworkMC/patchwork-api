package com.patchworkmc.mixin.event.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.GameMode;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {
	@Shadow
	public ServerWorld world;
	@Shadow
	public ServerPlayerEntity player;
	@Shadow
	private GameMode gameMode;

	@Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
	private void hookBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
		boolean preCancelEvent = false;

		ItemStack itemstack = player.getMainHandStack();
		if (!itemstack.isEmpty() && !itemstack.getItem().canMine(world.getBlockState(pos), world, pos, player)) {
			preCancelEvent = true;
		}

		// method_21701 => canMine
		// Isn't the function really canNotMine?

		if (player.method_21701(world, pos, gameMode)) {
			preCancelEvent = true;
		}

		// Tell client the block is gone immediately then process events
		if (world.getBlockEntity(pos) == null) {
			player.networkHandler.sendPacket(new BlockUpdateS2CPacket(EmptyBlockView.INSTANCE, pos));
		}

		// Post the block break event
		BlockState state = world.getBlockState(pos);
		BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
		event.setCanceled(preCancelEvent);
		MinecraftForge.EVENT_BUS.post(event);

		// Handle if the event is canceled
		if (event.isCanceled()) {
			// Let the client know the block still exists
			player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos));

			// Update any block entity data for this block
			BlockEntity entity = world.getBlockEntity(pos);
			if (entity != null) {
				BlockEntityUpdateS2CPacket packet = entity.toUpdatePacket();

				if (packet != null) {
					player.networkHandler.sendPacket(packet);
				}
			}

			callback.setReturnValue(false);
		} else if(event.getExpToDrop() != 0) {
			// TODO: Drop experience
			throw new UnsupportedOperationException("Cannot drop exp from a BreakEvent yet");
		}
	}
}

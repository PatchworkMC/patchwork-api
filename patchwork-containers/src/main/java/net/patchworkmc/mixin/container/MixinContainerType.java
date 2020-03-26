package net.patchworkmc.mixin.container;

import java.lang.invoke.MethodHandles;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraftforge.fml.network.IContainerFactory;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.patchworkmc.api.container.PatchworkContainerFactory;
import net.patchworkmc.impl.container.PatchworkContainerType;

@Mixin(ContainerType.class)
public class MixinContainerType<T extends Container> implements PatchworkContainerType<T> {
	@Unique
	private PatchworkContainerFactory<T> patchworkFactory = null;

	@Override
	public MethodHandles.Lookup patchwork_getPrivateLookup() {
		return MethodHandles.lookup();
	}

	@Override
	public void patchwork_setContainerFactory(PatchworkContainerFactory<T> factory) {
		patchworkFactory = factory;
	}

	@Environment(EnvType.CLIENT)
	@Inject(method = "Lnet/minecraft/container/ContainerType;create(ILnet/minecraft/entity/player/PlayerInventory;)Lnet/minecraft/container/Container;",
			at = @At("HEAD"),
			cancellable = true)
	private void createCallback(int syncId, PlayerInventory playerInventory, CallbackInfoReturnable<T> callback) {
		if (patchworkFactory != null) {
			callback.setReturnValue(patchworkFactory.create(syncId, playerInventory));
		}
	}

	// forge method
	@SuppressWarnings("unchecked")
	public T create(int syncId, PlayerInventory playerInventory, PacketByteBuf extraData) {
		if (patchworkFactory instanceof IContainerFactory) {
			return ((IContainerFactory<T>) patchworkFactory).create(syncId, playerInventory, extraData);
		} else {
			return ((ContainerType<T>) (Object) this).create(syncId, playerInventory);
		}
	}
}

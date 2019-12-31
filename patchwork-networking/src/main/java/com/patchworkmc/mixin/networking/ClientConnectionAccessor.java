package com.patchworkmc.mixin.networking;

import io.netty.channel.Channel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.network.ClientConnection;

@Mixin(ClientConnection.class)
public interface ClientConnectionAccessor {
	@Accessor
	public Channel getChannel();
}

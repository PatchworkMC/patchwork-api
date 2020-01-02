package net.minecraftforge.fml.network;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSidedProvider;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.ThreadExecutor;

import com.patchworkmc.mixin.networking.accessor.ClientConnectionAccessor;
import com.patchworkmc.mixin.networking.accessor.ThreadExecutorAccessor;

public class NetworkEvent extends Event {
	private final PacketByteBuf payload;
	private final Supplier<Context> source;
	private final int loginIndex;

	// For EventBus
	public NetworkEvent() {
		super();
		this.payload = null;
		this.source = null;
		this.loginIndex = -1;
	}
	private NetworkEvent(final ICustomPacket<?> payload, final Supplier<Context> source) {
		this.payload = payload.getInternalData();
		this.source = source;
		this.loginIndex = payload.getIndex();
	}

	private NetworkEvent(final PacketByteBuf payload, final Supplier<Context> source, final int loginIndex) {
		this.payload = payload;
		this.source = source;
		this.loginIndex = loginIndex;
	}

	public NetworkEvent(final Supplier<Context> source) {
		this.source = source;
		this.payload = null;
		this.loginIndex = -1;
	}

	public PacketByteBuf getPayload() {
		return payload;
	}

	public Supplier<Context> getSource() {
		return source;
	}

	public int getLoginIndex() {
		return loginIndex;
	}

	public enum RegistrationChangeType {
		REGISTER, UNREGISTER;
	}

	public static class ServerCustomPayloadEvent extends NetworkEvent {
		// For EventBus
		public ServerCustomPayloadEvent() {
			super();
		}

		ServerCustomPayloadEvent(final ICustomPacket<?> payload, final Supplier<Context> source) {
			super(payload, source);
		}
	}

	public static class ClientCustomPayloadEvent extends NetworkEvent {
		// For EventBus
		public ClientCustomPayloadEvent() {
			super();
		}

		ClientCustomPayloadEvent(final ICustomPacket<?> payload, final Supplier<Context> source) {
			super(payload, source);
		}
	}

	public static class ServerCustomPayloadLoginEvent extends ServerCustomPayloadEvent {
		// For EventBus
		public ServerCustomPayloadLoginEvent() {
			super();
		}

		ServerCustomPayloadLoginEvent(ICustomPacket<?> payload, Supplier<Context> source) {
			super(payload, source);
		}
	}

	public static class ClientCustomPayloadLoginEvent extends ClientCustomPayloadEvent {
		// For EventBus
		public ClientCustomPayloadLoginEvent() {
			super();
		}

		ClientCustomPayloadLoginEvent(ICustomPacket<?> payload, Supplier<Context> source) {
			super(payload, source);
		}
	}

	public static class GatherLoginPayloadsEvent extends Event {
		private final List<NetworkRegistry.LoginPayload> collected;
		private final boolean isLocal;

		// For EventBus
		public GatherLoginPayloadsEvent() {
			this.collected = null;
			this.isLocal = false;
		}

		public GatherLoginPayloadsEvent(final List<NetworkRegistry.LoginPayload> loginPayloadList, boolean isLocal) {
			this.collected = loginPayloadList;
			this.isLocal = isLocal;
		}

		public void add(PacketByteBuf buffer, Identifier channelName, String context) {
			collected.add(new NetworkRegistry.LoginPayload(buffer, channelName, context));
		}

		public boolean isLocal() {
			return isLocal;
		}
	}

	public static class LoginPayloadEvent extends NetworkEvent {
		// For EventBus
		public LoginPayloadEvent() {
			super();
		}

		LoginPayloadEvent(final PacketByteBuf payload, final Supplier<Context> source, final int loginIndex) {
			super(payload, source, loginIndex);
		}
	}

	/**
	 * Fired when the channel registration (see minecraft custom channel documentation) changes. Note the payload
	 * is not exposed. This fires to the {@link Identifier} that owns the channel, when it's registration changes state.
	 *
	 * It seems plausible that this will fire multiple times for the same state, depending on what the server is doing.
	 * It just directly dispatches upon receipt.
	 */
	public static class ChannelRegistrationChangeEvent extends NetworkEvent {
		private final RegistrationChangeType changeType;

		// For EventBus
		public ChannelRegistrationChangeEvent() {
			super();
			this.changeType = null;
		}

		ChannelRegistrationChangeEvent(final Supplier<Context> source, RegistrationChangeType changeType) {
			super(source);
			this.changeType = changeType;
		}

		public RegistrationChangeType getRegistrationChangeType() {
			return this.changeType;
		}
	}

	/**
	 * Context for {@link NetworkEvent}
	 */
	public static class Context {
		/**
		 * The {@link ClientConnection} for this message.
		 */
		private final ClientConnection clientConnection;

		/**
		 * The {@link NetworkDirection} this message has been received on.
		 */
		private final NetworkDirection networkDirection;

		/**
		 * The packet dispatcher for this event. Sends back to the origin.
		 */
		private final PacketDispatcher packetDispatcher;
		private boolean packetHandled;

		Context(ClientConnection netHandler, NetworkDirection networkDirection, int index) {
			this(netHandler, networkDirection, new PacketDispatcher.NetworkManagerDispatcher(netHandler, index, networkDirection.reply()::buildPacket));
		}

		Context(ClientConnection clientConnection, NetworkDirection networkDirection, PacketDispatcher dispatcher) {
			this.clientConnection = clientConnection;
			this.networkDirection = networkDirection;
			this.packetDispatcher = dispatcher;
		}

		public NetworkDirection getDirection() {
			return networkDirection;
		}

		public PacketDispatcher getPacketDispatcher() {
			return packetDispatcher;
		}

		public <T> Attribute<T> attr(AttributeKey<T> key) {
			return ((ClientConnectionAccessor) clientConnection).patchwork$getChannel().attr(key);
		}

		public boolean getPacketHandled() {
			return packetHandled;
		}

		public void setPacketHandled(boolean packetHandled) {
			this.packetHandled = packetHandled;
		}

		public CompletableFuture<Void> enqueueWork(Runnable runnable) {
			ThreadExecutor<?> executor = LogicalSidedProvider.WORKQUEUE.get(getDirection().getReceptionSide());
			// Must check ourselves as Minecraft will sometimes delay tasks even when they are received on the client thread
			// Same logic as ThreadTaskExecutor#runImmediately without the join
			if (!executor.isOnThread()) {
				// Use the internal method so thread check isn't done twice
				return ((ThreadExecutorAccessor) executor).patchwork$executeFuture(runnable);
			} else {
				runnable.run();
				return CompletableFuture.completedFuture(null);
			}
		}

		/**
		 * When available, gets the sender for packets that are sent from a client to the server.
		 */
		@Nullable
		public ServerPlayerEntity getSender() {
			PacketListener listener = clientConnection.getPacketListener();
			if (listener instanceof ServerPlayNetworkHandler) {
				ServerPlayNetworkHandler netHandlerPlayServer = (ServerPlayNetworkHandler) listener;
				return netHandlerPlayServer.player;
			}
			return null;
		}

		public ClientConnection getNetworkManager() {
			return clientConnection;
		}
	}
}

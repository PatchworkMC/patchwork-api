package net.minecraftforge.fml.network;

import net.minecraft.util.Identifier;

/**
 * Wrapper for custom login packets. Transforms unnamed login channel messages into channels dispatched the same
 * as regular custom packets.
 */
// Patchwork: only use for Identifier for now
// TODO: don't use this class if its not nesssiary for anything else in this PR.
public class FMLLoginWrapper {
	//	private static final Logger LOGGER = LogManager.getLogger();
	//	// Patchwork: make WRAPPER public
	public static final Identifier WRAPPER = new Identifier("fml:loginwrapper");
	//	private EventNetworkChannel wrapperChannel;
	//
	//	FMLLoginWrapper() {
	//		wrapperChannel = NetworkRegistry.ChannelBuilder.named(FMLLoginWrapper.WRAPPER).
	//			clientAcceptedVersions(a->true).
	//			serverAcceptedVersions(a->true).
	//			networkProtocolVersion(()-> FMLNetworkConstants.NETVERSION)
	//			.eventNetworkChannel();
	//		wrapperChannel.addListener(this::wrapperReceived);
	//	}
	//
	//	private <T extends NetworkEvent> void wrapperReceived(final T packet) {
	//		final NetworkEvent.Context wrappedContext = packet.getSource().get();
	//		final PacketByteBuf payload = packet.getPayload();
	//		Identifier targetNetworkReceiver = FMLNetworkConstants.FML_HANDSHAKE_RESOURCE;
	//		PacketByteBuf data = null;
	//		if (payload != null) {
	//			targetNetworkReceiver = payload.readIdentifier();
	//			final int payloadLength = payload.readVarInt();
	//			data = new PacketByteBuf(payload.readBytes(payloadLength));
	//		}
	//		final int loginSequence = packet.getLoginIndex();
	//		LOGGER.debug(FMLHandshakeHandler.FMLHSMARKER, "Recieved login wrapper packet event for channel {} with index {}", targetNetworkReceiver, loginSequence);
	//		final NetworkEvent.Context context = new NetworkEvent.Context(wrappedContext.getNetworkManager(), wrappedContext.getDirection(), new PacketDispatcher((rl, buf) -> {
	//			LOGGER.debug(FMLHandshakeHandler.FMLHSMARKER, "Dispatching wrapped packet reply for channel {} with index {}", rl, loginSequence);
	//			wrappedContext.getPacketDispatcher().sendPacket(WRAPPER, this.wrapPacket(rl, buf));
	//		}));
	//		final NetworkEvent.LoginPayloadEvent loginPayloadEvent = new NetworkEvent.LoginPayloadEvent(data, () -> context, loginSequence);
	//		NetworkRegistry.findTarget(targetNetworkReceiver).ifPresent(ni -> {
	//			ni.dispatchLoginPacket(loginPayloadEvent);
	//			wrappedContext.setPacketHandled(context.getPacketHandled());
	//		});
	//	}
	//
	//	private PacketByteBuf wrapPacket(final Identifier identifier, final PacketByteBuf buf) {
	//		PacketByteBuf pb = new PacketByteBuf(Unpooled.buffer(buf.capacity()));
	//		pb.writeIdentifier(identifier);
	//		pb.writeVarInt(buf.readableBytes());
	//		pb.writeBytes(buf);
	//		return pb;
	//	}
	//
	//	void sendServerToClientLoginPacket(final Identifier identifier, final PacketByteBuf buffer, final int index, final ClientConnection manager) {
	//		PacketByteBuf pb = wrapPacket(identifier, buffer);
	//
	//		manager.send(NetworkDirection.LOGIN_TO_CLIENT.buildPacket(Pair.of(pb, index), WRAPPER).getThis());
	//	}
}

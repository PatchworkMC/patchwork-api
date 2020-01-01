package net.minecraftforge.fml.network;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

/**
 * Means to distribute packets in various ways
 *
 * @see net.minecraftforge.fml.network.simple.SimpleChannel#send(PacketTarget, Object)
 *
 * @param <T>
 */
public class PacketDistributor<T> {
	/**
	 * Send to the player specified in the {@link Supplier}
	 *
	 * <p>{@link #with(Supplier)} Player</p>
	 */
	public static final PacketDistributor<ServerPlayerEntity> PLAYER = new PacketDistributor<>(PacketDistributor::playerConsumer, NetworkDirection.PLAY_TO_CLIENT);
	/**
	 * Send to everyone in the dimension specified in the {@link Supplier}
	 *
	 * <p>{@link #with(Supplier)} DimensionType</p>
	 */
	public static final PacketDistributor<DimensionType> DIMENSION = new PacketDistributor<>(PacketDistributor::playerListDimConsumer, NetworkDirection.PLAY_TO_CLIENT);
	/**
	 * Send to everyone near the {@link TargetPoint} specified in the {@link Supplier}
	 *
	 * <p>{@link #with(Supplier)} TargetPoint</p>
	 */
	public static final PacketDistributor<TargetPoint> NEAR = new PacketDistributor<>(PacketDistributor::playerListPointConsumer, NetworkDirection.PLAY_TO_CLIENT);
	/**
	 * Send to everyone
	 *
	 * <p>{@link #noArg()}</p>
	 */
	public static final PacketDistributor<Void> ALL = new PacketDistributor<>(PacketDistributor::playerListAll, NetworkDirection.PLAY_TO_CLIENT);
	/**
	 * Send to the server (CLIENT to SERVER)
	 *
	 * <p>{@link #noArg()}</p>
	 */
	public static final PacketDistributor<Void> SERVER = new PacketDistributor<>(PacketDistributor::clientToServer, NetworkDirection.PLAY_TO_SERVER);
	/**
	 * Send to all tracking the {@link Entity} in the {@link Supplier}
	 *
	 * <p>{@link #with(Supplier)} Entity</p>
	 */
	public static final PacketDistributor<Entity> TRACKING_ENTITY = new PacketDistributor<>(PacketDistributor::trackingEntity, NetworkDirection.PLAY_TO_CLIENT);
	/**
	 * Send to all tracking the {@link Entity} and {@link PlayerEntity} in the {@link Supplier}
	 *
	 * <p>{@link #with(Supplier)} Entity</p>
	 */
	public static final PacketDistributor<Entity> TRACKING_ENTITY_AND_SELF = new PacketDistributor<>(PacketDistributor::trackingEntityAndSelf, NetworkDirection.PLAY_TO_CLIENT);
	/**
	 * Send to all tracking the {@link WorldChunk} in the {@link Supplier}
	 *
	 * <p>{@link #with(Supplier)} Chunk</p>
	 */
	public static final PacketDistributor<WorldChunk> TRACKING_CHUNK = new PacketDistributor<>(PacketDistributor::trackingChunk, NetworkDirection.PLAY_TO_CLIENT);
	/**
	 * Send to the supplied list of {@link ClientConnection} instances in the {@link Supplier}
	 *
	 * <p>{@link #with(Supplier)} List of {@link ClientConnection}</p>
	 */
	public static final PacketDistributor<List<ClientConnection>> NMLIST = new PacketDistributor<>(PacketDistributor::networkManagerList, NetworkDirection.PLAY_TO_CLIENT);
	private final BiFunction<PacketDistributor<T>, Supplier<T>, Consumer<Packet<?>>> functor;
	private final NetworkDirection direction;

	public PacketDistributor(BiFunction<PacketDistributor<T>, Supplier<T>, Consumer<Packet<?>>> functor, NetworkDirection direction) {
		this.functor = functor;
		this.direction = direction;
	}

	/**
	 * Apply the supplied value to the specific distributor to generate an instance for sending packets to.
	 * @param input The input to apply
	 * @return A curried instance
	 */
	public PacketTarget with(Supplier<T> input) {
		return new PacketTarget(functor.apply(this, input), this);
	}

	/**
	 * Apply a no argument value to a distributor to generate an instance for sending packets to.
	 *
	 * @see #ALL
	 * @see #SERVER
	 * @return A curried instance
	 */
	public PacketTarget noArg() {
		return new PacketTarget(functor.apply(this, () -> null), this);
	}

	private Consumer<Packet<?>> playerConsumer(final Supplier<ServerPlayerEntity> player) {
		return packet -> player.get().networkHandler.client.send(packet);
	}

	private Consumer<Packet<?>> playerListDimConsumer(final Supplier<DimensionType> dimensionType) {
		return packet -> getServer().getPlayerManager().sendToDimension(packet, dimensionType.get());
	}

	private Consumer<Packet<?>> playerListAll(final Supplier<Void> ignored) {
		return packet -> getServer().getPlayerManager().sendToAll(packet);
	}

	private Consumer<Packet<?>> clientToServer(final Supplier<Void> ignored) {
		return packet -> MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
	}

	private Consumer<Packet<?>> playerListPointConsumer(final Supplier<TargetPoint> point) {
		return packet -> {
			final TargetPoint tp = point.get();
			getServer().getPlayerManager().sendToAround(tp.excluded, tp.x, tp.y, tp.z, tp.r2, tp.dim, packet);
		};
	}

	private Consumer<Packet<?>> trackingEntity(final Supplier<Entity> entitySupplier) {
		return packet -> {
			final Entity entity = entitySupplier.get();
			((ServerChunkManager) entity.getEntityWorld().getChunkManager()).sendToOtherNearbyPlayers(entity, packet);
		};
	}

	private Consumer<Packet<?>> trackingEntityAndSelf(final Supplier<Entity> entitySupplier) {
		return packet -> {
			final Entity entity = entitySupplier.get();
			((ServerChunkManager) entity.getEntityWorld().getChunkManager()).sendToNearbyPlayers(entity, packet);
		};
	}

	private Consumer<Packet<?>> trackingChunk(final Supplier<WorldChunk> worldChunk) {
		return packet -> {
			final WorldChunk chunk = worldChunk.get();
			((ServerChunkManager) chunk.getWorld().getChunkManager()).threadedAnvilChunkStorage.getPlayersWatchingChunk(chunk.getPos(), false)
				.forEach(e -> e.networkHandler.sendPacket(packet));
		};
	}

	private Consumer<Packet<?>> networkManagerList(final Supplier<List<ClientConnection>> connections) {
		return packet -> connections.get().forEach(connection -> connection.send(packet));
	}

	private MinecraftServer getServer() {
		return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
	}

	public static final class TargetPoint {

		private final ServerPlayerEntity excluded;
		private final double x;
		private final double y;
		private final double z;
		private final double r2;
		private final DimensionType dim;

		/**
		 * A target point with excluded entity
		 *
		 * @param excluded the {@link ServerPlayerEntity} to exclude
		 * @param x X
		 * @param y Y
		 * @param z Z
		 * @param r2 Radius
		 * @param dim DimensionType
		 */
		public TargetPoint(final ServerPlayerEntity excluded, final double x, final double y, final double z, final double r2, final DimensionType dim) {
			this.excluded = excluded;
			this.x = x;
			this.y = y;
			this.z = z;
			this.r2 = r2;
			this.dim = dim;
		}

		/**
		 * A target point that does not exclude any entities
		 * @param x X
		 * @param y Y
		 * @param z Z
		 * @param r2 Radius
		 * @param dim DimensionType
		 */
		public TargetPoint(final double x, final double y, final double z, final double r2, final DimensionType dim) {
			this.excluded = null;
			this.x = x;
			this.y = y;
			this.z = z;
			this.r2 = r2;
			this.dim = dim;
		}

		/**
		 * Helper to build a target point that does not exclude any entities
		 * @param x X
		 * @param y Y
		 * @param z Z
		 * @param r2 Radius
		 * @param dim DimensionType
		 * @return A TargetPoint supplier
		 */
		public static Supplier<TargetPoint> p(double x, double y, double z, double r2, DimensionType dim) {
			TargetPoint tp = new TargetPoint(x, y, z, r2, dim);
			return () -> tp;
		}

	}

	/**
	 * A Distributor curried with a specific value instance, for actual dispatch
	 *
	 * @see net.minecraftforge.fml.network.simple.SimpleChannel#send(PacketTarget, Object)
	 *
	 */
	public static class PacketTarget {
		private final Consumer<Packet<?>> packetConsumer;
		private final NetworkDirection direction;

		PacketTarget(final Consumer<Packet<?>> packetConsumer, final PacketDistributor<?> distributor) {
			this.packetConsumer = packetConsumer;
			this.direction = distributor.direction;
		}

		public void send(Packet<?> packet) {
			this.packetConsumer.accept(packet);
		}

		public NetworkDirection getDirection() {
			return this.direction;
		}

	}
}

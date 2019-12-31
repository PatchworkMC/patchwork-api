package net.minecraftforge.fml;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import com.patchworkmc.impl.fml.PatchworkFML;

public enum LogicalSidedProvider {
	WORKQUEUE((c) -> c.get(), (s) -> s.get()),
	INSTANCE((c) -> c.get(), (s) -> s.get()),
	CLIENTWORLD((c) -> Optional.<World>of(c.get().world), (s) -> Optional.<World>empty());
	private static Supplier<MinecraftClient> client;
	private static Supplier<MinecraftServer> server;

	// Patchwork: since the client never changes we can just set it directly
	static {
		if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) {
			client = () -> (MinecraftClient) FabricLoader.getInstance().getGameInstance();
		}
	}

	private final Function<Supplier<MinecraftClient>, ?> clientSide;
	private final Function<Supplier<MinecraftServer>, ?> serverSide;

	LogicalSidedProvider(Function<Supplier<MinecraftClient>, ?> clientSide, Function<Supplier<MinecraftServer>, ?> serverSide) {
		this.clientSide = clientSide;
		this.serverSide = serverSide;
	}

	/**
	 * Called by callbacks registered in {@link PatchworkFML}
	 */
	public static void setServer(Supplier<MinecraftServer> server) {

		LogicalSidedProvider.server = server;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(final LogicalSide side) {
		return (T) (side == LogicalSide.CLIENT ? clientSide.apply(client) : serverSide.apply(server));
	}
}

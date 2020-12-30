/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2020, 2019-2020
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.server;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.loading.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

public class ServerLifecycleHooks {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Marker SERVERHOOKS = MarkerManager.getMarker("SERVERHOOKS");
	private static final WorldSavePath SERVERCONFIG = new WorldSavePath("serverconfig");
	private static volatile CountDownLatch exitLatch = null;
	private static MinecraftServer currentServer;

	private static Path getServerConfigPath(final MinecraftServer server) {
		final Path serverConfig = server.getSavePath(SERVERCONFIG);
		FileUtils.getOrCreateDirectory(serverConfig, "serverconfig");
		return serverConfig;
	}

	public static boolean handleServerAboutToStart(final MinecraftServer server) {
		currentServer = server;
		// TODO: NETWORKING
		//currentServer.getServerMetadata().setForgeData(new FMLStatusPing()); //gathers NetworkRegistry data
		// on the dedi server we need to force the stuff to setup properly
		LogicalSidedProvider.setServer(() -> server);
		ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
		return !MinecraftForge.EVENT_BUS.post(new FMLServerAboutToStartEvent(server));
	}

	public static boolean handleServerStarting(final MinecraftServer server) {
		// TODO: languagehook
		//DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> LanguageHook.loadLanguagesOnServer(server));
		return !MinecraftForge.EVENT_BUS.post(new FMLServerStartingEvent(server));
	}

	public static void handleServerStarted(final MinecraftServer server) {
		MinecraftForge.EVENT_BUS.post(new FMLServerStartedEvent(server));
		//allowLogins.set(true);
	}

	public static void handleServerStopping(final MinecraftServer server) {
		//allowLogins.set(false);
		MinecraftForge.EVENT_BUS.post(new FMLServerStoppingEvent(server));
	}

	public static void expectServerStopped() {
		exitLatch = new CountDownLatch(1);
	}

	public static void handleServerStopped(final MinecraftServer server) {
		// TODO: freezing registries
		/*if (!server.isDedicated()) {
			GameData.revertToFrozen();
		}*/
		MinecraftForge.EVENT_BUS.post(new FMLServerStoppedEvent(server));
		currentServer = null;
		LogicalSidedProvider.setServer(null);
		CountDownLatch latch = exitLatch;

		if (latch != null) {
			latch.countDown();
			exitLatch = null;
		}

		ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
	}

	public static MinecraftServer getCurrentServer() {
		return currentServer;
	}

	/* TODO NETWORKING
	private static final AtomicBoolean allowLogins = new AtomicBoolean(false);

	public static boolean handleServerLogin(final HandshakeC2SPacket packet, final ClientConnection manager) {
		if (!allowLogins.get()) {
			LiteralText text = new LiteralText("Server is still starting! Please wait before reconnecting.");
			LOGGER.info(SERVERHOOKS, "Disconnecting Player (server is still starting): {}", text.asString());
			manager.send(new LoginDisconnectS2CPacket(text));
			manager.disconnect(text);
			return false;
		}

		if (packet.getIntendedState() == NetworkState.LOGIN) {
			final ConnectionType connectionType = ConnectionType.forVersionFlag(packet.getFMLVersion());
			final int versionNumber = connectionType.getFMLVersionNumber(packet.getFMLVersion());

			if (connectionType == ConnectionType.MODDED && versionNumber != FMLNetworkConstants.FMLNETVERSION) {
				rejectConnection(manager, connectionType, "This modded server is not network compatible with your modded client. Please verify your Forge version closely matches the server. Got net version " + versionNumber + " this server is net version " + FMLNetworkConstants.FMLNETVERSION);
				return false;
			}

			if (connectionType == ConnectionType.VANILLA && !NetworkRegistry.acceptsVanillaClientConnections()) {
				rejectConnection(manager, connectionType, "This server has mods that require Forge to be installed on the client. Contact your server admin for more details.");
				return false;
			}
		}

		if (packet.getIntendedState() == NetworkState.STATUS) {
			return true;
		}

		NetworkHooks.registerServerLoginChannel(manager, packet);
		VanillaConnectionNetworkFilter.injectIfNecessary(manager);

		return true;
	}

	private static void rejectConnection(final ClientConnection manager, ConnectionType type, String message) {
		manager.setState(NetworkState.LOGIN);
		LOGGER.info(SERVERHOOKS, "Disconnecting {} connection attempt: {}", type, message);
		LiteralText text = new LiteralText(message);
		manager.send(new LoginDisconnectS2CPacket(text));
		manager.disconnect(text);
	}*/

	public static void handleExit(int retVal) {
		System.exit(retVal);
	}

	/*//INTERNAL MODDERS DO NOT USE
	@Deprecated
	public static ResourcePackLoader.IPackInfoFinder buildPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ResourcePackProfile> packSetter) {
		return (packList, factory) -> serverPackFinder(modResourcePacks, packSetter, packList, factory);
	}

	private static void serverPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ResourcePackProfile> packSetter, Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
		for (Map.Entry<ModFile, ? extends ModFileResourcePack> e : modResourcePacks.entrySet()) {
			IModInfo mod = e.getKey().getModInfos().get(0);

			if (Objects.equals(mod.getModId(), "minecraft")) {
				continue; // skip the minecraft "mod"
			}

			final String name = "mod:" + mod.getModId();
			final ResourcePackProfile packInfo = ResourcePackProfile.of(name, true, e::getValue, factory, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.field_25347);

			if (packInfo == null) {
				// Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
				ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", e.getKey()));
				continue;
			}

			packSetter.accept(e.getValue(), packInfo);
			LOGGER.debug(CORE, "Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
			consumer.accept(packInfo);
		}
	}*/
}

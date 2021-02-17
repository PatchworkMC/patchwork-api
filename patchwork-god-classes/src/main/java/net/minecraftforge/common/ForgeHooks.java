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

package net.minecraftforge.common;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.apache.commons.lang3.NotImplementedException;

import net.minecraft.advancement.Advancement;
import net.minecraft.block.BlockState;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.patchworkmc.impl.event.entity.EntityEvents;
import net.patchworkmc.impl.extensions.block.BlockHarvestManager;
import net.patchworkmc.impl.loot.LootHooks;
import net.patchworkmc.annotations.Stubbed;

/**
 * A stubbed out copy of Forge's ForgeHooks, intended for use by Forge mods only.
 * For methods that you are implementing, don't keep implementation details here.
 * Elements should be thin wrappers around methods in other modules.
 * Do not depend on this class in other modules.
 */
public class ForgeHooks {
	//static final Pattern URL_PATTERN = Pattern.compile(
	//	//         schema                          ipv4            OR        namespace                 port     path         ends
	//	//   |-----------------|        |-------------------------|  |-------------------------|    |---------| |--|   |---------------|
	//	"((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))",
	//	Pattern.CASE_INSENSITIVE);
	//private static final Logger LOGGER = LogManager.getLogger();
	//private static final Marker FORGEHOOKS = MarkerManager.getMarker("FORGEHOOKS");
	//private static final DummyBlockReader DUMMY_WORLD = new DummyBlockReader();
	//private static final Map<TrackedDataHandler<?>, DataSerializerEntry> serializerEntries = GameData.getSerializerMap();
	//private static final Map<IRegistryDelegate<Item>, Integer> VANILLA_BURNS = new HashMap<>();
	//private static boolean toolInit = false;
	//private static ThreadLocal<PlayerEntity> craftingPlayer = new ThreadLocal<PlayerEntity>();
	@SuppressWarnings({ "unused" })
	private static ThreadLocal<?> lootContext = LootHooks.lootContext;
	//private static TriConsumer<Block, ToolType, Integer> blockToolSetter;

	@Stubbed
	public static boolean canContinueUsing(@NotNull ItemStack from, @NotNull ItemStack to) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static boolean canHarvestBlock(@NotNull BlockState state, @NotNull PlayerEntity player, @NotNull BlockView world, @NotNull BlockPos pos) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	/*
	@Stubbed
	public static boolean canToolHarvestBlock(ViewableWorld world, BlockPos pos, @NotNull ItemStack stack) {
		throw new NotImplementedException("ForgeHooks stub");
	} */

	/*
	@Stubbed
	public static boolean isToolEffective(ViewableWorld world, BlockPos pos, @NotNull ItemStack stack) {
		throw new NotImplementedException("ForgeHooks stub");
	} */

	@Stubbed
	static void initTools() {
		throw new NotImplementedException("ForgeHooks stub");
	}

	/**
	 * Called when a player uses 'pick block', calls new Entity and Block hooks.
	 */
	@Stubbed
	public static boolean onPickBlock(HitResult target, PlayerEntity player, World world) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static void onDifficultyChange(Difficulty difficulty, Difficulty oldDifficulty) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	//Optifine Helper Functions u.u, these are here specifically for Optifine
	//Note: When using Optifine, these methods are invoked using reflection, which
	//incurs a major performance penalty.
	public static void onLivingSetAttackTarget(LivingEntity entity, LivingEntity target) {
		EntityEvents.onLivingSetAttackTarget(entity, target);
	}

	public static boolean onLivingUpdate(LivingEntity entity) {
		return EntityEvents.onLivingUpdateEvent(entity);
	}

	// TODO: forge calls the equivilant to this in LivingEntity, but patchwork only calls the equivilant to onPlayerAttack
	public static boolean onLivingAttack(LivingEntity entity, DamageSource src, float amount) {
		return entity instanceof PlayerEntity || onPlayerAttack(entity, src, amount);
	}

	public static boolean onPlayerAttack(LivingEntity entity, DamageSource src, float amount) {
		return !EntityEvents.onLivingAttack(entity, src, amount);
	}

	/*
	@Stubbed
	public static LivingKnockBackEvent onLivingKnockBack(LivingEntity target, Entity attacker, float strength, double ratioX, double ratioZ) {
		throw new NotImplementedException("ForgeHooks stub");
	} */

	public static float onLivingHurt(LivingEntity entity, DamageSource src, float amount) {
		return EntityEvents.onLivingHurt(entity, src, amount);
	}

	public static float onLivingDamage(LivingEntity entity, DamageSource src, float amount) {
		return EntityEvents.onLivingDamage(entity, src, amount);
	}

	public static boolean onLivingDeath(LivingEntity entity, DamageSource src) {
		return EntityEvents.onLivingDeath(entity, src);
	}

	public static boolean onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) {
		return EntityEvents.onLivingDrops(entity, source, drops, lootingLevel, recentlyHit);
	}

	@Nullable
	public static float[] onLivingFall(LivingEntity entity, float distance, float damageMultiplier) {
		return EntityEvents.onLivingFall(entity, distance, damageMultiplier);
	}

	@Stubbed
	public static int getLootingLevel(Entity target, @Nullable Entity killer, DamageSource cause) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static int getLootingLevel(LivingEntity target, DamageSource cause, int level) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static double getPlayerVisibilityDistance(PlayerEntity player, double xzDistance, double maxXZDistance) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static boolean isLivingOnLadder(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static void onLivingJump(LivingEntity entity) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Nullable
	public static ItemEntity onPlayerTossEvent(@NotNull PlayerEntity player, @NotNull ItemStack item, boolean includeName) {
		// EntityEvents.onPlayerTossEvent is called through an Inject mixin into PlayerEntity.dropItem
		return player.dropItem(item, false, includeName);
	}

	@Stubbed
	@Nullable
	public static Text onServerChatEvent(ServerPlayNetworkHandler net, String raw, Text comp) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	public static Text newChatWithLinks(String string) {
		return newChatWithLinks(string, true);
	}

	@Stubbed
	public static Text newChatWithLinks(String string, boolean allowMissingHeader) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	public static int onBlockBreakEvent(World world, GameMode gameType, ServerPlayerEntity entityPlayer, BlockPos pos) {
		return BlockHarvestManager.onBlockBreakEvent(world, gameType, entityPlayer, pos);
	}

	@Stubbed
	public static ActionResult onPlaceItemIntoWorld(@NotNull ItemUsageContext context) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	public static boolean onAnvilChange(AnvilScreenHandler container, @NotNull ItemStack left, @NotNull ItemStack right, Inventory outputSlot, String name, int baseCost) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static float onAnvilRepair(PlayerEntity player, @NotNull ItemStack output, @NotNull ItemStack left, @NotNull ItemStack right) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static PlayerEntity getCraftingPlayer() {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static void setCraftingPlayer(PlayerEntity player) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	@NotNull
	public static ItemStack getContainerItem(@NotNull ItemStack stack) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	public static boolean onPlayerAttackTarget(PlayerEntity player, Entity target) {
		return EntityEvents.attackEntity(player, target);
	}

	public static boolean onTravelToDimension(Entity entity, DimensionType dimensionType) {
		return EntityEvents.onTravelToDimension(entity, dimensionType);
	}

	public static ActionResult onInteractEntityAt(PlayerEntity player, Entity entity, HitResult ray, Hand hand) {
		return EntityEvents.onInteractEntityAt(player, entity, ray, hand);
	}

	public static ActionResult onInteractEntityAt(PlayerEntity player, Entity entity, Vec3d vec3d, Hand hand) {
		return EntityEvents.onInteractEntityAt(player, entity, vec3d, hand);
	}

	public static ActionResult onInteractEntity(PlayerEntity player, Entity entity, Hand hand) {
		return EntityEvents.onInteractEntity(player, entity, hand);
	}

	public static ActionResult onItemRightClick(PlayerEntity player, Hand hand) {
		PlayerInteractEvent.RightClickItem event = EntityEvents.onItemRightClick(player, hand);

		return event.isCanceled() ? event.getCancellationResult() : null;
	}

	public static PlayerInteractEvent.LeftClickBlock onLeftClickBlock(PlayerEntity player, BlockPos pos, Direction face) {
		return EntityEvents.onBlockLeftClick(player, pos, face);
	}

	public static PlayerInteractEvent.RightClickBlock onRightClickBlock(PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
		return EntityEvents.onBlockRightClick(player, hand, pos, face);
	}

	public static void onEmptyClick(PlayerEntity player, Hand hand) {
		EntityEvents.onEmptyRightClick(player, hand);
	}

	public static void onEmptyLeftClick(PlayerEntity player) {
		EntityEvents.onEmptyLeftClick(player);
	}

	//private static LootTableContext getLootTableContext() {
	//	LootTableContext ctx = lootContext.get().peek();
	//
	//	if (ctx == null) {
	//		throw new JsonParseException("Invalid call stack, could not grab json context!"); // Should I throw this? Do we care about custom deserializers outside the manager?
	//	}
	//
	//	return ctx;
	//}

	@Nullable
	public static LootTable loadLootTable(Gson gson, Identifier name, JsonObject data, boolean custom, LootManager lootTableManager) {
		return LootHooks.loadLootTable(gson, name, data, custom, lootTableManager);
	}

	/*
	@Stubbed
	public static FluidAttributes createVanillaFluidAttributes(Fluid fluid) {
		throw new NotImplementedException("ForgeHooks stub");
	} */

	public static String readPoolName(JsonObject json) {
		return LootHooks.readPoolName(json);
	}

	@Stubbed
	public static String readLootEntryName(JsonObject json, String type) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static boolean onCropsGrowPre(World worldIn, BlockPos pos, BlockState state, boolean def) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static void onCropsGrowPost(World worldIn, BlockPos pos, BlockState state) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	/*
	@Stubbed
	@Nullable
	public static CriticalHitEvent getCriticalHit(PlayerEntity player, Entity target, boolean vanillaCritical, float damageModifier) {
		throw new NotImplementedException("ForgeHooks stub");
	} */

	@Stubbed
	public static void onAdvancement(ServerPlayerEntity player, Advancement advancement) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	/**
	 * Used as the default implementation of {@link Item#getCreatorModId}. Call that method instead.
	 */
	@Stubbed
	@Nullable
	public static String getDefaultCreatorModId(@NotNull ItemStack itemStack) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	public static boolean onFarmlandTrample(World world, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
		return BlockHarvestManager.onFarmlandTrample(world, pos, state, fallDistance, entity);
	}

	//Internal use only Modders, this is specifically hidden from you, as you shouldn't be editing other people's blocks.
	//public static void setBlockToolSetter(TriConsumer<Block, ToolType, Integer> setter) {
	//	blockToolSetter = setter;
	//}

	@Stubbed
	@SuppressWarnings("unchecked")
	private static <T, E> T getPrivateValue(Class<? super E> classToAccess, @Nullable E instance, int fieldIndex) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static int onNoteChange(World world, BlockPos pos, BlockState state, int old, int _new) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	// yes this is a direct copy of a forge method for once
	public static int canEntitySpawn(MobEntity entity, WorldAccess world, double x, double y, double z, MobSpawnerLogic spawner, SpawnReason spawnReason) {
		Event.Result res = ForgeEventFactory.canEntitySpawn(entity, world, x, y, z, null, spawnReason);
		return res == Event.Result.DEFAULT ? 0 : res == Event.Result.DENY ? -1 : 1;
	}

	@Stubbed
	public static <T> void deserializeTagAdditions(Tag.Builder<T> builder, Function<Identifier, Optional<T>> valueGetter, JsonObject json) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	@Nullable
	public static TrackedDataHandler<?> getSerializer(int id, Int2ObjectBiMap<TrackedDataHandler<?>> vanilla) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static int getSerializerId(TrackedDataHandler<?> serializer, Int2ObjectBiMap<TrackedDataHandler<?>> vanilla) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	//private static final ForgeRegistry<DataSerializerEntry> serializerRegistry = (ForgeRegistry<DataSerializerEntry>) ForgeRegistries.DATA_SERIALIZERS;
	// Do not reimplement this ^ it introduces a chicken-egg scenario by classloading registries during bootstrap

	@Stubbed
	public static boolean canEntityDestroy(World world, BlockPos pos, LivingEntity entity) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	/**
	 * Gets the burn time of this itemstack.
	 */
	@Stubbed
	public static int getBurnTime(ItemStack stack) {
		throw new NotImplementedException("ForgeHooks stub");
	}

	@Stubbed
	public static synchronized void updateBurns() {
		throw new NotImplementedException("ForgeHooks stub");
	}

	// Need to have the class here to make some mod hacks work
	public static class LootTableContext extends LootHooks.LootTableContext {
		private LootTableContext(Identifier name, boolean custom) {
			super(name, custom);
		}
	}

	//private static class DummyBlockReader implements BlockView {
	//
	//	@Override
	//	public BlockEntity getBlockEntity(BlockPos pos) {
	//		return null;
	//	}
	//
	//	@Override
	//	public BlockState getBlockState(BlockPos pos) {
	//		return Blocks.AIR.getDefaultState();
	//	}
	//
	//	@Override
	//	public FluidState getFluidState(BlockPos pos) {
	//		return Fluids.EMPTY.getDefaultState();
	//	}
	//
	//}

	//private static class OptionalTagEntry<T> extends Tag.TagEntry<T> {
	//	private Tag<T> resolvedTag = null;
	//
	//	OptionalTagEntry(Identifier referent) {
	//		super(referent);
	//	}
	//
	//	@Override
	//	public boolean applyTagGetter(@NotNull Function<Identifier, Tag<T>> resolver) {
	//		if (this.resolvedTag == null) {
	//			this.resolvedTag = resolver.apply(this.getId());
	//		}
	//		return true; // never fail if resolver returns null
	//	}
	//
	//	@Override
	//	public void build(@NotNull Collection<T> items) {
	//		if (this.resolvedTag != null) {
	//			items.addAll(this.resolvedTag.values());
	//		}
	//	}
	//}
}


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

package net.minecraftforge.common.extensions;

import java.util.Set;

import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.patchworkmc.annotations.Stubbed;
import net.patchworkmc.impl.capability.IForgeItemStackDuck;

/*
 * Extension added to ItemStack that bounces to ItemSack sensitive Item methods. Typically this is just for convince.
 */
public interface IForgeItemStack extends IForgeItemStackDuck {
	// Helpers for accessing Item data
	default ItemStack getStack() {
		return (ItemStack) (Object) this;
	}

	default IForgeItem patchwork$getForgeItem() {
		return (IForgeItem) this.getStack().getItem();
	}

	// this doesn't exist here for some reason but our mass asm relies on it
	default int patchwork$getItemStackLimit() {
		return patchwork$getForgeItem().getItemStackLimit(getStack());
	}

	/**
	 * ItemStack sensitive version of getContainerItem. Returns a full ItemStack
	 * instance of the result.
	 *
	 * @param itemStack The current ItemStack
	 * @return The resulting ItemStack
	 */
	@Stubbed
	default ItemStack getContainerItem() {
		return patchwork$getForgeItem().getContainerItem(getStack());
	}

	/**
	 * ItemStack sensitive version of hasContainerItem
	 *
	 * @param stack The current item stack
	 * @return True if this item has a 'container'
	 */
	@Stubbed
	default boolean hasContainerItem() {
		return patchwork$getForgeItem().hasContainerItem(getStack());
	}

	/**
	 * @return the fuel burn time for this itemStack in a furnace. Return 0 to make
	 * it not act as a fuel. Return -1 to let the default vanilla logic
	 * decide.
	 */
	@Stubbed
	default int getBurnTime() {
		return patchwork$getForgeItem().getBurnTime(getStack());
	}

	/**
	 * Queries the harvest level of this item stack for the specified tool class,
	 * Returns -1 if this tool is not of the specified type
	 *
	 * @param tool   the tool type of the item
	 * @param player The player trying to harvest the given blockstate
	 * @param state  The block to harvest
	 * @return Harvest level, or -1 if not the specified tool type.
	 */
	@Stubbed
	default int getHarvestLevel(ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState state) {
		return patchwork$getForgeItem().getHarvestLevel(getStack(), tool, player, state);
	}

	// only callsite in forge is an uncalled method in ForgeHooks
	default Set<ToolType> getToolTypes() {
		return patchwork$getForgeItem().getToolTypes(getStack());
	}

	@Stubbed
	default ActionResult onItemUseFirst(ItemUsageContext context) {
		PlayerEntity entityplayer = context.getPlayer();
		BlockPos blockpos = context.getBlockPos();
		CachedBlockPosition blockworldstate = new CachedBlockPosition(context.getWorld(), blockpos, false);

		if (entityplayer != null && !entityplayer.abilities.allowModifyWorld && !getStack().canPlaceOn(context.getWorld().getTagManager(), blockworldstate)) {
			return ActionResult.PASS;
		} else {
			Item item = getStack().getItem();
			ActionResult enumactionresult = patchwork$getForgeItem().onItemUseFirst(getStack(), context);

			if (entityplayer != null && enumactionresult.isAccepted()) {
				entityplayer.incrementStat(Stats.USED.getOrCreateStat(item));
			}

			return enumactionresult;
		}
	}

	default CompoundTag serializeNBT() {
		CompoundTag ret = new CompoundTag();
		getStack().toTag(ret);
		return ret;
	}

	/**
	 * Called before a block is broken. Return true to prevent default block
	 * harvesting.
	 * <p>
	 * Note: In SMP, this is called on both client and server sides!
	 *
	 * @param pos    Block's position in world
	 * @param player The Player that is wielding the item
	 * @return True to prevent harvesting, false to continue as normal
	 */
	@Stubbed
	default boolean onBlockStartBreak(BlockPos pos, PlayerEntity player) {
		return !getStack().isEmpty() && patchwork$getForgeItem().onBlockStartBreak(getStack(), pos, player);
	}

	/**
	 * Called when the player is mining a block and the item in his hand changes.
	 * Allows to not reset blockbreaking if only NBT or similar changes.
	 *
	 * @param newStack The new stack
	 * @return True to reset block break progress
	 */
	@Stubbed
	default boolean shouldCauseBlockBreakReset(ItemStack newStack) {
		return patchwork$getForgeItem().shouldCauseBlockBreakReset(getStack(), newStack);
	}

	/**
	 * Checks whether an item can be enchanted with a certain enchantment. This
	 * applies specifically to enchanting an item in the enchanting table and is
	 * called when retrieving the list of possible enchantments for an item.
	 * Enchantments may additionally (or exclusively) be doing their own checks in
	 * {@link net.minecraft.enchantment.Enchantment#canApplyAtEnchantingTable(ItemStack)};
	 * check the individual implementation for reference. By default this will check
	 * if the enchantment type is valid for this item type.
	 *
	 * @param stack       the item stack to be enchanted
	 * @param enchantment the enchantment to be applied
	 * @return true if the enchantment can be applied to this item
	 */
	@Stubbed
	default boolean canApplyAtEnchantingTable(Enchantment enchantment) {
		return patchwork$getForgeItem().canApplyAtEnchantingTable(getStack(), enchantment);
	}

	/**
	 * ItemStack sensitive version of getItemEnchantability
	 *
	 * @return the item echantability value
	 */
	@Stubbed
	default int getItemEnchantability() {
		return patchwork$getForgeItem().getItemEnchantability(getStack());
	}

	/**
	 * Override this to set a non-default armor slot for an ItemStack, but <em>do
	 * not use this to get the armor slot of said stack; for that, use
	 * {@link net.minecraft.entity.EntityLiving#getSlotForItemStack(ItemStack)}.</em>
	 *
	 * @return the armor slot of the ItemStack, or {@code null} to let the default
	 * vanilla logic as per {@code EntityLiving.getSlotForItemStack(stack)}
	 * decide
	 */
	@Nullable
	@Stubbed
	default EquipmentSlot getEquipmentSlot() {
		return patchwork$getForgeItem().getEquipmentSlot(getStack());
	}

	/**
	 * Can this Item disable a shield
	 *
	 * @param shield   The shield in question
	 * @param entity   The EntityLivingBase holding the shield
	 * @param attacker The EntityLivingBase holding the ItemStack
	 * @retrun True if this ItemStack can disable the shield in question.
	 */
	default boolean canDisableShield(ItemStack shield, LivingEntity entity, LivingEntity attacker) {
		return patchwork$getForgeItem().canDisableShield(getStack(), shield, entity, attacker);
	}

	/**
	 * Is this Item a shield
	 *
	 * @param entity The Entity holding the ItemStack
	 * @return True if the ItemStack is considered a shield
	 */
	@Stubbed
	default boolean isShield(@Nullable LivingEntity entity) {
		return patchwork$getForgeItem().isShield(getStack(), entity);
	}

	/**
	 * Called when a entity tries to play the 'swing' animation.
	 *
	 * @param entity The entity swinging the item.
	 * @return True to cancel any further processing by EntityLiving
	 */
	@Stubbed
	default boolean onEntitySwing(LivingEntity entity) {
		return patchwork$getForgeItem().onEntitySwing(getStack(), entity);
	}

	/**
	 * Called each tick while using an item.
	 *
	 * @param player The Player using the item
	 * @param count  The amount of time in tick the item has been used for
	 *               continuously
	 */
	@Stubbed
	default void onUsingTick(LivingEntity player, int count) {
		patchwork$getForgeItem().onUsingTick(getStack(), player, count);
	}

	/**
	 * Retrieves the normal 'lifespan' of this item when it is dropped on the ground
	 * as a EntityItem. This is in ticks, standard result is 6000, or 5 mins.
	 *
	 * @param world The world the entity is in
	 * @return The normal lifespan in ticks.
	 */
	default int getEntityLifespan(World world) {
		return patchwork$getForgeItem().getEntityLifespan(getStack(), world);
	}

	/**
	 * Called by the default implemetation of EntityItem's onUpdate method, allowing
	 * for cleaner control over the update of the item without having to write a
	 * subclass.
	 *
	 * @param entity The entity Item
	 * @return Return true to skip any further update code.
	 */
	@Stubbed
	default boolean onEntityItemUpdate(ItemEntity entity) {
		return patchwork$getForgeItem().onEntityItemUpdate(getStack(), entity);
	}

	/**
	 * Determines the amount of durability the mending enchantment
	 * will repair, on average, per point of experience.
	 */
	@Stubbed
	default float getXpRepairRatio() {
		return patchwork$getForgeItem().getXpRepairRatio(getStack());
	}

	/**
	 * Called to tick armor in the armor slot. Override to do something
	 */
	@Stubbed
	default void onArmorTick(World world, PlayerEntity player) {
		patchwork$getForgeItem().onArmorTick(getStack(), world, player);
	}

	/**
	 * Called every tick from {@link EntityHorse#onUpdate()} on the item in the
	 * armor slot.
	 *
	 * @param world the world the horse is in
	 * @param horse the horse wearing this armor
	 */
	@Stubbed
	default void onHorseArmorTick(World world, MobEntity horse) {
		patchwork$getForgeItem().onHorseArmorTick(getStack(), world, horse);
	}

	/**
	 * Determines if the specific ItemStack can be placed in the specified armor
	 * slot, for the entity.
	 *
	 * @param armorType Armor slot to be verified.
	 * @param entity    The entity trying to equip the armor
	 * @return True if the given ItemStack can be inserted in the slot
	 */
	@Stubbed
	default boolean canEquip(EquipmentSlot armorType, Entity entity) {
		return patchwork$getForgeItem().canEquip(getStack(), armorType, entity);
	}

	/**
	 * Allow or forbid the specific book/item combination as an anvil enchant
	 *
	 * @param stack The item
	 * @param book  The book
	 * @return if the enchantment is allowed
	 */
	@Stubbed
	default boolean isBookEnchantable(ItemStack book) {
		return patchwork$getForgeItem().isBookEnchantable(getStack(), book);
	}

	/**
	 * Called when a player drops the item into the world, returning false from this
	 * will prevent the item from being removed from the players inventory and
	 * spawning in the world
	 *
	 * @param player The player that dropped the item
	 * @param item   The item stack, before the item is removed.
	 */
	@Stubbed
	default boolean onDroppedByPlayer(PlayerEntity player) {
		return patchwork$getForgeItem().onDroppedByPlayer(getStack(), player);
	}

	/**
	 * Allow the item one last chance to modify its name used for the tool highlight
	 * useful for adding something extra that can't be removed by a user in the
	 * displayed name, such as a mode of operation.
	 *
	 * @param displayName the name that will be displayed unless it is changed in
	 *                    this method.
	 */
	@Stubbed
	default Text getHighlightTip(Text displayName) {
		return patchwork$getForgeItem().getHighlightTip(getStack(), displayName);
	}

	/**
	 * Get the NBT data to be sent to the client. The Item can control what data is kept in the tag.
	 * <p>
	 * Note that this will sometimes be applied multiple times, the following MUST
	 * be supported:
	 * Item item = stack.getItem();
	 * NBTTagCompound nbtShare1 = item.getNBTShareTag(stack);
	 * stack.setTagCompound(nbtShare1);
	 * NBTTagCompound nbtShare2 = item.getNBTShareTag(stack);
	 * assert nbtShare1.equals(nbtShare2);
	 *
	 * @return The NBT tag
	 */
	@Nullable
	@Stubbed
	default CompoundTag getShareTag() {
		return patchwork$getForgeItem().getShareTag(getStack());
	}

	/**
	 * Override this method to decide what to do with the NBT data received from
	 * getNBTShareTag().
	 *
	 * @param stack The stack that received NBT
	 * @param nbt   Received NBT, can be null
	 */
	@Stubbed
	default void readShareTag(@Nullable CompoundTag nbt) {
		patchwork$getForgeItem().readShareTag(getStack(), nbt);
	}

	/**
	 * Should this item, when held, allow sneak-clicks to pass through to the underlying block?
	 *
	 * @param world  The world
	 * @param pos    Block position in world
	 * @param player The Player that is wielding the item
	 * @return
	 */
	@Stubbed
	default boolean doesSneakBypassUse(net.minecraft.world.WorldView world, BlockPos pos, PlayerEntity player) {
		return getStack().isEmpty() || patchwork$getForgeItem().doesSneakBypassUse(getStack(), world, pos, player);
	}

	/**
	 * Modeled after ItemStack.areItemStackTagsEqual
	 * Uses Item.getNBTShareTag for comparison instead of NBT and capabilities.
	 * Only used for comparing itemStacks that were transferred from server to client using Item.getNBTShareTag.
	 */
	@Stubbed
	default boolean areShareTagsEqual(ItemStack other) {
		CompoundTag shareTagA = this.getShareTag();
		CompoundTag shareTagB = ((IForgeItemStack) (Object) other).getShareTag();

		if (shareTagA == null) {
			return shareTagB == null;
		} else {
			return shareTagB != null && shareTagA.equals(shareTagB);
		}
	}

	/**
	 * Determines if the ItemStack is equal to the other item stack, including Item, Count, and NBT.
	 *
	 * @param other     The other stack
	 * @param limitTags True to use shareTag False to use full NBT tag
	 * @return true if equals
	 */
	@Stubbed
	default boolean equals(ItemStack other, boolean limitTags) {
		if (getStack().isEmpty()) {
			return other.isEmpty();
		} else {
			return !other.isEmpty() && getStack().getCount() == other.getCount() && getStack().getItem() == other.getItem()
					&& (limitTags ? this.areShareTagsEqual(other) : ItemStack.areTagsEqual(getStack(), other));
		}
	}

	/**
	 * Determines if a item is reparable, used by Repair recipes and Grindstone.
	 *
	 * @return True if reparable
	 */
	@Stubbed
	default boolean isRepairable() {
		return patchwork$getForgeItem().isRepairable(getStack());
	}

	/**
	 * Called by Piglins when checking to see if they will give an item or something in exchange for this item.
	 *
	 * @return True if this item can be used as "currency" by piglins
	 */
	@Stubbed
	default boolean isPiglinCurrency() {
		return patchwork$getForgeItem().isPiglinCurrency(getStack());
	}

	/**
	 * Called by Piglins to check if a given item prevents hostility on sight. If this is true the Piglins will be neutral to the entity wearing this item, and will not
	 * attack on sight. Note: This does not prevent Piglins from becoming hostile due to other actions, nor does it make Piglins that are already hostile stop being so.
	 *
	 * @param wearer The entity wearing this ItemStack
	 * @return True if piglins are neutral to players wearing this item in an armor slot
	 */
	@Stubbed
	default boolean makesPiglinsNeutral(LivingEntity wearer) {
		return patchwork$getForgeItem().makesPiglinsNeutral(getStack(), wearer);
	}

	/**
	 * Whether this Item can be used to hide player head for enderman.
	 *
	 * @param player         The player watching the enderman
	 * @param endermanEntity The enderman that the player look
	 * @return true if this Item can be used.
	 */
	@Stubbed
	default boolean isEnderMask(PlayerEntity player, EndermanEntity endermanEntity) {
		return patchwork$getForgeItem().isEnderMask(getStack(), player, endermanEntity);
	}

	/**
	 * Used to determine if the player can use Elytra flight.
	 * This is called Client and Server side.
	 *
	 * @param entity The entity trying to fly.
	 * @return True if the entity can use Elytra flight.
	 */
	@Stubbed
	default boolean canElytraFly(LivingEntity entity) {
		return patchwork$getForgeItem().canElytraFly(getStack(), entity);
	}

	/**
	 * Used to determine if the player can continue Elytra flight,
	 * this is called each tick, and can be used to apply ItemStack damage,
	 * consume Energy, or what have you.
	 * For example the Vanilla implementation of this, applies damage to the
	 * ItemStack every 20 ticks.
	 *
	 * @param entity      The entity currently in Elytra flight.
	 * @param flightTicks The number of ticks the entity has been Elytra flying for.
	 * @return True if the entity should continue Elytra flight or False to stop.
	 */
	@Stubbed
	default boolean elytraFlightTick(LivingEntity entity, int flightTicks) {
		return patchwork$getForgeItem().elytraFlightTick(getStack(), entity, flightTicks);
	}
}

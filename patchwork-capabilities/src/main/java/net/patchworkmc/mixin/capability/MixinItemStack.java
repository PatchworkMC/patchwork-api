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

package net.patchworkmc.mixin.capability;

import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import net.patchworkmc.api.capability.CapabilityProviderConvertible;
import net.patchworkmc.api.capability.PatchworkItemStack;
import net.patchworkmc.impl.capability.BaseCapabilityProvider;
import net.patchworkmc.impl.capability.CapabilityProviderHolder;
import net.patchworkmc.impl.capability.IForgeItemDuck;
import net.patchworkmc.impl.capability.IForgeItemStackDuck;
import net.patchworkmc.impl.capability.ItemStackCapabilityAccess;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements CapabilityProviderHolder, IForgeItemStackDuck,
		ICapabilitySerializable<CompoundTag>, /*forge does this in IForgeItemStack for no good reason*/
		ItemStackCapabilityAccess {
	@Shadow
	public abstract void setTag(@Nullable CompoundTag tag);

	@Shadow
	@Final
	private Item item;

	@Shadow
	public abstract Item getItem();

	@Shadow
	public abstract CompoundTag toTag(CompoundTag tag);

	@Unique
	private CompoundTag capNBT;

	@Unique
	private final CapabilityProvider<?> internalProvider = new BaseCapabilityProvider<>(ItemStack.class, this);

	@NotNull
	@Override
	public CapabilityProvider<?> patchwork$getCapabilityProvider() {
		return internalProvider;
	}

	@Override
	public CompoundTag patchwork$getCapNBT() {
		return capNBT;
	}

	@Override
	public void patchwork$setCapNBT(CompoundTag tag) {
		this.capNBT = tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		final ItemStack itemStack = ItemStack.fromTag(nbt);
		this.setTag(itemStack.getTag());

		@SuppressWarnings("ConstantConditions")
		CompoundTag otherCapNBT = ((ItemStackCapabilityAccess) (Object) itemStack).patchwork$getCapNBT();

		if (otherCapNBT != null) {
			deserializeCaps(otherCapNBT);
		}
	}

	@Inject(method = "toTag", at = @At("TAIL"))
	private void patchwork$callSerializeCaps(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
		CompoundTag caps = this.serializeCaps();

		if (caps != null && !caps.isEmpty()) {
			tag.put("ForgeCaps", caps);
		}
	}

	// Note: this can be a redirect if it causes conflicts somehow
	@Redirect(method = "copy()Lnet/minecraft/item/ItemStack;", at = @At(value = "NEW", target = "net/minecraft/item/ItemStack",
			ordinal = 0))
	private ItemStack patchwork$copyCaps(ItemConvertible item, int size) {
		return PatchworkItemStack.of(item, size, this.serializeCaps());
	}

	/**
	 * Makes sure capabilities are included in item comparison.
	 * We need to convert
	 * <pre>{@code return left.tag == null || left.tag.equals(right.tag)}</pre>
	 * to
	 * <pre>{@code return (left.tag == null || left.tag.equals(right.tag))  && left.areCapsCompatible(right) }</pre>
	 * We achieve this by just cancelling the method and returning false if the caps aren't compatible
	 */
	// Note: there isn't a good enough Slice point for this, so we'll just go with the ordinal.
	@SuppressWarnings("unchecked")
	@Inject(method = "areTagsEqual", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD,
			target = "Lnet/minecraft/item/ItemStack;tag:Lnet/minecraft/nbt/CompoundTag;", ordinal = 2), cancellable = true)
	private static void patchwork$ensureCapsEqual(ItemStack left, ItemStack right, CallbackInfoReturnable<Boolean> cir) {
		if (!getAsCapabilityProvider(left).areCapsCompatible(getAsCapabilityProvider(right))) {
			cir.setReturnValue(false);
		}
	}

	// exactly the same as areTagsEqual but instance instead of static
	@Inject(method = "isEqual", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD,
			target = "Lnet/minecraft/item/ItemStack;tag:Lnet/minecraft/nbt/CompoundTag;", ordinal = 2), cancellable = true)
	private void patchwork$ensureCapsEqual2(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (!this.areCapsCompatible(getAsCapabilityProvider(stack))) {
			cir.setReturnValue(false);
		}
	}

	@SuppressWarnings({ "rawtypes", "ConstantConditions" })
	@Unique
	private static CapabilityProvider getAsCapabilityProvider(ItemStack stack) {
		return ((CapabilityProviderConvertible) (Object) stack).patchwork$getCapabilityProvider();
	}

	// Note the other constructor is handled by PatchworkItemStack
	@Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
	private void patchwork$callInitCaps(CompoundTag tag, CallbackInfo ci) {
		this.patchwork$initCaps();
	}

	@Override
	public void patchwork$initCaps() {
		Item item = this.item;

		if (this.item != null) {
			// just default to null if we don't have patchwork-extensions-item
			ICapabilityProvider provider = item instanceof IForgeItemDuck ? ((IForgeItemDuck) item).initCapabilities((ItemStack) (Object) this, capNBT) : null;
			this.gatherCapabilities(provider);

			if (this.capNBT != null) {
				deserializeCaps(this.capNBT);
			}
		}
	}
}

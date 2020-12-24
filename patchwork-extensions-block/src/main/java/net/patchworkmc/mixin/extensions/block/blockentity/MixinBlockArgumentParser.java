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

package net.patchworkmc.mixin.extensions.block.blockentity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockArgumentParser;
import net.patchworkmc.impl.extensions.block.BlockContext;
import net.patchworkmc.impl.extensions.block.Signatures;

@Mixin(BlockArgumentParser.class)
public abstract class MixinBlockArgumentParser {
	// First occurrence: return this.blockState.getBlock().hasBlockEntity();
	@Redirect(method = "suggestSnbtOrBlockProperties", at = @At(value = "INVOKE", target = Signatures.Block_hasBlockEntity, ordinal = 0))
	private boolean patchwork_suggestSnbtOrBlockProperties_hasBlockEntity(Block dummy) {
		BlockArgumentParser me = (BlockArgumentParser) (Object) this;
		BlockState blockState = me.getBlockState();
		return BlockContext.hasBlockEntity(blockState);
	}

	// First occurrence: return this.blockState.getBlock().hasBlockEntity();
	@Redirect(method = "hasBlockEntity()Z", at = @At(value = "INVOKE", target = Signatures.Block_hasBlockEntity, ordinal = 0))
	private boolean patchwork_hasBlockEntity_hasBlockEntity(Block dummy) {
		BlockArgumentParser me = (BlockArgumentParser) (Object) this;
		BlockState blockState = me.getBlockState();
		return BlockContext.hasBlockEntity(blockState);
	}

	// Second occurrence in BlockArgumentParser.hasBlockEntity()Z
	// while(var2.hasNext()) {
	//     Block block = (Block)var2.next();
	//     if (block.hasBlockEntity()) return true;
	// }
	//
	// Forge's patch:
	// - if (block.getDefaultState().hasTileEntity()) {
	// + if (block.hasBlockEntity()) {
	//
	// We leave it unpatched and the behavior is identical

	// First occurrence in private CompletableFuture<Suggestions> suggestSnbtOrTagProperties(SuggestionsBuilder suggestionsBuilder)
	// We leave it unpatched and the behavior is identical
}

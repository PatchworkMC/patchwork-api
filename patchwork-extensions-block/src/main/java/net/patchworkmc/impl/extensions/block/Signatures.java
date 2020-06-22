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

package net.patchworkmc.impl.extensions.block;

public class Signatures {
	public static final String PATCHWORK_YARN_CLS_BLOCKENTITYPROVIDER = "classValue=net/minecraft/block/BlockEntityProvider";
	public static final String PATCHWORK_REOBF_CLS_BLOCKENTITYPROVIDER = "classValue=net/minecraft/class_2343";
	public static final String Block_hasBlockEntity = "net/minecraft/block/Block.hasBlockEntity()Z";
	public static final String BlockState_getBlock = "net/minecraft/block/BlockState.getBlock()Lnet/minecraft/block/Block;";
	public static final String BlockEntityProvider_createBlockEntity = "net/minecraft/block/BlockEntityProvider.createBlockEntity(Lnet/minecraft/world/BlockView;)Lnet/minecraft/block/entity/BlockEntity;";
	public static final String Blocks_FIRE = "net/minecraft/block/Blocks.FIRE:Lnet/minecraft/block/Block;";
	public static final String World_getBlockState = "net/minecraft/world/World.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;";
}

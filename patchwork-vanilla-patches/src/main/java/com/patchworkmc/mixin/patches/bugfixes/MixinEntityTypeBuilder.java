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

package com.patchworkmc.mixin.patches.bugfixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

@Mixin(EntityType.Builder.class)
public abstract class MixinEntityTypeBuilder<T extends Entity> {
	@Unique
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Fixes MC-170128: Cannot build an EntityType without a datafixer due to an IllegalArgumentException.
	 */
	@Redirect(method = "build", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/schemas/Schema;getChoiceType(Lcom/mojang/datafixers/DSL$TypeReference;Ljava/lang/String;)Lcom/mojang/datafixers/types/Type;", remap = false))
	public Type catchIllegalArgumentExceptionForDataFixers(Schema schema, DSL.TypeReference type, String choiceName) {
		try {
			return schema.getChoiceType(type, choiceName);
		} catch (IllegalArgumentException ex) {
			LOGGER.warn("No data fixer registered for entity {}", choiceName);
		}

		// This return result is ignored
		return null;
	}
}

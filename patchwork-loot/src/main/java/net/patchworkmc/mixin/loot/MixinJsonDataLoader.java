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

package net.patchworkmc.mixin.loot;

import java.util.Map;

import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;

// TODO: Is there a better place to put this?
@Mixin(JsonDataLoader.class)
public abstract class MixinJsonDataLoader extends SinglePreparationResourceReloadListener<Map<Identifier, JsonObject>> {
	@Shadow
	@Final
	String dataType;

	// This does not get its own interface -- any mixins on subclasses wishing to use it should probably extend this mixin.
	protected Identifier getPreparedPath(Identifier id) {
		return new Identifier(id.getNamespace(), dataType + "/" + id.getPath() + ".json");
	}
}

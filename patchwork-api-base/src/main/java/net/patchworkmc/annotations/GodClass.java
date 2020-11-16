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

package net.patchworkmc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.lang.model.element.Modifier;

/**
 * Decorator for elements present in a "god class".
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
public @interface GodClass {
	/**
	 * Fully qualified name of the targeted god class + ':' + the name of the element.
	 * e.g. "net.minecraftforge.common.ForgeHooks:onLivingUpdate"
	 *
	 * @return FQN of target class + element name.
	 */
	String value();

	/**
	 * Modifiers for the target generated element.
	 *
	 * @return the modifiers that should be used when generating the element on the target class
	 */
	Modifier[] modifiers() default { Modifier.PUBLIC };

	/**
	 * Decorator for constructors that should be generated in an inner class.
	 */
	@Target({ ElementType.CONSTRUCTOR })
	public @interface Constructor {
		/**
		 * @return the modifiers that should be used when generating the constructor on the target inner class
		 */
		Modifier[] value() default { Modifier.PUBLIC };
	}
}

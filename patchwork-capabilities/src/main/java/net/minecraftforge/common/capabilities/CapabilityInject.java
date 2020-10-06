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

package net.minecraftforge.common.capabilities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.patchworkmc.api.capability.CapabilityRegisteredCallback;

/**
 * When placed on a field, the field will be set to an
 * instance of {@link Capability} once that capability is registered.
 * That field must be static and be able to hold a instance
 * of '{@link Capability}'
 *
 * <p>Example:
 * <blockquote>
 * {@literal @}CapabilityInject(IExampleCapability.class)<br>
 * private static final Capability&lt;IExampleCapability&gt; TEST_CAP = null;
 * </blockquote>
 *
 * <p>When placed on a method, the method will be invoked once the
 * capability is registered. This allows you to have a 'enable features'
 * callback. It MUST have one parameter of type 'Capability;
 *
 * <p>Example:
 * <blockquote>
 * {@literal @}CapabilityInject(IExampleCapability.class)
 * private static void capRegistered(Capability&lt;IExampleCapability&gt; cap) {}
 * </blockquote>
 *
 * <b>Warning</b>: Capability injections are run in the thread that the capability is registered.
 * Due to parallel mod loading, this can potentially be off of the main thread.
 *
 * @deprecated Don't use this in patchwork - use {@link CapabilityRegisteredCallback}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Deprecated
public @interface CapabilityInject {
	/**
	 * The capability interface to listen for registration.
	 * Note:
	 * When reading annotations, DO NOT call this function as it will cause a hard dependency on the class.
	 */
	Class<?> value();
}

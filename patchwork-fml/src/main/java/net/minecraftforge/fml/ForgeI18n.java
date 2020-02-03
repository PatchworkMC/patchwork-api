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

package net.minecraftforge.fml;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import net.minecraftforge.fml.loading.StringUtils;
import org.apache.commons.lang3.text.ExtendedMessageFormat;
import org.apache.commons.lang3.text.FormatFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.resource.language.I18n;

@SuppressWarnings("deprecation")
public class ForgeI18n {
	private static final Logger LOGGER = LogManager.getLogger();
	private static Map<String, FormatFactory> customFactories;

	static {
		customFactories = new HashMap<>();
		// TODO {0,modinfo,id} -> modid from ModInfo object; {0,modinfo,name} -> displayname from ModInfo object
		// {0,lower} -> lowercase supplied string
		customFactories.put("lower", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, objectToParse) -> stringBuffer.append(StringUtils.toLowerCase(String.valueOf(objectToParse)))));
		// {0,upper> -> uppercase supplied string
		customFactories.put("upper", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, objectToParse) -> stringBuffer.append(StringUtils.toUpperCase(String.valueOf(objectToParse)))));
		// {0,exc,class} -> class of exception; {0,exc,msg} -> message from exception
		customFactories.put("exc", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, objectToParse) -> parseException(formatString, stringBuffer, objectToParse)));
		// {0,vr} -> transform VersionRange into cleartext string using fml.messages.version.restriction.* strings
		customFactories.put("vr", (name, formatString, locale) -> new CustomReadOnlyFormat(((stringBuffer, o) -> MavenVersionStringHelper.parseVersionRange(formatString, stringBuffer, o))));
		// {0,i18n,fml.message} -> pass object to i18n string 'fml.message'
		customFactories.put("i18n", (name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, o) -> stringBuffer.append(ForgeI18n.parseMessage(formatString, o))));
		// {0,ornull,fml.absent} -> append String value of o, or i18n string 'fml.absent' (message format transforms nulls into the string literal "null")
		customFactories.put("ornull", ((name, formatString, locale) -> new CustomReadOnlyFormat((stringBuffer, o) -> stringBuffer.append(Objects.equals(String.valueOf(o), "null") ? ForgeI18n.parseMessage(formatString) : String.valueOf(o)))));
	}

	private static void parseException(final String formatString, final StringBuffer stringBuffer, final Object objectToParse) {
		Throwable t = (Throwable) objectToParse;

		if (Objects.equals(formatString, "msg")) {
			stringBuffer.append(t.getMessage());
		} else if (Objects.equals(formatString, "cls")) {
			stringBuffer.append(t.getClass());
		}
	}

	public static String getPattern(final String patternName) {
		return I18n.translate(patternName);
	}

	public static String parseMessage(final String i18nMessage, Object... args) {
		final String pattern = getPattern(i18nMessage);

		try {
			return parseFormat(pattern, args);
		} catch (IllegalArgumentException e) {
			LOGGER.error(CORE, "Illegal format found `{}`", pattern);
			return pattern;
		}
	}

	public static String parseFormat(final String format, final Object... args) {
		final ExtendedMessageFormat extendedMessageFormat = new ExtendedMessageFormat(format, customFactories);
		return extendedMessageFormat.format(args);
	}

	public static class CustomReadOnlyFormat extends Format {
		private final BiConsumer<StringBuffer, Object> formatter;

		CustomReadOnlyFormat(final BiConsumer<StringBuffer, Object> formatter) {
			this.formatter = formatter;
		}

		@Override
		public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
			formatter.accept(toAppendTo, obj);
			return toAppendTo;
		}

		@Override
		public Object parseObject(final String source, final ParsePosition pos) {
			throw new UnsupportedOperationException("Parsing is not supported");
		}
	}
}

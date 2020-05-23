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

package net.minecraftforge.fml.event.lifecycle;

import java.io.File;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * A special event used when the {@link Mod#certificateFingerprint()} doesn't match the certificate loaded from the JAR
 * file. You could use this to log a warning that the code that is running might not be yours, for example.
 *
 * <p>TODO: Patchwork will never fire this event. All jars that pass through patchwork are modified sweepingly.</p>
 */
public class FMLFingerprintViolationEvent extends ModLifecycleEvent {
	private final boolean isDirectory;
	private final Set<String> fingerprints;
	private final File source;
	private final String expectedFingerprint;

	public FMLFingerprintViolationEvent(boolean isDirectory, File source, ImmutableSet<String> fingerprints, String expectedFingerprint) {
		super(null);
		this.isDirectory = isDirectory;
		this.source = source;
		this.fingerprints = fingerprints;
		this.expectedFingerprint = expectedFingerprint;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public Set<String> getFingerprints() {
		return fingerprints;
	}

	public File getSource() {
		return source;
	}

	public String getExpectedFingerprint() {
		return expectedFingerprint;
	}
}

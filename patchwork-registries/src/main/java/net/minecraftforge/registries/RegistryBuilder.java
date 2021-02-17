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

package net.minecraftforge.registries;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraftforge.registries.IForgeRegistry.AddCallback;
import net.minecraftforge.registries.IForgeRegistry.BakeCallback;
import net.minecraftforge.registries.IForgeRegistry.ClearCallback;
import net.minecraftforge.registries.IForgeRegistry.CreateCallback;
import net.minecraftforge.registries.IForgeRegistry.DummyFactory;
import net.minecraftforge.registries.IForgeRegistry.MissingFactory;
import net.minecraftforge.registries.IForgeRegistry.ValidateCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class RegistryBuilder<T extends IForgeRegistryEntry<T>> {
	private static final int MAX_ID = Integer.MAX_VALUE - 1;

	private Identifier registryName;
	private Class<T> registryType;
	private Identifier optionalDefaultKey;
	private int minId = 0;
	private int maxId = MAX_ID;
	private List<AddCallback<T>> addCallback = new LinkedList<>();
	private List<ClearCallback<T>> clearCallback = new LinkedList<>();
	private List<CreateCallback<T>> createCallback = new LinkedList<>();
	private List<ValidateCallback<T>> validateCallback = new LinkedList<>();
	private List<BakeCallback<T>> bakeCallback = new LinkedList<>();
	private boolean saveToDisc = true;
	private boolean sync = true;
	private boolean allowOverrides = true;
	private boolean allowModifications = false;
	private boolean hasWrapper = false;
	@Nullable
	private DummyFactory<T> dummyFactory;
	private MissingFactory<T> missingFactory;
	private Set<Identifier> legacyNames = new HashSet<>();

	private RegistryKey<?> patchwork$vanillaRegistryKey;
	private static final Logger LOGGER = LogManager.getLogger(RegistryBuilder.class);

	/**
	 * Used by the Patchwork Vanilla Wrapper.
	 */
	public RegistryBuilder<T> setVanillaRegistryKey(RegistryKey<?> vanillaRegistryKey) {
		this.patchwork$vanillaRegistryKey = vanillaRegistryKey;
		return this;
	}

	@SuppressWarnings("unchecked")
	public Registry<T> patchwork$getVanillaRegistry() {
		// generics are pain, life is pain, just use the identifier instead and hope it works
		return this.patchwork$vanillaRegistryKey == null ? null : (Registry<T>) Registry.REGISTRIES.get(this.patchwork$vanillaRegistryKey.getValue());
	}

	public RegistryKey<?> patchwork$getVanillaRegistryKey() {
		return this.patchwork$vanillaRegistryKey;
	}

	public RegistryBuilder<T> setName(Identifier name) {
		this.registryName = name;
		return this;
	}

	public RegistryBuilder<T> setType(Class<T> type) {
		if (type == null || type.isAssignableFrom(Object.class)) {
			throw new UnsupportedOperationException("Registry type cannot be Object or null!");
		}

		this.registryType = type;
		return this;
	}

	public RegistryBuilder<T> setIDRange(int min, int max) {
		this.minId = Math.max(min, 0);
		this.maxId = Math.min(max, MAX_ID);
		return this;
	}

	public RegistryBuilder<T> setMaxID(int max) {
		return this.setIDRange(0, max);
	}

	public RegistryBuilder<T> setDefaultKey(Identifier key) {
		this.optionalDefaultKey = key;
		return this;
	}

	@SuppressWarnings("unchecked")
	public RegistryBuilder<T> addCallback(Object inst) {
		if (inst instanceof AddCallback) {
			this.add((AddCallback<T>) inst);
		} else if (inst instanceof ClearCallback) {
			this.add((ClearCallback<T>) inst);
		} else if (inst instanceof CreateCallback) {
			this.add((CreateCallback<T>) inst);
		} else if (inst instanceof ValidateCallback) {
			this.add((ValidateCallback<T>) inst);
		} else if (inst instanceof BakeCallback) {
			this.add((BakeCallback<T>) inst);
		} else if (inst instanceof DummyFactory) {
			this.set((DummyFactory<T>) inst);
		} else if (inst instanceof MissingFactory) {
			this.set((MissingFactory<T>) inst);
		}

		return this;
	}

	public RegistryBuilder<T> add(AddCallback<T> add) {
		this.addCallback.add(add);
		return this;
	}

	public RegistryBuilder<T> add(ClearCallback<T> clear) {
		this.clearCallback.add(clear);
		return this;
	}

	public RegistryBuilder<T> add(CreateCallback<T> create) {
		this.createCallback.add(create);
		return this;
	}

	public RegistryBuilder<T> add(ValidateCallback<T> validate) {
		LOGGER.warn("IForgeRegistry.ValidateCallback is not supported by Patchwork yet.");
		this.validateCallback.add(validate);
		return this;
	}

	public RegistryBuilder<T> add(BakeCallback<T> bake) {
		LOGGER.warn("IForgeRegistry.BakeCallback is not supported by Patchwork yet.");
		this.bakeCallback.add(bake);
		return this;
	}

	public RegistryBuilder<T> set(DummyFactory<T> factory) {
		LOGGER.warn("IForgeRegistry.DummyFactory is not supported by Patchwork yet.");
		this.dummyFactory = factory;
		return this;
	}

	public RegistryBuilder<T> set(MissingFactory<T> missing) {
		LOGGER.warn("IForgeRegistry.MissingFactory is not supported by Patchwork yet.");
		this.missingFactory = missing;
		return this;
	}

	public RegistryBuilder<T> disableSaving() {
		LOGGER.warn("disableSaving() is not implemented by Patchwork yet.");
		this.saveToDisc = false;
		return this;
	}

	public RegistryBuilder<T> disableSync() {
		LOGGER.warn("disableSync() is not implemented by Patchwork yet.");
		this.sync = false;
		return this;
	}

	public RegistryBuilder<T> disableOverrides() {
		this.allowOverrides = false;
		return this;
	}

	public RegistryBuilder<T> allowModification() {
		this.allowModifications = true;
		return this;
	}

	public RegistryBuilder<T> legacyName(String name) {
		LOGGER.warn("legacyName() is not implemented by Patchwork yet.");
		return legacyName(new Identifier(name));
	}

	public RegistryBuilder<T> legacyName(Identifier name) {
		LOGGER.warn("legacyName() is not implemented by Patchwork yet.");
		this.legacyNames.add(name);
		return this;
	}

	public IForgeRegistry<T> create() {
		return RegistryManager.ACTIVE.createRegistry(registryName, this);
	}

	@Nullable
	public AddCallback<T> getAdd() {
		if (addCallback.isEmpty()) {
			return null;
		}

		if (addCallback.size() == 1) {
			return addCallback.get(0);
		}

		return (owner, stage, id, obj, old) -> {
			for (AddCallback<T> cb : this.addCallback) {
				cb.onAdd(owner, stage, id, obj, old);
			}
		};
	}

	@Nullable
	public ClearCallback<T> getClear() {
		if (clearCallback.isEmpty()) {
			return null;
		}

		if (clearCallback.size() == 1) {
			return clearCallback.get(0);
		}

		return (owner, stage) -> {
			for (ClearCallback<T> cb : this.clearCallback) {
				cb.onClear(owner, stage);
			}
		};
	}

	@Nullable
	public CreateCallback<T> getCreate() {
		if (createCallback.isEmpty()) {
			return null;
		}

		if (createCallback.size() == 1) {
			return createCallback.get(0);
		}

		return (owner, stage) -> {
			for (CreateCallback<T> cb : this.createCallback) {
				cb.onCreate(owner, stage);
			}
		};
	}

	@Nullable
	public ValidateCallback<T> getValidate() {
		if (validateCallback.isEmpty()) {
			return null;
		}

		if (validateCallback.size() == 1) {
			return validateCallback.get(0);
		}

		return (owner, stage, id, key, obj) -> {
			for (ValidateCallback<T> cb : this.validateCallback) {
				cb.onValidate(owner, stage, id, key, obj);
			}
		};
	}

	@Nullable
	public BakeCallback<T> getBake() {
		if (bakeCallback.isEmpty()) {
			return null;
		}

		if (bakeCallback.size() == 1) {
			return bakeCallback.get(0);
		}

		return (owner, stage) -> {
			for (BakeCallback<T> cb : this.bakeCallback) {
				cb.onBake(owner, stage);
			}
		};
	}

	public Class<T> getType() {
		return registryType;
	}

	@Nullable
	public Identifier getDefault() {
		return this.optionalDefaultKey;
	}

	public int getMinId() {
		return minId;
	}

	public int getMaxId() {
		return maxId;
	}

	public boolean getAllowOverrides() {
		return allowOverrides;
	}

	public boolean getAllowModifications() {
		return allowModifications;
	}

	@Nullable
	public DummyFactory<T> getDummyFactory() {
		return dummyFactory;
	}

	@Nullable
	public MissingFactory<T> getMissingFactory() {
		return missingFactory;
	}

	public boolean getSaveToDisc() {
		return saveToDisc;
	}

	public boolean getSync() {
		return sync;
	}

	public Set<Identifier> getLegacyNames() {
		return legacyNames;
	}
}

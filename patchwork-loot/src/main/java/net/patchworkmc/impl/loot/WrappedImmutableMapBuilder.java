package net.patchworkmc.impl.loot;

import java.util.function.BiFunction;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableMap;

import net.patchworkmc.mixin.loot.MixinLootManager;

/**
 * A builder for creating immutable map instances that applies some sort of transformation
 * before putting items into the map. See {@link ImmutableMap.Builder} for normal usage.
 *
 * <p>Yes, this is a hack, see usage in {@link MixinLootManager}</p>
 */
@ParametersAreNonnullByDefault
public class WrappedImmutableMapBuilder<K, V> extends ImmutableMap.Builder<K, V> {
	private final BiFunction<K, V, V> wrap;

	public WrappedImmutableMapBuilder(BiFunction<K, V, V> wrap) {
		super();
		this.wrap = wrap;
	}

	@Override
	public ImmutableMap.Builder<K, V> put(K key, V value) {
		return super.put(key, wrap.apply(key, value));
	}
}

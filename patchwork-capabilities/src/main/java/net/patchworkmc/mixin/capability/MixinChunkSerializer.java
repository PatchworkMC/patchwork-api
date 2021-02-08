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

package net.patchworkmc.mixin.capability;

import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.poi.PointOfInterestStorage;

import net.patchworkmc.api.capability.CapabilityProviderConvertible;

@Mixin(value = ChunkSerializer.class, priority = 900)
public abstract class MixinChunkSerializer {
	@Shadow
	private static CompoundTag writeStructures(ChunkPos pos, Map<StructureFeature<?>, StructureStart<?>> structureStarts, Map<StructureFeature<?>, LongSet> structureReferences) {
		return null;
	}

	@Shadow
	public static ListTag toNbt(ShortList[] lists) {
		return null;
	}

	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	public static native ChunkStatus.ChunkType getChunkType(CompoundTag tag);

	@Shadow
	private static native void writeEntities(CompoundTag tag, WorldChunk chunk);

	@Shadow
	private static native Map<StructureFeature<?>, StructureStart<?>> readStructureStarts(StructureManager structureManager, CompoundTag tag, long worldSeed);

	@Shadow
	protected static native Map<StructureFeature<?>, LongSet> readStructureReferences(ChunkPos pos, CompoundTag tag);

	/**
	 * @author TheGlitch76
	 * @reason same as below
	 */
	@SuppressWarnings("checkstyle:RegexpMultiline")
	@Overwrite
	public static ProtoChunk deserialize(ServerWorld worldIn, StructureManager templateManagerIn, PointOfInterestStorage poiManager, ChunkPos pos, CompoundTag compound) {
		// CHECKSTYLE.OFF: overwrite
		ChunkGenerator chunkgenerator = worldIn.getChunkManager().getChunkGenerator();
		BiomeSource biomeprovider = chunkgenerator.getBiomeSource();
		CompoundTag compoundnbt = compound.getCompound("Level");
		ChunkPos chunkpos = new ChunkPos(compoundnbt.getInt("xPos"), compoundnbt.getInt("zPos"));

		if (!Objects.equals(pos, chunkpos)) {
			LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", pos, pos, chunkpos);
		}

		BiomeArray biomecontainer = new BiomeArray(worldIn.getRegistryManager().get(Registry.BIOME_KEY), pos, biomeprovider, compoundnbt.contains("Biomes", 11) ? compoundnbt.getIntArray("Biomes") : null);
		UpgradeData upgradedata = compoundnbt.contains("UpgradeData", 10) ? new UpgradeData(compoundnbt.getCompound("UpgradeData")) : UpgradeData.NO_UPGRADE_DATA;
		ChunkTickScheduler<Block> chunkprimerticklist = new ChunkTickScheduler<>((p_222652_0_) -> {
			return p_222652_0_ == null || p_222652_0_.getDefaultState().isAir();
		}, pos, compoundnbt.getList("ToBeTicked", 9));
		ChunkTickScheduler<Fluid> chunkprimerticklist1 = new ChunkTickScheduler<>((p_222646_0_) -> {
			return p_222646_0_ == null || p_222646_0_ == Fluids.EMPTY;
		}, pos, compoundnbt.getList("LiquidsToBeTicked", 9));
		boolean flag = compoundnbt.getBoolean("isLightOn");
		ListTag listnbt = compoundnbt.getList("Sections", 10);
		int i = 16;
		ChunkSection[] achunksection = new ChunkSection[16];
		boolean flag1 = worldIn.getDimension().hasSkyLight();
		ChunkManager abstractchunkprovider = worldIn.getChunkManager();
		LightingProvider worldlightmanager = abstractchunkprovider.getLightingProvider();

		if (flag) {
			worldlightmanager.setRetainData(pos, true);
		}

		for(int j = 0; j < listnbt.size(); ++j) {
			CompoundTag compoundnbt1 = listnbt.getCompound(j);
			int k = compoundnbt1.getByte("Y");

			if (compoundnbt1.contains("Palette", 9) && compoundnbt1.contains("BlockStates", 12)) {
				ChunkSection chunksection = new ChunkSection(k << 4);
				chunksection.getContainer().read(compoundnbt1.getList("Palette", 10), compoundnbt1.getLongArray("BlockStates"));
				chunksection.calculateCounts();

				if (!chunksection.isEmpty()) {
					achunksection[k] = chunksection;
				}

				poiManager.initForPalette(pos, chunksection);
			}

			if (flag) {
				if (compoundnbt1.contains("BlockLight", 7)) {
					worldlightmanager.enqueueSectionData(LightType.BLOCK, ChunkSectionPos.from(pos, k), new ChunkNibbleArray(compoundnbt1.getByteArray("BlockLight")), true);
				}

				if (flag1 && compoundnbt1.contains("SkyLight", 7)) {
					worldlightmanager.enqueueSectionData(LightType.SKY, ChunkSectionPos.from(pos, k), new ChunkNibbleArray(compoundnbt1.getByteArray("SkyLight")), true);
				}
			}
		}

		long k1 = compoundnbt.getLong("InhabitedTime");
		ChunkStatus.ChunkType chunkstatus$type = getChunkType(compound);
		Chunk ichunk;

		if (chunkstatus$type == ChunkStatus.ChunkType.field_12807) {
			TickScheduler<Block> iticklist;

			if (compoundnbt.contains("TileTicks", 9)) {
				iticklist = SimpleTickScheduler.fromNbt(compoundnbt.getList("TileTicks", 10), Registry.BLOCK::getId, Registry.BLOCK::get);
			} else {
				iticklist = chunkprimerticklist;
			}

			TickScheduler<Fluid> iticklist1;

			if (compoundnbt.contains("LiquidTicks", 9)) {
				iticklist1 = SimpleTickScheduler.fromNbt(compoundnbt.getList("LiquidTicks", 10), Registry.FLUID::getId, Registry.FLUID::get);
			} else {
				iticklist1 = chunkprimerticklist1;
			}

			ichunk = new WorldChunk(worldIn.toServerWorld(), pos, biomecontainer, upgradedata, iticklist, iticklist1, k1, achunksection, (p_222648_1_) -> {
				writeEntities(compoundnbt, p_222648_1_);
			});

			if (compoundnbt.contains("ForgeCaps")) ((CapabilityProviderConvertible) ichunk).patchwork$getCapabilityProvider().deserializeCaps(compoundnbt.getCompound("ForgeCaps"));
		} else {
			ProtoChunk chunkprimer = new ProtoChunk(pos, upgradedata, achunksection, chunkprimerticklist, chunkprimerticklist1);
			chunkprimer.setBiomes(biomecontainer);
			ichunk = chunkprimer;
			chunkprimer.setInhabitedTime(k1);

			chunkprimer.setStatus(ChunkStatus.byId(compoundnbt.getString("Status")));

			if (chunkprimer.getStatus().isAtLeast(ChunkStatus.FEATURES)) {
				chunkprimer.setLightingProvider(worldlightmanager);
			}

			if (!flag && chunkprimer.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
				for(BlockPos blockpos : BlockPos.iterate(pos.getStartX(), 0, pos.getStartZ(), pos.getEndX(), 255, pos.getEndZ())) {
					if (ichunk.getBlockState(blockpos).getLuminance() != 0) {
						chunkprimer.addLightSource(blockpos);
					}
				}
			}
		}

		ichunk.setLightOn(flag);
		CompoundTag compoundnbt3 = compoundnbt.getCompound("Heightmaps");
		EnumSet<Heightmap.Type> enumset = EnumSet.noneOf(Heightmap.Type.class);

		for(Heightmap.Type heightmap$type : ichunk.getStatus().getHeightmapTypes()) {
			String s = heightmap$type.getName();

			if (compoundnbt3.contains(s, 12)) {
				ichunk.setHeightmap(heightmap$type, compoundnbt3.getLongArray(s));
			} else {
				enumset.add(heightmap$type);
			}
		}

		Heightmap.populateHeightmaps(ichunk, enumset);
		CompoundTag compoundnbt4 = compoundnbt.getCompound("Structures");
		ichunk.setStructureStarts(readStructureStarts(templateManagerIn, compoundnbt4, worldIn.getSeed()));
		ichunk.setStructureReferences(readStructureReferences(pos, compoundnbt4));

		if (compoundnbt.getBoolean("shouldSave")) {
			ichunk.setShouldSave(true);
		}

		ListTag listnbt3 = compoundnbt.getList("PostProcessing", 9);

		for(int l1 = 0; l1 < listnbt3.size(); ++l1) {
			ListTag listnbt1 = listnbt3.getList(l1);

			for(int l = 0; l < listnbt1.size(); ++l) {
				ichunk.markBlockForPostProcessing(listnbt1.getShort(l), l1);
			}
		}

		if (chunkstatus$type == ChunkStatus.ChunkType.field_12807) {
			//net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkDataEvent.Load(ichunk, compound, chunkstatus$type));
			return new ReadOnlyChunk((WorldChunk)ichunk);
		} else {
			ProtoChunk chunkprimer1 = (ProtoChunk)ichunk;
			ListTag listnbt4 = compoundnbt.getList("Entities", 10);

			for(int i2 = 0; i2 < listnbt4.size(); ++i2) {
				chunkprimer1.addEntity(listnbt4.getCompound(i2));
			}

			ListTag listnbt5 = compoundnbt.getList("TileEntities", 10);

			for(int i1 = 0; i1 < listnbt5.size(); ++i1) {
				CompoundTag compoundnbt2 = listnbt5.getCompound(i1);
				ichunk.addPendingBlockEntityTag(compoundnbt2);
			}

			ListTag listnbt6 = compoundnbt.getList("Lights", 9);

			for(int j2 = 0; j2 < listnbt6.size(); ++j2) {
				ListTag listnbt2 = listnbt6.getList(j2);

				for(int j1 = 0; j1 < listnbt2.size(); ++j1) {
					chunkprimer1.addLightSource(listnbt2.getShort(j1), j2);
				}
			}

			CompoundTag compoundnbt5 = compoundnbt.getCompound("CarvingMasks");

			for(String s1 : compoundnbt5.getKeys()) {
				GenerationStep.Carver generationstage$carving = GenerationStep.Carver.valueOf(s1);
				chunkprimer1.setCarvingMask(generationstage$carving, BitSet.valueOf(compoundnbt5.getByteArray(s1)));
			}

			//net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkDataEvent.Load(ichunk, compound, chunkstatus$type));
			return chunkprimer1;
		}
	}

	// CHECKSTYLE.ON: overwrite
	/**
	 * Forge injects in a spot that's basically impossible to target in such a large class,
	 * but also makes an injection that is very unlikely to actually break anything, so we lower our
	 * priority and pray.
	 * <p>
	 * This includes some other Forge patches around adding extra try+catch blocks.
	 * It's safer to pull in the extra code than to take our risks manually fixing FernFlower's decompile errors.
	 * @author TheGlitch76
	 * @reason see above
	 */
	@Overwrite
	public static CompoundTag serialize(ServerWorld worldIn, Chunk chunkIn) {
		// CHECKSTYLE.OFF: overwrite
		ChunkPos chunkpos = chunkIn.getPos();
		CompoundTag compoundnbt = new CompoundTag();
		CompoundTag compoundnbt1 = new CompoundTag();
		compoundnbt.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		compoundnbt.put("Level", compoundnbt1);
		compoundnbt1.putInt("xPos", chunkpos.x);
		compoundnbt1.putInt("zPos", chunkpos.z);
		compoundnbt1.putLong("LastUpdate", worldIn.getTime());
		compoundnbt1.putLong("InhabitedTime", chunkIn.getInhabitedTime());
		compoundnbt1.putString("Status", chunkIn.getStatus().getId());
		UpgradeData upgradedata = chunkIn.getUpgradeData();

		if (!upgradedata.isDone()) {
			compoundnbt1.put("UpgradeData", upgradedata.toTag());
		}

		ChunkSection[] achunksection = chunkIn.getSectionArray();
		ListTag listnbt = new ListTag();
		LightingProvider worldlightmanager = worldIn.getChunkManager().getLightingProvider();
		boolean flag = chunkIn.isLightOn();

		for(int i = -1; i < 17; ++i) {
			int j = i;
			ChunkSection chunksection = Arrays.stream(achunksection).filter((p_222657_1_) -> {
				return p_222657_1_ != null && p_222657_1_.getYOffset() >> 4 == j;
			}).findFirst().orElse(WorldChunk.EMPTY_SECTION);
			ChunkNibbleArray nibblearray = worldlightmanager.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(chunkpos, j));
			ChunkNibbleArray nibblearray1 = worldlightmanager.get(LightType.SKY).getLightSection(ChunkSectionPos.from(chunkpos, j));

			if (chunksection != WorldChunk.EMPTY_SECTION || nibblearray != null || nibblearray1 != null) {
				CompoundTag compoundnbt2 = new CompoundTag();
				compoundnbt2.putByte("Y", (byte)(j & 255));

				if (chunksection != WorldChunk.EMPTY_SECTION) {
					chunksection.getContainer().write(compoundnbt2, "Palette", "BlockStates");
				}

				if (nibblearray != null && !nibblearray.isUninitialized()) {
					compoundnbt2.putByteArray("BlockLight", nibblearray.asByteArray());
				}

				if (nibblearray1 != null && !nibblearray1.isUninitialized()) {
					compoundnbt2.putByteArray("SkyLight", nibblearray1.asByteArray());
				}

				listnbt.add(compoundnbt2);
			}
		}

		compoundnbt1.put("Sections", listnbt);

		if (flag) {
			compoundnbt1.putBoolean("isLightOn", true);
		}

		BiomeArray biomecontainer = chunkIn.getBiomeArray();

		if (biomecontainer != null) {
			compoundnbt1.putIntArray("Biomes", biomecontainer.toIntArray());
		}

		ListTag listnbt1 = new ListTag();

		for(BlockPos blockpos : chunkIn.getBlockEntityPositions()) {
			CompoundTag compoundnbt4 = chunkIn.getPackedBlockEntityTag(blockpos);

			if (compoundnbt4 != null) {
				listnbt1.add(compoundnbt4);
			}
		}

		compoundnbt1.put("TileEntities", listnbt1);
		ListTag listnbt2 = new ListTag();

		if (chunkIn.getStatus().getChunkType() == ChunkStatus.ChunkType.field_12807) {
			WorldChunk chunk = (WorldChunk)chunkIn;
			chunk.setUnsaved(false);

			for(int k = 0; k < chunk.getEntitySectionArray().length; ++k) {
				for(Entity entity : chunk.getEntitySectionArray()[k]) {
					CompoundTag compoundnbt3 = new CompoundTag();

					try {
						if (entity.saveToTag(compoundnbt3)) {
							chunk.setUnsaved(true);
							listnbt2.add(compoundnbt3);
						}
					} catch (Exception e) {
						LogManager.getLogger().error("An Entity type {} has thrown an exception trying to write state. It will not persist. Report this to the mod author", entity.getType(), e);
					}
				}
			}

			try {
				final CompoundTag capTag = ((CapabilityProviderConvertible) chunk).patchwork$getCapabilityProvider().serializeCaps();
				if (capTag != null) compoundnbt1.put("ForgeCaps", capTag);
			} catch (Exception exception) {
				LogManager.getLogger().error("A capability provider has thrown an exception trying to write state. It will not persist. Report this to the mod author", exception);
			}
		} else {
			ProtoChunk chunkprimer = (ProtoChunk)chunkIn;
			listnbt2.addAll(chunkprimer.getEntities());
			compoundnbt1.put("Lights", toNbt(chunkprimer.getLightSourcesBySection()));
			CompoundTag compoundnbt5 = new CompoundTag();

			for(GenerationStep.Carver generationstage$carving : GenerationStep.Carver.values()) {
				BitSet bitset = chunkprimer.getCarvingMask(generationstage$carving);

				if (bitset != null) {
					compoundnbt5.putByteArray(generationstage$carving.toString(), bitset.toByteArray());
				}
			}

			compoundnbt1.put("CarvingMasks", compoundnbt5);
		}

		compoundnbt1.put("Entities", listnbt2);
		TickScheduler<Block> iticklist = chunkIn.getBlockTickScheduler();

		if (iticklist instanceof ChunkTickScheduler) {
			compoundnbt1.put("ToBeTicked", ((ChunkTickScheduler)iticklist).toNbt());
		} else if (iticklist instanceof SimpleTickScheduler) {
			compoundnbt1.put("TileTicks", ((SimpleTickScheduler)iticklist).toNbt());
		} else {
			compoundnbt1.put("TileTicks", worldIn.getBlockTickScheduler().toTag(chunkpos));
		}

		TickScheduler<Fluid> iticklist1 = chunkIn.getFluidTickScheduler();

		if (iticklist1 instanceof ChunkTickScheduler) {
			compoundnbt1.put("LiquidsToBeTicked", ((ChunkTickScheduler)iticklist1).toNbt());
		} else if (iticklist1 instanceof SimpleTickScheduler) {
			compoundnbt1.put("LiquidTicks", ((SimpleTickScheduler)iticklist1).toNbt());
		} else {
			compoundnbt1.put("LiquidTicks", worldIn.getFluidTickScheduler().toTag(chunkpos));
		}

		compoundnbt1.put("PostProcessing", toNbt(chunkIn.getPostProcessingLists()));
		CompoundTag compoundnbt6 = new CompoundTag();

		for(Map.Entry<Heightmap.Type, Heightmap> entry : chunkIn.getHeightmaps()) {
			if (chunkIn.getStatus().getHeightmapTypes().contains(entry.getKey())) {
				compoundnbt6.put(entry.getKey().getName(), new LongArrayTag(entry.getValue().asLongArray()));
			}
		}

		compoundnbt1.put("Heightmaps", compoundnbt6);
		compoundnbt1.put("Structures", writeStructures(chunkpos, chunkIn.getStructureStarts(), chunkIn.getStructureReferences()));
		return compoundnbt;
		// CHECKSTYLE.ON: overwrite
	}
}

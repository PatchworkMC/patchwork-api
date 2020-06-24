1. `IForgeBlockState.hasTileEntity` and `IForgeBlockState.createTileEntity`
	// TODO Call locations: Patches: Block, Block*, PistonBlock*, RepeaterBlock*, WorldRenderer*, ChunkRenderer*, BlockArgumentParser*, FallingBlockEntity*, ChestBlockEntity*, HopperBlockEntity*, Explosion*, World*, WorldChunk*, ChunkRegion*, ChunkHolder*, Forge classes: ForgeHooks, FluidUtil, ForgeHooks*, VanillaInventoryCodeHooks*
```
* OK net.minecraft.command.arguments.BlockArgumentParser
* OK net.minecraft.block.entity.HopperBlockEntity
* OK net.minecraft.block.entity.ChestBlockEntity
* OK net.minecraft.block.AbstractRedstoneGateBlock (net.minecraft.block.RedstoneDiodeBlock)
* OK net.minecraft.block.Block
* OK net.minecraft.block.SpongeBlock
* OK net.minecraft.block.PistonBlock
* OK net.minecraft.client.render.chunk.ChunkRenderer (net.minecraft.client.renderer.chunk.ChunkRenderer)
* OK net.minecraft.client.render.WorldRenderer
* OK net.minecraft.client.MinecraftClient
* OK net.minecraft.entity.FallingBlockEntity (net.minecraft.entity.item.FallingBlockEntity)
* OK net.minecraft.server.world.ChunkHolder (net.minecraft.world.server.ChunkHolder)
* OK net.minecraft.world.explosion.Explosion (net.minecraft.world.Explosion)
* OK net.minecraft.world.chunk.WorldChunk (net.minecraft.world.chunk.Chunk)
* OK net.minecraft.world.ChunkRegion (net.minecraft.world.gen.WorldGenRegion)
* OK net.minecraft.world.World
```

1. `IForgeBlockState.isBurning`
```
*   net.minecraft.world.World
```

1. `IForgeBlockState.removedByPlayer and canHarvestBlock`
```
*   net.minecraft.server.network.ServerPlayerInteractionManager
*   net.minecraft.client.network.ClientPlayerInteractionManager
```
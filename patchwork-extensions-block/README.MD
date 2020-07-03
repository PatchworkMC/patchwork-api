# `IForgeBlockState.hasTileEntity` and `IForgeBlockState.createTileEntity`
We patched all vanilla calls to those two methods so that Forge's blockstate-sensitive handlers can receive blockstate as the parameter. 
There are around 20 patches in this group:
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
Vanilla uses both `block.hasBlockEntity()` and `block instanceof BlockEntityProvider`, which is considered to be a bad practice. All of these checks are redirected to `BlockContext`.
`BlockContext.hasBlockEntity(BlockState)` calls the patched `Block.hasBlockEntity()`, which then calls the `IForgeBlock.hasBlockEntity(BlockState)`. 
`Block.hasBlockEntity()` will attempt to get the blockstate from a ThreadLocal managed by `BlockContext.hasBlockEntity(BlockState)` as the parameter, if the ThreadLocal is not set, 
the default blockstate of that block will be passed to `IForgeBlock.hasBlockEntity(BlockState)`. 
__The reason why we did not deprecate the vanilla `block.hasBlockEntity()` is that Fabric mods may call them.__


Common patch types are:
1. For `blockstate.getBlock().hasBlockEntity()` \
`getBlock()` is redirected to `call BlockContext.hasBlockEntity`, if the test returns true, the mixin method will return a vanilla block which is known to always have BlockEntity, 
otherwise it will return `Blocks.AIR`.
1. For `block.hasBlockEntity()` \
Similar to the previous case, but the blockstate is not directly available here. 
Either get it from the method args or collect it from the context. \
The latter requires ThreadLocal variables (called context here), 
they are collected using a @Injection which captures all local variables visible to the scope. 
The @Injection mixin set the context, the mixin which does the check consumes and then releases the context.
__To support potential recursive calling to the mixin target function, the lifespan of the context has to be minimized.__
1. `block instanceof BlockEntityProvider` \
These are patched with @ModifyConstant. Some cases also require a ThreadLocal for temporary storing the blockstate. The lifespan rule from the previous case also applies here.

# `IForgeBlockState.isBurning`
```
*   net.minecraft.world.World
```

# `IForgeBlockState.removedByPlayer and canHarvestBlock`
```
*   net.minecraft.server.network.ServerPlayerInteractionManager
*   net.minecraft.client.network.ClientPlayerInteractionManager
```

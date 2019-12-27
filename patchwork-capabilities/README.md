## Reasoning
MinecraftForge offers a capability API

## TODO
* Move the `net.minecraftforge.common.util` and `mcp` packages to another sub-project
* Still need to implement special cases for
  * [ChestBlockEntity](https://github.com/MinecraftForge/MinecraftForge/blob/d28cd0352b6b0fe86e062f29e681c3b14572c6d5/patches/minecraft/net/minecraft/tileentity/ChestTileEntity.java.patch#L34-L43)
  * [LockableContainerBlockEntity](https://github.com/MinecraftForge/MinecraftForge/blob/d28cd0352b6b0fe86e062f29e681c3b14572c6d5/patches/minecraft/net/minecraft/tileentity/LockableTileEntity.java.patch#L13-L19)
  * [AbstractFurnaceBlockEntity](https://github.com/MinecraftForge/MinecraftForge/blob/d28cd0352b6b0fe86e062f29e681c3b14572c6d5/patches/minecraft/net/minecraft/tileentity/AbstractFurnaceTileEntity.java.patch#L123-L134)
  * [BrewingStandBlockEntity](https://github.com/MinecraftForge/MinecraftForge/blob/d28cd0352b6b0fe86e062f29e681c3b14572c6d5/patches/minecraft/net/minecraft/tileentity/BrewingStandTileEntity.java.patch#L57-L68)
  * [LivingEntity](https://github.com/MinecraftForge/MinecraftForge/blob/d28cd0352b6b0fe86e062f29e681c3b14572c6d5/patches/minecraft/net/minecraft/entity/LivingEntity.java.patch#L488-L496)
  * [HorseBaseEntity](https://github.com/MinecraftForge/MinecraftForge/blob/d28cd0352b6b0fe86e062f29e681c3b14572c6d5/patches/minecraft/net/minecraft/entity/passive/horse/AbstractHorseEntity.java.patch#L43-L48)
  * [StorageMinecartEntity](https://github.com/MinecraftForge/MinecraftForge/blob/d28cd0352b6b0fe86e062f29e681c3b14572c6d5/patches/minecraft/net/minecraft/entity/item/minecart/ContainerMinecartEntity.java.patch#L44-L49)
  * [PlayerEntity](https://github.com/MinecraftForge/MinecraftForge/blob/d28cd0352b6b0fe86e062f29e681c3b14572c6d5/patches/minecraft/net/minecraft/entity/player/PlayerEntity.java.patch#L497-L505)
* Still need to implement `@CapabilityInject`

## Patcher
* Right before `INVOKEVIRTUAL net/minecraftforge/common/capabilities/CapabilityProvider ??? (???)???`,
  place an `INVOKESTATIC com/patchworkmc/impl/capability/CapabilityProxy getProvider (Ljava/lang/Object;)Lnet/minecraftforge/common/capabilities/CapabilityProvider; true`

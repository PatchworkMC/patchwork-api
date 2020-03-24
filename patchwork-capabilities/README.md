## Reasoning
MinecraftForge offers a capability API

## TODO
* Move the `net.minecraftforge.common.util` package to another sub-project
* Still need to implement special cases for
  * [ChestBlockEntity](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/patches/minecraft/net/minecraft/block/entity/ChestBlockEntity.java.patch#L46-L55)
  * [LockableContainerBlockEntity](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/patches/minecraft/net/minecraft/block/entity/LockableContainerBlockEntity.java.patch#L13-L19)
  * [AbstractFurnaceBlockEntity](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/patches/minecraft/net/minecraft/block/entity/AbstractFurnaceBlockEntity.java.patch#L123-L134)
  * [BrewingStandBlockEntity](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/patches/minecraft/net/minecraft/block/entity/BrewingStandBlockEntity.java.patch#L67-L78)
  * [LivingEntity](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/patches/minecraft/net/minecraft/entity/LivingEntity.java.patch#L512-L520)
  * [HorseBaseEntity](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/patches/minecraft/net/minecraft/entity/passive/HorseBaseEntity.java.patch#L43-L48)
  * [StorageMinecartEntity](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/patches/minecraft/net/minecraft/entity/vehicle/StorageMinecartEntity.java.patch#L44-L49)
  * [PlayerEntity](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/patches/minecraft/net/minecraft/entity/player/PlayerEntity.java.patch#L521-L529)

## Patcher
`CapabilityInject` on methods require a `CapabilityRegisteredCallback` to be registered. `CapabilityInject` for fields
requires a bridge callback method to be created, where the field is assigned from.

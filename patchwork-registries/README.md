# Implemented features
1. `RegistryEvent.NewRegistry` for allowing forge mods to add their own registry
1. `RegistryEvent.Register` for different in-game object registration and forge mods' custom registry 
1. StructureFeature and Feature registry
1. IForgeRegistry callbacks: CreateCallback, AddCallback and ClearCallback
1. Default entry support
1. Slave maps for wrapped vanilla registries: IForgeRegistry#getSlaveMap

# TODO
1. StructureFeature registration needs more testing
1. A fully modifiable Forge mod registry implementation
1. Code clean up in ForgeRegistries, the entry order should match Forge's version.
1. Custom forge registries and Patchwork's wrapper implementation, see GameData:
```
public static final Identifier MODDIMENSIONS = new Identifier("forge:moddimensions");
public static final Identifier SERIALIZERS = new Identifier("minecraft:dataserializers");
public static final Identifier LOOT_MODIFIER_SERIALIZERS = new Identifier("forge:loot_modifier_serializers");
```
1. Make fabric registries visible to Forge mods (Carefully consider this!)
1. Unimplemented features in ForgeRegistry and RegistryManager: saveToDisk, legacyName, alias, sync, dump
1. Unimplemented IForgeRegistry callbacks: ValidateCallback, BakeCallback, DummyFactory and MissingFactory
1. Some vanilla registry types are not patched yet, add checks to avoid crash, see `ForgeRegistry#<init>`

# Note
1. Vanilla and Fabric registry system does not support replacement and clearing, an error will be thrown if such operation is performed by a Forge mod
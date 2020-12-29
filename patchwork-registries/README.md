# Still needing updating
1. Biomes
2. Client registries
3. Some semi-internal classes lack newer helper methods (RegistryBuilder, etc.)
# TODO
1. StructureFeature registration needs more testing
2. ~~A fully modifiable Forge mod registry implementation~~
3. Code clean up in ForgeRegistries, the entry order should match Forge's version.
4. Custom forge registries and Patchwork's wrapper implementation, see GameData:
```
public static final Identifier MODDIMENSIONS = new Identifier("forge:moddimensions");
public static final Identifier SERIALIZERS = new Identifier("minecraft:dataserializers");
public static final Identifier LOOT_MODIFIER_SERIALIZERS = new Identifier("forge:loot_modifier_serializers");
```
5. ~~Make fabric registries visible to Forge mods (Carefully consider this!)~~
6. Unimplemented features in ForgeRegistry and RegistryManager: saveToDisk, legacyName, alias, sync, dump
7. Unimplemented IForgeRegistry callbacks: ValidateCallback, BakeCallback, DummyFactory and MissingFactory
8. Some vanilla registry types are not patched yet, add checks to avoid crash, see `ForgeRegistry#<init>`
9. ~~Make callbacks for handling StructureFeature registry~~

# Note
1. Vanilla and Fabric registry system does not support replacement and clearing, an error will be thrown if such operation is performed by a Forge mod

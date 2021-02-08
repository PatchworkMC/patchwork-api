# Patchwork API

A best-effort reimplementation of the Minecraft Forge API.

## Here be dragons
Patchwork only runs a small amount of mods. It is not yet ready for general use--we are only looking for developers. Testers and users aren't needed or desired at this time.

While we don't encourage (or provide support for) running Patchwork, you can track our progess at our [Discord server](https://discord.gg/YYZtNBG).

If you know Java and would like to contribute, check out our [contribution guide](CONTRIBUTING.md), and join our [Discord server](https://discord.gg/YYZtNBG).
## Technical details
Patchwork API aims to re-implement the entirety of Minecraft Forge as a set of small and modular Fabric mods. This is in order to run Minecraft Forge mods patched using [Patchwork Patcher](https://github.com/PatchworkMC/patchwork-patcher) on Fabric.

However, Patchwork aims not only to simply *run* Forge mods on Fabric but additionally aims to maintain a superior level of code quality to Forge. We feel the Fabric toolchain is superior to Forge on a technical level, but lacks both the sheer quantity of hooks and features as well as existing mod support. Thus, Patchwork seeks to provide both the benefits of Fabric and the benefits of Forge.

This modular organization is modeled after the organization of [Fabric API](https://github.com/FabricMC/fabric). This may seem like a confusing decision because Minecraft Forge itself is monolithic, but there are a few good reasons:

1. **Porting**. Since each module is a separate compilation unit, it is possible to port individual modules at a time to newer Minecraft versions. Less critical modules can also be disabled until more critical modules have been ported first.
2. **Organization**. Modules keep related code together and unrelated code separate. This means that Mixins are located in the same place with code using them. Keeping related code close together makes it easier to reason about the code, and analyze each module in isolation.
3. **It works**. The modular organization has already been used by Fabric API to great success.

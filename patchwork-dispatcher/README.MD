# Patchwork Dispatcher
A sane and simple framework around FML for Patchwork.

This document intends to (eventually) describe how mods are identified and loaded.
## Custom block
Patchwork Patcher will provide the following metadata at the root of the `custom` block:
1. `patchwork:source`: An object with a member named `loader` of value `forge`.
This is for ModMenu, and should not be considered a reliable or stable marker of a patched mod (it will be removed in 1.16)

Additionally, the following metadata will be provided through the `patchwork:patcherMeta` object:
1. `patchedOn`: An [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) date that shows when this file was patched.
It should be in UTC time and have the format of the format `yyyy-MM-dd'T'HH:mm'Z'` (which results in something like `2020-10-10T22:03Z`)
2. `patcherVersion`: The version of Patcher that this mod has been patched with.
3. `annotations`:  Points to a json file somewhere in the JAR that holds the annotation metadata.
 Must either be a valid SemVer version (i.e `1.0.0`), or `develop`. When checking if a Patcher version is recent enough for the current API version, `develop` will always be considered compatible.
4. `parent`: If the mod is a skeleton mod (TODO: Describe in more detail), this tells the mod id of it's parent
5. `children`: If the mod has children, this array lists all additional mod ids that were provided in the original Forge mod.

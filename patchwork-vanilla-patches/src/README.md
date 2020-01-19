# patchwork-vanilla-patches
A collection of bugfixes and other changes to vanilla behavior that Forge makes.

Bugfixes should be put here instead of other modules whenever possible, in order to force those fixes to be done in a way
that it doesn't break other mods.

Current changes:
- Fix MC-170128: Cannot build an EntityType without a datafixer due to an IllegalArgumentException

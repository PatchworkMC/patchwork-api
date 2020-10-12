# Patchwork Dispatcher
A sane and simple framework around FML for Patchwork.

This document intends to describe how mods are identified and loaded.
## Custom block
Patchwork Patcher will provide the following metadata at the root of the `custom` block:
1. `patchwork:source`: An object with a member named `loader` of value `forge`.
This is for ModMenu, and should not be considered a reliable or stable marker of a patched mod
2. `patchwork:annotations`: A property that points to a json file somewhere in the JAR that holds the annotation metadata. TODO: document it!

Additionally, the following metadata will be provided through the `patchwork:patcher_meta` object:
1. `patched_on`: A property string that is an [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) date format in UTC time. It should be of the format `yyyy-MM-dd'T'HH:mm'Z'` (which results in something like `2020-10-10T22:03Z`)
2. `patcher_version`: The version of Patcher that this mod has been patched with.
 Must either be a valid SemVer version (i.e `1.0.0`), or `develop`. When checking if a Patcher version is recent enough for the current API version, `develop` will always be considered compatible.
 
 

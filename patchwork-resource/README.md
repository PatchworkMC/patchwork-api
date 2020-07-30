This module contains Forge's "Selective Resource Reload Listener" (`ISelectiveResourceReloadListener`), which is used by Forge mods and Forge itself.


The ISelectiveResourceReloadListener is designed to reload only specific resources to save time. 
However, Forge's vanilla patches are not finished yet; vanilla in-game reloading still reloads everything. 
For example, when you switch the Gui language, it also reloads sounds and models which is considered to be surplus.
`ISelectiveResourceReloadListener` of Forge mods and Forge itself handles selective resource reloading correctly.

__This feature is toggleable in Forge, which indicates that Forge mods and Forge itself should be able to reload everything flawlessly in both cases.__

In Patchwork, we don't change the vanilla behavior and all reloads are not selective, at least for now, or until Forge implements this properly.

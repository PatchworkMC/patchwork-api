# patchwork-god-classes

Implementation of ForgeHooks, ForgeHooksClient, ForgeEventFactory, ClientHooks, and BasicEventHooks that dispatches to different patchwork modules.

## Reasoning

Forge uses these classes from inside their patches in order to keep the size of their patches down.
Patchwork keeps its hooks seperated between modules, however, some mods also use these classes.
Therefore, this module exists to dispatch calls to these classes to the disperate modules that actually implement the relevant methods.

Don't depend on this module from other modules. If you feel the need to do so, something has gone wrong
and implementation details should be moved into its proper module.

## TODO
 * Implement the methods on these classes that are actually already implemented in Patchwork.

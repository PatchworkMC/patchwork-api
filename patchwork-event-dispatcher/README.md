# patchwork-event-dispatcher

Implementation of ForgeHooks, ForgeHooksClient, ForgeEventFactory, and BasicEventHooks that dispatches to different patchwork modules.

## Reasoning

Forge uses these classes from inside their patches in order to keep the size of their patches down.
Patchwork keeps its hooks seperated between modules, however, some mods also use these classes.
Therefore, this module exists to dispatch calls to these classes to the disperate modules that actually implement the relevant methods.

## TODO
 * Implement the methods on these classes that are actually already implemented in Patchwork.

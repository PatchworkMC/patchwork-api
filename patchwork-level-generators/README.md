# patchwork-level-generators

## Reasoning

* Minecraft forge patches the LevelGeneratorType to be extensible through providing IForgeWorldType and making a public constrcutor
* Numerous mods for forge use this LevelGeneratorType API

## TODO
* Use IForgeDimension for the getHorizonHeight client mixin to World

# Contributing to Patchwork

Thank you for your interest in contributing! This document provides an overview of the process of contributing to Patchwork.

## Are there any prerequisites?

Patchwork is a somewhat complex project, and there's a few things you should have before you start contributing.

1. Basic knowledge of Java
2. Knowledge of and / or experience using [Mixins](https://github.com/SpongePowered/Mixin)
3. Familiarity with Fabric and modding in general


## Contribution notes

We use [MinecraftForge @ d28cd0352b6b0fe86e062f29e681c3b14572c6d5](https://github.com/MinecraftForge/MinecraftForge/tree/d28cd0352b6b0fe86e062f29e681c3b14572c6d5) as our reference commit. All features should be based on this version of Forge for consistency. For you convenience, ramidzkh has [remapped](https://github.com/PatchworkMC/YarnForge/tree/04d384add800bc395f4934507721f72eb733389f) Forge from MCP to Yarn: contributors should use this remapped version of Forge instead.

This will be changed once we update to 1.15.


## What is the general flow of contribution?

1. Pick a feature to implement. There are a few ways to do this:
    * Checking the `#wishlist` channel on the [Discord server](https://discord.gg/YYZtNBG)
    * Scrolling through the [issue tracker](https://github.com/PatchworkMC/patchwork-api/issues)
    * Picking something to implement yourself
2. Locate mods to test the feature
    * It's important to make sure that you have a way to test what you're implementing. We all accidentally introduce bugs, and it's important to eliminate the more obvious ones through testing first.
    * In addition, this also helps verify that the feature being implemented is actually used. Our time is limited, and it's good to make sure that we're spending it wisely.
    * For larger mods, it's often better to find a smaller mod using that feature instead. One option is to make a [test mod](https://github.com/PatchworkMC/patchwork-testmods) and submit a PR for it. However, please make sure that there is an existing mod using this feature, for the reason stated above.
3. Determine the correct module to put the feature in, or make a new module.
    * If you're not sure what to do, ask on [Discord](https://discord.gg/YYZtNBG) in `#api`.
    * In general, look at existing modules to see the naming conventions.
4. Implement the feature and get it running.
5. Clean up the code so that it adheres to Patchwork's standards.
    * Remove [obvious lambda abuse](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/src/main/java/net/minecraftforge/fml/network/NetworkRegistry.java#L164-L180) and replace it with more readable code.
    * Avoid [reckless usage](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/src/main/java/net/minecraftforge/fml/network/ICustomPacket.java) at runtime of Unsafe, Reflection, or ASM if possible.
    * Clean up the JavaDoc to replace [MCP names](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/src/main/java/net/minecraftforge/event/entity/EntityEvent.java#L64-L76) with Yarn names. Also replace usage of `<br>` with proper paragraph tags.
    * Make sure your code passes checkstyle by running `./gradlew checkstyleMain` (`gradlew.bat checkstyleMain` on Windows)
6. Test your feature.
    * You can either use the built in (but fragile) test mods feature by placing mods within `jars` (in the root project directory) or running `gradlew clean build` and grabbing the compiled jar out of `build/libs`, then setting up a Fabric instance in the launcher of your choice.
7. Submit a pull request to [Patchwork](https://github.com/PatchworkMC/patchwork-api)!

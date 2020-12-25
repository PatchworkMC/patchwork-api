# Contributing to Patchwork

Thank you for your interest in contributing! This document provides an overview of the process of contributing to Patchwork.

## Are there any prerequisites?

Patchwork is a somewhat complex project, and there's a few things you should have before you start contributing.

1. Basic knowledge of Java
2. Knowledge of and / or experience using [Mixins](https://github.com/SpongePowered/Mixin)
3. Familiarity with Fabric and modding in general


## Contribution notes

We use [A set version of Forge](https://github.com/PatchworkMC/YarnForge) as our reference commit. All features should be based on this version of Forge for consistency. The version at this link has been remapped to Yarn, but you can find the original Forge commit by reading the full commit message (for example, [here](https://github.com/PatchworkMC/YarnForge/commit/bb6ad3ceb8a2f5d1e27514ab62b08b8554288c51))


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
   * If you're writing mixins, make sure to follow these guidelines:
        * Avoid overwrites if at all possible. Try to write your mixin in the way that touches the least amount of code.
        * All annotations besides `@Inject` must have full signatures in the method target, like this:
        `method = "method(I)Ljava/lang/Object;"`
        * All `@At` annotations must include an ordinal (besides ones where it does nothing like `HEAD` and `TAIL`)
        * If your ordinal is greater than 1, you should try to use a slice.
        * Methods added by Forge must have a duck interface
        * By the same token, make duck interfaces with getter/setter methods for fields
        * Any added methods that are not from forge should have the prefix `patchwork$`
5. Clean up the code so that it adheres to Patchwork's standards.
    * Remove [obvious lambda abuse](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/src/main/java/net/minecraftforge/fml/network/NetworkRegistry.java#L164-L180) and replace it with more readable code.
    * Avoid [reckless usage](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/src/main/java/net/minecraftforge/fml/network/ICustomPacket.java) at runtime of Unsafe, Reflection, or ASM if possible.
    * Clean up the JavaDoc to replace [MCP names](https://github.com/PatchworkMC/YarnForge/blob/04d384add800bc395f4934507721f72eb733389f/src/main/java/net/minecraftforge/event/entity/EntityEvent.java#L64-L76) with Yarn names.
    * Make sure your code passes checkstyle by running `./gradlew checkstyleMain` (`gradlew.bat checkstyleMain` on Windows)
6. Test your feature.
    * Simply drop your patched mods into the `run/mods` folder.
7. Submit a pull request to [Patchwork](https://github.com/PatchworkMC/patchwork-api)!

This module implements:
1. Everything in `IForgeTileEntity`
2. Hooks for BlockEntityRenderer registration. (We cannot reuse Fabric hooks for this, the Fabric registration event is fired much earlier than Forge's)
3. FastTESR in 1.14.4, will be removed and replaced with vanilla BlockEntityRenderer in 1.15 and above.
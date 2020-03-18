**IForgeItem TODOs**

----

**NYI Classes**

ICapabilityProvider  

ToolType  

ITimeValue

----

**Misc**

Move temporary BEACON\_PAYMENT field to net.minecraftforge.common.Tags  

Default impl of ForgeHooks.getDefaultCreatorModId

----

**Method call locations**

Call location classes in **bold** indicate the class is calling the ItemStack or IForgeItemStack version of the method instead of the IForgeItem one

Patches: LivingEntity, ItemStack  
Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack)

Patches: **PlayerEntity**  
Forge classes: IForgeItemStack  
boolean onDroppedByPlayer(ItemStack item, PlayerEntity player)

Patches: InGameHud  
String getHighlightTip(ItemStack item, String displayName)

Patches: **PlayerController**, ItemStack, **ServerPlayerInteractionManager**  
Forge classes: IForgeItemStack  
ActionResult onItemUseFirst(ItemStack stack, ItemUsageContext context)

Patches: **RepairItemRecipe**, **GrindstoneContainer**  
Forge classes: IForgeItemStack  
boolean isRepairable(ItemStack stack)

Patches: **ExperienceOrbEntity**  
Forge classes: IForgeItemStack  
float getXpRepairRatio(ItemStack stack)

Patches: **PacketByteBuf**  
Forge classes: IForgeItemStack  
CompoundTag getShareTag(ItemStack stack)

Patches: **PacketByteBuf**  
Forge classes: IForgeItemStack  
void readShareTag(ItemStack stack, @Nullable CompoundTag nbt)

Patches: **ClientPlayerInteractionManager**, **ServerPlayerInteractionManager**  
Forge classes: IForgeItemStack  
boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player)

Patches: **LivingEntity**  
Forge classes: IForgeItemStack  
void onUsingTick(ItemStack stack, LivingEntity player, int count)

Patches: **BannerDuplicateRecipe**, **BookCloningRecipe**, **Recipe**, **BrewingStandBlockEntity**, **AbstractFurnaceBlockEntity**  
Forge classes: IForgeItemStack, ForgeHooks  
ItemStack getContainerItem(ItemStack itemStack)

Patches: **BannerDuplicateRecipe**, **BookCloningRecipe**, **Recipe**, **BrewingStandBlockEntity**, **AbstractFurnaceBlockEntity**  
Forge classes: IForgeItemStack, ForgeHooks  
boolean hasContainerItem(ItemStack stack)

Patches: **ItemEntity**  
Forge classes: IForgeItemStack, ForgeEventFactory  
int getEntityLifespan(ItemStack itemStack, World world)

Forge classes: ForgeInternalHandler  
boolean hasCustomEntity(ItemStack stack)

Forge classes: ForgeInternalHandler  
Entity createEntity(World world, Entity location, ItemStack itemstack)

Patches: **ItemEntity**  
Forge classes: IForgeItemStack  
boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)

Patches: PlayerController, **ClientPlayerInteractionManager**, **ServerPlayerInteractionManager**  
Forge classes: IForgeItemStack  
boolean doesSneakBypassUse(ItemStack stack, CollisionView world, BlockPos pos, PlayerEntity player)

Patches: PlayerInventory  
Forge classes: IForgeItemStack  
void onArmorTick(ItemStack stack, World world, PlayerEntity player)

Patches: **PlayerContainer**  
Forge classes: PlayerArmorInvWrapper, IForgeItemStack  
boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity)

Patches: **MobEntity**  
Forge classes: IForgeItemStack  
EquipmentSlot getEquipmentSlot(ItemStack stack)

Patches: **AnvilContainer**  
Forge classes: IForgeItemStack  
boolean isBookEnchantable(ItemStack stack, ItemStack book)

Patches: ArmorFeatureRenderer  
Forge classes: ForgeHooksClient  
String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type)

Patches: Screen, AbstractContainerScreen, CreativeInventoryScreen  
TextRenderer getFontRenderer(ItemStack stack)

Patches: ArmorBipedFeatureRenderer  
Forge classes: ForgeHooksClient  
<A extends BipedEntityModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A \_default)

Patches: **LivingEntity**  
Forge classes: IForgeItemStack  
boolean onEntitySwing(ItemStack stack, LivingEntity entity)

Forge classes: ForgeIngameGui  
void renderHelmetOverlay(ItemStack stack, PlayerEntity player, int width, int height, float partialTicks)

Patches: ItemStack  
int getDamage(ItemStack stack)

Patches: ItemRenderer  
boolean showDurabilityBar(ItemStack stack)

Patches: ItemRenderer  
double getDurabilityForDisplay(ItemStack stack)

Patches: ItemRenderer  
int getRGBDurabilityForDisplay(ItemStack stack)

Patches: **GrindstoneContainer**, **RepairItemRecipe**, **AnvilContainer**, ItemStack  
Forge classes: **ForgeHooks**  
int getMaxDamage(ItemStack stack)

Patches: ItemStack  
boolean isDamaged(ItemStack stack)

Patches: ItemStack  
void setDamage(ItemStack stack, int damage)

Patches: ItemStack  
boolean canHarvestBlock(ItemStack stack, BlockState state)

Patches: ItemStack  
int getItemStackLimit(ItemStack stack)

Patches: MiningToolItem  
Forge classes: IForgeItemStack, **ForgeHooks**  
Set<ToolType> getToolTypes(ItemStack stack);

Forge classes: IForgeItemStack, ForgeHooks  
int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState)

Patches: EnchantmentHelper  
Forge classes: IForgeItemStack  
int getItemEnchantability(ItemStack stack)

Patches: Enchantment, EnchantmentHelper  
Forge classes: IForgeItemStack  
boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)

Patches: **BeaconContainer**  
Forge classes: IForgeItemStack  
boolean isBeaconPayment(ItemStack stack)

Forge classes: ForgeHooksClient  
boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)

Patches: ClientPlayerInteractionManager  
Forge classes: IForgeItemStack  
boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack)

Forge classes: ForgeHooks  
boolean canContinueUsing(ItemStack oldStack, ItemStack newStack)

Patches: ItemStack  
ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)

Patches: MobEntity, PlayerEntity  
Forge classes: IForgeItemStack  
boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker)

Patches: MobEntity, PlayerEntity  
Forge classes: IForgeItemStack  
boolean isShield(ItemStack stack, @Nullable LivingEntity entity)

Forge classes: IForgeItemStack, **ForgeHooks**  
int getBurnTime(ItemStack itemStack)

Patches: HorseEntity  
Forge classes: IForgeItemStack  
void onHorseArmorTick(ItemStack stack, World world, MobEntity horse)

Patches: ItemRenderer, DynamicBlockRenderer  
ItemDynamicRenderer getTileEntityItemStackRenderer()

Patches: MinecraftClient  
Set<Identifier> getTags()

Patches: ItemStack  
<T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)

----

/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2020, 2019-2020
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.apache.commons.lang3.NotImplementedException;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.client.Mouse;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

import net.patchworkmc.impl.event.input.InputEvents;
import net.patchworkmc.impl.event.render.RenderEvents;
import net.patchworkmc.impl.extensions.item.PatchworkArmorItemHandler;
import net.patchworkmc.annotations.Stubbed;

/**
 * A stubbed out copy of Forge's ForgeHooksClient, intended for use by Forge mods only.
 * For methods that you are implementing, don't keep implementation details here.
 * Elements should be thin wrappers around methods in other modules.
 * Do not depend on this class in other modules.
 */
public class ForgeHooksClient {
	// static final ThreadLocal<BlockRenderLayer> renderLayer = new ThreadLocal<BlockRenderLayer>();

	// private static final ResourceLocation ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	// private static final Logger LOGGER = LogManager.getLogger();
	// private static final Matrix4f flipX;
	// private static final FloatBuffer matrixBuf = BufferUtils.createFloatBuffer(16);
	// private static final LightGatheringTransformer lightGatherer = new LightGatheringTransformer();
	public static String forgeStatusLine;
	// static RenderBlocks VertexBufferRB;
	// static int worldRenderPass;
	// private static int skyX, skyZ;
	// private static boolean skyInit;
	// private static int skyRGBMultiplier;
	// private static int slotMainHand = 0;

	/**
	 * Initialization of Forge Renderers.
	 */
	static {
		//FluidRegistry.renderIdFluid = RenderingRegistry.getNextAvailableRenderId();
		//RenderingRegistry.registerBlockHandler(RenderBlockFluid.instance);
	}

	//static {
	//	flipX = new Matrix4f();
	//	flipX.setIdentity();
	//	flipX.m00 = -1;
	//}

	public static String getArmorTexture(Entity entity, ItemStack armor, String defaultTexture, EquipmentSlot slot, String type) {
		return PatchworkArmorItemHandler.patchwork$getArmorTexture(entity, armor, defaultTexture, slot, type);
	}

	@Stubbed
	public static boolean onDrawBlockHighlight(WorldRenderer context, Camera info, HitResult target, int subID, float partialTicks) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static void dispatchRenderLast(WorldRenderer context, float partialTicks) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	public static boolean renderFirstPersonHand(WorldRenderer context, float partialTicks) {
		return RenderEvents.onRenderHand(context, partialTicks);
	}

	public static boolean renderSpecificFirstPersonHand(Hand hand, float partialTicks, float interpPitch, float swingProgress, float equipProgress, ItemStack stack) {
		return RenderEvents.onRenderSpecificHand(hand, partialTicks, interpPitch, swingProgress, equipProgress, stack);
	}

	public static void onTextureStitchedPre(SpriteAtlasTexture map, Set<Identifier> resourceLocations) {
		RenderEvents.onTextureStitchPre(map, resourceLocations);
	}

	public static void onTextureStitchedPost(SpriteAtlasTexture map) {
		RenderEvents.onTextureStitchPost(map);
	}

	public static void onBlockColorsInit(BlockColors blockColors) {
		RenderEvents.onBlockColorsInit(blockColors);
	}

	public static void onItemColorsInit(ItemColors itemColors, BlockColors blockColors) {
		RenderEvents.onItemColorsInit(itemColors, blockColors);
	}

	/*
	@Stubbed
	public static void setRenderLayer(BlockRenderLayer layer) {
		throw new NotImplementedException("ForgeHooksClient stub");
		//renderLayer.set(layer);
	} */

	public static <A extends BipedEntityModel<?>> A getArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot slot, A defaultModel) {
		return PatchworkArmorItemHandler.patchwork$getArmorModel(livingEntity, itemStack, slot, defaultModel);
	}

	// NOTE: this appears to be unused?
	//This properly moves the domain, if provided, to the front of the string before concatenating
	public static String fixDomain(String base, String complex) {
		int idx = complex.indexOf(':');

		if (idx == -1) {
			return base + complex;
		}

		String name = complex.substring(idx + 1);

		if (idx > 1) {
			String domain = complex.substring(0, idx);
			return domain + ':' + base + name;
		} else {
			return base + name;
		}
	}

	/* TODO (Forge): mouse input
	public static boolean postMouseEvent() {
		return MinecraftForge.EVENT_BUS.post(new MouseEvent());
	} */

	@Stubbed
	public static float getOffsetFOV(PlayerEntity entity, float fov) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static double getFOVModifier(GameRenderer renderer, Camera info, double renderPartialTicks, double fov) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static int getSkyBlendColour(World world, BlockPos center) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static void renderMainMenu(TitleScreen gui, TextRenderer font, int width, int height) {
		throw new NotImplementedException("ForgeHooksClient stub");
		// status line var is set here
	}

	@Stubbed
	public static SoundInstance playSound(SoundSystem manager, SoundInstance sound) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static int getWorldRenderPass() {
		throw new NotImplementedException("ForgeHooksClient stub");
		//return worldRenderPass;
	}

	@Stubbed
	public static void drawScreen(Screen screen, int mouseX, int mouseY, float partialTicks) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static float getFogDensity(BackgroundRenderer fogRenderer, GameRenderer renderer, Camera info, float partial, float density) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static void onFogRender(BackgroundRenderer fogRenderer, GameRenderer renderer, Camera info, float partial, int mode, float distance) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	/*
	@Stubbed
	public static EntityViewRenderEvent.CameraSetup onCameraSetup(GameRenderer renderer, Camera info, float partial, float yaw, float pitch, float roll) {
		throw new NotImplementedException("ForgeHooksClient stub");
	} */

	@Stubbed
	public static void onModelBake(BakedModelManager modelManager, Map<Identifier, BakedModel> modelRegistry, ModelLoader modelLoader) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@SuppressWarnings("deprecation")
	@Stubbed
	public static Matrix4f getMatrix(Transformation transform) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	// moved and expanded from WorldVertexBufferUploader.draw

	@Stubbed
	public static BakedModel handleCameraTransforms(BakedModel model, ModelTransformation.Mode cameraTransformType, boolean leftHandHackery) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static void multiplyCurrentGlMatrix(Matrix4f matrix) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static void preDraw(VertexFormatElement.Type attrType, VertexFormat format, int element, int stride, ByteBuffer buffer) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static void postDraw(VertexFormatElement.Type attrType, VertexFormat format, int element, int stride, ByteBuffer buffer) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static void transform(Vector3f vec, Matrix4f m) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static Matrix4f getMatrix(ModelRotation modelRotation) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static void putQuadColor(BufferBuilder renderer, BakedQuad quad, int color) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	/*
	@Stubbed
	public static Sprite[] getFluidSprites(ExtendedBlockView world, BlockPos pos, FluidState fluidStateIn) {
		throw new NotImplementedException("ForgeHooksClient stub");
	} */

	@Stubbed
	public static void gatherFluidTextures(Set<Identifier> textures) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static void renderLitItem(ItemRenderer ri, BakedModel model, int color, ItemStack stack) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	private static void drawSegment(ItemRenderer ri, int baseColor, ItemStack stack, List<BakedQuad> segment, int bl, int sl, boolean shade, boolean updateLighting, boolean updateShading) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	/**
	 * internal, relies on fixed format of FaceBakery.
	 */
	@Stubbed
	public static void fillNormal(int[] faceData, Direction facing) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	private static Vector3f getVertexPos(int[] data, int vertex) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	/*
	@SuppressWarnings("deprecation")
	@Stubbed
	public static Optional<TRSRTransformation> applyTransform(Transformation transform, Optional<? extends IModelPart> part) {
		throw new NotImplementedException("ForgeHooksClient stub");
	} */

	/*
	@Stubbed
	public static Optional<TRSRTransformation> applyTransform(ModelRotation rotation, Optional<? extends IModelPart> part) {
		throw new NotImplementedException("ForgeHooksClient stub");
	} */

	/*
	@Stubbed
	public static Optional<TRSRTransformation> applyTransform(Matrix4f matrix, Optional<? extends IModelPart> part) {
		throw new NotImplementedException("ForgeHooksClient stub");
	} */

	@Stubbed
	public static void loadEntityShader(Entity entity, GameRenderer entityRenderer) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	/*
	@Stubbed
	public static BakedModel getDamageModel(BakedModel ibakedmodel, Sprite texture, BlockState state, ExtendedBlockView world, BlockPos pos, long randomPosition) {
		throw new NotImplementedException("ForgeHooksClient stub");
	} */

	@Stubbed
	public static boolean shouldCauseReequipAnimation(@NotNull ItemStack from, @NotNull ItemStack to, int slot) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	/*
	@Stubbed
	public static ModelElementTexture applyUVLock(ModelElementTexture blockFaceUV, Direction originalSide, ITransformation rotation) {
		throw new NotImplementedException("ForgeHooksClient stub");
	} */

	/*
	@Stubbed
	public static RenderGameOverlayEvent.BossInfo bossBarRenderPre(Window res, ClientBossBar bossInfo, int x, int y, int increment) {
		throw new NotImplementedException("ForgeHooksClient stub");
	} */

	@Stubbed
	public static void bossBarRenderPost(Window res) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	/*
	@Stubbed
	public static ScreenshotEvent onScreenshot(NativeImage image, File screenshotFile) {
		throw new NotImplementedException("ForgeHooksClient stub");
	} */

	@SuppressWarnings("deprecation")
	@Stubbed
	public static Pair<? extends BakedModel, Matrix4f> handlePerspective(BakedModel model, ModelTransformation.Mode type) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static void onInputUpdate(PlayerEntity player, Input movementInput) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	/*
	@Stubbed
	public static void refreshResources(MinecraftClient mc, VanillaResourceType... types) {
		throw new NotImplementedException("ForgeHooksClient stub");
	} */

	@Stubbed
	public static boolean onGuiMouseClickedPre(Screen guiScreen, double mouseX, double mouseY, int button) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiMouseClickedPost(Screen guiScreen, double mouseX, double mouseY, int button) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiMouseReleasedPre(Screen guiScreen, double mouseX, double mouseY, int button) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiMouseReleasedPost(Screen guiScreen, double mouseX, double mouseY, int button) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiMouseDragPre(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiMouseDragPost(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiMouseScrollPre(Mouse mouseHelper, Screen guiScreen, double scrollDelta) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiMouseScrollPost(Mouse mouseHelper, Screen guiScreen, double scrollDelta) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiKeyPressedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiKeyPressedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiKeyReleasedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiKeyReleasedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiCharTypedPre(Screen guiScreen, char codePoint, int modifiers) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static boolean onGuiCharTypedPost(Screen guiScreen, char codePoint, int modifiers) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	@Stubbed
	public static void onRecipesUpdated(RecipeManager mgr) {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	// Resets cached thread fields in ThreadNameCachingStrategy and ReusableLogEventFactory to be repopulated during their next access.
	// This serves a workaround for no built-in method of triggering this type of refresh as brought up by LOG4J2-2178.
	@Stubbed
	public static void invalidateLog4jThreadCache() {
		throw new NotImplementedException("ForgeHooksClient stub");
	}

	public static void fireMouseInput(int button, int action, int mods) {
		InputEvents.fireMouseInput(button, action, mods);
	}

	public static void fireKeyInput(int key, int scanCode, int action, int modifiers) {
		InputEvents.fireKeyInput(key, scanCode, action, modifiers);
	}

	public static boolean onMouseScroll(Mouse mouseHelper, double scrollDelta) {
		return InputEvents.onMouseScroll(mouseHelper, scrollDelta);
	}

	public static boolean onRawMouseClicked(int button, int action, int mods) {
		return InputEvents.onRawMouseClicked(button, action, mods);
	}

	//private static class LightGatheringTransformer extends QuadGatheringTransformer {
	//
	//	private static final VertexFormat FORMAT = new VertexFormat().add(VertexFormats.UV_ELEMENT).add(VertexFormats.LMAP_ELEMENT);
	//
	//	int blockLight, skyLight;
	//
	//	{
	//		setVertexFormat(FORMAT);
	//	}
	//
	//	boolean hasLighting() {
	//		return dataLength[1] >= 2;
	//	}
	//
	//	@Override
	//	protected void processQuad() {
	//		// Reset light data
	//		blockLight = 0;
	//		skyLight = 0;
	//		// Compute average light for all 4 vertices
	//		for (int i = 0; i < 4; i++) {
	//			blockLight += (int) ((quadData[1][i][0] * 0xFFFF) / 0x20);
	//			skyLight += (int) ((quadData[1][i][1] * 0xFFFF) / 0x20);
	//		}
	//		// Values must be multiplied by 16, divided by 4 for average => x4
	//		blockLight *= 4;
	//		skyLight *= 4;
	//	}
	//
	//	// Dummy overrides
	//
	//	@Override
	//	public void setQuadTint(int tint) {
	//	}
	//
	//	@Override
	//	public void setQuadOrientation(Direction orientation) {
	//	}
	//
	//	@Override
	//	public void setApplyDiffuseLighting(boolean diffuse) {
	//	}
	//
	//	@Override
	//	public void setTexture(Sprite texture) {
	//	}
	//}
}


package net.patchworkmc.mixin.extensions.bakedmodel;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraftforge.client.model.data.IModelData;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.patchworkmc.impl.extensions.bakedmodel.ForgeBlockRenderManager;
import net.patchworkmc.impl.extensions.bakedmodel.ModelDataParameter;
import net.patchworkmc.impl.extensions.bakedmodel.ForgeBlockModelRenderer;

/**
 * tesselateBlock() and renderBlock().
 */
@Mixin(BlockRenderManager.class)
public class MixinBlockRenderManager implements ForgeBlockRenderManager {
	@Unique
	private static final ModelDataParameter tesselateBlock_IModelData = new ModelDataParameter();

	@Override
	public void patchwork$tesselateBlock_ModelData(IModelData modelData) {
		tesselateBlock_IModelData.setFuncParam(modelData);
	}

	@Inject(method = "tesselateBlock", at = @At("HEAD"))
	private void hookHead_tesselateBlock(BlockState blockState, BlockPos blockPos, BlockRenderView blockRenderView, BufferBuilder bufferBuilder, Random random,
			CallbackInfoReturnable<Boolean> cir) {
		tesselateBlock_IModelData.setupLocalVar();
	}

	@Inject(method = "tesselateBlock", at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE,
			target = "net/minecraft/client/render/block/BlockModelRenderer.tesselate("
					+ "Lnet/minecraft/world/BlockRenderView;"
					+ "Lnet/minecraft/client/render/model/BakedModel;"
					+ "Lnet/minecraft/block/BlockState;"
					+ "Lnet/minecraft/util/math/BlockPos;"
					+ "Lnet/minecraft/client/render/BufferBuilder;"
					+ "Z"
					+ "Ljava/util/Random;"
					+ "J)Z"))
	private void beforeBlockTesselate(CallbackInfoReturnable<Boolean> cir) {
		IModelData modelData = tesselateBlock_IModelData.getLocalVar();
		BlockRenderManager me = (BlockRenderManager) (Object) this;
		BlockModelRenderer render = me.getModelRenderer();
		((ForgeBlockModelRenderer) render).patchwork$tesselate_ModelData(modelData);
	}

	@Inject(method = "tesselateBlock", at = @At("RETURN"))
	private void hookReturn_tesselateBlock(CallbackInfoReturnable<Boolean> cir) {
		tesselateBlock_IModelData.releaseLocalVar();
	}
}

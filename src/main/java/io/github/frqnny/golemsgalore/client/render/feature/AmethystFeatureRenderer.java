package io.github.frqnny.golemsgalore.client.render.feature;

import io.github.frqnny.golemsgalore.client.render.model.ModGolemEntityModel;
import io.github.frqnny.golemsgalore.entity.ModGolemEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

public class AmethystFeatureRenderer<T extends ModGolemEntity, M extends ModGolemEntityModel<T>> extends FeatureRenderer<T, M> {
    public static final BlockState state = Blocks.AMETHYST_CLUSTER.getDefaultState();

    public AmethystFeatureRenderer(FeatureRendererContext<T, M> c) {
        super(c);
    }

    private static void method_37314(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, boolean bl, BlockRenderManager blockRenderManager, BlockState blockState, int j, BakedModel bakedModel) {
        if (bl) {
            blockRenderManager.getModelRenderer().render(matrixStack.peek(), vertexConsumerProvider.getBuffer(RenderLayer.getOutline(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)), blockState, bakedModel, 0.0F, 0.0F, 0.0F, i, j);
        } else {
            blockRenderManager.renderBlockAsEntity(blockState, matrixStack, vertexConsumerProvider, i, j);
        }

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl = minecraftClient.hasOutline(entity) && entity.isInvisible();
        if (!entity.isInvisible() || bl) {
            BlockRenderManager blockRenderManager = minecraftClient.getBlockRenderManager();
            int m = LivingEntityRenderer.getOverlay(entity, 0.0F);
            BakedModel bakedModel = blockRenderManager.getModel(state);

            matrices.push();
            this.getContextModel().getHead().rotate(matrices);
            matrices.translate(0.0D, -0.999999988079071D, -0.20000000298023224D);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-78.0F));
            matrices.scale(-0.75F, -0.75F, 0.75F);
            matrices.translate(-0.5D, -0.5D, -0.5D);
            AmethystFeatureRenderer.method_37314(matrices, vertexConsumers, light, bl, blockRenderManager, state, m, bakedModel);
            matrices.pop();


            matrices.push();
            this.getContextModel().getRightArm().rotate(matrices);
            matrices.translate(-0.65D, -0.279999988079071D, 0);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-78.0F));
            matrices.scale(-0.35F, -0.35F, 0.35F);
            matrices.translate(-0.5D, -0.5D, -0.5D);
            AmethystFeatureRenderer.method_37314(matrices, vertexConsumers, light, bl, blockRenderManager, state, m, bakedModel);
            matrices.pop();

            matrices.push();
            this.getContextModel().getLeftArm().rotate(matrices);
            matrices.translate(0.65D, -0.299999988079071D, 0);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-78.0F));
            matrices.scale(-0.4F, -0.4F, 0.4F);
            matrices.translate(-0.5D, -0.5D, -0.5D);
            AmethystFeatureRenderer.method_37314(matrices, vertexConsumers, light, bl, blockRenderManager, state, m, bakedModel);
            matrices.pop();


        }
    }
}

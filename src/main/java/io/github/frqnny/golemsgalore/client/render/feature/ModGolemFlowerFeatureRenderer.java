package io.github.frqnny.golemsgalore.client.render.feature;

import io.github.frqnny.golemsgalore.client.render.model.ModGolemEntityModel;
import io.github.frqnny.golemsgalore.entity.ModGolemEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class ModGolemFlowerFeatureRenderer<T extends ModGolemEntity, M extends ModGolemEntityModel<T>> extends FeatureRenderer<T, M> {
    public ModGolemFlowerFeatureRenderer(FeatureRendererContext<T, M> c) {
        super(c);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch) {
        if (entity.getLookingAtVillagerTicks() != 0) {
            matrices.push();
            ModelPart modelPart = (this.getContextModel()).getRightArm();

            modelPart.rotate(matrices);
            matrices.translate(-1.1875D, 1.0625D, -0.9375D);
            matrices.translate(0.5D, 0.5D, 0.5D);
            matrices.scale(0.5F, 0.5F, 0.5F);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            matrices.translate(-0.5D, -0.5D, -0.5D);
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(Blocks.POPPY.getDefaultState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
    }
}

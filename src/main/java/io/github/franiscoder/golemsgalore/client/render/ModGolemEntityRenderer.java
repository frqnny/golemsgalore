package io.github.franiscoder.golemsgalore.client.render;

import io.github.franiscoder.golemsgalore.client.render.feature.ModGolemCrackFeatureRenderer;
import io.github.franiscoder.golemsgalore.client.render.feature.ModGolemFlowerFeatureRenderer;
import io.github.franiscoder.golemsgalore.client.render.model.ModGolemEntityModel;
import io.github.franiscoder.golemsgalore.entity.ModGolemEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;

public class ModGolemEntityRenderer extends MobEntityRenderer<ModGolemEntity, ModGolemEntityModel<ModGolemEntity>> {
    public ModGolemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ModGolemEntityModel<>(), 1);
        this.addFeature(new ModGolemCrackFeatureRenderer<>(this));
        this.addFeature(new ModGolemFlowerFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(ModGolemEntity entity) {
        return entity.getGolemType().texture;

    }

    protected void setupTransforms(ModGolemEntity ironGolemEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(ironGolemEntity, matrixStack, f, g, h);
        if ((double) ironGolemEntity.limbDistance >= 0.01D) {
            float j = ironGolemEntity.limbAngle - ironGolemEntity.limbDistance * (1.0F - h) + 6.0F;
            float k = (Math.abs(j % 13.0F - 6.5F) - 3.25F) / 3.25F;
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(6.5F * k));
        }
    }

    @Override
    public void render(ModGolemEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}

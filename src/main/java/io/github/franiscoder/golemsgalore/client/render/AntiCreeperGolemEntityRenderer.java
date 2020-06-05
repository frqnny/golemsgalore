package io.github.franiscoder.golemsgalore.client.render;

import io.github.franiscoder.golemsgalore.GolemsGalore;
import io.github.franiscoder.golemsgalore.client.render.feature.ModGolemCrackFeatureRenderer;
import io.github.franiscoder.golemsgalore.client.render.feature.ModGolemFlowerFeatureRenderer;
import io.github.franiscoder.golemsgalore.client.render.model.ModGolemEntityModel;
import io.github.franiscoder.golemsgalore.entity.ModGolemEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;

public class AntiCreeperGolemEntityRenderer extends MobEntityRenderer<ModGolemEntity, ModGolemEntityModel<ModGolemEntity>> {
    public static final Identifier TEXTURE = GolemsGalore.id("textures/entity/golem/anticreeper_golem.png");

    public AntiCreeperGolemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ModGolemEntityModel<>(), 1);
        this.addFeature(new ModGolemCrackFeatureRenderer<>(this));
        this.addFeature(new ModGolemFlowerFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(ModGolemEntity entity) {
        return TEXTURE;

    }

    protected void setupTransforms(ModGolemEntity golem, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(golem, matrixStack, f, g, h);
        if ((double) golem.limbDistance >= 0.01D) {
            float j = golem.limbAngle - golem.limbDistance * (1.0F - h) + 6.0F;
            float k = (Math.abs(j % 13.0F - 6.5F) - 3.25F) / 3.25F;
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(6.5F * k));
        }
    }
}
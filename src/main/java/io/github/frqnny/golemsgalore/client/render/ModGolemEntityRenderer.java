package io.github.frqnny.golemsgalore.client.render;

import io.github.frqnny.golemsgalore.GolemsGaloreClient;
import io.github.frqnny.golemsgalore.client.render.feature.ModGolemCrackFeatureRenderer;
import io.github.frqnny.golemsgalore.client.render.feature.ModGolemFlowerFeatureRenderer;
import io.github.frqnny.golemsgalore.client.render.model.ModGolemEntityModel;
import io.github.frqnny.golemsgalore.entity.ModGolemEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class ModGolemEntityRenderer extends MobEntityRenderer<ModGolemEntity, ModGolemEntityModel<ModGolemEntity>> {
    public ModGolemEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ModGolemEntityModel<>(ctx.getPart(GolemsGaloreClient.MOD_GOLEM)), 1);
        this.addFeature(new ModGolemCrackFeatureRenderer<>(this));
        this.addFeature(new ModGolemFlowerFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(ModGolemEntity entity) {
        return entity.getGolemType().texture();

    }

    protected void setupTransforms(ModGolemEntity golem, MatrixStack matrices, float f, float g, float h) {
        super.setupTransforms(golem, matrices, f, g, h);
        if ((double) golem.limbDistance >= 0.01D) {
            float j = golem.limbAngle - golem.limbDistance * (1.0F - h) + 6.0F;
            float k = (Math.abs(j % 13.0F - 6.5F) - 3.25F) / 3.25F;
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(6.5F * k));
        }
    }
}

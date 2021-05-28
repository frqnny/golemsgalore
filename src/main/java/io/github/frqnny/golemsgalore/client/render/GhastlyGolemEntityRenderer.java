package io.github.frqnny.golemsgalore.client.render;

import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.GolemsGaloreClient;
import io.github.frqnny.golemsgalore.client.render.model.GhastlyGolemEntityModel;
import io.github.frqnny.golemsgalore.entity.GhastlyGolemEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GhastlyGolemEntityRenderer extends MobEntityRenderer<GhastlyGolemEntity, GhastlyGolemEntityModel> {
    public static final Identifier TEXTURE = GolemsGalore.id("textures/entity/golem/ghastly_golem.png");
    public static final Identifier EMPTY = new Identifier("minecraft", "textures/block/redstone_dust_overlay.png");
    public static final RenderLayer RENDER_LAYER = RenderLayer.getEntityTranslucent(TEXTURE);

    public GhastlyGolemEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new GhastlyGolemEntityModel(ctx.getPart(GolemsGaloreClient.GHASTLY_GOLEM)), 0);
        addFeature(new FeatureRenderer<GhastlyGolemEntity, GhastlyGolemEntityModel>(this) {
            @Override
            public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, GhastlyGolemEntity golem, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RENDER_LAYER);
                int overlay = LivingEntityRenderer.getOverlay(golem, 0.0F);
                getContextModel().render(matrices, vertexConsumer, light, overlay, 1, 1, 1, 1);
            }
        });
    }


    @Override
    public Identifier getTexture(GhastlyGolemEntity entity) {
        return EMPTY;
    }
}

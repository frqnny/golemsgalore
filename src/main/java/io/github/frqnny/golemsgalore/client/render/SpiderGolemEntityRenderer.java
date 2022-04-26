package io.github.frqnny.golemsgalore.client.render;

import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.GolemsGaloreClient;
import io.github.frqnny.golemsgalore.client.render.feature.SpiderGolemEyesFeatureRenderer;
import io.github.frqnny.golemsgalore.client.render.model.SpiderGolemEntityModel;
import io.github.frqnny.golemsgalore.entity.SpiderGolemEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class SpiderGolemEntityRenderer<T extends SpiderGolemEntity> extends MobEntityRenderer<T, SpiderGolemEntityModel<T>> {
    private static final Identifier TEXTURE = GolemsGalore.id("textures/entity/golem/spider_golem.png");

    public SpiderGolemEntityRenderer(EntityRendererFactory.Context context) {
        this(context, GolemsGaloreClient.SPIDER_GOLEM);
    }

    public SpiderGolemEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx, new SpiderGolemEntityModel<>(ctx.getPart(layer)), 0.8f);
        this.addFeature(new SpiderGolemEyesFeatureRenderer<>(this));
    }

    @Override
    protected float getLyingAngle(T spiderEntity) {
        return 180.0f;
    }

    @Override
    public Identifier getTexture(T spiderEntity) {
        return TEXTURE;
    }
}
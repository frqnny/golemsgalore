package io.github.frqnny.golemsgalore.client.render.feature;

import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.client.render.model.SpiderGolemEntityModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class SpiderGolemEyesFeatureRenderer<T extends Entity, M extends SpiderGolemEntityModel<T>>
        extends EyesFeatureRenderer<T, M> {
    private static final RenderLayer SKIN = RenderLayer.getEyes(GolemsGalore.id("textures/entity/spider_eyes.png"));

    public SpiderGolemEyesFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return SKIN;
    }
}

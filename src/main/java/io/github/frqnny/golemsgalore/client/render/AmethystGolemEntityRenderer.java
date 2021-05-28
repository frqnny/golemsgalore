package io.github.frqnny.golemsgalore.client.render;

import io.github.frqnny.golemsgalore.client.render.feature.AmethystFeatureRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;

public class AmethystGolemEntityRenderer extends ModGolemEntityRenderer {

    public AmethystGolemEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.addFeature(new AmethystFeatureRenderer<>(this));
    }

}

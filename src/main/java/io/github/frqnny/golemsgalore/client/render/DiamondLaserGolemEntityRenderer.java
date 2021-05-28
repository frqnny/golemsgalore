package io.github.frqnny.golemsgalore.client.render;

import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.entity.LaserGolemEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class DiamondLaserGolemEntityRenderer extends LaserGolemEntityRenderer {
    public static final Identifier TEXTURE = GolemsGalore.id("textures/entity/golem/diamond_laser_golem.png");

    public DiamondLaserGolemEntityRenderer(EntityRendererFactory.Context e) {
        super(e);
    }

    @Override
    public Identifier getTexture(LaserGolemEntity entity) {
        return TEXTURE;
    }
}

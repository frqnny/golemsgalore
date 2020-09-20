package io.github.franiscoder.golemsgalore.client.render;

import io.github.franiscoder.golemsgalore.GolemsGalore;
import io.github.franiscoder.golemsgalore.entity.LaserGolemEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;

public class DiamondLaserGolemEntityRenderer extends LaserGolemEntityRenderer {
    public static final Identifier TEXTURE = GolemsGalore.id("textures/entity/golem/diamond_laser_golem.png");

    public DiamondLaserGolemEntityRenderer(EntityRenderDispatcher e) {
        super(e);
    }

    @Override
    public Identifier getTexture(LaserGolemEntity entity) {
        return TEXTURE;
    }
}

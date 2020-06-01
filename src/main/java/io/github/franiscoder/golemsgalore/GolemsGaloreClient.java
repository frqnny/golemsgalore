package io.github.franiscoder.golemsgalore;

import io.github.franiscoder.golemsgalore.client.render.LaserGolemEntityRenderer;
import io.github.franiscoder.golemsgalore.client.render.ModGolemEntityRenderer;
import io.github.franiscoder.golemsgalore.entity.ModGolemEntity;
import io.github.franiscoder.golemsgalore.init.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;

public class GolemsGaloreClient implements ClientModInitializer {
    private static void register(EntityType<ModGolemEntity> golem) {
        EntityRendererRegistry.INSTANCE.register(golem, (entityRenderDispatcher, context) -> new ModGolemEntityRenderer(entityRenderDispatcher));
    }

    @Override
    public void onInitializeClient() {
        register(ModEntities.DIAMOND_GOLEM);
        register(ModEntities.NETHERITE_GOLEM);
        register(ModEntities.GOLDEN_GOLEM);
        register(ModEntities.QUARTZ_GOLEM);
        register(ModEntities.OBSIDIAN_GOLEM);

        EntityRendererRegistry.INSTANCE.register(ModEntities.LASER_GOLEM, (entityRenderDispatcher, context) -> new LaserGolemEntityRenderer(entityRenderDispatcher));
    }
}

package io.github.franiscoder.golemsgalore;

import io.github.franiscoder.golemsgalore.client.render.*;
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
        register(ModEntities.HAY_GOLEM);

        EntityRendererRegistry.INSTANCE.register(ModEntities.LASER_GOLEM, (entityRenderDispatcher, context) -> new LaserGolemEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(ModEntities.ANTI_CREEPER_GOLEM, (entityRenderDispatcher, context) -> new AntiCreeperGolemEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(ModEntities.DIAMOND_LASER_GOLEM, (entityRenderDispatcher, context) -> new DiamondLaserGolemEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(ModEntities.OBAMA_PRISM_GOLEM, (entityRenderDispatcher, context) -> new ObamaPyramidGolemEntityRenderer(entityRenderDispatcher));

    }
}

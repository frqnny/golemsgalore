package io.github.frqnny.golemsgalore;

import draylar.omegaconfiggui.OmegaConfigGui;
import io.github.frqnny.golemsgalore.client.render.*;
import io.github.frqnny.golemsgalore.entity.ModGolemEntity;
import io.github.frqnny.golemsgalore.init.ModEntities;
import io.github.frqnny.golemsgalore.init.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.texture.SpriteAtlasTexture;
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
        EntityRendererRegistry.INSTANCE.register(ModEntities.GHASTLY_GOLEM, (entityRenderDispatcher, context) -> new GhastlyGolemEntityRenderer(entityRenderDispatcher));

        OmegaConfigGui.registerConfigScreen(GolemsGalore.getConfig());

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(GolemsGalore.id("particle/laser_particle"));
        }));

        ParticleFactoryRegistry.getInstance().register(ModParticles.LASER, FlameParticle.Factory::new);

        //if (FabricLoader.getInstance().isModLoaded("frex")) {
        //    Renderer.get().registerMaterial(GolemsGalore.id("laser_material"), Renderer.get().)
        //}
    }
}
